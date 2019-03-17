package com.clownvin.soulcraft.soul;

public abstract class Soul implements ISoul {

    protected String personalityName = "???";
    protected int killCount;
    protected int hitCount;
    protected int useCount;
    protected double xp;

    @Override
    public double getXP() {
        return xp;
    }

    @Override
    public void addXP(double newXP) {
        xp += newXP;
    }

    @Override
    public void setXP(double newXP) {
        xp = newXP;
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
    public void setPersonalityName(String personalityName) {
        this.personalityName = personalityName;
    }

    @Override
    public void setKillCount(int newKillCount) {
        killCount = newKillCount;
    }

    @Override
    public void setHitCount(int newHitCount) {
        hitCount = newHitCount;
    }

    @Override
    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    /*

    Defaults from here on. Override as needed

    */

    @Override
    public boolean isAsleep() {
        return false; //Never sleeps
    }

    @Override
    public void setSleeping(boolean sleeping) {
        //Never sleeps
    }

    @Override
    public boolean isQuieted() {
        return true; //Personality doesn't exist
    }

    @Override
    public void setQuieted(boolean quieted) {
        //Personality doesn't exist
    }

    @Override
    public long getLastTalk() {
        return 0; //Doesn't talk.
    }

    @Override
    public void setLastTalk(long newLastTalk) {
        //Doesn't talk.
    }
}
