package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class EnchantmentWrapper implements ISoul {

    protected final NBTTagCompound tag;

    public EnchantmentWrapper(final NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public boolean isAsleep() {
        return tag.getBoolean(ASLEEP);
    }

    @Override
    public void setSleeping(boolean sleeping) {
        tag.setBoolean(ASLEEP, sleeping);
    }

    @Override
    public boolean isQuieted() {
        return tag.getBoolean(QUIETED);
    }

    @Override
    public void setQuieted(boolean quieted) {
        tag.setBoolean(QUIETED, quieted);
    }

    @Override
    public double getXP() {
        return tag.getDouble(XP);
    }

    @Override
    public void addXP(double newXP) {
        tag.setDouble(XP, getXP() + newXP);
    }

    @Override
    public void setXP(double newXP) {
        tag.setDouble(XP, newXP);
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
    public String getPersonalityName() {
        String name = tag.getString(PERSONALITY_NAME);
        if (name == null || name.isEmpty())
            name = "???";
        return name;
    }

    @Override
    public int getKillCount() {
        return tag.getInteger(KILL_COUNT);
    }

    @Override
    public int getHitCount() {
        return tag.getInteger(HIT_COUNT);
    }

    @Override
    public int getUseCount() {
        return tag.getInteger(USAGE_COUNT);
    }

    @Override
    public long getLastTalk() {
        return tag.getLong(LAST_TALK);
    }

    @Override
    public void setLastTalk(long newLastTalk) {
        tag.setLong(LAST_TALK, newLastTalk);
    }

    @Override
    public void setPersonalityName(String personalityName) {
        tag.setString(PERSONALITY_NAME, personalityName);
    }

    @Override
    public void setKillCount(int newKillCount) {
        tag.setInteger(KILL_COUNT, newKillCount);
    }

    @Override
    public void setHitCount(int newHitCount) {
        tag.setInteger(HIT_COUNT, newHitCount);
    }

    @Override
    public void setUseCount(int useCount) {
        tag.setInteger(USAGE_COUNT, useCount);
    }

    @Override
    public int getStrength() {
        if (Enchantment.getEnchantmentByID(tag.getShort("id")) == SoulCraft.FAINT_SOUL_ENCHANTMENT) {
            return FAINT;
        }
        return STRONG;
    }

    @Override
    public void setStrength(int strength) {
        if (strength == FAINT) {
            tag.setShort("id", (short) Enchantment.getEnchantmentID(SoulCraft.FAINT_SOUL_ENCHANTMENT));
        } else {
            tag.setShort("id", (short) Enchantment.getEnchantmentID(SoulCraft.SOUL_ENCHANTMENT));
        }
    }
}
