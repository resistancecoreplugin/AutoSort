package com.resistancecore.autosort.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.resistancecore.autosort.AutoSortPlugin;

import java.util.List;

public class ConfigManager {
    
    private final AutoSortPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(AutoSortPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public boolean isAutoSortEnabled() {
        return config.getBoolean("settings.enabled", true);
    }
    
    public boolean isShiftClickEnabled() {
        return config.getBoolean("settings.shift-click-sort", true);
    }
    
    public boolean isRightClickEnabled() {
        return config.getBoolean("settings.right-click-sort", false);
    }
    
    public boolean isCommandEnabled() {
        return config.getBoolean("settings.command-sort", true);
    }
    
    public boolean isSortOnOpen() {
        return config.getBoolean("settings.auto-sort-on-open", false);
    }
    
    public String getSortType() {
        return config.getString("settings.sort-type", "name");
    }
    
    public boolean isReverseSort() {
        return config.getBoolean("settings.reverse-sort", false);
    }
    
    public boolean shouldPlaySound() {
        return config.getBoolean("settings.play-sound", true);
    }
    
    public String getSortSound() {
        return config.getString("settings.sort-sound", "BLOCK_CHEST_CLOSE");
    }
    
    public boolean shouldSendMessage() {
        return config.getBoolean("settings.send-message", true);
    }
    
    public String getSortMessage() {
        return config.getString("messages.sort-success", "&aChest sorted successfully!");
    }
    
    public String getNoPermissionMessage() {
        return config.getString("messages.no-permission", "&cYou don't have permission to use AutoSort!");
    }
    
    public String getReloadMessage() {
        return config.getString("messages.reload-success", "&aAutoSort configuration reloaded!");
    }
    
    public List<String> getDisabledWorlds() {
        return config.getStringList("settings.disabled-worlds");
    }
    
    public double getCooldown() {
        return config.getDouble("settings.cooldown", 1.0);
    }
    
    public boolean isStackSimilarItems() {
        return config.getBoolean("settings.stack-similar-items", true);
    }
    
    public boolean isMoveToTopEnabled() {
        return config.getBoolean("settings.move-to-top", false);
    }
}