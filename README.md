<h1 align="center">Introducing Bean's Backpacks! </h1>

<h3> FYI <img align="right" src="assets/images/back_slot.gif" alt="Back Slot" style="margin:10px"> </h3>

1.20.2 updates will come slower than 1.20.1 until a full release. If there are any critical bugs then they will be patched but 1.20.1 will be the most up to date version. Ports to 1.20.4 will come after we're out of beta.

### Mod Packs

All the mod's config is handled by **Data Packs**, checkout <a href="https://github.com/BeansGalaxy/Beans-Backpacks-2/blob/master/assets/examples/Create%20Your%20Own.md">Creating Your Own</a> for how to disable the backslot when wearing an item or blacklisting items from backpack inventories. Creating mod packs is what got me into modding so if you have any suggestions for config leave it in the issues tab.

<h2> Changelog  </h2>

### 20.1-0.5-v2
- Binding the Backpack's Action Key to a key on the mouse now instantly places it.
- You can now blacklist items from the Backpack's Inventory using datapacks
- backpacks will now not drop on death if Keep Inventory is set to true.
- Dying with Curios/Trinkets installed would cause backpack to drop as an entity and item.
- The backpack's back slot is now designated as the players first back slot when using Curios/Trinkets
<br><br> Thanks to avilonlegends for the keybind suggestions 

<h3> 20.2-0.10-v2 & 20.1-0.4-v2 <img align="right" src="assets/images/recipes.gif" alt="Back Slot" style="margin:10px"> </h3>

- Re-binding the Backpack's action key now works if bound to sneak/shift
- Added compatibility with jjblock21's Better Smithing Table
- Fixed Crash when trinkets had added more slots.
- Minor bugfixes
 
### 20.2-0.9-v2 & 20.1-0.3-v2 
- Fixed Bug where Back Slot was not appearing in the Inventory
- Removed Insert Slot without curios installed.
- You can now place items on top of the equipped backpack to place/take items from its inventory. (Use Hotkey + Click in place of Shift Click)
- Decorated Pots now show their inventory in its tooltip while equipped
 
### 20.2-0.8-v2 & 20.1-0.2-v2 
- Re-wrote how backpack data is stored on the player
- Simplified the inventory screen controls
- Clicking an equipped backpack in the inventory screen will insert/take items to/from the backpack.
- Punching the backpack if you are not the player that placed it takes one extra hit
- Added compatibility with curios (un-tested with other mods)
- Fixed backpacks dumping error into log if there was no trim
- Fixed when moving items into pots, it would say no key was found in log

### 20.2-0.7-v2 & 20.1-0.1-v2 
- Added Unique Sounds for the 3 Levels of Backpacks
- Added Recipes to REI & JEI
- Fixed bug causing no REI/JEI crafting table recipes to load
- Fixed crash when picking up an Item with a full inventory and no backpack equipped

### 20.2-0.6-v2 & 20.1-0.0-v2
- Backpacks now reference their traits from a global list. Updating data pack will update backpacks correctly
- Shulker Boxes can no longer be stored in Backpacks
- Added advancement screen (and it looks super cool too)

### 20.2-0.5-v2 
- Fixed looping class when quick moving item from Back Slot inventory using the backpack action key

### 20.2-0.4-v2
- Fixed crash when item was picked up
- Added Backpacks to creative tab
- Added Items to REI/JEI (No recipes)

### 20.2-0.3-v2
- Backpacks are now visible in Smithing Screen
- Leather Backpacks are now more colorful and look closer to their item's color
- Added the option to rebind the key to place Backpacks and move items.
- Hovering over the rebind option explains how it works.
- Empty and equipped Backpacks now display text on how to move items in inventory instead of an empty backpack inventory.
- Added a bar on equipped backpacks that display how full its inventory is.
<br><br>Big thanks to ApionXD for his suggestions. :)


### 20.2-0.2-v2
- Changing dimensions would de-sync the display inventory of an equipped Backpack
- Returning from the end would delete everything in the Backpack and the Backpack itself (very sorry)
- Forge players couldn't open Backpacks worn on other players
- You could take off your Backpack with items still inside

### 20.2-0.1-v2
- Complete overhaul of the code
- Added support to create custom backpacks through data and resource packs.
- Backpacks placed on snow, slabs, or any other block with unique hitboxes now are placed correctly.
- Retextured placed Gold Backpacks.
- Overhauled how storage on Players and placed Backpacks are handled

