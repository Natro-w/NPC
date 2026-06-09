package ru.Natro.npc;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;

import java.util.Arrays;
import java.util.List;

public class NPCCommand extends Command {

    private static final List<String> ACTIONS = Arrays.asList(
            "spawn", "edit", "remove", "entities", "getid",
            "addcmd", "addplayercmd", "delcmd", "delplayercmd",
            "delallcmd", "listcmd", "help"
    );

    private static final List<String> EDIT_ACTIONS = Arrays.asList(
            "item", "offhanditem", "armor", "scale", "name",
            "scoretag", "tphere", "block", "skin"
    );

    private static final List<String> ENTITIES = Arrays.asList(
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

    public NPCCommand() {
        super("npc", "NPC management command", "/npc <action> [args]");
        this.setPermission("npc.edit");

        CommandEnum actionEnum = new CommandEnum("NPCAction", ACTIONS);
        CommandEnum entityEnum = new CommandEnum("NPCEntity", ENTITIES);
        CommandEnum editEnum = new CommandEnum("NPCEditAction", EDIT_ACTIONS);

        this.commandParameters.clear();

        this.commandParameters.put("spawn", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCSpawnAction", List.of("spawn"))),
                CommandParameter.newEnum("entity", false, entityEnum),
                CommandParameter.newType("name", true, CommandParameter.ARG_TYPE_STRING)
        });

        this.commandParameters.put("edit", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCEditActionAlias", List.of("edit"))),
                CommandParameter.newType("id", false, CommandParameter.ARG_TYPE_INT),
                CommandParameter.newEnum("editAction", false, editEnum),
                CommandParameter.newType("value", true, CommandParameter.ARG_TYPE_STRING)
        });

        this.commandParameters.put("remove", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCRemoveAction", List.of("remove")))
        });

        this.commandParameters.put("entities", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCEntitiesAction", List.of("entities")))
        });

        this.commandParameters.put("getid", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCGetIdAction", List.of("getid")))
        });

        CommandParameter[] cmdParams = new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCCmdAction", ACTIONS)),
                CommandParameter.newType("id", false, CommandParameter.ARG_TYPE_INT),
                CommandParameter.newType("cmd", false, CommandParameter.ARG_TYPE_STRING)
        };

        this.commandParameters.put("addcmd", cmdParams);
        this.commandParameters.put("addplayercmd", cmdParams);
        this.commandParameters.put("delcmd", cmdParams);
        this.commandParameters.put("delplayercmd", cmdParams);
        this.commandParameters.put("delallcmd", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCDelAllAction", List.of("delallcmd"))),
                CommandParameter.newType("id", false, CommandParameter.ARG_TYPE_INT)
        });
        this.commandParameters.put("listcmd", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCListCmdAction", List.of("listcmd"))),
                CommandParameter.newType("id", false, CommandParameter.ARG_TYPE_INT)
        });

        this.commandParameters.put("help", new CommandParameter[]{
                CommandParameter.newEnum("action", false, new CommandEnum("NPCHelpAction", List.of("help")))
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return CommandHandler.handle(sender, this, args);
    }
}