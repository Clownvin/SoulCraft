package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemSoul extends Soul implements ICapabilitySerializable<NBTBase> {

    protected int strength;
    protected long lastTalk;
    protected boolean asleep = false;
    protected boolean quieted = false;

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulCraft.ITEM_SOUL_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    public boolean isAsleep() {
        return asleep;
    }

    @Override
    public void setSleeping(boolean sleeping) {
        asleep = sleeping;
    }

    @Override
    public boolean isQuieted() {
        return quieted;
    }

    @Override
    public void setQuieted(boolean quieted) {
        this.quieted = quieted;
    }

    @Override
    public double getLevelXPMod() {
        return SCConfig.souls.items.levelXPModifier;
    }

    @Override
    public double getToolEffectivenessMod() {
        return SCConfig.souls.items.toolEffectivenessPerLevel;
    }

    @Override
    public double getWeaponEffectivenessMod() {
        return SCConfig.souls.items.weaponEffectivenessPerLevel;
    }

    @Override
    public double getArmorEffectivenessMod() {
        return SCConfig.souls.items.armorEffectivenessPerLevel;
    }

    @Override
    public long getLastTalk() {
        return lastTalk;
    }

    @Override
    public void setLastTalk(long newLastTalk) {
        this.lastTalk = newLastTalk;
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
        return SoulCraft.ITEM_SOUL_CAPABILITY.getStorage().writeNBT(SoulCraft.ITEM_SOUL_CAPABILITY, this, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        SoulCraft.ITEM_SOUL_CAPABILITY.getStorage().readNBT(SoulCraft.ITEM_SOUL_CAPABILITY, this, null, nbt);
    }
}
