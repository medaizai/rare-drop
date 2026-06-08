package com.meda;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("raredrop")
public interface RareDropConfig extends Config
{
    @ConfigItem(
            keyName = "trackedItems",
            name = "Tracked Items",
            description = "Item names, separated by commas"
    )
    default String trackedItems()
    {
        return "uncut sapphire, uncut ruby, uncut diamond, scroll box";
    }

    @ConfigItem(
            keyName = "duration",
            name = "Display Duration (ms)",
            description = "How long the alert stays"
    )
    default int duration()
    {
        return 2000;
    }

    @ConfigItem(
            keyName = "enableFlash",
            name = "Enable Screen Flash",
            description = "Toggle background rainbow flash"
    )
    default boolean enableFlash()
    {
        return true;
    }

    @ConfigItem(
            keyName = "flashIntensity",
            name = "Flash Intensity",
            description = "Transparency (0–255)"
    )
    default int flashIntensity()
    {
        return 40;
    }
}