package com.clownvin.soulcraft.event;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SoulCraftConfig;
import com.clownvin.soulcraft.soul.ISoul;
import com.clownvin.soulcraft.soul.SoulVessel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(SoulCraft.FAINT_SOUL_ENCHANTMENT, SoulCraft.SOUL_ENCHANTMENT);
    }

    @SubscribeEvent
    public static void attachSouls(AttachCapabilitiesEvent<?> event) {
        if (!(event.getObject() instanceof SoulVessel)) {
            return;
        }
        event.addCapability(SoulCraft.SOUL_RESOURCE_LOCATION, SoulCraft.SOUL_CAPABILITY.getDefaultInstance());
    }
    @SubscribeEvent
    public static void onPlayerPickedUpXP(PlayerPickupXpEvent event) { //Mending style XP
        if (event.getOrb().world.isRemote)
            return;
        if (SoulCraftConfig.general.xpStyle != 0)
            return;
        List<ItemStack> items = SoulCraft.getAllEquipedSoulItems(event.getEntityPlayer());
        ItemStack stack = items.get((int) (Math.random() * items.size()));
        if (stack.isEmpty())
            return;
        ISoul soul = ISoul.getSoul(stack);
        int xp = event.getOrb().xpValue == 1 ? 1 : event.getOrb().xpValue / 2;
        if (SoulCraftConfig.general.xpShare)
            SoulCraft.addExp(event.getEntityPlayer(), xp * SoulCraftConfig.vanillaModifier);
        else
            SoulCraft.addExp(event.getEntityPlayer(), stack, soul, xp * SoulCraftConfig.vanillaModifier);
        event.getOrb().xpValue -= xp;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;
        if (event.getItemStack().getItem() instanceof ItemArmor) {
            int livingLevel = SoulCraft.getArmorsCumulativeSoulLevel(event.getEntityPlayer());
            if (livingLevel != 0) {
                event.getToolTip().add(new TextComponentTranslation("tooltip.currently_worn").getUnformattedText());
                event.getToolTip().add(TextFormatting.BLUE + " " + new TextComponentTranslation("tooltip.damage_reduction", String.format("%.1f", (1 - (1.0F / ISoul.getArmorEffectivenessModifier(livingLevel, 0.25f))) * 100) + "%" + TextFormatting.BLUE).getUnformattedText());
            }
        }
        ISoul soul = ISoul.getSoul(event.getItemStack());
        if (soul == null || !soul.doesExist()) {
            return;
        }
        float multiplier = soul.getWeaponEffectivenessModifier();
        if (!(event.getItemStack().getItem() instanceof ItemArmor) && multiplier != 1) {
            String attackDamageText = new TextComponentTranslation("tooltip.attack_damage").getUnformattedText();
            for (int i = event.getToolTip().size() - 1; i >= 0; i--) {
                if (event.getToolTip().get(i).contains(attackDamageText)) {
                    try {
                        String text = event.getToolTip().get(i).replace(attackDamageText, "").replace(" ", "");
                        text = SoulCraft.removeFormatting(text);
                        float damage = multiplier * Float.parseFloat(text);
                        event.getToolTip().set(i, " " + String.format(damage % 1.0f == 0 ? "%.0f" : "%.1f", damage) + " " + attackDamageText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        int index = 1;
        if (soul.getStrength() > ISoul.FAINT) {
            event.getToolTip().add(index++, TextFormatting.GOLD + new TextComponentTranslation("tooltip.lvl", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + soul.getLevel() + TextFormatting.RESET).getUnformattedText());
            double xp = soul.getXP(), nextLevelXp = ISoul.xpRequiredForLevel(soul.getLevel() + 1);
            event.getToolTip().add(index++, TextFormatting.GOLD + new TextComponentTranslation("tooltip.exp", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + String.format("%.1f", xp) + TextFormatting.RESET + "/" + TextFormatting.GREEN.toString() + String.format("%.1f", nextLevelXp) + TextFormatting.RESET).getUnformattedText());
        }
        int i = soul.getKillCount();
        if (i > 0) {
            event.getToolTip().add(i + new TextComponentTranslation("tooltip.things_killed").getUnformattedText());
        }
        i = soul.getUseCount();
        if (i > 0) {
            Item item = event.getItemStack().getItem();
            if (item instanceof ItemPickaxe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_pickaxed").getUnformattedText());
            else if (item instanceof ItemAxe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_axed").getUnformattedText());
            else if (item instanceof ItemSpade)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_shoveled").getUnformattedText());
            else if (item instanceof ItemHoe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_hoed").getUnformattedText());
            else
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_tooled").getUnformattedText());
        }
        i = soul.getHitCount();
        if (i > 0) {
            event.getToolTip().add(i + new TextComponentTranslation("tooltip.hits_taken").getUnformattedText());
        }
        if (SoulCraftConfig.personalities.showPersonalities) {
            event.getToolTip().add(index++, TextFormatting.GOLD + new TextComponentTranslation("tooltip.personality", TextFormatting.RESET + "" + TextFormatting.GREEN + soul.getPersonalityName()).getUnformattedText());
        }
        index = 1;
        if (soul.isAsleep()) {
            event.getToolTip().add(index++, TextFormatting.BOLD + "" + TextFormatting.ITALIC + "" + TextFormatting.DARK_GRAY + new TextComponentTranslation("tooltip.sleeping").getFormattedText());
        }
        if (soul.isQuieted()) {
            event.getToolTip().add(index++, TextFormatting.ITALIC + "" + TextFormatting.DARK_GRAY + new TextComponentTranslation("tooltip.quieted").getFormattedText());
        }
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;
        if (SoulCraftConfig.personalities.showDialogue && event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().getHeldItemMainhand().isItemEnchanted()) {
            ISoul soul = ISoul.getSoul(event.getEntityLiving().getHeldItemMainhand());
            if (soul != null && SoulCraftConfig.personalities.showDialogue) {
                SoulCraft.talk((EntityPlayer) event.getEntityLiving(), event.getEntityLiving().getHeldItemMainhand(), soul, soul.getPersonality().getOnDeath());
            }
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ISoul soul = null;
        if (event.getSource().damageType.equals("arrow") && player.getHeldItemOffhand().getItem() instanceof ItemBow)
            soul = ISoul.getSoul(player.getHeldItemOffhand());
        if (soul == null)
            soul = ISoul.getSoul(player.getHeldItemMainhand());
        if (soul != null)
            soul.setKillCount(soul.getKillCount() + 1);
        List<ItemStack> items = SoulCraft.getAllEquipedSoulItems(player);
        if (items.isEmpty()) {
            return;
        }
        ItemStack item = items.get((int) (Math.random() * items.size()));
        SoulCraft.doExpDrop(player, event.getEntityLiving().getPosition(), SoulCraftConfig.getXPForLiving(event.getEntityLiving()));
        SoulCraft.doTalking(player, item, ISoul.getSoul(item), event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        int targetLivingLevel = SoulCraft.getArmorsCumulativeSoulLevel(event.getEntityLiving());
        if (targetLivingLevel > 0) {
            event.setAmount(event.getAmount() * (1.0F / ISoul.getArmorEffectivenessModifier(targetLivingLevel, 0.25f)));
            SoulCraft.addHitCount(event.getEntityLiving());
            if (event.getEntityLiving() instanceof EntityPlayer) {
                List<ItemStack> items = SoulCraft.getAllEquipedSoulItems(event.getEntityLiving());
                ItemStack item = items.get((int) (Math.random() * items.size()));
                SoulCraft.doTalking((EntityPlayer) event.getEntityLiving(), item, ISoul.getSoul(item), event);
            }
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack weapon = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(weapon);
        if (soul == null || !soul.doesExist() || soul.isAsleep())
            return;
        float multiplier = soul.getWeaponEffectivenessModifier();
        event.setAmount(event.getAmount() * multiplier);
        SoulCraft.doTalking(player, weapon, soul, event);
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ISoul input = ISoul.getSoul(event.getItemInput());
        ISoul output = ISoul.getSoul(event.getItemResult());
        if (input == null || output == null)
            return;
        output.copySoul(input);
    }

    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event) {
        if (event.getWorld().isRemote)
            return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || !soul.doesExist() || soul.isAsleep())
            return;
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (!(block instanceof BlockGrass) && block != Blocks.DIRT)
            return;
        soul.setUseCount(soul.getUseCount() + 1);
        SoulCraft.doExpDrop(player, event.getPos(), 1);
        SoulCraft.doTalking(player, heldItem, soul, event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote)
            return;
        EntityPlayer player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || !soul.doesExist() || soul.isAsleep())
            return;
        if (!SoulCraft.isToolEffective(heldItem, event.getState()))
            return;
        soul.setUseCount(soul.getUseCount() + 1);
        SoulCraft.doExpDrop(player, event.getPos(), SoulCraftConfig.getXPForBlock(player.world, event.getPos(), event.getState()));
        SoulCraft.doTalking(player, heldItem, soul, event);
    }

    @SubscribeEvent
    public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.getEntityPlayer().getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || !soul.doesExist() || soul.isAsleep())
            return;
        if (!SoulCraftConfig.general.effectivenessAffectsAllBlocks && !SoulCraft.isToolEffective(heldItem, event.getState()))
            return;
        float multiplier = soul.getToolEffectivenessModifier();
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

}
