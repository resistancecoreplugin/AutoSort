package com.resistancecore.autosort.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.resistancecore.autosort.AutoSortPlugin;

public class ChestSortListener implements Listener {
    
    private final AutoSortPlugin plugin;
    
    public ChestSortListener(AutoSortPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getConfigManager().isAutoSortEnabled()) {
            return;
        }
        
        if (!plugin.getConfigManager().isSortOnOpen()) {
            return;
        }
        
        Inventory inventory = event.getInventory();
        
        if (!isChestLikeInventory(inventory)) {
            return;
        }
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.getOpenInventory().getTopInventory().equals(inventory)) {
                plugin.getSortManager().sortInventory(player, inventory);
            }
        }, 1L);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!plugin.getConfigManager().isAutoSortEnabled()) {
            return;
        }
        
        if (event.getClick() == ClickType.MIDDLE) {
            handleMiddleClickSort(event, player);
            return;
        }
        
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            handleHotkeySort(event, player);
        }
    }
    
    private void handleMiddleClickSort(InventoryClickEvent event, Player player) {
        Inventory clickedInventory = event.getClickedInventory();
        
        if (clickedInventory == null || !isChestLikeInventory(clickedInventory)) {
            return;
        }
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            event.setCancelled(true);
            plugin.getSortManager().sortInventory(player, clickedInventory);
        }
    }
    
    private void handleHotkeySort(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            return;
        }
        
        Inventory clickedInventory = event.getClickedInventory();
        
        if (clickedInventory == null || !isChestLikeInventory(clickedInventory)) {
            return;
        }
        
        InventoryView view = event.getView();
        if (clickedInventory.equals(view.getTopInventory())) {
            event.setCancelled(true);
            plugin.getSortManager().sortInventory(player, clickedInventory);
        }
    }
    
    private boolean isChestLikeInventory(Inventory inventory) {
        InventoryType type = inventory.getType();
        
        return type == InventoryType.CHEST ||
               type == InventoryType.ENDER_CHEST ||
               type == InventoryType.SHULKER_BOX ||
               type == InventoryType.BARREL ||
               type == InventoryType.HOPPER ||
               type == InventoryType.DISPENSER ||
               type == InventoryType.DROPPER;
    }
}