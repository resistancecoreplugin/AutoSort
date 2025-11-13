package com.resistancecore.autosort.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.resistancecore.autosort.AutoSortPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoSortCommand implements CommandExecutor, TabCompleter {
    
    private final AutoSortPlugin plugin;
    
    public AutoSortCommand(AutoSortPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelpMessage(sender);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "sort":
                handleSort(sender);
                break;
                
            case "info":
                handleInfo(sender);
                break;
                
            case "toggle":
                handleToggle(sender, args);
                break;
                
            default:
                sendMessage(sender, "&cUnknown subcommand. Use /autosort help for available commands.", NamedTextColor.RED);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("autosort.reload")) {
            sendMessage(sender, plugin.getConfigManager().getNoPermissionMessage(), NamedTextColor.RED);
            return;
        }
        
        plugin.reloadPlugin();
        sendMessage(sender, plugin.getConfigManager().getReloadMessage(), NamedTextColor.GREEN);
    }
    
    private void handleSort(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cThis command can only be used by players!", NamedTextColor.RED);
            return;
        }
        
        if (!player.hasPermission("autosort.use")) {
            sendMessage(sender, plugin.getConfigManager().getNoPermissionMessage(), NamedTextColor.RED);
            return;
        }
        
        InventoryView openInventory = player.getOpenInventory();
        Inventory topInventory = openInventory.getTopInventory();
        
        if (!isChestLikeInventory(topInventory)) {
            sendMessage(sender, "&cYou need to have a chest or container open to use this command!", NamedTextColor.RED);
            return;
        }
        
        boolean success = plugin.getSortManager().sortInventory(player, topInventory);
        
        if (!success) {
            sendMessage(sender, "&cFailed to sort inventory. Check your permissions and cooldown.", NamedTextColor.RED);
        }
    }
    
    private void handleInfo(CommandSender sender) {
        if (!sender.hasPermission("autosort.command")) {
            sendMessage(sender, plugin.getConfigManager().getNoPermissionMessage(), NamedTextColor.RED);
            return;
        }
        
        sendMessage(sender, "&6=== AutoSort Plugin Info ===", NamedTextColor.GOLD);
        sendMessage(sender, "&7Version: &a" + plugin.getDescription().getVersion(), NamedTextColor.GRAY);
        sendMessage(sender, "&7Status: &a" + (plugin.getConfigManager().isAutoSortEnabled() ? "Enabled" : "Disabled"), NamedTextColor.GRAY);
        sendMessage(sender, "&7Sort Type: &a" + plugin.getConfigManager().getSortType(), NamedTextColor.GRAY);
        sendMessage(sender, "&7Shift-Click: &a" + (plugin.getConfigManager().isShiftClickEnabled() ? "Enabled" : "Disabled"), NamedTextColor.GRAY);
        sendMessage(sender, "&7Right-Click: &a" + (plugin.getConfigManager().isRightClickEnabled() ? "Enabled" : "Disabled"), NamedTextColor.GRAY);
        sendMessage(sender, "&7Auto-Sort on Open: &a" + (plugin.getConfigManager().isSortOnOpen() ? "Enabled" : "Disabled"), NamedTextColor.GRAY);
        sendMessage(sender, "&7Cooldown: &a" + plugin.getConfigManager().getCooldown() + "s", NamedTextColor.GRAY);
    }
    
    private void handleToggle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("autosort.reload")) {
            sendMessage(sender, plugin.getConfigManager().getNoPermissionMessage(), NamedTextColor.RED);
            return;
        }
        
        if (args.length < 2) {
            sendMessage(sender, "&cUsage: /autosort toggle <setting>", NamedTextColor.RED);
            sendMessage(sender, "&7Available settings: enabled, shift-click, right-click, auto-sort-on-open", NamedTextColor.GRAY);
            return;
        }
        
        String setting = args[1].toLowerCase();
        toggleSetting(sender, setting);
    }
    
    private void toggleSetting(CommandSender sender, String setting) {
        switch (setting) {
            case "enabled":
                plugin.getConfig().set("settings.enabled", !plugin.getConfigManager().isAutoSortEnabled());
                sendMessage(sender, "&aAutoSort " + (plugin.getConfigManager().isAutoSortEnabled() ? "disabled" : "enabled") + "!", NamedTextColor.GREEN);
                break;
            case "shift-click":
                plugin.getConfig().set("settings.shift-click-sort", !plugin.getConfigManager().isShiftClickEnabled());
                sendMessage(sender, "&aShift-click sorting " + (plugin.getConfigManager().isShiftClickEnabled() ? "disabled" : "enabled") + "!", NamedTextColor.GREEN);
                break;
            case "right-click":
                plugin.getConfig().set("settings.right-click-sort", !plugin.getConfigManager().isRightClickEnabled());
                sendMessage(sender, "&aRight-click sorting " + (plugin.getConfigManager().isRightClickEnabled() ? "disabled" : "enabled") + "!", NamedTextColor.GREEN);
                break;
            case "auto-sort-on-open":
                plugin.getConfig().set("settings.auto-sort-on-open", !plugin.getConfigManager().isSortOnOpen());
                sendMessage(sender, "&aAuto-sort on open " + (plugin.getConfigManager().isSortOnOpen() ? "disabled" : "enabled") + "!", NamedTextColor.GREEN);
                break;
            default:
                sendMessage(sender, "&cUnknown setting: " + setting, NamedTextColor.RED);
                return;
        }
        
        plugin.saveConfig();
        plugin.reloadPlugin();
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sendMessage(sender, "&6=== AutoSort Commands ===", NamedTextColor.GOLD);
        sendMessage(sender, "&7/autosort help &f- Show this help message", NamedTextColor.GRAY);
        sendMessage(sender, "&7/autosort sort &f- Sort the currently open chest", NamedTextColor.GRAY);
        sendMessage(sender, "&7/autosort info &f- Show plugin information", NamedTextColor.GRAY);
        sendMessage(sender, "&7/autosort reload &f- Reload plugin configuration", NamedTextColor.GRAY);
        sendMessage(sender, "&7/autosort toggle <setting> &f- Toggle plugin settings", NamedTextColor.GRAY);
        sendMessage(sender, "", NamedTextColor.WHITE);
        sendMessage(sender, "&6=== Hotkeys ===", NamedTextColor.GOLD);
        sendMessage(sender, "&7Shift + Left-Click &f- Sort chest (empty slot)", NamedTextColor.GRAY);
        sendMessage(sender, "&7Middle-Click &f- Sort chest (empty slot)", NamedTextColor.GRAY);
        sendMessage(sender, "&7Shift + Right-Click &f- Sort chest (on chest block)", NamedTextColor.GRAY);
    }
    
    private boolean isChestLikeInventory(Inventory inventory) {
        String typeName = inventory.getType().name();
        return typeName.contains("CHEST") || 
               typeName.contains("SHULKER") ||
               typeName.contains("BARREL") ||
               typeName.contains("HOPPER") ||
               typeName.contains("DISPENSER") ||
               typeName.contains("DROPPER");
    }
    
    private void sendMessage(CommandSender sender, String message, NamedTextColor color) {
        Component component = Component.text(message.replace("&", "§")).color(color);
        sender.sendMessage(component);
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "sort", "info", "reload", "toggle");
            String partial = args[0].toLowerCase();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partial)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            List<String> settings = Arrays.asList("enabled", "shift-click", "right-click", "auto-sort-on-open");
            String partial = args[1].toLowerCase();
            
            for (String setting : settings) {
                if (setting.startsWith(partial)) {
                    completions.add(setting);
                }
            }
        }
        
        return completions;
    }
}