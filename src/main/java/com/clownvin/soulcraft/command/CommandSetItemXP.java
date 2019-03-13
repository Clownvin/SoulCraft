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

public class CommandSetItemXP extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "setitemxp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.setitemxp.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        if (args.length < 1)
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        try {
            float xp = Float.parseFloat(args[0]);
            if (xp < 0)
                throw new WrongUsageException("commands.livingenchantment.invalid_xp_number", new Object[0]);
            ItemStack heldItem = player.getHeldItemMainhand();
            ISoul soul = ISoul.getSoul(heldItem);
            if (soul == null)
                throw new WrongUsageException("commands.mainhand_item_soulless", new Object[0]);
            SoulCraft.setExp(player, heldItem, soul, xp);
            notifyCommandListener(sender, this, "commands.setitemxp.success", new Object[]{heldItem.getDisplayName(), xp});
        } catch (NumberFormatException e) {
            throw new WrongUsageException("commands.livingenchantment.invalid_xp_number", new Object[0]);
        }
    }
}
