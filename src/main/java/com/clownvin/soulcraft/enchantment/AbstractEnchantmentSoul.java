package com.clownvin.soulcraft.enchantment;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;

public abstract class AbstractEnchantmentSoul extends Enchantment {

    protected final boolean treasure;
    protected final int minEnchantability;
    protected final int maxEnchantability;

    protected AbstractEnchantmentSoul(String registryName, Enchantment.Rarity rarity, boolean treasure, int minEnchantability, int maxEnchantability) {
        super(rarity, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS});
        this.treasure = treasure;
        this.minEnchantability = minEnchantability;
        this.maxEnchantability = maxEnchantability;
        this.setName(registryName);
        this.setRegistryName(SoulCraft.MOD_ID, getName());
    }

    @Override
    public boolean canApply(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemSword || item instanceof ItemBow;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return treasure;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return minEnchantability;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return maxEnchantability;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench == null)
            return true;
        if (ench instanceof EnchantmentSoul) //Cannot have multiple souls, lol...
            return false;
        if (SoulCraftConfig.general.allowDamageEnchantments)
            return true;
        if (ench instanceof EnchantmentArrowDamage || ench instanceof EnchantmentDamage || ench instanceof EnchantmentDigging || ench instanceof EnchantmentProtection)
            return false;
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Enchantment)) {
            return false;
        }
        return ((Enchantment)other).getRegistryName().equals(getRegistryName());
    }
}
