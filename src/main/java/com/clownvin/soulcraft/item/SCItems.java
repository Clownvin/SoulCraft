package com.clownvin.soulcraft.item;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.block.SCBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class SCItems {

    public static Item ITEM_SOULBELL;

    private static Item[] items;

    public static void init() {
        items =new Item[] {ITEM_SOULBELL = new ItemBlock(SCBlocks.BLOCK_SOULBELL).setUnlocalizedName(SCBlocks.BLOCK_SOULBELL.getUnlocalizedName()).setRegistryName(SoulCraft.MOD_ID, "soulbell").setCreativeTab(CreativeTabs.DECORATIONS)};
    }

    @SubscribeEvent
    public static void registerItemsEvent(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(items);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        //System.out.println("Registering models for items...");
        for (Item item : items) {
            //item.setRegistryName(EarthenBounty.MODID, item.getUnlocalizedName());
            //System.out.println("Registering model for item with registry name: "+item.getRegistryName());
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        //System.out.println("Finished registering models for items");
    }
}
