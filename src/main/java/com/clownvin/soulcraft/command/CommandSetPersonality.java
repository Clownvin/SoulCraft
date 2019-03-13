package com.clownvin.soulcraft.command;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.personality.Personality;
import com.clownvin.soulcraft.soul.ISoul;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandSetPersonality extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "setpersonality";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.setpersonality.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            //throw new WrongUsageException(getUsage(sender), new Object[0]);
            StringBuilder sb = new StringBuilder();
            for (Personality p : Personality.getRegistry().getValuesCollection()) {
                if (sb.length() != 0)
                    sb.append(", ");
                sb.append(p.name);
            }
            notifyCommandListener(sender, this, "commands.setpersonality.listpersonalities", new Object[]{sb.toString()});
            return;
        }
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack heldItem = player.getHeldItemMainhand();
        String name = "???";
        ISoul soul = ISoul.getSoul(heldItem);
        String arg = args[0];
        for (int i = 1; i < args.length; i++)
            arg += " " + args[i];
        Personality personality = Personality.getPersonality(arg);
        if (personality == null)
            throw new WrongUsageException("commands.setpersonality.not_in_range", arg);
        soul.setPersonalityName(personality.name);
        name = personality.name;
        notifyCommandListener(sender, this, "commands.setpersonality.success", new Object[]{heldItem.getDisplayName(), name});
    }
}
