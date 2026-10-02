package com.github.smallinger.copperagebackport.entity.ai;

import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.ModSounds;
import com.github.smallinger.copperagebackport.ModTags;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.block.entity.CopperChestBlockEntity;
import com.github.smallinger.copperagebackport.entity.CopperGolemState;
import com.github.smallinger.copperagebackport.entity.ai.behavior.InteractWithDoor;
import com.github.smallinger.copperagebackport.entity.ai.behavior.TransportItemsBetweenContainers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.BiPredicate;

/**
 * AI Brain System für Copper Golem
 * Basierend auf der Original-Implementation aus Minecraft 1.21.10
 */
public class CopperGolemAi {
    
    // Sensor Types für Brain-System
    private static final ImmutableList<SensorType<? extends Sensor<? super CopperGolemEntity>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES,
        SensorType.HURT_BY
    );
    
    // Memory Module Types für Brain-System
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.PATH,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.NEAREST_LIVING_ENTITIES,
        MemoryModuleType.DOORS_TO_CLOSE,  // Für Tür-Interaktionen
        ModMemoryTypes.GAZE_COOLDOWN_TICKS.get(),
        ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(),
        ModMemoryTypes.VISITED_BLOCK_POSITIONS.get(),
        ModMemoryTypes.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get()
    );
    
    /**
     * Erstellt Brain.Provider für Copper Golem
     */
    public static Brain.Provider<CopperGolemEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }
    
    /**
     * Erstellt und konfiguriert Brain für Copper Golem
     */
    public static Brain<CopperGolemEntity> makeBrain(Brain<CopperGolemEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }
    
    /**
     * Core Activity - Grundlegende Behaviors die immer aktiv sind
     * Kein Swim-Task: underwater breathing is handled by the finalized entity-type tag.
     */
    private static void initCoreActivity(Brain<CopperGolemEntity> brain) {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.<BehaviorControl<? super CopperGolemEntity>>of(
                new AnimalPanic<>(1.5F),  // Panik-Verhalten wenn beschädigt
                new LookAtTargetSink(45, 90),  // Schaut zum Look-Target
                new MoveToTargetSink(),  // Bewegt sich zum Walk-Target
                InteractWithDoor.create(),  // Türen öffnen und schließen
                new CountDownCooldownTicks(ModMemoryTypes.GAZE_COOLDOWN_TICKS.get()),  // Gaze Cooldown
                new CountDownCooldownTicks(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get())  // Transport Cooldown
            )
        );
    }
    
    /**
     * Idle Activity - Behaviors wenn Golem nichts Spezielles tut
     * Priority 0: Item Transport (höchste Priorität)
     * Priority 1: Schaue manchmal Spieler an
     * Priority 2: Herumlaufen oder Stillstehen (wenn Cooldown aktiv)
     */
    private static void initIdleActivity(Brain<CopperGolemEntity> brain) {
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super CopperGolemEntity>>> behaviorsBuilder = ImmutableList.builder();
        
        // Prio 0: Item Transport zwischen Copper Chests und Target Chests (alle kompatiblen Chests)
        behaviorsBuilder.add(Pair.of(0, new TransportItemsBetweenContainers(
            1.0F,  // Speed Modifier
            state -> state.is(ModTags.Blocks.COPPER_CHESTS),  // Source: Nur Copper Chests (fest)
            state -> isValidDestinationContainer(state),  // Target: Chest / Trapped Chest
            32,  // Horizontal Search Distance
            8,   // Vertical Search Distance
            getTargetReachedInteractions(),  // Interaction callbacks
            onTravelling(),  // On start travelling callback
            shouldQueueForTarget()  // Should queue predicate
        )));
        
        // Prio 1: Schaue manchmal Spieler an (6 Blöcke Reichweite, 40-80 Ticks Interval)
        behaviorsBuilder.add(Pair.of(1, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(40, 80))));
        
        // Prio 2: Herumlaufen oder Stillstehen
        // Nur wenn kein Walk-Target gesetzt ist UND Transport-Cooldown aktiv ist
        behaviorsBuilder.add(Pair.of(2, new RunOne<>(
            ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), MemoryStatus.VALUE_PRESENT
            ),
            ImmutableList.of(
                // 50% Chance: Zufällig herumlaufen (1.0 Speed, max 2 Blöcke horizontal, 2 Blöcke vertikal)
                Pair.of(RandomStroll.stroll(1.0F, 2, 2), 1),
                // 50% Chance: Stillstehen für 30-60 Ticks
                Pair.of(new DoNothing(30, 60), 1)
            )
        )));
        
        brain.addActivity(Activity.IDLE, behaviorsBuilder.build());
    }
    
    /**
     * Erstellt die Map mit Interaktions-Callbacks für Container-Interaktionen
     */
    private static Map<TransportItemsBetweenContainers.ContainerInteractionState, TransportItemsBetweenContainers.OnTargetReachedInteraction> getTargetReachedInteractions() {
        return Map.of(
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_ITEM, ModSounds.COPPER_GOLEM_ITEM_GET.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.GETTING_NO_ITEM, ModSounds.COPPER_GOLEM_ITEM_NO_GET.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_ITEM, ModSounds.COPPER_GOLEM_ITEM_DROP.get()),
            TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM,
            onReachedTargetInteraction(CopperGolemState.DROPPING_NO_ITEM, ModSounds.COPPER_GOLEM_ITEM_NO_DROP.get())
        );
    }
    
    /**
     * Callback wenn Golem ein Target erreicht hat
     * Setzt Animation State und öffnet/schließt Container
     */
    private static TransportItemsBetweenContainers.OnTargetReachedInteraction onReachedTargetInteraction(
        CopperGolemState state, @Nullable net.minecraft.sounds.SoundEvent sound
    ) {
        return (mob, target, tick) -> {
            if (mob instanceof CopperGolemEntity copperGolem) {
                if (tick == 1) {
                    // Container öffnen mit Sound
                    playChestSound(copperGolem, target.pos(), true);
                    copperGolem.setOpenedChestPos(target.pos());
                    copperGolem.setState(state);
                }
                
                // Tick 9: Item-Interaction Sound abspielen
                if (tick == 9 && sound != null) {
                    copperGolem.playSound(sound, 1.0F, 1.0F);
                }
                
                if (tick == 60) {
                    // Container schließen mit Sound
                    playChestSound(copperGolem, target.pos(), false);
                    copperGolem.clearOpenedChestPos();
                }
            }
        };
    }
    
    /**
     * Spielt den Chest Open/Close Sound ab und triggert die Animation
     * Unterstützt: Copper Chests, Barrels, Regular Chests, und mod-kompatible Container
     */
    /**
     * Emulates modern Inventory#onOpen/onClose for 1.21.1, whose chest API only
     * accepts Player viewers. This preserves double-chest animation, existing
     * player opener counts, and oxidation-specific Copper Chest sounds.
     */
    private static void playChestSound(CopperGolemEntity golem, BlockPos pos, boolean open) {
        Level level = golem.level();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) {
            return;
        }

        net.minecraft.world.level.block.state.properties.ChestType type =
            state.getValue(ChestBlock.TYPE);
        BlockPos connectedPos = type == net.minecraft.world.level.block.state.properties.ChestType.SINGLE
            ? null
            : pos.relative(ChestBlock.getConnectedDirection(state));

        // Modern double-container open/close reaches both halves.
        updateChestHalf(level, golem, pos, open);
        if (connectedPos != null) {
            updateChestHalf(level, golem, connectedPos, open);
        }

        // Vanilla chest sounds are emitted only by the non-LEFT half.
        BlockPos soundPos = pos;
        BlockState soundState = state;
        if (type == net.minecraft.world.level.block.state.properties.ChestType.LEFT && connectedPos != null) {
            soundPos = connectedPos;
            soundState = level.getBlockState(soundPos);
        }

        if (getPlayerOpenerCount(level, soundPos) == 0) {
            net.minecraft.sounds.SoundEvent soundEvent;
            if (soundState.getBlock() instanceof com.github.smallinger.copperagebackport.block.CopperChestBlock copperChest) {
                soundEvent = open ? copperChest.getOpenSound() : copperChest.getCloseSound();
            } else {
                soundEvent = open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE;
            }
            playVanillaChestSound(level, soundPos, soundState, soundEvent);
        }
    }

    private static void updateChestHalf(Level level, CopperGolemEntity golem, BlockPos pos, boolean open) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) {
            return;
        }

        int playerOpeners = getPlayerOpenerCount(level, pos);
        int displayedOpeners = open ? playerOpeners + 1 : playerOpeners;
        level.blockEvent(pos, state.getBlock(), 1, displayedOpeners);

        // ContainerOpenersCounter emits these only on the 0<->1 transition.
        if (playerOpeners == 0) {
            level.gameEvent(
                golem,
                open
                    ? net.minecraft.world.level.gameevent.GameEvent.CONTAINER_OPEN
                    : net.minecraft.world.level.gameevent.GameEvent.CONTAINER_CLOSE,
                pos
            );
        }
    }

    private static int getPlayerOpenerCount(Level level, BlockPos pos) {
        net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CopperChestBlockEntity copperChest) {
            return copperChest.getPlayerOpenerCount();
        }
        if (blockEntity instanceof ChestBlockEntity) {
            return ChestBlockEntity.getOpenCount(level, pos);
        }
        return 0;
    }

    private static void playVanillaChestSound(
        Level level,
        BlockPos pos,
        BlockState state,
        net.minecraft.sounds.SoundEvent soundEvent
    ) {
        net.minecraft.world.level.block.state.properties.ChestType type =
            state.getValue(ChestBlock.TYPE);
        if (type == net.minecraft.world.level.block.state.properties.ChestType.LEFT) {
            return;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        if (type == net.minecraft.world.level.block.state.properties.ChestType.RIGHT) {
            net.minecraft.core.Direction connected = ChestBlock.getConnectedDirection(state);
            x += connected.getStepX() * 0.5;
            z += connected.getStepZ() * 0.5;
        }

        level.playSound(
            null,
            x,
            y,
            z,
            soundEvent,
            SoundSource.BLOCKS,
            0.5F,
            level.random.nextFloat() * 0.1F + 0.9F
        );
    }

    /**
     * Callback wenn Golem zu einem Target läuft
     * Setzt State zurück auf IDLE
     */
    private static Consumer<PathfinderMob> onTravelling() {
        return mob -> {
            if (mob instanceof CopperGolemEntity copperGolem) {
                copperGolem.clearOpenedChestPos();
                copperGolem.setState(CopperGolemState.IDLE);
            }
        };
    }
    
    /**
     * Prüft ob ein anderer Mob bereits mit dem Target interagiert
     * Wenn ja, sollte der Golem in eine Warteschlange gehen
     */
    private static BiPredicate<TransportItemsBetweenContainers.TransportItemTarget, PathfinderMob> shouldQueueForTarget() {
        return (target, mob) -> {
            Level level = mob.level();

            // Player-opened chests count as viewers, matching the modern vanilla storage predicate.
            if (target.blockEntity() instanceof CopperChestBlockEntity copperChest) {
                if (copperChest.isChestOpen()) {
                    return true;
                }
            } else if (target.blockEntity() instanceof ChestBlockEntity
                    && ChestBlockEntity.getOpenCount(level, target.pos()) > 0) {
                return true;
            }

            // 1.21.1's ChestBlockEntity opener counter only tracks players, so also
            // account for another backported Copper Golem currently interacting
            // with this chest (or the connected half of a double chest).
            AABB interactionArea = new AABB(target.pos()).inflate(3.0);
            return !level.getEntitiesOfClass(
                CopperGolemEntity.class,
                interactionArea,
                other -> other != mob && other.isViewingContainerAt(target.pos())
            ).isEmpty();
        };
    }
    
    /**
     * Prüft ob ein BlockState ein gültiges Ziel für Item-Transport ist.
     * Unterstützt Vanilla Chests, Barrels und mod-kompatible Container.
     * Auch Mod-Chests die von ChestBlock erben (z.B. Woodworks, Quark) werden erkannt.
     * WICHTIG: Copper Chests sind ausgeschlossen (sind nur Source, nicht Destination)!
     */
    private static boolean isValidDestinationContainer(BlockState state) {
        if (state.is(ModTags.Blocks.COPPER_CHESTS)) {
            return false;
        }

        // Vanilla Copper Golems deposit only into normal and trapped chests.
        return state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST);
    }

    /**
     * Update Activity - Wird jeden Tick aufgerufen
     */
    public static void updateActivity(CopperGolemEntity golem) {
        golem.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}

