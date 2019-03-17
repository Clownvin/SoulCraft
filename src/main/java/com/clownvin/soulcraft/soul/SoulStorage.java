package com.clownvin.soulcraft.soul;

import static com.clownvin.soulcraft.soul.ISoul.*;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SoulStorage<T extends ISoul> implements Capability.IStorage<T> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
        NBTTagCompound base = new NBTTagCompound();
        base.setBoolean(ASLEEP, instance.isAsleep());
        base.setBoolean(QUIETED, instance.isQuieted());
        base.setString(PERSONALITY_NAME, instance.getPersonalityName());
        base.setInteger(KILL_COUNT, instance.getKillCount());
        base.setInteger(HIT_COUNT, instance.getHitCount());
        base.setInteger(USAGE_COUNT, instance.getUseCount());
        base.setInteger(STRENGTH, instance.getStrength());
        if (instance.getStrength() <= FAINT)
            return base;
        base.setDouble(XP, instance.getXP());
        return base;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        instance.setSleeping(compound.getBoolean(ASLEEP));
        instance.setQuieted(compound.getBoolean(QUIETED));
        instance.setPersonalityName(compound.getString(PERSONALITY_NAME));
        instance.setKillCount(compound.getInteger(KILL_COUNT));
        instance.setHitCount(compound.getInteger(HIT_COUNT));
        instance.setUseCount(compound.getInteger(USAGE_COUNT));
        instance.setStrength(compound.getInteger(STRENGTH));
        if (instance.getStrength() <= FAINT)
            return;
        instance.setXP(compound.getDouble(XP));
    }
}
