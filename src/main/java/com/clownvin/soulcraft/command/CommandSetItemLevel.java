package com.clownvin.soulcraft.command;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import com.clownvin.soulcraft.soul.ISoul;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandSetItemLevel extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "setitemlevel";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.setitemlevel.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        if (args.length < 1)
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        try {
            int level = Integer.parseInt(args[0]);
            if (level < 0 || level > SoulCraftConfig.general.maxLevel)
                throw new WrongUsageException("commands.setitemlevel.invalid_level_number", new Object[]{SoulCraftConfig.general.maxLevel});
            ItemStack heldItem = player.getHeldItemMainhand();
            ISoul soul = ISoul.getSoul(heldItem);
            if (soul == null)
                throw new WrongUsageException("commands.mainhand_item_soulless", new Object[0]);
            SoulCraft.setExp(player, heldItem, soul, ISoul.xpRequiredForLevel(level) + 0.5f); //+0.5f because some levels have rounding underflows for "next level xp"
            notifyCommandListener(sender, this, "commands.setitemlevel.success", new Object[]{heldItem.getDisplayName(), level});
        } catch (NumberFormatException e) {
            throw new WrongUsageException("commands.setitemlevel.invalid_level_number", new Object[0]);
        }
    }
}
