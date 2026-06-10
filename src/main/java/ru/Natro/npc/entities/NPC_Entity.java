package ru.Natro.npc.entities;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.math.Vector3;
import java.util.HashMap;
import java.util.Map;

public abstract class NPC_Entity extends Entity {

    private static final Map<Integer, Float> SIZES = new HashMap<>();

    static {
        SIZES.put(10, 0.7f); SIZES.put(11, 1.3f); SIZES.put(12, 0.9f); SIZES.put(13, 1.3f);
        SIZES.put(14, 0.8f); SIZES.put(15, 1.9f); SIZES.put(16, 1.3f); SIZES.put(17, 0.95f);
        SIZES.put(18, 0.402f); SIZES.put(19, 0.9f); SIZES.put(20, 2.9f); SIZES.put(21, 1.8f);
        SIZES.put(22, 0.7f); SIZES.put(23, 1.6f); SIZES.put(24, 1.6f); SIZES.put(25, 1.6f);
        SIZES.put(26, 1.6f); SIZES.put(27, 1.6f); SIZES.put(114, 1.9f); SIZES.put(118, 1.9f);
        SIZES.put(32, 1.9f); SIZES.put(33, 1.8f); SIZES.put(34, 1.9f); SIZES.put(35, 0.9f);
        SIZES.put(36, 1.9f); SIZES.put(37, 0.52f); SIZES.put(38, 2.9f); SIZES.put(39, 0.3f);
        SIZES.put(40, 0.5f); SIZES.put(41, 4.0f); SIZES.put(42, 0.52f); SIZES.put(43, 1.8f);
        SIZES.put(44, 1.9f); SIZES.put(45, 1.9f); SIZES.put(46, 1.9f); SIZES.put(47, 2.01f);
        SIZES.put(48, 2.412f); SIZES.put(104,1.9f); SIZES.put(57, 1.9f); SIZES.put(105,0.8f);
        SIZES.put(121, 0.7f); SIZES.put(122, 0.5f); SIZES.put(31, 0.6f); SIZES.put(74, 0.4f);
        SIZES.put(125, 1.85f); SIZES.put(124, 0.9f); SIZES.put(123, 1.9f); SIZES.put(126, 0.9f);
        SIZES.put(59, 1.9f); SIZES.put(127, 1.9f); SIZES.put(128, 0.9f); SIZES.put(129, 0.95f);
        SIZES.put(130, 0.42f); SIZES.put(131, 2.9f); SIZES.put(132, 0.55f); SIZES.put(133, 0.8f);
        SIZES.put(134, 0.6f);
    }

    public NPC_Entity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.fireProof = true;
        if (namedTag.contains("Scale")) {
            setScale(namedTag.getFloat("Scale"));
        }
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

    public float getHeight() {
        return SIZES.getOrDefault(this.getNetworkId(), 1.0f);
    }

    public float getWidth() {
        return 0.95f;
    }
}
