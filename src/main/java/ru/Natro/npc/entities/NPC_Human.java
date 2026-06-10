package ru.Natro.npc.entities;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.skin.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.math.Vector3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPC_Human extends EntityHuman {

    private String currentScoreTag;

    private long helmetDisplayEid = -1;
    private final Set<Long> helmetViewers = new HashSet<>();
    private Item lastHelmetItem;

    public NPC_Human(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (namedTag.contains("Scale")) {
            setScale(namedTag.getFloat("Scale"));
        }
        updateScoreTag();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity instanceof Player && !namedTag.getBoolean("Collidable")) {
            return false;
        }
        return super.canCollideWith(entity);
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (!(entity instanceof Player)) {
            super.applyEntityCollision(entity);
            return;
        }
        boolean collidable = namedTag.getBoolean("Collidable");
        if (!collidable) return;
        float knockback = namedTag.getFloat("Knockback");
        if (knockback <= 0) {
            super.applyEntityCollision(entity);
            return;
        }
        double dx = entity.x - this.x;
        double dz = entity.z - this.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.01) return;
        double nx = dx / dist;
        double nz = dz / dist;
        entity.setMotion(new Vector3(nx * knockback, 0, nz * knockback));
    }

    @Override
    public void spawnTo(Player player) {
        if (player != null && this.chunk != null && !this.hasSpawned.containsKey(player.getLoaderId())) {
            Boolean hasChunk = player.usedChunks.get(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()));
            if (hasChunk != null && hasChunk) {
                this.hasSpawned.put(player.getLoaderId(), player);

                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.checkSkin(this.skin), new Player[]{player});

                AddPlayerPacket pk = new AddPlayerPacket();
                pk.uuid = this.getUniqueId();
                pk.username = this.getName();
                pk.entityUniqueId = this.getId();
                pk.entityRuntimeId = this.getId();
                pk.x = (float) this.x;
                pk.y = (float) this.y;
                pk.z = (float) this.z;
                pk.speedX = (float) this.motionX;
                pk.speedY = (float) this.motionY;
                pk.speedZ = (float) this.motionZ;
                pk.yaw = (float) this.yaw;
                pk.pitch = (float) this.pitch;
                pk.item = this.inventory.getItemInHand();
                pk.metadata = this.dataProperties;
                player.dataPacket(pk);

                this.inventory.sendHeldItem(player);
                this.inventory.sendArmorContents(player);
                this.offhandInventory.sendContents(player);

                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});

                spawnHelmetDisplay(player);
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        super.despawnFrom(player);
        if (helmetDisplayEid != -1) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = helmetDisplayEid;
            player.dataPacket(pk);
            helmetViewers.remove((long) player.getLoaderId());
        }
    }

    public boolean onUpdate(int tick) {
        if (!this.closed) {
            tickHelmetDisplay();
        }
        if (tick % 40 == 0) {
            updateScoreTag();
        }
        return true;
    }

    private void tickHelmetDisplay() {
        Item item = this.inventory.getHelmet();
        boolean hasItem = item != null && item.getId() != 0;

        if (!hasItem) {
            if (helmetDisplayEid != -1) {
                removeHelmetDisplay();
            }
            return;
        }

        if (helmetDisplayEid == -1) {
            helmetDisplayEid = Entity.entityCount++;
        }

        boolean itemChanged = lastHelmetItem == null || !item.equals(lastHelmetItem);
        double headY = this.y + (this.getHeight() + 0.6) * this.getScale();

        for (Player player : this.getViewers().values()) {
            boolean isViewer = helmetViewers.contains((long) player.getLoaderId());

            if (!isViewer || itemChanged) {
                spawnHelmetDisplayFor(player, item, headY);
            } else {
                updateHelmetPosition(player, headY);
            }
        }

        helmetViewers.removeIf(id -> !this.getViewers().containsKey(id));
        lastHelmetItem = item;
    }

    private void spawnHelmetDisplay(Player player) {
        Item item = this.inventory.getHelmet();
        if (item == null || item.getId() == 0) return;
        if (helmetDisplayEid == -1) {
            helmetDisplayEid = Entity.entityCount++;
        }
        double headY = this.y + (this.getHeight() + 0.6) * this.getScale();
        spawnHelmetDisplayFor(player, item, headY);
    }

    private void spawnHelmetDisplayFor(Player player, Item item, double headY) {
        AddItemEntityPacket pk = new AddItemEntityPacket();
        pk.entityUniqueId = helmetDisplayEid;
        pk.entityRuntimeId = helmetDisplayEid;
        pk.item = item;
        pk.x = (float) this.x;
        pk.y = (float) headY;
        pk.z = (float) this.z;
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.metadata = new EntityMetadata()
            .putLong(Entity.DATA_FLAGS, 1L << Entity.DATA_FLAG_IMMOBILE);
        player.dataPacket(pk);
        helmetViewers.add((long) player.getLoaderId());
    }

    private void updateHelmetPosition(Player player, double headY) {
        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = helmetDisplayEid;
        pk.x = (float) this.x;
        pk.y = (float) headY;
        pk.z = (float) this.z;
        pk.yaw = 0;
        pk.headYaw = 0;
        pk.pitch = 0;
        pk.onGround = true;
        player.dataPacket(pk);
    }

    private void removeHelmetDisplay() {
        if (helmetDisplayEid == -1) return;
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = helmetDisplayEid;
        Server.broadcastPacket(this.getViewers().values(), pk);
        helmetDisplayEid = -1;
        helmetViewers.clear();
        lastHelmetItem = null;
    }

    private Skin checkSkin(Skin skin) {
        skin.setTrusted(true);
        if (!skin.isPersona()) {
            skin.setFullSkinId(UUID.randomUUID().toString());
        }
        return skin;
    }

    private void updateScoreTag() {
        String tag = namedTag.getString("ScoreTag");
        if (tag.isEmpty()) return;
        tag = replacePlayers(tag);
        if (!tag.equals(this.currentScoreTag)) {
            this.currentScoreTag = tag;
            this.setDataProperty(new StringEntityData(DATA_SCORE_TAG, tag));
        }
    }

    private static final Pattern pattern = Pattern.compile("%playersof\\s+([\\w-]+)");

    private String replacePlayers(String input) {
        Matcher matcher = pattern.matcher(input);
        StringBuffer outputBuffer = new StringBuffer();
        while (matcher.find()) {
            String worldName = matcher.group(1);
            int playersCount = -1;
            Level l = getServer().getLevelByName(worldName);
            if (l != null) {
                playersCount = l.getPlayers().size();
            }
            matcher.appendReplacement(outputBuffer, Integer.toString(playersCount));
        }
        matcher.appendTail(outputBuffer);
        return outputBuffer.toString();
    }
}