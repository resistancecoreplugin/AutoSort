package com.resistancecore.autosort.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.resistancecore.autosort.AutoSortPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class SortManager {
    
    private final AutoSortPlugin plugin;
    private final Map<UUID, Long> cooldowns;
    
    public SortManager(AutoSortPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }
    
    public boolean sortInventory(Player player, Inventory inventory) {
        ConfigManager config = plugin.getConfigManager();
        
        try {
            if (!player.hasPermission("autosort.use")) {
                sendMessage(player, config.getNoPermissionMessage());
                return false;
            }
            
            if (isOnCooldown(player)) {
                return false;
            }
            
            if (config.getDisabledWorlds().contains(player.getWorld().getName())) {
                return false;
            }
            
            boolean sorted = performSort(inventory, config.getSortType(), config.isReverseSort(), config.isStackSimilarItems());
            
            if (sorted) {
                
                setCooldown(player);
                
                
                if (config.shouldSendMessage()) {
                    sendMessage(player, config.getSortMessage());
                }
                
                
                if (config.shouldPlaySound()) {
                    playSound(player, config.getSortSound());
                }
            }
            
            return sorted;
        } catch (Exception e) {
            plugin.getLogger().warning("Error sorting inventory for player " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
    
    private boolean performSort(Inventory inventory, String sortType, boolean reverse, boolean stackItems) {
        try {
            
            List<ItemStack> items = new ArrayList<>();
            
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item.clone());
                    inventory.setItem(i, null);
                }
            }
            
            if (items.isEmpty()) {
                return false;
            }
            
            
            if (stackItems) {
                items = stackSimilarItems(items);
            }
            
            
            switch (sortType.toLowerCase()) {
                case "name":
                    items.sort(Comparator.comparing(item -> item.getType().name()));
                    break;
                case "type":
                    items.sort(Comparator.comparing(item -> item.getType().toString()));
                    break;
                case "amount":
                    items.sort(Comparator.comparing(ItemStack::getAmount));
                    break;
                case "rarity":
                    items.sort(this::compareByRarity);
                    break;
                default:
                    items.sort(Comparator.comparing(item -> item.getType().name()));
            }
            
            
            if (reverse) {
                Collections.reverse(items);
            }
            
            
            int slot = 0;
            for (ItemStack item : items) {
                if (slot >= inventory.getSize()) break;
                if (item != null) {
                    inventory.setItem(slot++, item);
                }
            }
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Error performing sort: " + e.getMessage());
            return false;
        }
    }
    
    private List<ItemStack> stackSimilarItems(List<ItemStack> items) {
        Map<String, ItemStack> stackedItems = new HashMap<>();
        
        for (ItemStack item : items) {
            String key = generateItemKey(item);
            
            if (stackedItems.containsKey(key)) {
                ItemStack existingStack = stackedItems.get(key);
                int maxStackSize = item.getMaxStackSize();
                int currentAmount = existingStack.getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + item.getAmount());
                
                existingStack.setAmount(newAmount);
                
                // If there are leftover items, create new stack
                int remaining = currentAmount + item.getAmount() - newAmount;
                if (remaining > 0) {
                    ItemStack remainingStack = item.clone();
                    remainingStack.setAmount(remaining);
                    String newKey = key + "_" + System.nanoTime();
                    stackedItems.put(newKey, remainingStack);
                }
            } else {
                stackedItems.put(key, item.clone());
            }
        }
        
        return new ArrayList<>(stackedItems.values());
    }
    
    private String generateItemKey(ItemStack item) {
        StringBuilder key = new StringBuilder();
        key.append(item.getType().name());
        
        if (item.hasItemMeta()) {
            key.append("_").append(item.getItemMeta().hashCode());
        }
        
        return key.toString();
    }
    
    private int compareByRarity(ItemStack a, ItemStack b) {
        int rarityA = getRarityLevel(a.getType());
        int rarityB = getRarityLevel(b.getType());
        
        if (rarityA != rarityB) {
            return Integer.compare(rarityB, rarityA); 
        }
        
        return a.getType().name().compareTo(b.getType().name());
    }
    
    private int getRarityLevel(Material material) {
       
        String name = material.name();
        
        if (name.contains("DIAMOND") || name.contains("NETHERITE")) {
            return 4; 
        } else if (name.contains("GOLD") || name.contains("EMERALD")) {
            return 3; 
        } else if (name.contains("IRON") || name.contains("REDSTONE")) {
            return 2; 
        } else if (name.contains("STONE") || name.contains("WOOD")) {
            return 1; 
        }
        
        return 0; 
    }
    
    private boolean isOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        
        long lastUse = cooldowns.get(playerId);
        double cooldownTime = plugin.getConfigManager().getCooldown() * 1000; 
        
        return (System.currentTimeMillis() - lastUse) < cooldownTime;
    }
    
    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    private void sendMessage(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        
        Component component = Component.text(message.replace("&", "§"))
                .color(NamedTextColor.GREEN);
        player.sendMessage(component);
    }
    
    private void playSound(Player player, String soundName) {
        try {
            
            Sound sound = null;
            
            for (Sound s : Sound.values()) {
                if (s.name().equalsIgnoreCase(soundName)) {
                    sound = s;
                    break;
                }
            }
            
            if (sound != null) {
                player.playSound(player.getLocation(), sound, 0.5f, 1.0f);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
            }
        } catch (Exception e) {
            
            try {
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
            } catch (Exception ex) {
                
                plugin.getLogger().warning("Failed to play sound: " + ex.getMessage());
            }
        }
    }
}