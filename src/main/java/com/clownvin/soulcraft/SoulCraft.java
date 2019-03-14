package com.clownvin.soulcraft;

import com.clownvin.soulcraft.config.SoulCraftConfig;
import com.clownvin.soulcraft.enchantment.EnchantmentFaintSoul;
import com.clownvin.soulcraft.enchantment.EnchantmentSoul;
import com.clownvin.soulcraft.entity.item.EntitySoulXPOrb;
import com.clownvin.soulcraft.personality.Personality;
import com.clownvin.soulcraft.proxy.CommonProxy;
import com.clownvin.soulcraft.soul.ISoul;
import com.clownvin.soulcraft.soul.Soul;
import com.clownvin.soulcraft.world.storage.loot.LootInjector;
import com.clownvin.soulcraft.world.storage.loot.functions.GiveFaintSoul;
import com.clownvin.soulcraft.world.storage.loot.functions.GiveSoul;
import com.clownvin.soulcraft.command.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod(name = SoulCraft.MOD_NAME, modid = SoulCraft.MOD_ID, version = SoulCraft.MOD_VERSION, updateJSON = "https://raw.githubusercontent.com/Clownvin/SoulCraft/1.12.2/update.json")
@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID)
public class SoulCraft {

    public static final String MOD_ID = "soulcraft";
    public static final String MOD_VERSION = "1.0.0";
    public static final String MOD_NAME = "SoulCraft";

    @CapabilityInject(ISoul.class)
    public static final Capability<ISoul> SOUL_CAPABILITY = null;
    public static final ResourceLocation SOUL_RESOURCE_LOCATION = new ResourceLocation(MOD_ID, "soul_capability");

    public static EnchantmentFaintSoul FAINT_SOUL_ENCHANTMENT;
    public static EnchantmentSoul SOUL_ENCHANTMENT;

    @Mod.Instance
    public static SoulCraft instance;

