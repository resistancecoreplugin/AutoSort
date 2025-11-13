# AutoSort 📦

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/yourusername/AutoSort/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-brightgreen.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)
[![Paper](https://img.shields.io/badge/paper-1.21-orange.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-21-red.svg)](https://adoptium.net/)

A powerful and intuitive chest sorting plugin for Minecraft Paper servers. Automatically organize your inventory with customizable hotkeys, multiple sort algorithms, and advanced filtering options.

## ✨ Features

- **🔢 Multiple Sort Types** - Sort by name, type, amount, or rarity
- **⌨️ Flexible Hotkeys** - Shift+Click, Right-Click, and Middle-Click options
- **📦 Smart Stacking** - Automatically combine similar items
- **🚀 Auto-Sort on Open** - Optional automatic sorting when opening containers
- **⏱️ Cooldown System** - Prevent spam with configurable delays
- **🔊 Sound & Visual Feedback** - Customizable sounds and messages
- **🌍 World Restrictions** - Disable sorting in specific worlds
- **🔒 Permission-Based** - Fine-grained access control
- **📋 Container Support** - Works with chests, barrels, hoppers, and more

## 📥 Installation

### For Server Administrators

1. Download the latest release from [Releases](https://github.com/yourusername/AutoSort/releases)
2. Stop your Paper server
3. Place `AutoSort-1.0.0.jar` in your `plugins/` folder
4. Start your server
5. Configure `plugins/AutoSort/config.yml` as needed
6. Reload with `/autosort reload`

### Requirements

- **Server:** Paper 1.21+ (Spigot/Bukkit compatible)
- **Java:** 21 or higher
- **Dependencies:** None

## 🎮 Usage

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/autosort help` | Show help message | `autosort.command` |
| `/autosort sort` | Sort currently open chest | `autosort.use` |
| `/autosort info` | Display plugin information | `autosort.command` |
| `/autosort reload` | Reload configuration | `autosort.reload` |
| `/autosort toggle <setting>` | Toggle plugin settings | `autosort.command` |

**Aliases:** `/asort`, `/sort`

### Hotkeys

| Hotkey | Action | Requirement |
|--------|--------|-------------|
| `Shift + Left-Click` | Sort chest (empty slot) | `shift-click-sort: true` |
| `Middle-Click` | Sort chest (empty slot) | Any enabled method |
| `Shift + Right-Click` | Sort chest (on block) | `right-click-sort: true` |

### Permissions
```yaml
autosort.*              # All permissions
autosort.use            # Use sorting functionality (default: true)
autosort.command        # Use commands (default: true)
autosort.reload         # Reload configuration (default: op)
```

## ⚙️ Configuration

<details>
<summary>Click to view full configuration</summary>
```yaml
# AutoSort Plugin Configuration
# Made for Paper 1.21+

settings:
  enabled: true
  sort-type: "name"           # name, type, amount, rarity
  reverse-sort: false
  
  # Sorting Methods
  shift-click-sort: true
  right-click-sort: true
  command-sort: true
  auto-sort-on-open: false
  
  # Item Management
  stack-similar-items: true
  move-to-top: false
  
  # Cooldown in seconds
  cooldown: 1.0
  
  # Feedback
  play-sound: true
  sort-sound: "BLOCK_CHEST_CLOSE"
  send-message: true
  
  # World Restrictions
  disabled-worlds: []

messages:
  sort-success: "&aChest sorted successfully!"
  no-permission: "&cYou don't have permission to use AutoSort!"
  reload-success: "&aAutoSort configuration reloaded!"

# Rarity-based sorting priorities
rarity-priorities:
  NETHERITE_BLOCK: 100
  DIAMOND_BLOCK: 90
  EMERALD_BLOCK: 85
  GOLD_BLOCK: 80
  # ... more items
```

</details>

### Sort Types Explained

| Type | Description | Use Case |
|------|-------------|----------|
| `name` | Alphabetical by item name | General organization |
| `type` | Grouped by material type | Categorizing materials |
| `amount` | Stack size (highest first) | Finding full stacks |
| `rarity` | Custom rarity priorities | Valuable items first |

## 🔨 Building from Source

### Prerequisites

- Java Development Kit (JDK) 21
- Maven 3.6+
- Git

### Build Steps
```bash
# Clone the repository
git clone https://github.com/yourusername/AutoSort.git
cd AutoSort

# Build with Maven
mvn clean package

# The compiled jar will be in target/AutoSort-1.0.0.jar
```

### Development Setup
```bash
# Clone and open in your IDE
git clone https://github.com/yourusername/AutoSort.git

# Import as Maven project
# Set JDK to 21
# Run Maven 'clean install'
```

## 🏗️ Project Structure
```
AutoSort/
├── src/main/java/com/example/autosort/
│   ├── AutoSortPlugin.java          # Main plugin class
│   ├── commands/
│   │   └── AutoSortCommand.java     # Command handler
│   ├── listeners/
│   │   ├── ChestSortListener.java   # Inventory click events
│   │   └── PlayerInteractListener.java
│   └── managers/
│       ├── ConfigManager.java       # Configuration handler
│       └── SortManager.java         # Sorting algorithms
├── src/main/resources/
│   ├── config.yml                   # Default configuration
│   └── plugin.yml                   # Plugin metadata
└── pom.xml                          # Maven configuration
```

## 🔧 API Usage

### For Developers

You can integrate AutoSort into your plugin:
```java
// Get AutoSort instance
AutoSortPlugin autoSort = AutoSortPlugin.getInstance();

// Sort an inventory programmatically
Player player = // ... your player
Inventory inventory = // ... your inventory
boolean success = autoSort.getSortManager()
    .sortInventory(player, inventory);

// Check if sorting is enabled
boolean enabled = autoSort.getConfigManager()
    .isAutoSortEnabled();
```

### Maven Dependency
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>autosort</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

## 🐛 Troubleshooting

### Common Issues

<details>
<summary><b>Sorting doesn't work</b></summary>

- Check if player has `autosort.use` permission
- Verify `enabled: true` in config.yml
- Ensure sorting method is enabled (shift-click-sort, etc.)
- Check if world is in disabled-worlds list
</details>

<details>
<summary><b>Cooldown message appears constantly</b></summary>

- Adjust `cooldown` value in config.yml
- Lower cooldown or check for permission `autosort.bypass`
</details>

<details>
<summary><b>Plugin doesn't load</b></summary>

- Ensure you're using Paper 1.21+
- Check server logs for errors
- Verify Java 21 is installed
- Try deleting config.yml and regenerating
</details>

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add JavaDoc comments for public methods
- Write meaningful commit messages

## 📊 Statistics

- **Supported Containers:** 7 types (Chest, Barrel, Hopper, etc.)
- **Sort Algorithms:** 4 types (Name, Type, Amount, Rarity)
- **Configurable Options:** 15+ settings
- **Lines of Code:** ~1,500

## 🗺️ Roadmap

- [ ] GUI interface for easier configuration
- [ ] Custom sorting rules per player
- [ ] Integration with economy plugins
- [ ] Advanced filtering options
- [ ] Database support for player preferences
- [ ] Multi-language support
- [ ] PlaceholderAPI integration

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
```
MIT License

Copyright (c) 2025 AutoSort Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

## 🙏 Acknowledgments

- **Paper Team** - For the excellent server software
- **Spigot Community** - For extensive plugin development resources
- **Contributors** - Everyone who has contributed to this project
- **Users** - Thank you for using AutoSort!

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/yourusername/AutoSort/issues)
- **Discord:** [Join our community](https://discord.gg/yourserver)
- **Wiki:** [Documentation](https://github.com/yourusername/AutoSort/wiki)
- **Email:** support@yourserver.com

## 📈 Downloads

[![Downloads](https://img.shields.io/github/downloads/resistancecoreplugin/AutoSort/total.svg)](https://github.com/resistancecoreplugin/AutoSort/releases)
[![Stars](https://img.shields.io/github/stars/resistancecoreplugin/AutoSort.svg)](https://github.com/resistancecoreplugin/AutoSort/stargazers)
[![Forks](https://img.shields.io/github/forks/resistancecoreplugin/AutoSort.svg)](https://github.com/resistancecoreplugin/AutoSort/network)

---

<div align="center">

**[⬆ Back to Top](#autosort-)**

Made with ❤️ for the Minecraft community

If you find this plugin useful, please consider ⭐ starring the repository!

</div>
