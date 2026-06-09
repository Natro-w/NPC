# NPC

**Spawn fully interactive NPCs of any mob type on your Nukkit / Lumi server.** Over 70 entity types, human NPCs with full skin/armor/inventory support, command execution on interact, and real-time editing — all persistent across restarts.

> **Version:** 2.7.2  
> **Author:** Natro  
> **API:** 1.0.14  
> **Load:** STARTUP

---

## Features

- **70+ entity types** — Every vanilla mob, plus Humans (with player skins), and even Block NPCs.
- **Command execution** — Attach console commands or player commands that run when someone interacts with an NPC.
- **Full customization** — Change held items, armor, scale (1–25x), nametag, score tag, and skin in real time.
- **Persistent storage** — NPCs are saved via NBT and survive server restarts.
- **Real server entities** — NPCs are registered as full Nukkit entities, supporting AI, movement, and natural behavior where applicable.
- **Block NPCs** — Spawn blocks as placeable NPCs that look like actual blocks.

---

## Installation

1. Build the project with `mvn clean package` or use the prebuilt `NPC.jar`.
2. Place the jar into your server's `plugins/` folder.
3. Restart the server.

> **Compile dependency:** This project requires `Lumi-1.6.0.jar` (or a compatible Nukkit fork) available at the project root.

---

## Commands

### Spawning

| Command | Description |
|---|---|
| `/npc spawn <entity> [name]` | Spawn an NPC at your position |
| `/npc entities` | List all available entity types |
| `/npc remove` | Toggle kill mode — click an NPC to remove it |
| `/npc getid` | Toggle ID mode — click an entity to see its ID |

### Commands

| Command | Description |
|---|---|
| `/npc addcmd <ID> <command>` | Add a console command run on interact |
| `/npc addplayercmd <ID> <command>` | Add a player command run on interact |
| `/npc listcmd <ID>` | List all commands attached to an NPC |
| `/npc delcmd <ID> <command>` | Remove a console command |
| `/npc delplayercmd <ID> <command>` | Remove a player command |
| `/npc delallcmd <ID>` | Remove all commands from an NPC |

### Editing

| Command | Description |
|---|---|
| `/npc edit <ID> item` | Set NPC's hand to your held item |
| `/npc edit <ID> offhanditem` | Set NPC's offhand item |
| `/npc edit <ID> armor` | Copy your armor onto the NPC |
| `/npc edit <ID> skin` | Copy your skin onto the NPC |
| `/npc edit <ID> scale <value>` | Set NPC scale (1–25) |
| `/npc edit <ID> name <text>` | Set NPC nametag (`%n` = newline, `%k` = hide) |
| `/npc edit <ID> scoretag <text>` | Set NPC scoreboard tag |
| `/npc edit <ID> tphere` | Teleport NPC to your position |
| `/npc edit <ID> block <id:meta>` | Change a Block NPC's block type |

---

## Supported Entities

Allay, Axolotl, Bat, Bee, Blaze, Block, Cat, CaveSpider, Chicken, Cow, Creeper, Dolphin, Donkey, ElderGuardian, Enderman, Endermite, Evoker, Fox, Frog, Ghast, GlowSquid, Goat, Guardian, Hoglin, Horse, Human, Husk, IronGolem, Llama, MagmaCube, Mooshroom, Mule, Ocelot, Panda, Parrot, Phantom, Pig, Piglin, PiglinBrute, Pillager, PolarBear, Rabbit, Ravager, SkeletonHorse, Sheep, Shulker, Silverfish, Skeleton, Slime, Snowman, Spider, Squid, Stray, Strider, Tadpole, Turtle, Vex, Villager, Vindicator, WanderingTrader, Warden, Witch, Wither, WitherSkeleton, Wolf, Zoglin, ZombieHorse, Zombie, ZombiePigman, ZombieVillager

---

## Permissions

| Permission | Default | Description |
|---|---|---|
| `npc.edit` | `op` | Use `/npc` and all subcommands |

---

## License

MIT License — see [LICENSE](LICENSE).
