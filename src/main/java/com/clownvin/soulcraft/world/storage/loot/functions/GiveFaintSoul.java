package com.clownvin.soulcraft.world.storage.loot.functions;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

public class GiveFaintSoul extends GiveSoul {

    public GiveFaintSoul(LootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        if (stack.getItem() == Items.BOOK) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(SoulCraft.FAINT_SOUL_ENCHANTMENT, 1));
        } else {
            stack.addEnchantment(SoulCraft.FAINT_SOUL_ENCHANTMENT, 1);
        }
        if (stack.getMaxDamage() >= 2)
            stack.setItemDamage(rand.nextInt((stack.getMaxDamage() / 2)));
        if (stack.getItem() instanceof ItemAxe)
            stack.setStackDisplayName(AXE_NAMES[rand.nextInt(AXE_NAMES.length)]);
        if (stack.getItem() instanceof ItemSword)
            stack.setStackDisplayName(SWORD_NAMES[rand.nextInt(SWORD_NAMES.length)]);
        if (stack.getItem() instanceof ItemPickaxe)
            stack.setStackDisplayName(PICKAXE_NAMES[rand.nextInt(PICKAXE_NAMES.length)]);
        if (stack.getItem() instanceof ItemSpade)
            stack.setStackDisplayName(SHOVEL_NAMES[rand.nextInt(SHOVEL_NAMES.length)]);
        if (stack.getItem() instanceof ItemBow)
            stack.setStackDisplayName(BOW_NAMES[rand.nextInt(BOW_NAMES.length)]);
        if (stack.getItem() instanceof ItemHoe && SoulCraftConfig.general.hoeNames)
            stack.setStackDisplayName(HOE_NAMES[rand.nextInt(HOE_NAMES.length)]);
        SoulCraft.resetItem(stack);
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<GiveFaintSoul> {
        public Serializer() {
            super(new ResourceLocation(SoulCraft.MOD_ID, "give_faint_soul"), GiveFaintSoul.class);
        }

        public void serialize(JsonObject object, GiveFaintSoul functionClazz, JsonSerializationContext serializationContext) {

        }

        public GiveFaintSoul deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            return new GiveFaintSoul(conditionsIn);
        }
    }
}
