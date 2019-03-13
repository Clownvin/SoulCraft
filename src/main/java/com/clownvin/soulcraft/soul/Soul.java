package com.clownvin.soulcraft.soul;

public class Soul implements ISoul {

    public Soul() {

    }

    protected String personalityName = "???";
    protected int killCount;
    protected int hitCount;
    protected int useCount;
    protected int strength;
    protected long lastTalk;
    protected double xp;
    protected boolean exists = false;
    protected boolean asleep = false;
    protected boolean quieted = false;

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
    public boolean doesExist() {
        return exists;
    }

    @Override
    public void setExists(boolean exists) {
        this.exists = true;
    }

    @Override
    public void createSoul(int startingStrength) {
        this.exists = true;
    }

    @Override
    public double getXP() {
        return xp;
    }

    @Override
    public void addXP(double newXP) {
        this.xp += newXP;
    }

    @Override
    public void setXP(double newXP) {
        this.xp = newXP;
    }

    @Override
    public String getPersonalityName() {
        return personalityName;
    }

    @Override
    public int getKillCount() {
        return killCount;
    }

    @Override
    public int getHitCount() {
        return hitCount;
    }

    @Override
    public int getUseCount() {
        return useCount;
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
    public void setPersonalityName(String personalityName) {
        this.personalityName = personalityName;
    }

    @Override
    public void setKillCount(int newKillCount) {
        this.killCount = newKillCount;
    }

    @Override
    public void setHitCount(int newHitCount) {
        this.hitCount = newHitCount;
    }

    @Override
    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    @Override
    public int getStrength() {
        return 0;
    }

    @Override
    public void setStrength(int strength) {
        this.strength = strength;
    }
}