    @SidedProxy(clientSide = "com.clownvin.soulcraft.proxy.ClientProxy", serverSide = "com.clownvin.soulcraft.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static int getArmorsCumulativeSoulLevel(EntityLivingBase entity) {
        int cumulativeLivingLevel = 0;
        List<ItemStack> gear = getAllEquipedSoulItems(entity, (soul) -> soul.doesExist() && !soul.isAsleep() && soul.getStrength() > ISoul.FAINT);
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ItemArmor))
                continue;
            cumulativeLivingLevel += ISoul.getSoul(stack).getLevel();
        }
        return cumulativeLivingLevel;
    }

    public static void addHitCount(EntityLivingBase entity) {
        List<ItemStack> gear = getAllEquipedSoulItems(entity);
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ItemArmor))
                continue;
            ISoul soul = ISoul.getSoul(stack);
            soul.setHitCount(soul.getHitCount() + 1);
        }
    }

    public static String removeFormatting(String textIn) {
        String text = textIn;
        while (text.contains("§")) {
            int index = text.indexOf("§");
            text = text.replace(text.substring(index, index + 2), "");
        }
        return text;
    }

    public static NBTTagCompound getSoulEnchantNBT(ItemStack stack) {
        NBTTagCompound tag = null;
        for (NBTBase base : stack.getEnchantmentTagList()) {
            Enchantment enchantment = Enchantment.getEnchantmentByID(((NBTTagCompound) base).getShort("id"));
            if (enchantment != FAINT_SOUL_ENCHANTMENT && enchantment != SOUL_ENCHANTMENT)
                continue;
            tag = (NBTTagCompound) base;
            break;
        }
        return tag;
    }

    public static void doTalking(EntityPlayer player, ItemStack item, ISoul soul, Event reason) {
        if (!SoulCraftConfig.personalities.showDialogue || soul.isQuieted() || soul.isAsleep())
            return;
        Personality personality = soul.getPersonality();
        float damagePercent = item.getItemDamage() / (float) item.getMaxDamage();
        if (damagePercent >= 0.90f) {
            talk(player, item, soul, personality.getFivePercent(), 3000);
        } else if (damagePercent >= 0.75f) {
            talk(player, item, soul, personality.getTwentyPercent(), 3000);
        }
        if (reason instanceof LivingDeathEvent && Math.random() * personality.killOdds <= 1.0) {
            if (((LivingDeathEvent) reason).getEntityLiving().equals(player))
                talk(player, item, soul, personality.getOnDeath());
            else
                talk(player, item, soul, personality.getOnKill());
        } else if ((reason instanceof BlockEvent.BreakEvent || reason instanceof UseHoeEvent) && Math.random() * personality.useOdds <= 1.0) {
            talk(player, item, soul, personality.getOnUse());
        } else if (reason instanceof LivingHurtEvent && Math.random() * personality.hurtOdds <= 1.0) {
            Entity source = ((LivingHurtEvent) reason).getSource().getTrueSource();
            if (source != null && source.equals(player))
                talk(player, item, soul, personality.getOnTargetHurt());
            //else send ouch message
        }
    }

    public static void talk(EntityPlayer player, ItemStack stack, ISoul soul, String message) {
        talk(player, stack, soul, message, SoulCraftConfig.personalities.minimumDialogueDelay);
    }

    public static void talk(EntityPlayer player, ItemStack stack, ISoul soul, String message, int minimumDialogueDelay) {
        if (!SoulCraftConfig.personalities.showDialogue) {
            return;
        }
        if (System.currentTimeMillis() - soul.getLastTalk() < minimumDialogueDelay) {
            if (soul.getLastTalk() > System.currentTimeMillis())
                 soul.setLastTalk(System.currentTimeMillis());
            return;
        }
        soul.setLastTalk(System.currentTimeMillis());
        float durability = (1.0f - (stack.getItemDamage() / (float) stack.getMaxDamage())) * 100.0f;
        message = message.replace("$user", player.getName()).replace("$level", "" + soul.getLevel()).replace("$durability", String.format("%.1f", durability) + "%");
        player.sendMessage(new TextComponentString(stack.getDisplayName() + ": " + message));
    }

    public static void resetItem(ItemStack item) {
        ISoul soul = ISoul.getSoul(item);
        if (soul == null) {
            return; // It's not enchanted..
        }
        soul.destroySoul();
    }

    public static List<ItemStack> getAllEquipedSoulItems(EntityLivingBase entity) {
        return getAllEquipedSoulItems(entity, (soul) -> soul.doesExist() && !soul.isAsleep());
    }

    public static List<ItemStack> getAllEquipedSoulItems(EntityLivingBase entity, Predicate<ISoul> filter) {
        List<ItemStack> items = new ArrayList<>(6);
        for (ItemStack i : entity.getEquipmentAndArmor()) {
            ISoul soul;
            if ((soul = ISoul.getSoul(i)) != null && filter.test(soul))
                items.add(i);
        }
        return items;
    }

    public static boolean isToolEffective(ItemStack item, IBlockState state) {
        return item.getItem().getToolClasses(item).contains(state.getBlock().getHarvestTool(state));
    }

    public static void doExpDrop(EntityPlayer player, BlockPos pos, double exp) {
        if (SoulCraftConfig.general.xpStyle == 0)
            return;
        if (SoulCraftConfig.general.xpStyle == 2) {
            player.world.spawnEntity(new EntitySoulXPOrb(player.world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, exp));
        } else if (SoulCraftConfig.general.xpStyle == 1) {
            if (SoulCraftConfig.general.xpShare)
                addExp(player, exp);
            else {
                List<ItemStack> items = getAllEquipedSoulItems(player);
                ItemStack item = items.get((int) (Math.random() * items.size()));
                addExp(player, item, ISoul.getSoul(item), exp);
            }
        }
    }

    public static void addExp(EntityPlayer player, double exp) { //Adds EXP to all
        List<ItemStack> items = getAllEquipedSoulItems(player);
        for (ItemStack stack : items) {
            addExp(player, stack, ISoul.getSoul(stack), exp / items.size());
        }
    }

    public static void addExp(EntityPlayer player, ItemStack stack, ISoul soul, double exp) {
        //System.out.println(stack.getTagCompound());
        if (soul.getStrength() == ISoul.FAINT) {
            if ((Math.random() * 1000) < exp) {
                soul.setStrength(ISoul.STRONG);
                player.sendMessage(new TextComponentTranslation("text.soul_gained_strength", stack.getDisplayName()));
            } else {
                return;
            }
        }
        int currLevel = soul.getLevel();
        soul.addXP(exp);
        int newLevel = soul.getLevel();
        if (newLevel == currLevel)
            return;
        Personality personality = soul.getPersonality();
        player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 0.75F, 0.9F + (float) (Math.random() * 0.2F));
        if (SoulCraftConfig.personalities.showDialogue)
            talk(player, stack, soul, personality.getOnLevelUp(), 0);
    }

    public static void setExp(EntityPlayer player, ItemStack stack, ISoul soul, double exp) {
        soul.setXP(0);
        addExp(player, stack, soul, exp);
    }

    @EventHandler
    public static void serverStartingEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAddItemXP());
        event.registerServerCommand(new CommandSetItemXP());
        event.registerServerCommand(new CommandSetItemLevel());
        event.registerServerCommand(new CommandResetItem());
        event.registerServerCommand(new CommandSetPersonality());
        event.registerServerCommand(new CommandPutItemToSleep());
        event.registerServerCommand(new CommandQuietItem());
        event.registerServerCommand(new CommandUnquietItem());
        event.registerServerCommand(new CommandWakeupItem());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FAINT_SOUL_ENCHANTMENT = new EnchantmentFaintSoul();
        SOUL_ENCHANTMENT = new EnchantmentSoul();
        Personality.HEROBRINE = new Personality(0, "Herobrine",
                new String[]{ //Use
                        "Herobrine"
                },
                10,
                new String[]{ //Kill
                        "Herobrine"
                },
                10,
                new String[]{ //Death
                        "Herobrine"
                },
                new String[]{ //Level Up
                        "Herobrine"
                },
                new String[]{ //On Hurt
                        "Herobrine"
                },
                10,
                new String[]{ //Twenty percent
                        "Herobrine ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Herobrine ($durability durability remaining)",
                });
        if (Loader.isModLoaded("enderio")) {
            SoulCraftConfig.createEnderIOEnchantRecipe();
        }
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "soulXPOrb"), EntitySoulXPOrb.class, "soulXPOrb", 0, instance, 255, 3, true);
        LootInjector.init();
        LootFunctionManager.registerFunction(new GiveSoul.Serializer());
        LootFunctionManager.registerFunction(new GiveFaintSoul.Serializer());
        proxy.init(event);
        Blocks.BRICK_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.END_STONE.setHarvestLevel("pickaxe", 1);
        Blocks.STICKY_PISTON.setHarvestLevel("pickaxe", 0);
        Blocks.PISTON.setHarvestLevel("pickaxe", 0);
        Blocks.FURNACE.setHarvestLevel("pickaxe", 0);
        Blocks.LIT_FURNACE.setHarvestLevel("pickaxe", 0);
        Blocks.STANDING_SIGN.setHarvestLevel("axe", 0);
        Blocks.STONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_DOOR.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_FENCE.setHarvestLevel("axe", 0);
        Blocks.BIRCH_FENCE.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_FENCE.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_FENCE.setHarvestLevel("axe", 0);
        Blocks.NETHER_BRICK_FENCE.setHarvestLevel("pickaxe", 0);
        Blocks.OAK_FENCE.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_FENCE.setHarvestLevel("axe", 0);
        Blocks.GLOWSTONE.setHarvestLevel("pickaxe", 0);
        Blocks.TRAPDOOR.setHarvestLevel("axe", 0);
        Blocks.STONEBRICK.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_BARS.setHarvestLevel("pickaxe", 1);
        Blocks.STONE_BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.NETHER_BRICK.setHarvestLevel("pickaxe", 1);
        Blocks.NETHER_BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.WOODEN_SLAB.setHarvestLevel("axe", 0);
        Blocks.DOUBLE_WOODEN_SLAB.setHarvestLevel("axe", 0);
        Blocks.SANDSTONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_STAIRS.setHarvestLevel("axe", 0);
        Blocks.COBBLESTONE_WALL.setHarvestLevel("pickaxe", 1);
        Blocks.ANVIL.setHarvestLevel("pickaxe", 1);
        Blocks.REDSTONE_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.HOPPER.setHarvestLevel("pickaxe", 1);
        Blocks.QUARTZ_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.QUARTZ_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.DROPPER.setHarvestLevel("pickaxe", 1);
        Blocks.DISPENSER.setHarvestLevel("pickaxe", 1);
        Blocks.COAL_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.OAK_STAIRS.setHarvestLevel("axe", 0);
        Blocks.CRAFTING_TABLE.setHarvestLevel("axe", 0);
        Blocks.ACACIA_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.BIRCH_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.OAK_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.CAULDRON.setHarvestLevel("pickaxe", 1);
        Blocks.COCOA.setHarvestLevel("axe", 0);
        Blocks.BIRCH_STAIRS.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_STAIRS.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_STAIRS.setHarvestLevel("axe", 0);
        Blocks.HARDENED_CLAY.setHarvestLevel("pickaxe", 1);
        Blocks.STAINED_HARDENED_CLAY.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_TRAPDOOR.setHarvestLevel("pickaxe", 1);
        Blocks.PRISMARINE.setHarvestLevel("pickaxe", 1);
        Blocks.RED_SANDSTONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.DOUBLE_STONE_SLAB2.setHarvestLevel("pickaxe", 1);
        Blocks.STONE_SLAB2.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_DOOR.setHarvestLevel("axe", 0);
        Blocks.BIRCH_DOOR.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_DOOR.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_DOOR.setHarvestLevel("axe", 0);
        Blocks.OAK_DOOR.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_DOOR.setHarvestLevel("axe", 0);
        Blocks.CHORUS_PLANT.setHarvestLevel("axe", 0);
        Blocks.PURPUR_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_PILLAR.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_DOUBLE_SLAB.setHarvestLevel("pickaxe", 1);
        Blocks.SPRUCE_STAIRS.setHarvestLevel("axe", 0);
        Blocks.PURPUR_SLAB.setHarvestLevel("pickaxe", 1);
        Blocks.END_BRICKS.setHarvestLevel("pickaxe", 1);
        Blocks.RED_NETHER_BRICK.setHarvestLevel("pickaxe", 1);
        Blocks.BONE_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.OBSERVER.setHarvestLevel("pickaxe", 1);
        Blocks.BLACK_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BLUE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BROWN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.CYAN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.GRAY_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.GREEN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.LIGHT_BLUE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.LIME_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.MAGENTA_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.ORANGE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.PINK_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.PURPLE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.RED_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.SILVER_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.WHITE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.YELLOW_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BLACK_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.BLUE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.BROWN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.CYAN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.GRAY_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.GREEN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.LIME_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.MAGENTA_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.ORANGE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.PINK_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.PURPLE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.RED_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.SILVER_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.WHITE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.YELLOW_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.CONCRETE.setHarvestLevel("pickaxe", 1);
        /*for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            if (block.getHarvestTool(block.getDefaultState()) == null) {
                System.out.println(block+" has null harvest tool");
            }
        }*/
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        Personality.fillWeightedList();
    }
}