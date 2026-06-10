package ru.Natro.npc;

import me.iwareq.fakeinventories.FakeInventory;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import ru.Natro.npc.entities.NPC_Human;

import java.util.*;

public class NPCInventoryGUI implements Listener {

    private static Plugin plugin;

    private static final int SLOT_HELMET = 13;
    private static final int SLOT_CHESTPLATE = 22;
    private static final int SLOT_LEGGINGS = 31;
    private static final int SLOT_BOOTS = 40;
    private static final int SLOT_HAND = 24;
    private static final int SLOT_OFFHAND = 34;

    private static final int SLOT_HELMET_BARRIER = 12;
    private static final int SLOT_CHESTPLATE_BARRIER = 21;
    private static final int SLOT_LEGGINGS_BARRIER = 30;
    private static final int SLOT_BOOTS_BARRIER = 39;
    private static final int SLOT_HAND_BARRIER = 23;
    private static final int SLOT_OFFHAND_BARRIER = 35;

    private static final Set<Integer> BARRIER_SLOTS = new HashSet<>(Arrays.asList(
        SLOT_HELMET_BARRIER, SLOT_CHESTPLATE_BARRIER, SLOT_LEGGINGS_BARRIER,
        SLOT_BOOTS_BARRIER, SLOT_HAND_BARRIER, SLOT_OFFHAND_BARRIER
    ));

    private static final Map<UUID, long[]> editing = new HashMap<>();

    public NPCInventoryGUI(Plugin plugin) {
        NPCInventoryGUI.plugin = plugin;
    }

    public static void open(Player player, Entity npc) {
        if (!(npc instanceof NPC_Human)) {
            player.sendMessage("§cOnly human NPCs have equipment");
            return;
        }
        NPC_Human human = (NPC_Human) npc;
        long npcId = npc.getId();

        player.closeFormWindows();

        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
            if (player.isClosed() || !player.isOnline()) return;

            FakeInventory inv = new FakeInventory(InventoryType.DOUBLE_CHEST, "§8NPC Equipment");

            inv.setItem(SLOT_HELMET, human.getInventory().getHelmet());
            inv.setItem(SLOT_CHESTPLATE, human.getInventory().getChestplate());
            inv.setItem(SLOT_LEGGINGS, human.getInventory().getLeggings());
            inv.setItem(SLOT_BOOTS, human.getInventory().getBoots());
            inv.setItem(SLOT_HAND, human.getInventory().getItemInHand());
            inv.setItem(SLOT_OFFHAND, human.getOffhandInventory().getItem(0));

            setBarrier(inv, SLOT_HELMET_BARRIER, "§cHelmet");
            setBarrier(inv, SLOT_CHESTPLATE_BARRIER, "§6Chestplate");
            setBarrier(inv, SLOT_LEGGINGS_BARRIER, "§eLeggings");
            setBarrier(inv, SLOT_BOOTS_BARRIER, "§aBoots");
            setBarrier(inv, SLOT_HAND_BARRIER, "§bMain Hand");
            setBarrier(inv, SLOT_OFFHAND_BARRIER, "§dOff Hand");

            editing.put(player.getUniqueId(), new long[]{npcId});

            inv.setCloseHandler((p) -> {
                editing.remove(p.getUniqueId());
                Entity raw = p.getLevel().getEntity(npcId);
                if (!(raw instanceof NPC_Human)) return;
                NPC_Human edited = (NPC_Human) raw;
                applyAll(edited, inv);
                edited.saveNBT();
                broadcastEquipment(edited);
                p.sendMessage("§aNPC equipment saved");
            });

            player.addWindow(inv);
        }, 15);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTransaction(InventoryTransactionEvent event) {
        Player player = event.getTransaction().getSource();
        long[] data = editing.get(player.getUniqueId());
        if (data == null) return;
        long npcId = data[0];
        Entity raw = player.getLevel().getEntity(npcId);
        if (!(raw instanceof NPC_Human)) return;
        NPC_Human npc = (NPC_Human) raw;

        boolean changed = false;
        for (InventoryAction action : event.getTransaction().getActions()) {
            if (!(action instanceof SlotChangeAction)) continue;
            SlotChangeAction sca = (SlotChangeAction) action;
            Inventory inv = sca.getInventory();
            if (!(inv instanceof FakeInventory)) continue;
            int slot = sca.getSlot();

            if (BARRIER_SLOTS.contains(slot)) {
                event.setCancelled(true);
                return;
            }

            applySlot(npc, slot, sca.getTargetItem());
            changed = true;
        }

        if (changed) {
            npc.saveNBT();
            broadcastEquipment(npc);
        }
    }

    private static void setBarrier(FakeInventory inv, int slot, String name) {
        Item barrier = Item.get(415);
        barrier.setCustomName(name);
        inv.setItem(slot, barrier, (item, event) -> event.setCancelled(true));
    }

    private static void applySlot(NPC_Human npc, int slot, Item item) {
        if (slot == SLOT_HELMET) npc.getInventory().setHelmet(item);
        else if (slot == SLOT_CHESTPLATE) npc.getInventory().setChestplate(item);
        else if (slot == SLOT_LEGGINGS) npc.getInventory().setLeggings(item);
        else if (slot == SLOT_BOOTS) npc.getInventory().setBoots(item);
        else if (slot == SLOT_HAND) npc.getInventory().setItemInHand(item);
        else if (slot == SLOT_OFFHAND) npc.getOffhandInventory().setItem(0, item);
    }

    private static void broadcastSlot(NPC_Human npc, int slot) {
        for (Player viewer : npc.getLevel().getPlayers().values()) {
            if (slot == SLOT_HAND) {
                npc.getInventory().sendHeldItem(viewer);
            } else if (slot == SLOT_OFFHAND) {
                npc.getOffhandInventory().sendContents(viewer);
            } else {
                npc.getInventory().sendArmorContents(viewer);
            }
        }
    }

    private static void applyAll(NPC_Human npc, FakeInventory inv) {
        npc.getInventory().setHelmet(inv.getItem(SLOT_HELMET));
        npc.getInventory().setChestplate(inv.getItem(SLOT_CHESTPLATE));
        npc.getInventory().setLeggings(inv.getItem(SLOT_LEGGINGS));
        npc.getInventory().setBoots(inv.getItem(SLOT_BOOTS));
        npc.getInventory().setItemInHand(inv.getItem(SLOT_HAND));
        npc.getOffhandInventory().setItem(0, inv.getItem(SLOT_OFFHAND));
    }

    private static void broadcastEquipment(NPC_Human npc) {
        for (Player viewer : npc.getLevel().getPlayers().values()) {
            npc.getInventory().sendArmorContents(viewer);
            npc.getInventory().sendHeldItem(viewer);
            npc.getOffhandInventory().sendContents(viewer);
        }
    }
}