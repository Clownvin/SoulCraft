package com.clownvin.soulcraft.proxy;

import com.clownvin.soulcraft.soul.ISoul;
import com.clownvin.soulcraft.soul.Soul;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ISoul.class, new ISoul.Storage(), Soul::new);
    }

    public void init(FMLInitializationEvent event) {
        //Crafting
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}