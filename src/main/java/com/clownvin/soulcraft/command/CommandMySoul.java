package com.clownvin.soulcraft.command;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.soul.ISoul;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandMySoul extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "mysoul";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.mysoul.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ISoul soul = ISoul.getSoul(player);
        if (soul == null) {
            notifyCommandListener(sender, this, "commands.mysoul.soulless");
            return;
        }
        notifyCommandListener(sender, this, "commands.mysoul.success", new Object[]{soul.getLevel(), String.format("%.2f", soul.getXP()), soul.getKillCount(), soul.getHitCount(), soul.getUseCount(), SoulCraft.getOutgoingDamageModifiers(player), SoulCraft.getIncomingDamageModifiers(player), SoulCraft.getBreakSpeedModifiers(player)});
    }
}
