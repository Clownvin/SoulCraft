package com.clownvin.soulcraft.proxy;

import com.clownvin.soulcraft.block.SCBlocks;
import com.clownvin.soulcraft.item.SCItems;
import com.clownvin.soulcraft.soul.*;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(ItemSoul.class, new SoulStorage<>(), ItemSoul::new);
        CapabilityManager.INSTANCE.register(PlayerSoul.class, new SoulStorage<>(), PlayerSoul::new);
        CapabilityManager.INSTANCE.register(AnimalSoul.class, new SoulStorage<>(), AnimalSoul::new);
        CapabilityManager.INSTANCE.register(MobSoul.class, new SoulStorage<>(), MobSoul::new);
        SCBlocks.init();
        SCItems.init();
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}