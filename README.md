# More Offhand Slots

A forge mod that adds additional offhand slots using the Curios API, allowing players to cycle through items with customizable keybinds.

![image](https://media.forgecdn.net/attachments/description/null/description_db6266ce-f9f2-4c66-9a51-793366820bc9.png)

The main inspiration for this mod was the [Inventorio](https://www.curseforge.com/minecraft/mc-mods/inventorio-forge) 
mod by [Lizard_OfOz](https://www.curseforge.com/members/lizard_ofoz/projects).
however, I wanted to create a simpler, lightweight version with only the expanded offhand that is guaranteed to work with most mods.
If you want a more fleshed-out mod with additional inventory features, I recommend checking out Inventorio.

## Installation

Get the mod from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/more-offhand-slots)
Or [Modrinth](https://modrinth.com/mod/more-offhand-slots)

## Features

- Adds additional offhand slots to the player (default: 3, configurable)
- Cycle through items with customizable keybinds
- Visual indicator showing previous and next items in the cycle
- Two main modes: Cycle empty slots or skip them
- Option to bind scroll wheel to cycle through offhand items


## Usage

### Default Controls

- **Mouse Button 5**: Cycle forward through offhand items
- **Mouse Button 4**: Cycle backward through offhand items

You can rebind these keys in the Minecraft controls menu.

### Visual Indicator

When you have items in your offhand slots, a visual indicator appears near your hotbar showing:
- Your current offhand item in the center, this is the vanilla offhand slot
- The previous item in the cycle on the left
- The next item in the cycle on the right

This makes it easy to see what item you'll get when cycling forward or backward.

## Configuration

### More Offhand Slots configs

The mod can be configured by editing the `moreoffhandslots-client.toml` file in the `config` folder (or by using Configured or any similar mod in-game). The following options are available:

`cycleEmptySlots` If `true`, empty slots will be included when cycling through offhand items.

`renderEmptyOffhand` If `true`, the visual indicator will be always rendered, even when no item is equipped in the offhand.

`useScrollForOffhand` If `true`, the scroll wheel will be used to cycle through offhand items.

`invertScrollDirection` If `true`, the scroll wheel will cycle through offhand items in the opposite direction.

`scrollShiftMode` Three options are available:
- `none`: The scroll wheel will cycle through offhand items normally.
- `mainhand`:  When shift is held, scroll through the mainhand slots, otherwise scroll through the offhand slots.
- `offhand`: When shift is held, scroll through the offhand slots, otherwise scroll through the mainhand slots.

### Curios configs

If you want to change the number of offhand slots, you can do so by editing the `curios-common.toml` file.
For example:

```toml
slots = ["id=offhand;size=8"]
```

## Resource Packs

The visual indicator uses the default Minecraft textures for the offhand slots.
I intentionally avoided using custom textures to keep the mod compatible with resource packs.

## Support

If you encounter any issues or have suggestions, feel free to open an issue.
Or leave a comment on the CurseForge page [CurseForge](https://www.curseforge.com/minecraft/mc-mods/more-offhand-slots)