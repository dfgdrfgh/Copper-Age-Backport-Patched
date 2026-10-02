package com.github.smallinger.copperagebackport.client.gui;

import com.github.smallinger.copperagebackport.client.gui.options.*;
import com.github.smallinger.copperagebackport.client.gui.options.control.TextBoxControl;
import com.github.smallinger.copperagebackport.client.gui.options.control.TickBoxControl;
import com.github.smallinger.copperagebackport.config.CommonConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines all config options and organizes them into pages.
 */
public class ConfigOptions {

    public static List<OptionPage> createPages() {
        List<OptionPage> pages = new ArrayList<>();
        
        pages.add(createVisualPage());
        pages.add(createCompatibilityPage());
        // Add more pages here as needed
        
        return pages;
    }

    private static OptionPage createCompatibilityPage() {
        // Lightning Rod oxidation info - always enabled, just informational
        Option<String> lightningRodInfo = OptionImpl.<String>builder(String.class)
            .name("config.copperagebackport.lightning_rod_oxidation")
            .control(TextBoxControl::new)
            .binding(
                () -> "config.copperagebackport.lightning_rod_oxidation.text",
                (v) -> {} // Read-only
            )
            .defaultValue("config.copperagebackport.lightning_rod_oxidation.text")
            .build();

        // Create group
        OptionGroup lightningRodGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.lightning_rod")
            .add(lightningRodInfo)
            .build();

        return OptionPage.builder()
            .name("config.copperagebackport.page.compatibility")
            .add(lightningRodGroup)
            .build();
    }

    private static OptionPage createVisualPage() {
        // End Flash toggle option
        Option<Boolean> endFlashEnabled = OptionImpl.<Boolean>builder(Boolean.class)
            .name("config.copperagebackport.end_flash_enabled")
            .tooltip("config.copperagebackport.end_flash_enabled.tooltip")
            .control(TickBoxControl::new)
            .binding(
                CommonConfig::endFlashEnabled,
                CommonConfig::setEndFlashEnabled
            )
            .defaultValue(true)
            .build();

        // Create group
        OptionGroup endGroup = OptionGroup.builder()
            .name("config.copperagebackport.group.end")
            .add(endFlashEnabled)
            .build();

        return OptionPage.builder()
            .name("config.copperagebackport.page.visual")
            .add(endGroup)
            .build();
    }
}
