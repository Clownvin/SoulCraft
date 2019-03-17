package com.clownvin.soulcraft.event;

import com.clownvin.soulcraft.SoulCraft;
import com.clownvin.soulcraft.config.SCConfig;
import com.clownvin.soulcraft.soul.ISoul;
import com.clownvin.soulcraft.soul.SoulVessel;

import com.clownvin.soulcraft.world.gen.SoulbellDecorator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
    public static void attachSoulsItems(AttachCapabilitiesEvent<ItemStack> event) {
        if (!(event.getObject().getItem() instanceof SoulVessel)) {
            return;
        }
        event.addCapability(SoulCraft.ITEM_SOUL_RESOURCE_LOCATION, SoulCraft.ITEM_SOUL_CAPABILITY.getDefaultInstance());
    }

    @SubscribeEvent
    public static void attachSoulsEntities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityMob) {
            event.addCapability(SoulCraft.MOB_SOUL_RESOURCE_LOCATION, SoulCraft.MOB_SOUL_CAPABILITY.getDefaultInstance());
            return;
        }
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SoulCraft.PLAYER_SOUL_RESOURCE_LOCATION, SoulCraft.PLAYER_SOUL_CAPABILITY.getDefaultInstance());
            return;
        }
        if (event.getObject() instanceof EntityAnimal) {
            event.addCapability(SoulCraft.ANIMAL_SOUL_RESOURCE_LOCATION, SoulCraft.ANIMAL_SOUL_CAPABILITY.getDefaultInstance());
            return;
        }
    }

    @SubscribeEvent
    public static void onPlayerPickedUpXP(PlayerPickupXpEvent event) { //Mending style XP
        if (event.getOrb().world.isRemote)
            return;
        if (SCConfig.general.xpStyle != 0)
            return;
        double  xp = event.getOrb().xpValue == 1 ? 1.0D : event.getOrb().xpValue / 2.0D;
        event.getOrb().xpValue -= SoulCraft.addXP(event.getEntityPlayer(), xp);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;
        if (event.getItemStack().getItem() instanceof ItemArmor) {
            double damageReduction = SoulCraft.getIncomingDamageModifiers(event.getEntityPlayer());
            if (damageReduction > 0) {
                event.getToolTip().add(new TextComponentTranslation("tooltip.current_damage_reduction").getUnformattedText());
                event.getToolTip().add(TextFormatting.BLUE + " " + new TextComponentTranslation("tooltip.damage_reduction", String.format("%.1f", (1 - (1.0F / damageReduction)) * 100) + "%" + TextFormatting.BLUE).getUnformattedText());
            }
        }
        double multiplier = SoulCraft.getOutgoingDamageModifiers(event.getEntityPlayer());
        if (!(event.getItemStack().getItem() instanceof ItemArmor) && multiplier != 1) {
            String attackDamageText = new TextComponentTranslation("attribute.name.generic.attackDamage").getUnformattedText();
            for (int i = event.getToolTip().size() - 1; i >= 0; i--) {
                if (event.getToolTip().get(i).contains(attackDamageText)) {
                    try {
                        String text = event.getToolTip().get(i).replace(attackDamageText, "").replace(" ", "");
                        text = SoulCraft.removeFormatting(text);
                        double damage = multiplier * Float.parseFloat(text);
                        event.getToolTip().set(i, " " + String.format(damage % 1.0f == 0 ? "%.0f" : "%.1f", damage) + " " + attackDamageText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        ISoul soul = ISoul.getSoul(event.getItemStack());
        if (soul == null) {
            return;
        }
        int index = 1;
        if (soul.getStrength() > ISoul.FAINT) {
            event.getToolTip().add(index++, TextFormatting.GOLD + new TextComponentTranslation("tooltip.lvl", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + soul.getLevel() + TextFormatting.RESET).getUnformattedText());
            double xp = soul.getXP(), nextLevelXp = ISoul.xpRequiredForLevel(soul.getLevel() + 1, SCConfig.souls.items.levelXPModifier);
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
        if (SCConfig.personalities.showPersonalities) {
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
        if (event.getEntityLiving() instanceof EntityPlayer) {
            ItemStack item = SoulCraft.getRandomEquippedSoulItem(event.getEntityLiving());
            if (item != null) {
                SoulCraft.doTalking((EntityPlayer) event.getEntityLiving(), item, ISoul.getSoul(item), event);
            }
        }
        SoulCraft.addKillCount(event.getSource().getTrueSource());
        SoulCraft.doExpDrop(event.getSource().getTrueSource(), event.getEntityLiving().getPosition(), SCConfig.getXPForLiving(event.getEntityLiving()));
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase living = (EntityLivingBase) event.getSource().getTrueSource();
        ISoul soul = null;
        if (event.getSource().damageType.equals("arrow") && living.getHeldItemOffhand().getItem() instanceof ItemBow)
            soul = ISoul.getSoul(living.getHeldItemOffhand());
        if (soul == null)
            soul = ISoul.getSoul(living.getHeldItemMainhand());
        if (soul != null)
            soul.setKillCount(soul.getKillCount() + 1);
        if (!(living instanceof EntityPlayer)) {
            return;
        }
        ItemStack item = SoulCraft.getRandomEquippedSoulItem(living);
        if (item == null) {
            return;
        }
        SoulCraft.doTalking((EntityPlayer) living, item, ISoul.getSoul(item), event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        double damageMod = SoulCraft.getIncomingDamageModifiers(event.getEntityLiving());
        event.setAmount((float) (event.getAmount() / damageMod));
        SoulCraft.addHitCount(event.getEntityLiving());
        if (event.getEntityLiving() instanceof EntityPlayer) {
            ItemStack item = SoulCraft.getRandomEquippedSoulItem(event.getEntityLiving());
            if (item != null) {
                SoulCraft.doTalking((EntityPlayer) event.getEntityLiving(), item, ISoul.getSoul(item), event);
            }
        }
        double multiplier = SoulCraft.getOutgoingDamageModifiers(event.getSource().getTrueSource());
        event.setAmount((float) (event.getAmount() * multiplier * SCConfig.general.globalDamageModifier));
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack weapon = player.getHeldItemMainhand();
        SoulCraft.doTalking(player, weapon, ISoul.getSoul(weapon), event);
    }

    @SubscribeEvent
    public static void onPopulateChunkPost(PopulateChunkEvent.Post event) {
        System.out.println("Decorating biome event for: "+event.getWorld().provider.getDimension());
        new SoulbellDecorator().generate(event.getWorld(), new ChunkPos(event.getChunkX(), event.getChunkZ()), event.getRand());
    }


    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ISoul input = ISoul.getSoul(event.getLeft());
        ISoul output = ISoul.getSoul(event.getOutput());
        if (input == null || output == null)
            return;
        output.copySoul(input);
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
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (!(block instanceof BlockGrass) && block != Blocks.DIRT)
            return;
        SoulCraft.addUseCount(event.getEntityLiving());
        SoulCraft.doExpDrop(event.getEntityLiving(), event.getPos(), 1.0D);
        ItemStack heldItem = event.getEntityLiving().getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || soul.isAsleep())
            return;
        SoulCraft.doTalking(event.getEntityPlayer(), heldItem, soul, event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote)
            return;
        EntityPlayer player = event.getPlayer();
        if (!player.canHarvestBlock(event.getState())) {
            return;
        }
        SoulCraft.addUseCount(player);
        SoulCraft.doExpDrop(player, event.getPos(), SCConfig.getXPForBlock(player.world, event.getPos(), event.getState()));
        ItemStack heldItem = player.getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || soul.isAsleep())
            return;
        SoulCraft.doTalking(player, heldItem, soul, event);
    }

    @SubscribeEvent
    public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
        event.setNewSpeed((float) (event.getNewSpeed() * SCConfig.general.globalMiningSpeedModifier));
        if (!SCConfig.general.effectivenessAffectsAllBlocks && !event.getEntityPlayer().canHarvestBlock(event.getState())) {
            return;
        }
        event.setNewSpeed((float) (event.getNewSpeed() * SoulCraft.getBreakSpeedModifiers(event.getEntityPlayer())));
        /*
        ItemStack heldItem = event.getEntityPlayer().getHeldItemMainhand();
        ISoul soul = ISoul.getSoul(heldItem);
        if (soul == null || soul.isAsleep())
            return;
        if (!SCConfig.general.effectivenessAffectsAllBlocks && !SoulCraft.isToolEffective(heldItem, event.getState()))
            return;
        float multiplier = soul.getBreakSpeedModifier();
        event.setNewSpeed(event.getNewSpeed() * multiplier);*/
    }

}
