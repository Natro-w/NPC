package ru.Natro.npc;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import ru.Natro.npc.entities.NPC_Block;
import ru.Natro.npc.entities.NPC_Entity;
import ru.Natro.npc.entities.NPC_Human;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class NPCGUI implements Listener {

    static final List<String> ENTITIES = Arrays.asList(
            "Allay", "Axolotl", "Bat", "Bee", "Blaze", "Block", "Cat", "CaveSpider",
            "Chicken", "Cow", "Creeper", "Dolphin", "Donkey", "ElderGuardian", "Enderman",
            "Endermite", "Evoker", "Frog", "Fox", "Ghast", "GlowSquid", "Goat", "Guardian",
            "Hoglin", "Horse", "Human", "Husk", "IronGolem", "Llama", "Mooshroom", "MagmaCube",
            "Mule", "Ocelot", "Panda", "Parrot", "Phantom", "Pig", "Piglin", "PiglinBrute",
            "Pillager", "PolarBear", "Rabbit", "Ravager", "SkeletonHorse", "Sheep", "Shulker",
            "Silverfish", "Skeleton", "Slime", "Snowman", "Spider", "Squid", "Stray", "Strider",
            "Tadpole", "Turtle", "Vex", "Villager", "Vindicator", "WanderingTrader", "Warden",
            "Witch", "Wither", "WitherSkeleton", "Wolf", "Zoglin", "ZombieHorse", "Zombie",
            "ZombiePigman", "ZombieVillager"
    );

    static final Map<String, List<String>> CATEGORIES = new LinkedHashMap<>();
    static {
        CATEGORIES.put("Passive", Arrays.asList("Allay", "Axolotl", "Bat", "Cat", "Chicken", "Cow", "Dolphin",
                "Donkey", "Fox", "Frog", "GlowSquid", "Goat", "Horse", "Llama", "Mooshroom", "Mule",
                "Ocelot", "Panda", "Parrot", "Pig", "PolarBear", "Rabbit", "Sheep", "SkeletonHorse",
                "Snowman", "Squid", "Strider", "Tadpole", "Turtle", "Villager", "WanderingTrader", "Wolf", "ZombieHorse"));
        CATEGORIES.put("Neutral", Arrays.asList("Bee", "CaveSpider", "Enderman", "IronGolem", "Piglin",
                "PiglinBrute", "Spider", "ZombiePigman"));
        CATEGORIES.put("Hostile", Arrays.asList("Blaze", "Creeper", "Drowned", "ElderGuardian", "Endermite",
                "Evoker", "Ghast", "Guardian", "Hoglin", "Husk", "MagmaCube", "Phantom", "Pillager",
                "Ravager", "Shulker", "Silverfish", "Skeleton", "Slime", "Snowman", "Stray", "Vex", "Vindicator",
                "Warden", "Witch", "Wither", "WitherSkeleton", "Zoglin", "Zombie", "ZombieVillager"));
        CATEGORIES.put("Special", Arrays.asList("Block", "Human"));
    }

    private static final Map<UUID, GUIState> states = new HashMap<>();

    private static class GUIState {
        String step;
        String data;
        long npcId;
        boolean playerCmd;
        List<Long> npcIds;
    }

    public static void openMainMenu(Player player) {
        FormWindowSimple form = new FormWindowSimple("§l§8[NPC Manager]", "Select an option:");
        form.addButton(new ElementButton("§aNPC List\n§7View all NPCs"));
        form.addButton(new ElementButton("§eSpawn NPC\n§7Choose entity type"));
        form.addButton(new ElementButton("§bEdit NPC\n§7Modify existing NPC"));
        form.addButton(new ElementButton("§cRemove Mode\n§7Click NPC to remove"));
        form.addButton(new ElementButton("§dID Mode\n§7Get NPC IDs"));
        GUIState state = new GUIState();
        state.step = "main";
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openCategoryMenu(Player player) {
        FormWindowSimple form = new FormWindowSimple("§l§8[Spawn NPC]", "Select a category:");
        for (String cat : CATEGORIES.keySet()) {
            List<String> ents = CATEGORIES.get(cat);
            form.addButton(new ElementButton("§e" + cat + "\n§7" + ents.size() + " entities"));
        }
        form.addButton(new ElementButton("§c<- Back"));
        GUIState state = new GUIState();
        state.step = "category";
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openEntitySpawnMenu(Player player, String category) {
        List<String> entities = CATEGORIES.get(category);
        FormWindowSimple form = new FormWindowSimple("§l§8[Spawn NPC]", "Select an entity type:");
        for (String e : entities) {
            form.addButton(new ElementButton("§e" + e));
        }
        form.addButton(new ElementButton("§c<- Back"));
        GUIState state = new GUIState();
        state.step = "entity_spawn";
        state.data = category;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openSpawnNameInput(Player player, String entityType) {
        FormWindowCustom form = new FormWindowCustom("§l§8[Spawn " + entityType + "]");
        form.addElement(new ElementLabel("Entity: §e" + entityType));
        form.addElement(new ElementInput("NPC Name", "Use & for colors", ""));
        form.addElement(new ElementToggle("Use nametag", true));
        GUIState state = new GUIState();
        state.step = "spawn_name";
        state.data = entityType;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openEditSelect(Player player) {
        List<Entity> npcs = getNPCs(player);
        if (npcs.isEmpty()) {
            player.sendMessage("§cNo NPCs found");
            return;
        }
        FormWindowSimple form = new FormWindowSimple("§l§8[Edit NPC]", "Click an NPC to edit:");
        List<Long> ids = new ArrayList<>();
        for (Entity e : npcs) {
            ids.add(e.getId());
            String name = e.getNameTag();
            if (name == null || name.isEmpty() || "%k".equals(name)) {
                name = "§7(no name)";
            }
            String type = e instanceof NPC_Human ? "Human" :
                    e instanceof NPC_Block ? "Block" : e.getClass().getSimpleName().replace("NPC_", "");
            form.addButton(new ElementButton("§e#" + e.getId() + " §f" + name + "\n§7" + type));
        }
        GUIState state = new GUIState();
        state.step = "edit_select";
        state.npcIds = ids;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openEditMenu(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) {
            player.sendMessage("§cNPC no longer exists");
            return;
        }
        String name = npc.getNameTag();
        if (name == null || name.isEmpty() || "%k".equals(name)) name = "§7(no name)";
        String type = npc instanceof NPC_Human ? "Human" :
                npc instanceof NPC_Block ? "Block" : npc.getClass().getSimpleName().replace("NPC_", "");

        FormWindowSimple form = new FormWindowSimple(
                "§l§8[Edit NPC] §e#" + npc.getId(),
                "Name: §f" + name + "\nType: §e" + type + "\nScale: §e" + npc.getScale());

        form.addButton(new ElementButton("§bChange Name"));
        form.addButton(new ElementButton("§6Change Scale"));
        if (npc instanceof NPC_Human) {
            form.addButton(new ElementButton("§aCopy Item/Armor"));
            form.addButton(new ElementButton("§9Inventory\n§7Edit equipment"));
            form.addButton(new ElementButton("§dCopy Skin"));
        }
        if (npc instanceof NPC_Block) {
            form.addButton(new ElementButton("§7Change Block"));
        }
        form.addButton(new ElementButton("§3Commands"));
        form.addButton(new ElementButton("§8Collision\n§7Toggle + knockback"));
        form.addButton(new ElementButton("§eTeleport Here"));
        form.addButton(new ElementButton("§cRemove NPC"));

        GUIState state = new GUIState();
        state.step = "edit_npc";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openCollisionEdit(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) { player.sendMessage("§cNPC no longer exists"); return; }
        boolean collidable = npc.namedTag.getBoolean("Collidable");
        float knockback = npc.namedTag.getFloat("Knockback");

        FormWindowCustom form = new FormWindowCustom("§l§8[Collision] §e#" + npcId);
        form.addElement(new ElementToggle("Enable Collision", collidable));
        form.addElement(new ElementSlider("Knockback Strength", 0, 10, 1, (int) knockback));
        GUIState state = new GUIState();
        state.step = "collision_edit";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static void openCommandMenu(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) { player.sendMessage("§cNPC no longer exists"); return; }
        List<StringTag> cmds = npc.namedTag.getList("Commands", StringTag.class).getAll();
        List<StringTag> pCmds = npc.namedTag.getList("PlayerCommands", StringTag.class).getAll();

        FormWindowSimple form = new FormWindowSimple(
                "§l§8[Commands] §e#" + npc.getId(),
                "Console cmds: §e" + cmds.size() + " | Player cmds: §e" + pCmds.size());

        form.addButton(new ElementButton("§aAdd Console Cmd"));
        form.addButton(new ElementButton("§bAdd Player Cmd"));
        if (!cmds.isEmpty() || !pCmds.isEmpty()) {
            form.addButton(new ElementButton("§cClear All"));
        }

        GUIState state = new GUIState();
        state.step = "command_menu";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    public static List<Entity> getNPCs(Player player) {
        List<Entity> result = new ArrayList<>();
        for (cn.nukkit.level.Level level : player.getServer().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity instanceof NPC_Entity || entity instanceof NPC_Human || entity.namedTag.getBoolean("npc")) {
                    result.add(entity);
                }
            }
        }
        Collections.sort(result, new Comparator<Entity>() {
            public int compare(Entity a, Entity b) {
                return Long.compare(a.getId(), b.getId());
            }
        });
        return result;
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (event.getResponse() == null) return;
        GUIState state = states.get(player.getUniqueId());
        if (state == null) return;

        String step = state.step;

        if (step.equals("main") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            String btn = form.getResponse().getClickedButton().getText();
            if (btn.startsWith("§aNPC List")) {
                openEditSelect(player);
            } else if (btn.startsWith("§eSpawn NPC")) {
                openCategoryMenu(player);
            } else if (btn.startsWith("§bEdit NPC")) {
                openEditSelect(player);
            } else if (btn.startsWith("§cRemove")) {
                NPC.cmd_kill.add(player.getId());
                player.sendMessage("§eKill mode activated - click an NPC to remove it");
                states.remove(player.getUniqueId());
            } else if (btn.startsWith("§dID")) {
                NPC.cmd_id.add(player.getId());
                player.sendMessage("§eID mode activated - click an NPC to get its ID");
                states.remove(player.getUniqueId());
            }
            return;
        }

        if (step.equals("category") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            String btn = form.getResponse().getClickedButton().getText();
            btn = btn.replace("§e", "");
            String cat = btn.split("\n")[0];
            if (CATEGORIES.containsKey(cat)) {
                openEntitySpawnMenu(player, cat);
            } else {
                states.remove(player.getUniqueId());
            }
            return;
        }

        if (step.equals("entity_spawn") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            String btn = form.getResponse().getClickedButton().getText();
            if (btn.startsWith("§c<-")) {
                openCategoryMenu(player);
                return;
            }
            String entityType = btn.replace("§e", "");
            if (ENTITIES.contains(entityType)) {
                openSpawnNameInput(player, entityType);
            }
            return;
        }

        if (step.equals("spawn_name") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            String name = form.getResponse().getInputResponse(1).trim();
            boolean visible = form.getResponse().getToggleResponse(2);
            String entityType = state.data;
            spawnNPC(player, entityType, name, visible);
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("edit_select") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            if (state.npcIds == null) { states.remove(player.getUniqueId()); return; }
            String btn = form.getResponse().getClickedButton().getText();
            for (int i = 0; i < state.npcIds.size(); i++) {
                if (btn.startsWith("§e#" + state.npcIds.get(i) + " ")) {
                    openEditMenu(player, state.npcIds.get(i));
                    return;
                }
            }
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("edit_npc") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            long npcId = state.npcId;
            Entity entity = player.getLevel().getEntity(npcId);
            if (entity == null) {
                player.sendMessage("§cNPC no longer exists");
                states.remove(player.getUniqueId());
                return;
            }
            String btn = form.getResponse().getClickedButton().getText();
            if (btn.equals("§bChange Name")) {
                openNameEdit(player, npcId);
            } else if (btn.equals("§6Change Scale")) {
                openScaleEdit(player, npcId);
            } else if (btn.equals("§aCopy Item/Armor")) {
                if (entity instanceof NPC_Human) {
                    setInventories((NPC_Human) entity, player);
                    entity.respawnToAll();
                    player.sendMessage("§aItem/armor copied from you");
                }
                states.remove(player.getUniqueId());
            } else if (btn.startsWith("§9Inventory")) {
                NPCInventoryGUI.open(player, entity);
                states.remove(player.getUniqueId());
            } else if (btn.equals("§dCopy Skin")) {
                copySkin(player, npcId);
                states.remove(player.getUniqueId());
            } else if (btn.equals("§7Change Block")) {
                openBlockEdit(player, npcId);
            } else if (btn.equals("§3Commands")) {
                openCommandMenu(player, npcId);
            } else if (btn.startsWith("§8Collision")) {
                openCollisionEdit(player, npcId);
            } else if (btn.equals("§eTeleport Here")) {
                entity.teleport(player);
                entity.saveNBT();
                entity.respawnToAll();
                player.sendMessage("§aNPC teleported to you");
                states.remove(player.getUniqueId());
            } else if (btn.equals("§cRemove NPC")) {
                entity.close();
                player.sendMessage("§aNPC removed");
                states.remove(player.getUniqueId());
            }
            return;
        }

        if (step.equals("name_edit") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            Entity npc = player.getLevel().getEntity(state.npcId);
            if (npc == null) { player.sendMessage("§cNPC no longer exists"); states.remove(player.getUniqueId()); return; }
            String newName = form.getResponse().getInputResponse(0).trim();
            if (newName.isEmpty()) {
                newName = "%k";
                npc.setNameTagVisible(false);
                npc.setNameTagAlwaysVisible(false);
            } else {
                newName = newName.replace("&", "\u00a7").replace("%n", "\n");
                npc.setNameTagVisible(true);
                npc.setNameTagAlwaysVisible(true);
            }
            npc.setNameTag(newName);
            npc.namedTag.putString("NameTag", newName);
            npc.saveNBT();
            player.sendMessage("§aName updated");
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("scale_edit") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            Entity npc = player.getLevel().getEntity(state.npcId);
            if (npc == null) { player.sendMessage("§cNPC no longer exists"); states.remove(player.getUniqueId()); return; }
            float scale = form.getResponse().getSliderResponse(0);
            npc.setScale(scale);
            npc.namedTag.putFloat("Scale", scale);
            npc.saveNBT();
            player.sendMessage("§aScale changed to §e" + scale);
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("block_edit") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            Entity npc = player.getLevel().getEntity(state.npcId);
            if (npc == null) { player.sendMessage("§cNPC no longer exists"); states.remove(player.getUniqueId()); return; }
            try {
                String[] parts = form.getResponse().getInputResponse(0).split(":");
                int blockId = Integer.parseInt(parts[0]);
                int damage = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                npc.namedTag.putInt("Tile", blockId);
                npc.namedTag.putByte("Data", (byte) damage);
                npc.saveNBT();
                npc.respawnToAll();
                player.sendMessage("§aBlock updated");
            } catch (Exception e) {
                player.sendMessage("§cInvalid format. Use ID:Meta (e.g. 1:0)");
            }
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("collision_edit") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            Entity npc = player.getLevel().getEntity(state.npcId);
            if (npc == null) { player.sendMessage("§cNPC no longer exists"); states.remove(player.getUniqueId()); return; }
            boolean collidable = form.getResponse().getToggleResponse(0);
            int knockback = (int) form.getResponse().getSliderResponse(1);
            npc.namedTag.putBoolean("Collidable", collidable);
            npc.namedTag.putFloat("Knockback", knockback);
            npc.saveNBT();
            player.sendMessage("§aCollision: " + (collidable ? "§eON" : "§cOFF") + " §a| Knockback: §e" + knockback);
            states.remove(player.getUniqueId());
            return;
        }

        if (step.equals("command_menu") && event.getWindow() instanceof FormWindowSimple) {
            FormWindowSimple form = (FormWindowSimple) event.getWindow();
            long npcId = state.npcId;
            String btn = form.getResponse().getClickedButton().getText();
            if (btn.equals("§aAdd Console Cmd")) {
                addCommandInput(player, npcId, false);
            } else if (btn.equals("§bAdd Player Cmd")) {
                addCommandInput(player, npcId, true);
            } else if (btn.equals("§cClear All")) {
                Entity npc = player.getLevel().getEntity(npcId);
                if (npc != null) {
                    npc.namedTag.putList(new ListTag<StringTag>("Commands"))
                            .putList(new ListTag<StringTag>("PlayerCommands"));
                    npc.saveNBT();
                    player.sendMessage("§aAll commands cleared");
                }
                states.remove(player.getUniqueId());
            }
            return;
        }

        if (step.equals("add_cmd") && event.getWindow() instanceof FormWindowCustom) {
            FormWindowCustom form = (FormWindowCustom) event.getWindow();
            Entity npc = player.getLevel().getEntity(state.npcId);
            if (npc == null) { player.sendMessage("§cNPC no longer exists"); states.remove(player.getUniqueId()); return; }
            String cmd = form.getResponse().getInputResponse(1).trim();
            if (cmd.isEmpty()) { player.sendMessage("§cCommand cannot be empty"); states.remove(player.getUniqueId()); return; }
            String tagName = state.playerCmd ? "PlayerCommands" : "Commands";
            npc.namedTag.getList(tagName, StringTag.class).add(new StringTag(cmd, cmd));
            npc.saveNBT();
            player.sendMessage("§aCommand added");
            states.remove(player.getUniqueId());
        }
    }

    private static void openNameEdit(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) { player.sendMessage("§cNPC no longer exists"); return; }
        String current = npc.getNameTag();
        if ("%k".equals(current)) current = "";
        FormWindowCustom form = new FormWindowCustom("§l§8[Change Name]");
        form.addElement(new ElementInput("New name", "Use & for colors", current));
        GUIState state = new GUIState();
        state.step = "name_edit";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    private static void openScaleEdit(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) { player.sendMessage("§cNPC no longer exists"); return; }
        FormWindowCustom form = new FormWindowCustom("§l§8[Change Scale]");
        form.addElement(new ElementSlider("Scale", 0, 25, 1, Math.round(npc.getScale())));
        GUIState state = new GUIState();
        state.step = "scale_edit";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    private static void openBlockEdit(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (npc == null) { player.sendMessage("§cNPC no longer exists"); return; }
        FormWindowCustom form = new FormWindowCustom("§l§8[Change Block]");
        form.addElement(new ElementInput("Block ID:Meta", "e.g. 1:0", npc.namedTag.getInt("Tile") + ":" + npc.namedTag.getByte("Data")));
        GUIState state = new GUIState();
        state.step = "block_edit";
        state.npcId = npcId;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    private static void addCommandInput(Player player, long npcId, boolean playerCmd) {
        FormWindowCustom form = new FormWindowCustom("§l§8[Add " + (playerCmd ? "Player" : "Console") + " Cmd]");
        form.addElement(new ElementLabel("Placeholders: §e%p §7(player), §e%uuid"));
        form.addElement(new ElementInput("Command", "e.g. say Hello %p", ""));
        GUIState state = new GUIState();
        state.step = "add_cmd";
        state.npcId = npcId;
        state.playerCmd = playerCmd;
        states.put(player.getUniqueId(), state);
        player.showFormWindow(form);
    }

    private static void spawnNPC(Player player, String entityType, String name, boolean nameVisible) {
        name = name.replace("&", "\u00a7").replace("%n", "\n");
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Pos")
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", player.x))
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", player.y))
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", player.z)))
                .putList(new ListTag<cn.nukkit.nbt.tag.DoubleTag>("Motion")
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", 0))
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", 0))
                        .add(new cn.nukkit.nbt.tag.DoubleTag("", 0)))
                .putList(new ListTag<cn.nukkit.nbt.tag.FloatTag>("Rotation")
                        .add(new cn.nukkit.nbt.tag.FloatTag("", (float) player.getYaw()))
                        .add(new cn.nukkit.nbt.tag.FloatTag("", (float) player.getPitch())))
                .putBoolean("Invulnerable", true)
                .putString("NameTag", name)
                .putList(new ListTag<StringTag>("Commands"))
                .putList(new ListTag<StringTag>("PlayerCommands"))
                .putFloat("Scale", 1.0f)
                .putBoolean("npc", true);
        if ("Human".equals(entityType)) {
            nbt.putCompound("Skin", createSkinTag(player));
            nbt.putBoolean("ishuman", true);
        } else if ("Block".equals(entityType)) {
            nbt.putInt("Tile", 2);
            nbt.putByte("Data", 0);
        }
        Entity ent = Entity.createEntity("NPC_" + entityType, player.chunk, nbt);
        if (ent == null) {
            player.sendMessage("§cFailed to spawn NPC");
            return;
        }
        ent.setNameTag(name);
        if (nameVisible && !"%k".equals(name)) {
            ent.setNameTagVisible(true);
            ent.setNameTagAlwaysVisible(true);
        }
        if (ent instanceof NPC_Human) {
            setInventories((NPC_Human) ent, player);
        }
        ent.spawnToAll();
        player.sendMessage("§aSpawned §e" + entityType + "§a NPC (ID: §e" + ent.getId() + "§a)");
    }

    private static CompoundTag createSkinTag(Player p) {
        cn.nukkit.entity.data.skin.Skin skin = p.getSkin();
        CompoundTag skinTag = new CompoundTag()
                .putString("ModelId", skin.getSkinId())
                .putByteArray("Data", skin.getSkinData().data)
                .putInt("SkinImageWidth", skin.getSkinData().width)
                .putInt("SkinImageHeight", skin.getSkinData().height)
                .putString("CapeId", skin.getCapeId())
                .putByteArray("CapeData", skin.getCapeData().data)
                .putInt("CapeImageWidth", skin.getCapeData().width)
                .putInt("CapeImageHeight", skin.getCapeData().height)
                .putByteArray("SkinResourcePatch", skin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                .putByteArray("GeometryData", skin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                .putByteArray("SkinAnimationData", skin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                .putBoolean("PremiumSkin", skin.isPremium())
                .putBoolean("PersonaSkin", skin.isPersona())
                .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic())
                .putString("ArmSize", skin.getArmSize())
                .putString("SkinColor", skin.getSkinColor())
                .putBoolean("IsTrustedSkin", true);
        java.util.List<cn.nukkit.entity.data.skin.SkinAnimation> animations = skin.getAnimations();
        if (!animations.isEmpty()) {
            ListTag<CompoundTag> animationsTag = new ListTag<>("AnimatedImageData");
            for (cn.nukkit.entity.data.skin.SkinAnimation animation : animations) {
                animationsTag.add(new CompoundTag()
                        .putFloat("Frames", animation.frames)
                        .putInt("Type", animation.type)
                        .putInt("ImageWidth", animation.image.width)
                        .putInt("ImageHeight", animation.image.height)
                        .putInt("AnimationExpression", animation.expression)
                        .putByteArray("Image", animation.image.data));
            }
            skinTag.putList(animationsTag);
        }
        return skinTag;
    }

    private static void copySkin(Player player, long npcId) {
        Entity npc = player.getLevel().getEntity(npcId);
        if (!(npc instanceof NPC_Human)) return;
        cn.nukkit.entity.data.skin.Skin skin = player.getSkin();
        CompoundTag skinTag = new CompoundTag()
                .putString("ModelId", skin.getSkinId())
                .putByteArray("Data", skin.getSkinData().data)
                .putInt("SkinImageWidth", skin.getSkinData().width)
                .putInt("SkinImageHeight", skin.getSkinData().height)
                .putString("CapeId", skin.getCapeId())
                .putByteArray("CapeData", skin.getCapeData().data)
                .putInt("CapeImageWidth", skin.getCapeData().width)
                .putInt("CapeImageHeight", skin.getCapeData().height)
                .putByteArray("SkinResourcePatch", skin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                .putByteArray("GeometryData", skin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                .putByteArray("SkinAnimationData", skin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                .putBoolean("PremiumSkin", skin.isPremium())
                .putBoolean("PersonaSkin", skin.isPersona())
                .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic())
                .putString("ArmSize", skin.getArmSize())
                .putString("SkinColor", skin.getSkinColor())
                .putBoolean("IsTrustedSkin", true);
        npc.namedTag.putCompound("Skin", skinTag);
        npc.saveNBT();
        npc.respawnToAll();
        player.sendMessage("§aSkin copied");
    }

    private static void setInventories(NPC_Human ent, Player pl) {
        cn.nukkit.inventory.PlayerInventory inv = pl.getInventory();
        cn.nukkit.inventory.PlayerInventory eInv = ent.getInventory();
        eInv.setHelmet(inv.getHelmet());
        eInv.setChestplate(inv.getChestplate());
        eInv.setLeggings(inv.getLeggings());
        eInv.setBoots(inv.getBoots());
        eInv.setItemInHand(inv.getItemInHand());
        ent.getOffhandInventory().setItem(0, pl.getOffhandInventory().getItem(0));
        ent.saveNBT();
    }
}