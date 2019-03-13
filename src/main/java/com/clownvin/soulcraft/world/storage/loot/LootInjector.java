package com.clownvin.soulcraft.world.storage.loot;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class LootInjector {

    private static LootEntry books;
    private static LootEntry armor;
    private static LootEntry uniques;

    public static void init() {
        books = new LootEntryTable(new ResourceLocation(SoulCraft.MOD_ID + ":inject/living_book_loot"), 1, 3, new LootCondition[]{}, "living_book_loot");
        armor = new LootEntryTable(new ResourceLocation(SoulCraft.MOD_ID + ":inject/unique_armor_loot"), 1, 3, new LootCondition[]{}, "unique_armor_loot");
        uniques = new LootEntryTable(new ResourceLocation(SoulCraft.MOD_ID + ":inject/unique_weap_tool_loot"), 1, 3, new LootCondition[]{}, "unique_weap_tool_loot");
    }

    private static LootEntry[] createLoot(int type, int weight) {
        LootEntry empty;
        LootEntry[] loot;
        ResourceLocation emptyLocation = new ResourceLocation(SoulCraft.MOD_ID + ":inject/empty");
        if (type == SoulCraftConfig.JUST_BOOKS) {
            empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
            loot = new LootEntry[]{books, empty};
        } else { //Armor
            if (type == SoulCraftConfig.JUST_UNIQUES) {
                empty = new LootEntryTable(emptyLocation, (weight - 1) * 2, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{armor, uniques, empty};
            } else {
                empty = new LootEntryTable(emptyLocation, (weight - 1) * 3, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{armor, books, uniques, empty};
            }
        }
        return loot;
    }

    @SubscribeEvent
    public static void loadLootEvent(LootTableLoadEvent event) {
        if (SoulCraftConfig.loot.fishingLoot && event.getName().toString().equals("minecraft:gameplay/fishing")) {
            LootPool pool = new LootPool(createLoot(SoulCraftConfig.loot.fishingLootType, SoulCraftConfig.loot.fishingLootChance), new LootCondition[]{}, new RandomValueRange(1, 1), new RandomValueRange(0, 0), "soulcraft_fishing_loot");
            event.getTable().addPool(pool);
        } else if (SoulCraftConfig.loot.chestLoot && event.getName().toString().contains("chests/")) {
            LootPool pool = new LootPool(createLoot(SoulCraftConfig.loot.chestLootType, SoulCraftConfig.loot.chestLootChance), new LootCondition[]{}, new RandomValueRange(1, 2), new RandomValueRange(0, 6), "soulcraft_chest_loot");
            event.getTable().addPool(pool);
        }
    }
}
