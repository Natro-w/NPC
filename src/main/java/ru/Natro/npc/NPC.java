package ru.Natro.npc;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.Registries;
import ru.Natro.npc.entities.*;

import java.util.ArrayList;
import java.util.List;

public class NPC extends PluginBase {

    static List<Long> cmd_id = new ArrayList<>();
    static List<Long> cmd_kill = new ArrayList<>();

    @Override
    public void onLoad() {
        registerNPCs();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new NPCGUI(), this);
        getServer().getPluginManager().registerEvents(new NPCInventoryGUI(this), this);
        getServer().getCommandMap().register("npc", new NPCCommand());
    }

    private static void registerNPCs() {
        Registries.ENTITY.register(NPC_Bat.class.getSimpleName(), NPC_Bat.class);
        Registries.ENTITY.register(NPC_Chicken.class.getSimpleName(), NPC_Chicken.class);
        Registries.ENTITY.register(NPC_Cow.class.getSimpleName(), NPC_Cow.class);
        Registries.ENTITY.register(NPC_Donkey.class.getSimpleName(), NPC_Donkey.class);
        Registries.ENTITY.register(NPC_Horse.class.getSimpleName(), NPC_Horse.class);
        Registries.ENTITY.register(NPC_Mooshroom.class.getSimpleName(), NPC_Mooshroom.class);
        Registries.ENTITY.register(NPC_Mule.class.getSimpleName(), NPC_Mule.class);
        Registries.ENTITY.register(NPC_Ocelot.class.getSimpleName(), NPC_Ocelot.class);
        Registries.ENTITY.register(NPC_Pig.class.getSimpleName(), NPC_Pig.class);
        Registries.ENTITY.register(NPC_PolarBear.class.getSimpleName(), NPC_PolarBear.class);
        Registries.ENTITY.register(NPC_Rabbit.class.getSimpleName(), NPC_Rabbit.class);
        Registries.ENTITY.register(NPC_Sheep.class.getSimpleName(), NPC_Sheep.class);
        Registries.ENTITY.register(NPC_SkeletonHorse.class.getSimpleName(), NPC_SkeletonHorse.class);
        Registries.ENTITY.register(NPC_Villager.class.getSimpleName(), NPC_Villager.class);
        Registries.ENTITY.register(NPC_Wolf.class.getSimpleName(), NPC_Wolf.class);
        Registries.ENTITY.register(NPC_ZombieHorse.class.getSimpleName(), NPC_ZombieHorse.class);
        Registries.ENTITY.register(NPC_ElderGuardian.class.getSimpleName(), NPC_ElderGuardian.class);
        Registries.ENTITY.register(NPC_Guardian.class.getSimpleName(), NPC_Guardian.class);
        Registries.ENTITY.register(NPC_Snowman.class.getSimpleName(), NPC_Snowman.class);
        Registries.ENTITY.register(NPC_Llama.class.getSimpleName(), NPC_Llama.class);
        Registries.ENTITY.register(NPC_Squid.class.getSimpleName(), NPC_Squid.class);
        Registries.ENTITY.register(NPC_Vindicator.class.getSimpleName(), NPC_Vindicator.class);
        Registries.ENTITY.register(NPC_Vex.class.getSimpleName(), NPC_Vex.class);
        Registries.ENTITY.register(NPC_IronGolem.class.getSimpleName(), NPC_IronGolem.class);
        Registries.ENTITY.register(NPC_Blaze.class.getSimpleName(), NPC_Blaze.class);
        Registries.ENTITY.register(NPC_Wither.class.getSimpleName(), NPC_Wither.class);
        Registries.ENTITY.register(NPC_Ghast.class.getSimpleName(), NPC_Ghast.class);
        Registries.ENTITY.register(NPC_CaveSpider.class.getSimpleName(), NPC_CaveSpider.class);
        Registries.ENTITY.register(NPC_Creeper.class.getSimpleName(), NPC_Creeper.class);
        Registries.ENTITY.register(NPC_Enderman.class.getSimpleName(), NPC_Enderman.class);
        Registries.ENTITY.register(NPC_Endermite.class.getSimpleName(), NPC_Endermite.class);
        Registries.ENTITY.register(NPC_ZombiePigman.class.getSimpleName(), NPC_ZombiePigman.class);
        Registries.ENTITY.register(NPC_Silverfish.class.getSimpleName(), NPC_Silverfish.class);
        Registries.ENTITY.register(NPC_Skeleton.class.getSimpleName(), NPC_Skeleton.class);
        Registries.ENTITY.register(NPC_Spider.class.getSimpleName(), NPC_Spider.class);
        Registries.ENTITY.register(NPC_Stray.class.getSimpleName(), NPC_Stray.class);
        Registries.ENTITY.register(NPC_Witch.class.getSimpleName(), NPC_Witch.class);
        Registries.ENTITY.register(NPC_Husk.class.getSimpleName(), NPC_Husk.class);
        Registries.ENTITY.register(NPC_Zombie.class.getSimpleName(), NPC_Zombie.class);
        Registries.ENTITY.register(NPC_ZombieVillager.class.getSimpleName(), NPC_ZombieVillager.class);
        Registries.ENTITY.register(NPC_Evoker.class.getSimpleName(), NPC_Evoker.class);
        Registries.ENTITY.register(NPC_Shulker.class.getSimpleName(), NPC_Shulker.class);
        Registries.ENTITY.register(NPC_Slime.class.getSimpleName(), NPC_Slime.class);
        Registries.ENTITY.register(NPC_WitherSkeleton.class.getSimpleName(), NPC_WitherSkeleton.class);
        Registries.ENTITY.register(NPC_MagmaCube.class.getSimpleName(), NPC_MagmaCube.class);
        Registries.ENTITY.register(NPC_Human.class.getSimpleName(), NPC_Human.class);
        Registries.ENTITY.register(NPC_Parrot.class.getSimpleName(), NPC_Parrot.class);
        Registries.ENTITY.register(NPC_Dolphin.class.getSimpleName(), NPC_Dolphin.class);
        Registries.ENTITY.register(NPC_Turtle.class.getSimpleName(), NPC_Turtle.class);
        Registries.ENTITY.register(NPC_Phantom.class.getSimpleName(), NPC_Phantom.class);
        Registries.ENTITY.register(NPC_Drowned.class.getSimpleName(), NPC_Drowned.class);
        Registries.ENTITY.register(NPC_Cat.class.getSimpleName(), NPC_Cat.class);
        Registries.ENTITY.register(NPC_Panda.class.getSimpleName(), NPC_Panda.class);
        Registries.ENTITY.register(NPC_Pillager.class.getSimpleName(), NPC_Pillager.class);
        Registries.ENTITY.register(NPC_WanderingTrader.class.getSimpleName(), NPC_WanderingTrader.class);
        Registries.ENTITY.register(NPC_Fox.class.getSimpleName(), NPC_Fox.class);
        Registries.ENTITY.register(NPC_Bee.class.getSimpleName(), NPC_Bee.class);
        Registries.ENTITY.register(NPC_Strider.class.getSimpleName(), NPC_Strider.class);
        Registries.ENTITY.register(NPC_Zoglin.class.getSimpleName(), NPC_Zoglin.class);
        Registries.ENTITY.register(NPC_Piglin.class.getSimpleName(), NPC_Piglin.class);
        Registries.ENTITY.register(NPC_Hoglin.class.getSimpleName(), NPC_Hoglin.class);
        Registries.ENTITY.register(NPC_Ravager.class.getSimpleName(), NPC_Ravager.class);
        Registries.ENTITY.register(NPC_Block.class.getSimpleName(), NPC_Block.class);
        Registries.ENTITY.register(NPC_PiglinBrute.class.getSimpleName(), NPC_PiglinBrute.class);
        Registries.ENTITY.register(NPC_Goat.class.getSimpleName(), NPC_Goat.class);
        Registries.ENTITY.register(NPC_Allay.class.getSimpleName(), NPC_Allay.class);
        Registries.ENTITY.register(NPC_Axolotl.class.getSimpleName(), NPC_Axolotl.class);
        Registries.ENTITY.register(NPC_Frog.class.getSimpleName(), NPC_Frog.class);
        Registries.ENTITY.register(NPC_GlowSquid.class.getSimpleName(), NPC_GlowSquid.class);
        Registries.ENTITY.register(NPC_Tadpole.class.getSimpleName(), NPC_Tadpole.class);
        Registries.ENTITY.register(NPC_Warden.class.getSimpleName(), NPC_Warden.class);
    }


}
