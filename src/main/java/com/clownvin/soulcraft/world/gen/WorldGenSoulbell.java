package com.clownvin.soulcraft.world.gen;

import com.clownvin.soulcraft.block.SCBlocks;
import com.clownvin.soulcraft.item.SCItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class WorldGenSoulbell extends WorldGenerator {

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        System.out.println("Generating at: "+position);
        for (int i = 0; i < 256; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(16) - rand.nextInt(16), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && SCBlocks.BLOCK_SOULBELL.canSustainPlant(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), EnumFacing.UP, (IPlantable) SCBlocks.BLOCK_SOULBELL)) {//worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.NETHERRACK)
                worldIn.setBlockState(blockpos, SCBlocks.BLOCK_SOULBELL.getDefaultState());
                System.out.println("Created soulbell at: "+blockpos);
            }
        }
        return true;
    }

}
