<h1>Getting Started </h1>

---
If you are familiar with creating a resource pack then this guide will 
be very straight forward. The resource pack Json should familiar once
we understand how to handle the data for the backpack. It helps to
think of the resource pack as handling the client and the data pack 
handling the server, therefore, we need to first describe the data on 
the server before we send any to the client.

<h2> Defining Your Data</h2>

Create a file under [`data/beansbackpacks/recipes`](Copper%20Backpacks%20Data%2Fdata%2Fbeansbackpacks%2Frecipes). This file will 
describe the all the backpack's unique data. The name is not 
important, but should be easily recognizable.

Inside the file you first need to specify the `"type"`. This determines
where you'll be crafting this backpack and can either be
`beansbackpacks:crafting` or `beansbackpacks:smithing`.

Next is deciding what items you will use to craft the Backpack. This
differs between whether it is a crafting or smithing recipe, but 
`"material"` is always the ingredient the backpack is trying to imitate.

| beansbackpacks:crafting        | beansbackpacks:smithing                 |
|--------------------------------|-----------------------------------------|
| `"material"`  Primary material | `"template"` Template used for smithing |
| `"binder"`  Secondary material | `"base"` Item being upgraded            |
|                                | `"material"` Primary material           |

Finally, define the data. You'll need to define all of these fields for either `"type"`.

| Field          | Definition                                                                                        |
|----------------|---------------------------------------------------------------------------------------------------|
| `"name"`       | The default display name for the item                                                             |
| `"key"`        | Unique identifier for the resources. <br/> Must only use characters a-z A-Z and _                 |
| `"kind"`       | Determines special properties of a backpack <br/> Must be either `LEATHER`, `METAL` or `UPGRADED` |
| `"max_stacks"` | How many stacks can a backpack can hold<br/> Note that                                            |


<dl>
<strong>What does <code>"kind"</code> mean?</strong>
<dd>
    <code>LEATHER</code> backpacks can be dyed <br>
    <code>METAL</code> backpacks can be trimmed <br>
    <code>UPGRADED</code> backpacks can be trimmed and are fire-resistant
</dd>
<strong>Note on <code>"max_stacks"</code></strong>
<dd> 
    Remember that 4 stacks doesn't seem like much but 256 unique items is almost 
    10 shulker boxes.If a backpack is too good it could limit creativity.
</dd>
</dl>

Now your .json file should look something like this.

```json
{
  "type": "beansbackpacks:crafting",
  "material":  "minecraft:iron_ingot",
  "binder": "minecraft:phantom_membrane",
  "name": "Iron Backpack",
  "key": "iron",
  "kind": "METAL",
  "max_stacks": 7
}
```
```json
{
  "type": "beansbackpacks:smithing",
  "template": "minecraft:netherite_upgrade_smithing_template",
  "base": "beansbackpacks:metal_backpack",
  "material": "minecraft:netherite_ingot",
  "name": "Netherite Backpack",
  "key": "netherite",
  "kind": "UPGRADED",
  "max_stacks": 9
}
```

<h2> Configuring Backpacks and the Back Slot </h2>

Under [`data/beansbackpacks/modify`](https://github.com/BeansGalaxy/Beans-Backpacks-2/tree/20.1/common/src/main/resources/data/beansbackpacks/modify) you'll find two files, <br>

<dl>
<code>disable_chestplate</code>
<dd>
    Enables item to be worn in the Back Slot, and disables it from the Chestplate,
    <b><i>However</i></b>, items with any special attributes do not keep their 
    functionality nor support their custom models.
</dd>
<code>disables_back_slot</code>
<dd> 
    If this item is worn in the Chestplate, it disables anything from being worn
    in the Back Slot. Good for mods that add larger custom armor models.
</dd>
<code>blacklist_items</code>
<dd> 
    Any items written into this file will no longer be able to be stored in any backpack 
    inventory. This is useful to stop other mod's portable storage to be kept in backpack.
</dd>
</dl>

These files are simply a list of items. If I wanted Iron and Golden Chestplates to 
be stored in the Back Slot I would write in `disable_chestplate`

```
minecraft:iron_chestplate, minecraft:golden_chestplate
```

If an Item listed in `disable_chestplate` is also in `disables_back_slot` it will be removed 
from `disable_chestplate`. Useful if you no longer want the Elytra worn in the Back Slot.

<h2> Setting Up Your Resources </h2>

If you are familiar with resource packs then this section will be a breeze. The 
`"key"` we previously declared is what the game will use to search for the Item Model 
and the Entity's Texture. 

For example if my `"key"` was `"copper"` then the file tree would look like this.

```
assets
└── beansbackpacks
    ├── models
    |   └── item
    |       └── backpack
    |           └── copper.json
    └── textures
        ├── entity
        |   └── copper.png
        └── item
            └── copper_backpack.png
```

Inside the Json file is just like any ordinary vanilla resource pack except that `layer1` never inherits any dye color.

[HERE](..%2F..%2Fcommon%2Fsrc%2Fmain%2Fresources%2Fassets%2Fbeansbackpacks%2Ftextures)
are the textures for the backpacks already in the game, and [HERE](Blank%20Textures)
is where you can find template textures if you would like to start from scratch.
