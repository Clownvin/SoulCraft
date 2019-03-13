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

public class CommandResetItem extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "resetitem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.resetitem.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack heldItem = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null)
            throw new WrongUsageException("commands.mainhand_item_soulless", new Object[0]);
        SoulCraft.resetItem(heldItem);
        notifyCommandListener(sender, this, "commands.resetitem.success", new Object[]{heldItem.getDisplayName()});
    }
}
