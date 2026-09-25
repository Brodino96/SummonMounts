# SummonMounts

SummonMounts is a Fabric mod for Minecraft that lets you store a tamed mount in an ocarina and summon or recall it whenever you need it. Mount data such as ownership, variants, equipment, and effects is preserved between summons.

## Features

- Capture supported tamed mounts in tiered ocarinas
- Summon and recall a mount by using its ocarina
- Preserve inventory, variants, ownership, and active effects
- Repair an ocarina by feeding the summoned mount matching tier feed
- Automatically recall mounts when their lifetime, idle, or airborne limit is reached
- Recall mounts safely when the player disconnects, changes dimension, or the mount dies
- Dye ocarinas to change particle colors (enabled via LuckPerms permission `summonmounts.custom_particles`)
- Included alternative 2D ocarina resource pack

Mythic Mounts is optional, but its mounts are included in the default configuration. Mod Menu and Catalogue are also optional integrations

## How to use

1. Tame a supported mount. The mod gives you the appropriate filled ocarina automatically
2. Use the filled ocarina to summon the mount. Use it again to recall the mount
3. Feed the summoned mount with matching tier feed to repair the ocarina

Each player can have one active summoned mount. Ocarinas are fireproof, stack to one, and lose durability when summoning. A filled ocarina displays the mount it contains in its tooltip

### Ocarina tiers

| Tier     | Default mounts                                                                                                |
|----------|---------------------------------------------------------------------------------------------------------------|
| Inferior | Donkey                                                                                                        |
| Lesser   | Mule, Llama                                                                                                   |
| Medium   | Horse                                                                                                         |
| Greater  | Mythic Mounts: Acencia, Archelon, Courierbird, Direwolf, Geckotoalizard, Nightmare, Nudibranch, Riding Lizard |
| Superior | Mythic Mounts: Colelytra, Dragon, Firebird, Griffon, Moth, Netherbat                                          |

Mount assignments can be changed in the configuration file

## Commands

All commands are under `/mount`:

| Command                              | Description                                                       |
|--------------------------------------|-------------------------------------------------------------------|
| `/mount reload`                      | Reload `config/summonmounts.json` (operator permission level 2)   |
| `/mount summon <entity> [pos] [nbt]` | Spawn and mount an entity (operator permission level 2)           |
| `/mount convert`                     | Convert a legacy mount item held in the main hand into an ocarina |

The conversion command is intended for upgrading items from pre 2.0.0 versions of the mod, data used to be saved in a different way

## Configuration

The generated `config/summonmounts.json` controls:

- `allowedDimensions`: dimensions where ocarinas can be used, defaults to Overworld
- `time.ocarinasCooldownSeconds`: cooldown between uses
- `time.mountAliveSeconds`: maximum summoned lifetime
- `time.mountIdleSeconds`: time an unmounted mount can remain idle
- `time.mountAirborneSeconds`: time a mount can remain airborne before being forced to land
- `heightLimit`: flight height limit, set to `-1` to disable it
- `durability`: durability for each ocarina tier
- `foodRepair`: durability repaired by one matching feed item
- `mounts`: entity IDs and their required ocarina tiers

After editing the file, run `/mount reload` or `/reload` as an operator

## License

SummonMounts is licensed under the [MIT License](LICENSE.txt)
