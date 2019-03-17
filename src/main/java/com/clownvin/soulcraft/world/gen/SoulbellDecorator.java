package com.clownvin.soulcraft.world.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;


public class SoulbellDecorator {

    private void runGenerator(WorldGenerator generator, World world, Random random, int x, int z) {
        generator.generate(world, random, new BlockPos((x * 16) + random.nextInt(16) + 8, random.nextInt(120) + 4, (z * 16) + random.nextInt(16) + 8));
    }

    public void generate(World world, ChunkPos chunkPos, Random random) {
        if (world.provider.getDimension() == DimensionType.THE_END.getId()) {
            //Run it 5 times! WoW!!! xD
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
        }
        if (world.provider.getDimension() == DimensionType.NETHER.getId()) {
            runGenerator(new WorldGenSoulbell(), world, random, chunkPos.x, chunkPos.z);
        }
    }
}