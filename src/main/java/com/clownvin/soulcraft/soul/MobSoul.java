package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobSoul extends Soul implements ICapabilitySerializable<NBTBase> {

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulCraft.MOB_SOUL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    public MobSoul() {
    }

    @Override
    public double getLevelXPMod() {
        return SCConfig.souls.mobs.levelXPModifier;
    }

    @Override
    public double getToolEffectivenessMod() {
        return 0;
    }

    @Override
    public double getWeaponEffectivenessMod() {
        return SCConfig.souls.mobs.weaponEffectivenessPerLevel;
    }

    @Override
    public double getArmorEffectivenessMod() {
        return SCConfig.souls.mobs.armorEffectivenessPerLevel;
    }

    @Override
    public int getStrength() {
        return STRONG; //Mobs always at least Strong
    }

    @Override
    public void setStrength(int strength) {
        //Mobs always at least strong. Override if want to change.
    }

    @Override
    public NBTBase serializeNBT() {
        return SoulCraft.MOB_SOUL_CAPABILITY.getStorage().writeNBT(SoulCraft.MOB_SOUL_CAPABILITY, this, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SoulCraft.MOB_SOUL_CAPABILITY.getStorage().readNBT(SoulCraft.MOB_SOUL_CAPABILITY, this, null, nbt);
    }
}
