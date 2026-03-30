![image](https://github.com/AkkynaA/moreoffhandslots/blob/main/modpage/banner.png?raw=true)

[![Modrinth](https://img.shields.io/badge/Modrinth-More%20Offhand%20Slots-green?logo=modrinth)](https://modrinth.com/mod/more-offhand-slots)
[![CurseForge](https://img.shields.io/badge/CurseForge-More%20Offhand%20Slots-orange?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/more-offhand-slots)
[![License](https://img.shields.io/badge/License-LGPL--3.0-blue)](LICENSE)

A Minecraft mod that adds additional offhand slots, allowing players to cycle through items with customizable keybinds and visual indicators.

## Features

- **Additional offhand slots** (configurable, default: 3)
- **Cycle through offhand items** using keybinds or the scroll wheel
- **Visual indicators** with multiple styles: Default, Detailed, Vanilla, and Hotbar
- **Scroll wheel modes** for flexible control schemes
- **HUD customization** with offset and alignment options
- **Mod compatibility** with Better Combat, Auto HUD, Create, and more

## Version Support

| Minecraft Version | Loader   | Status                          |
|-------------------|----------|---------------------------------|
| 1.21.1            | NeoForge | **Active Development** (main)   |
| 1.21.5            | NeoForge | Legacy version currently, Porting in progress             |
| 1.21.8            | NeoForge | Porting in progress             |
| 1.21.11           | NeoForge | Porting in progress             |
| 1.20.1            | Forge    | Active                          |
| 1.20.6            | Forge    | Porting in progress*            |
| 26.1              | TBD      | Planned                         |

> Each version lives on its own branch (e.g. `neoforge-1.21.1`, `forge-1.20.1`).

## Dependencies

- **SlotLib** (bundled) - Custom slot system library, see [SlotLib documentation](#slotlib) below

## Default Controls

| Key            | Action                    |
|----------------|---------------------------|
| Mouse Button 5 | Cycle forward             |
| Mouse Button 4 | Cycle backward            |
| Left Shift     | Scroll wheel modifier key |

All keys are rebindable in the Minecraft controls menu.

## Configuration

Configuration is done via `moreoffhandslots-client.toml` in the config folder, or in-game with a config mod like YACL.

| Option                 | Description                                                        | Default   |
|------------------------|--------------------------------------------------------------------|-----------|
| `indicatorStyle`       | HUD style: `DEFAULT`, `DETAILED`, `VANILLA`, `HOTBAR`             | `DEFAULT`  |
| `scrollMode`           | Scroll behavior: `VANILLA`, `OFFHAND_ONLY`, `MAINHAND_WITH_MODIFIER`, `OFFHAND_WITH_MODIFIER` | `VANILLA` |
| `emptySlotBehavior`    | Empty Slot Cycling Behavior: `SKIP`, `CYCLE`, `COLLAPSE`           | `SKIP`    |
| `renderEmptyOffhand`   | Always render indicator, even when offhand is empty                | `false`   |
| `invertScrollDirection`| Invert scroll wheel cycling direction                              | `false`   |
| `alignToCenter`        | Align offhand and main hotbar to screen center                     | `false`   |
| `xOffset`              | X offset for the offhand indicator                                 | `0`       |
| `yOffset`              | Y offset for the offhand indicator                                 | `0`       |

---

## SlotLib

SlotLib is a library mod bundled with More Offhand Slots that provides a flexible extra-slot system for players. It replaces the previous Curios API dependency.

- **Source:** [github.com/AkkynaA/slotlib](https://github.com/AkkynaA/slotlib)
- **License:** GPL-3.0-or-later

### What SlotLib Does

SlotLib adds configurable extra inventory slots to players, accessible through a custom inventory screen. It handles:

- Slot storage and NBT serialization (persists across death with `keepInventory`)
- Network synchronization between server and client
- A custom inventory GUI with a button in the survival/creative inventory
- Item ticking and Mending enchantment support for items in SlotLib slots
- Optional compatibility with Curios API and YYZS Backpack

### SlotLib Configuration

| Option                    | Description                                          | Default        |
|---------------------------|------------------------------------------------------|----------------|
| `slotCount`               | Number of extra inventory slots (1-9)                | `3`            |
| `buttonCorner`            | GUI button position: `TOP_LEFT`, `TOP_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_RIGHT` | `BOTTOM_RIGHT` |
| `buttonXOffset`           | X offset for button in survival inventory            | `0`            |
| `buttonYOffset`           | Y offset for button in survival inventory            | `0`            |
| `creativeButtonXOffset`   | X offset for button in creative inventory            | `0`            |
| `creativeButtonYOffset`   | Y offset for button in creative inventory            | `0`            |

### Using SlotLib in Your Mod

SlotLib exposes a player attachment for accessing the extra inventory:

```java
// Get the SlotLib inventory for a player
SlotLibInventory inventory = player.getData(SlotLibRegistry.INVENTORY);

// Read slots
int slotCount = inventory.getSlots();
ItemStack stack = inventory.getStackInSlot(0);

// Modify slots
inventory.setStackInSlot(0, itemStack);

// Get all stacks
NonNullList<ItemStack> stacks = inventory.getStacks();
```

Add SlotLib as a dependency in your `neoforge.mods.toml`:

```toml
[[dependencies.yourmodid]]
modId = "slotlib"
type = "required"
versionRange = "[21.1.0,)"
ordering = "AFTER"
side = "BOTH"
```

---

## Credits

- [Lizard_OfOz](https://github.com/Lizard-Of-Oz) for **Inventorio**, the main inspiration for this mod
- [Fuzs](https://github.com/Fuzss) for **Slot Cycler**, which served as further inspiration
- [TheIllusiveC4](https://github.com/TheIllusiveC4) for creating **Curios API**, used in legacy versions

## License

This project is licensed under the [GNU Lesser General Public License v3.0](LICENSE).
