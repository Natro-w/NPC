package ru.Natro.npc.entities;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.skin.Skin;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPC_Human extends EntityHuman {

    private String currentScoreTag;

    public NPC_Human(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (namedTag.contains("Scale")) {
            setScale(namedTag.getFloat("Scale"));
        }
        updateScoreTag();
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
            }
        }
    }

    private Skin checkSkin(Skin skin) {
        skin.setTrusted(true);
        if (!skin.isPersona()) {
            skin.setFullSkinId(UUID.randomUUID().toString());
        }
        return skin;
    }

    public boolean onUpdate(int tick) {
        if (tick % 40 == 0) {
            updateScoreTag();
        }
        return true;
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
