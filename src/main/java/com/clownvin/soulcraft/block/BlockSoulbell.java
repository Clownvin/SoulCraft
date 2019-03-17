package com.clownvin.soulcraft.block;

import com.clownvin.soulcraft.SoulCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class BlockSoulbell extends BlockBush {

    public BlockSoulbell() {
        this.setUnlocalizedName("soulbell");
        this.setRegistryName(SoulCraft.MOD_ID, "soulbell");
        this.setLightLevel(0.666f);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return super.getItemDropped(state, rand, fortune);
    }

    @Override
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.MOSSY_COBBLESTONE || block == Blocks.SOUL_SAND || block == Blocks.NETHERRACK || block == Blocks.OBSIDIAN || block == Blocks.END_STONE;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Nether;
    }
}
