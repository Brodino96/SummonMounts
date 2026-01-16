# SummonMounts Mod Implementation Plan

## Overview
This Minecraft Fabric mod allows players to summon and dismiss mounts using configurable items. When a mount is tamed, it transforms into an item that can be used to summon/dismiss the mount. Mounts automatically dismiss after a configurable idle time without passengers.

## Current State Analysis
The mod has basic structure with config loading, particle effects, and partial mount/item binding. However, core functionality is incomplete.

## Features to Implement

### 1. Configuration Enhancements
- [ ] Add `idleTimeoutSeconds` to ConfigType (default: 300 seconds / 5 minutes)
- [ ] Add `maxSummonsPerPlayer` to ConfigType (default: 1)
- [ ] Add `dismissOnDimensionChange` boolean (default: true)
- [ ] Add `dismissOnServerLeave` boolean (default: true)
- [ ] Update Config getters for new fields
- [ ] Add validation for summonItem (ensure it's a valid item ID)

### 2. Mount Data Storage and Management
- [ ] Complete Mount.fromEntity() method to properly extract all entity data
- [ ] Implement Mount.toItemStack() method to store mount data in item NBT
- [ ] Add mount data fields to item NBT:
  - `mount.type`: Entity type ID
  - `mount.uuid`: Mount UUID
  - `mount.owner`: Owner UUID
  - `mount.genericData`: Entity NBT data
  - `mount.armor`: Armor items
  - `mount.saddle`: Saddle item
  - `mount.name`: Custom name
- [ ] Implement Mount.dismiss() method to remove entity and return item to player

### 3. MountManager Enhancements
- [ ] Complete bindMountToItem() method:
  - Create Mount instance from entity
  - Store mount data in item stack
  - Remove entity from world
  - Register mount to player in PLAYER_MOUNTS map
- [ ] Add summonMount() method:
  - Check if player already has max summons
  - Create mount from item stack
  - Call Mount.summon()
  - Update PLAYER_MOUNTS map
- [ ] Add dismissMount() method:
  - Find active mount for player
  - Call Mount.dismiss()
  - Update PLAYER_MOUNTS map
- [ ] Add getActiveMount() method to retrieve player's current mount
- [ ] Add dismissAllMountsForPlayer() for dimension changes/server leave

### 4. Mount Class Completion
- [ ] Complete summon() method:
  - Load genericData into spawned entity
  - Apply armorData and saddleData
  - Set customName
  - Set owner UUID
  - Store reference to activeEntity
- [ ] Implement dismiss() method:
  - Extract current data back to boundItem
  - Remove entity from world
  - Give item back to player
  - Clear activeEntity reference
- [ ] Add tick() method for idle checking:
  - Increment idleTicks if no passenger
  - Dismiss if idleTicks exceeds config timeout
- [ ] Add hasPassenger() helper method

### 5. Event Handlers
- [ ] Item Use Event:
  - Register for item use events
  - Check if item is summon item with mount data
  - If player has active mount: dismiss it
  - If no active mount: summon from item
- [ ] Player Dimension Change Event:
  - Dismiss all mounts when changing dimensions (if config enabled)
- [ ] Player Leave Server Event:
  - Dismiss all mounts when player leaves (if config enabled)
- [ ] Server Tick Event:
  - Call tick() on all active mounts for idle checking

### 6. Item Interaction Logic
- [ ] Create SummonItem class extending Item
- [ ] Override use() method to handle summon/dismiss logic
- [ ] Add item to item registry
- [ ] Update config to use item registry key instead of string ID
- [ ] Add item to creative tab or provide command to get it

### 7. Permission and Validation
- [ ] Add dimension validation in summon() method
- [ ] Add mount type validation in bindMountToItem()
- [ ] Add player permission checks (if needed)
- [ ] Add world/server validation for summon locations

### 8. Data Persistence
- [ ] Implement saving active mounts on server shutdown
- [ ] Implement loading active mounts on server startup
- [ ] Handle mount cleanup on server crashes

### 9. Error Handling and Logging
- [ ] Add proper error handling for invalid mount data
- [ ] Add debug logging for mount operations
- [ ] Add player feedback messages for summon/dismiss actions
- [ ] Handle edge cases (mount dies while summoned, etc.)

### 10. Testing and Validation
- [ ] Test taming different mount types (horse, donkey, mule, etc.)
- [ ] Test summon/dismiss cycle
- [ ] Test idle timeout functionality
- [ ] Test dimension change dismissal
- [ ] Test single mount limit
- [ ] Test item durability and stackability
- [ ] Test with multiple players simultaneously

### 11. Additional Features (Future)
- [ ] Mount health/damage persistence
- [ ] Mount inventory persistence (for llamas, etc.)
- [ ] Custom mount skins/textures
- [ ] Mount level/experience persistence
- [ ] Admin commands for mount management
- [ ] GUI for mount customization

## Implementation Order
1. Complete Mount data storage and basic summon/dismiss
2. Implement event handlers for item use
3. Add idle timeout and tick system
4. Add dimension/server leave handling
5. Enhance configuration options
6. Add validation and error handling
7. Implement data persistence
8. Testing and bug fixes

## Dependencies
- Fabric API
- Minecraft server environment
- Proper mixin configuration for entity events

## Notes
- All mounts should be AbstractHorseEntity subclasses for consistency
- Item should be unstackable to prevent duplication issues
- Consider performance impact of frequent entity spawning/despawning
- Ensure compatibility with other mods that modify mounts