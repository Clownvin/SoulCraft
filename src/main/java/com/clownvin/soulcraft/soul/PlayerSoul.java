package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerSoul extends Soul implements ICapabilitySerializable<NBTBase> {

    protected int strength = STRONG;

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulCraft.PLAYER_SOUL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    public double getLevelXPMod() {
        return SCConfig.souls.players.levelXPModifier;
    }

    @Override
    public double getToolEffectivenessMod() {
        return SCConfig.souls.players.toolEffectivenessPerLevel;
    }

    @Override
    public double getWeaponEffectivenessMod() {
        return SCConfig.souls.players.weaponEffectivenessPerLevel;
    }

    @Override
    public double getArmorEffectivenessMod() {
        return SCConfig.souls.players.armorEffectivenessPerLevel;
    }

    @Override
    public int getStrength() {
        return strength;
    }

    @Override
    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public NBTBase serializeNBT() {
        return SoulCraft.PLAYER_SOUL_CAPABILITY.getStorage().writeNBT(SoulCraft.PLAYER_SOUL_CAPABILITY, this, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SoulCraft.PLAYER_SOUL_CAPABILITY.getStorage().readNBT(SoulCraft.PLAYER_SOUL_CAPABILITY, this, null, nbt);
    }
}
