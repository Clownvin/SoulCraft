package com.clownvin.soulcraft.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class SCBlocks {
    public static Block BLOCK_SOULBELL;

    private static Block[] blocks;

    public static void init() {
        blocks = new Block[] {BLOCK_SOULBELL = new BlockSoulbell()};
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(blocks);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        //System.out.println("Registering block models...");
        for (Block block : blocks) {
            //block.setRegistryName(EarthenBounty.MODID, block.getUnlocalizedName());
            Item item = Item.getItemFromBlock(block);
            //System.out.println("Registering block model with registry name: "+item.getRegistryName());
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        //System.out.println("Finished registering block models");
    }
}
