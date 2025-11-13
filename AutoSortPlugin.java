package com.resistancecore.autosort;

import com.resistancecore.autosort.commands.AutoSortCommand;
import com.resistancecore.autosort.listeners.ChestSortListener;
import com.resistancecore.autosort.listeners.PlayerInteractListener;
import com.resistancecore.autosort.managers.ConfigManager;
import com.resistancecore.autosort.managers.SortManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

public final class AutoSortPlugin extends JavaPlugin {
    
    private static AutoSortPlugin instance;
    private ConfigManager configManager;
    private SortManager sortManager;
    
    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        sortManager = new SortManager(this);
        configManager.loadConfig();
        registerCommands();
        registerListeners();
        getLogger().info("AutoSort Plugin has been enabled!");
        getLogger().info("Players can now sort chests using the configured hotkey!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("AutoSort Plugin has been disabled!");
        instance = null;
    }
    
    private void registerCommands() {
        AutoSortCommand autoSortCommand = new AutoSortCommand(this);
        getCommand("autosort").setExecutor(autoSortCommand);
        getCommand("autosort").setTabCompleter(autoSortCommand);
    }
    
    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new ChestSortListener(this), this);
    }
    
    public static AutoSortPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public SortManager getSortManager() {
        return sortManager;
    }
    
    public void reloadPlugin() {
        configManager.reloadConfig();
        getLogger().info("AutoSort configuration reloaded!");
    }
}