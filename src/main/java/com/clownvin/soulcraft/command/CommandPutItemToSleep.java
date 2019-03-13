package com.clownvin.soulcraft.command;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.soul.ISoul;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandPutItemToSleep extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "putitemtosleep";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.putitemtosleep.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack heldItem = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null)
            throw new WrongUsageException("commands.mainhand_item_soulless", new Object[0]);
        soul.setSleeping(true);
        notifyCommandListener(sender, this, "commands.putitemtosleep.success", new Object[]{heldItem.getDisplayName()});
    }
}