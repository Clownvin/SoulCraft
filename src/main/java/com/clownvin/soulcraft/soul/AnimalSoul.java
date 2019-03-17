package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnimalSoul extends Soul implements ICapabilitySerializable<NBTBase> {

    protected int strength = FAINT;

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulCraft.ANIMAL_SOUL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    public double getLevelXPMod() {
        return SCConfig.souls.animals.levelXPModifier;
    }

    @Override
    public double getToolEffectivenessMod() {
        return 0;
    }

    @Override
    public double getWeaponEffectivenessMod() {
        return 0;
    }

    @Override
    public double getArmorEffectivenessMod() {
        return SCConfig.souls.animals.armorEffectivenessPerLevel;
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
        return SoulCraft.ANIMAL_SOUL_CAPABILITY.getStorage().writeNBT(SoulCraft.ANIMAL_SOUL_CAPABILITY, this, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SoulCraft.ANIMAL_SOUL_CAPABILITY.getStorage().readNBT(SoulCraft.ANIMAL_SOUL_CAPABILITY, this, null, nbt);
    }
}
