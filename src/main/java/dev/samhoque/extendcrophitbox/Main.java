package dev.samhoque.extendcrophitbox;

import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 Created by Sam Hoque
 Self Explanatory
 */

@Mod(modid = "dev.samhoque.extendcrophitbox", name = "Extend Crop Hitbox", version = "1.0.0", acceptedMinecraftVersions = "[1.8.9]")
public class Main {
    @Instance
    public static Main main;

    //Crop Heights
    double[] WART_HEIGHTS = {0.3125D, 0.5D, 0.6875D, 0.875D};
    double[] CARROT_HEIGHTS = {0.125D, 0.1875D, 0.25D, 0.3125D, 0.375D, 0.4375D, 0.5D, 0.5625D};
    double[] WHEAT_HEIGHTS = {0.125D, 0.25D, 0.375D, 0.5D, 0.625D, 0.75D, 0.875D, 1.0D};

    public void extendHitbox(World world, BlockPos blockPos) {
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        double[] heights = WART_HEIGHTS;
        PropertyInteger age = null;

        if (block instanceof BlockNetherWart) {
            age = BlockNetherWart.AGE;
        } else if (block instanceof BlockCrops) {
            heights = block instanceof BlockPotato || block instanceof BlockCarrot ? CARROT_HEIGHTS : WHEAT_HEIGHTS;
            age = BlockCrops.AGE;
        }

        //This shouldn't happen, better safe than sorry.
        if(age == null) {
            return;
        }

        ObfuscationReflectionHelper.setPrivateValue(Block.class, block, heights[blockState.getValue(age)], "maxY", "field_149756_F");
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        main = this;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
