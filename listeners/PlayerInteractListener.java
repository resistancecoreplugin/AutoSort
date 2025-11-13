package com.resistancecore.autosort.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.resistancecore.autosort.AutoSortPlugin;

public class PlayerInteractListener implements Listener {
    
    private final AutoSortPlugin plugin;
    
    public PlayerInteractListener(AutoSortPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getConfigManager().isAutoSortEnabled()) {
            return;
        }
        
        if (plugin.getConfigManager().isRightClickEnabled()) {
            handleRightClickSort(event, player);
        }
        
        if (plugin.getConfigManager().isShiftClickEnabled()) {
            handleShiftClickSort(event, player);
        }
    }
    
    private void handleRightClickSort(PlayerInteractEvent event, Player player) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !isChestBlock(clickedBlock)) {
            return;
        }
        
        if (!player.isSneaking()) {
            return;
        }
        
        event.setCancelled(true);
        
        Inventory chestInventory = getChestInventory(clickedBlock);
        if (chestInventory != null) {
            plugin.getSortManager().sortInventory(player, chestInventory);
        }
    }
    
    private void handleShiftClickSort(PlayerInteractEvent event, Player player) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !isChestBlock(clickedBlock)) {
            return;
        }
        
        if (!player.isSneaking()) {
            return;
        }
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            Inventory chestInventory = getChestInventory(clickedBlock);
            if (chestInventory != null) {
                plugin.getSortManager().sortInventory(player, chestInventory);
            }
        }
    }
    
    private boolean isChestBlock(Block block) {
        Material type = block.getType();
        return type == Material.CHEST || 
               type == Material.TRAPPED_CHEST ||
               type == Material.ENDER_CHEST ||
               type == Material.SHULKER_BOX ||
               isShulkerBox(type);
    }
    
    private boolean isShulkerBox(Material material) {
        String name = material.name();
        return name.endsWith("_SHULKER_BOX");
    }
    
    private Inventory getChestInventory(Block block) {
        if (block.getState() instanceof InventoryHolder holder) {
            return holder.getInventory();
        }
        return null;
    }
}