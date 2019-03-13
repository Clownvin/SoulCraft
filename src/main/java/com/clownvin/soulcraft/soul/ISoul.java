package com.clownvin.soulcraft.soul;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import com.clownvin.soulcraft.personality.Personality;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISoul extends ICapabilitySerializable<NBTTagCompound> {

    String PERSONALITY_NAME = "personalityName";
    String KILL_COUNT = "kills";
    String HIT_COUNT = "hits";
    String USAGE_COUNT = "uses";
    String EXISTS = "exists";
    String STRENGTH = "strength";
    String LAST_TALK = "last_talk";
    String XP = "xp";
    String ASLEEP = "asleep";
    String QUIETED = "quieted";

    int FAINT = 0; //Just talks
    int STRONG = 1; //Can grow stronger (level up)

    class Storage implements Capability.IStorage<ISoul> {
        @Nullable
        @Override
        public NBTTagCompound writeNBT(Capability<ISoul> capability, ISoul instance, EnumFacing side) {
            NBTTagCompound base = new NBTTagCompound();
            base.setBoolean(EXISTS, instance.doesExist());
            if (!instance.doesExist())
                return base;
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
        public void readNBT(Capability<ISoul> capability, ISoul instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setExists(compound.getBoolean(EXISTS));
            if (!instance.doesExist())
                return;
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

    @Override
    default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SoulCraft.SOUL_CAPABILITY;
    }

    @Nullable
    @Override
    default <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    default NBTTagCompound serializeNBT() {
        return (NBTTagCompound) SoulCraft.SOUL_CAPABILITY.getStorage().writeNBT(SoulCraft.SOUL_CAPABILITY, this, null);
    }

    @Override
    default void deserializeNBT(NBTTagCompound nbt) {
        SoulCraft.SOUL_CAPABILITY.getStorage().readNBT(SoulCraft.SOUL_CAPABILITY, this, null, nbt);
    }

    static ISoul getSoul(ItemStack item) {
        if (item.hasCapability(SoulCraft.SOUL_CAPABILITY, null)) {
            return item.getCapability(SoulCraft.SOUL_CAPABILITY, null);
        }
        NBTTagCompound tag = SoulCraft.getSoulEnchantNBT(item);
        if (tag == null) {
            return null;
        }
        return new EnchantmentWrapper(tag);
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
        return getLevel() == SoulCraftConfig.general.maxLevel;
    }

    boolean isAsleep();

    void setSleeping(boolean sleeping);

    boolean isQuieted();

    void setQuieted(boolean quieted);

    boolean doesExist();

    default void destroySoul() {
        setExists(false);
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
        setExists(other.doesExist());
        if (!doesExist())
            return;
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

    void setExists(boolean exists);

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
        setExists(true);
        setStrength(startingStrength);
        setPersonalityName(personalityName);
    }

    double getXP();

    void addXP(double newXP);

    void setXP(double newXP);

    static double xpRequiredForLevel(int lvl) {
        if (SoulCraftConfig.general.xpFunction == 1)
            return (Math.round((4 * (Math.pow(lvl - 1, 3)) / 5)) * SoulCraftConfig.general.levelExpModifier);
        return ((9 * (lvl * lvl) - (4 * lvl)) * SoulCraftConfig.general.levelExpModifier);
    }

    default float getToolEffectivenessModifier() {
        return 1 + (float) (getLevel() * SoulCraftConfig.general.toolEffectivenessPerLevel);
    }

    default float getWeaponEffectivenessModifier() {
        return 1 + (float) (getLevel() * SoulCraftConfig.general.weaponEffectivenessPerLevel);
    }

    static float getArmorEffectivenessModifier(int level, float scale) {
        return 1 + (float) (level * SoulCraftConfig.general.armorEffectivenessPerLEvel * scale);
    }

    default int getLevel() {
        if (isAsleep() || getStrength() <= FAINT) {
            return 0;
        }
        int level;
        if (SoulCraftConfig.general.xpFunction == 1)
            level = (int) Math.pow((5 * (getXP() / SoulCraftConfig.general.levelExpModifier)) / 4, 1 / 3.0f) + 1;
        else
            level = (int) ((Math.sqrt((9 * (getXP() / SoulCraftConfig.general.levelExpModifier)) + 4) + 2) / 9.0);
        return Math.min(level, SoulCraftConfig.general.maxLevel);
    }

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
