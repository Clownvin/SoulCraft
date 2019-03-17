package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import com.clownvin.soulcraft.personality.Personality;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISoul {

    String PERSONALITY_NAME = "personalityName";
    String KILL_COUNT = "kills";
    String HIT_COUNT = "hits";
    String USAGE_COUNT = "uses";
    String STRENGTH = "strength";
    String LAST_TALK = "last_talk";
    String XP = "xp";
    String ASLEEP = "asleep";
    String QUIETED = "quieted";

    int FAINT = 0; //Just talks
    int STRONG = 1; //Can grow stronger (level up)

    static ISoul getSoul(Object object) {
        if (object instanceof EntityAnimal) {
            return ((EntityAnimal) object).getCapability(SoulCraft.ANIMAL_SOUL_CAPABILITY, null);
        }
        if (object instanceof EntityPlayer) {
            return ((EntityPlayer) object).getCapability(SoulCraft.PLAYER_SOUL_CAPABILITY, null);
        }
        if (object instanceof EntityMob) {
            return ((EntityMob) object).getCapability(SoulCraft.MOB_SOUL_CAPABILITY, null);
        }
        if (object instanceof ItemStack) {
            ItemStack item = (ItemStack) object;
            if (item.hasCapability(SoulCraft.ITEM_SOUL_CAPABILITY, null)) {
                return item.getCapability(SoulCraft.ITEM_SOUL_CAPABILITY, null);
            }
            NBTTagCompound tag = SoulCraft.getSoulEnchantNBT(item);
            if (tag == null) {
                return null;
            }
            return new EnchantmentWrapper(tag);
        }
        return null;
    }

    default Personality getPersonality() {
        Personality personality = Personality.getPersonality(getPersonalityName());
        if (personality == null) {
            personality = Personality.getPersonality();
            setPersonalityName(personality.name);
        }
        return personality;
    }

    default boolean isMaxLevel() {
        return getLevel() == SCConfig.general.maxLevel;
    }

    boolean isAsleep();

    void setSleeping(boolean sleeping);

    boolean isQuieted();

    void setQuieted(boolean quieted);

    default void resetSoul() {
        setSleeping(false);
        setQuieted(false);
        setPersonalityName("???");
        setKillCount(0);
        setHitCount(0);
        setUseCount(0);
        setStrength(0);
        setXP(0);
    }

    default void copySoul(ISoul other) {
        setPersonalityName(other.getPersonalityName());
        setKillCount(other.getKillCount());
        setUseCount(other.getUseCount());
        setHitCount(other.getHitCount());
        setStrength(other.getStrength());
        setQuieted(other.isQuieted());
        setSleeping(other.isAsleep());
        if (getStrength() > FAINT)
            setXP(other.getXP());
    }

    default void createSoul() {
        createSoul(0);
    }

    default void createSoul(int startingStrength) {
        createSoul(startingStrength, Personality.getPersonality().name);
    }

    default void createSoul(String personalityName) {
        createSoul(0, personalityName);
    }

    default void createSoul(int startingStrength, String personalityName) {
        setStrength(startingStrength);
        setPersonalityName(personalityName);
    }

    double getXP();

    void addXP(double newXP);

    void setXP(double newXP);

    static double xpRequiredForLevel(int lvl, double xpMod) {
        if (SCConfig.general.xpFunction == 1)
            return (Math.round((4 * (Math.pow(lvl - 1, 3)) / 5)) * xpMod);
        return ((9 * (lvl * lvl) - (4 * lvl)) * xpMod);
    }

    default double getBreakSpeedModifier() {
        return getLevel() * getToolEffectivenessMod();
    }

    default double getOutgoingDamageModifier() {
        return getLevel() * getWeaponEffectivenessMod();
    }

    default double getIncommingDamageModifier() {
        return getLevel() * getArmorEffectivenessMod();
    }

    default int getLevel() {
        if (isAsleep() || getStrength() <= FAINT) {
            return 0;
        }
        int level;
        if (SCConfig.general.xpFunction == 1)
            level = (int) Math.pow((5 * (getXP() / getLevelXPMod())) / 4, 1 / 3.0f) + 1;
        else
            level = (int) ((Math.sqrt((9 * (getXP() / getLevelXPMod())) + 4) + 2) / 9.0);
        return Math.min(level, SCConfig.general.maxLevel);
    }

    double getLevelXPMod();

    double getToolEffectivenessMod();

    double getWeaponEffectivenessMod();

    double getArmorEffectivenessMod();

    String getPersonalityName();

    int getKillCount();

    int getHitCount();

    int getUseCount();

    long getLastTalk();

    void setLastTalk(long newLastTalk);

    void setPersonalityName(String personalityName);

    void setKillCount(int newKillCount);

    void setHitCount(int newHitCount);

    void setUseCount(int useCount);

    int getStrength();

    void setStrength(int strength);
}
