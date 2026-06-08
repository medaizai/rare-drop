package com.meda;

import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayManager;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;



@PluginDescriptor(
        name = "Rare Drop Notifier",
        description = "Displays flashy rainbow alerts for rare drops. Never too much dopamine!"
)
public class RareDropPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ItemManager itemManager;
    @Inject private RareDropConfig config;
    @Inject private OverlayManager overlayManager;
    @Inject private RareDropOverlay overlay;

    private final Map<Integer, Integer> lastInventory = new HashMap<>();

    private String activeDrop = null;
    private long dropStart = 0;

    @Provides
    RareDropConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RareDropConfig.class);
    }

    @Override
    protected void startUp()
    {
        System.out.println("RARE DROP PLUGIN STARTED");
        System.out.println("RARE DROP PLUGIN STARTED");
        System.out.println("RARE DROP PLUGIN STARTED");
        System.out.println("RARE DROP PLUGIN STARTED");
        System.out.println("RARE DROP PLUGIN STARTED");
        System.out.println("RARE DROP PLUGIN STARTED");
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
            return;

        Item[] items = event.getItemContainer().getItems();
        Map<Integer, Integer> current = new HashMap<>();

        for (Item item : items)
        {
            if (item.getId() == -1) continue;
            current.merge(item.getId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Integer, Integer> entry : current.entrySet())
        {
            int id = entry.getKey();
            int qty = entry.getValue();

            int oldQty = lastInventory.getOrDefault(id, 0);

            if (qty > oldQty && isTracked(id))
            {
                activeDrop = itemManager.getItemComposition(id).getName();
                dropStart = System.currentTimeMillis();
            }
        }

        lastInventory.clear();
        lastInventory.putAll(current);
    }

    private boolean isTracked(int itemId)
    {
        String name = itemManager.getItemComposition(itemId).getName().toLowerCase();

        for (String s : config.trackedItems().split(","))
        {
            if (name.contains(s.trim().toLowerCase()))
                return true;
        }
        return false;
    }

    public boolean isActive()
    {
        return activeDrop != null &&
                System.currentTimeMillis() - dropStart < config.duration();
    }

    public String getDrop()
    {
        return activeDrop;
    }

    public float getProgress()
    {
        return (System.currentTimeMillis() - dropStart) / (float) config.duration();
    }

    public long getStartTime()
    {
        return dropStart;
    }
}