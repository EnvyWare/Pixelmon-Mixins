package com.envyful.mixins.bop;

import biomesoplenty.common.item.ItemBiomeFinder;
import biomesoplenty.common.util.biome.BiomeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@Mixin(ItemBiomeFinder.class)
public abstract class MixinItemBiomeFinder extends Item {

    @Shadow
    private static void writeNBTSearching(NBTTagCompound nbt, World world) {
    }

    @Shadow
    private static void sendChatMessage(EntityPlayer player, String msg, Object format,
                                        TextFormatting color) {
    }

    @Inject(method = "writeNBTNotFound", remap = false, at = @At("HEAD"))
    private static void onWriteNBTNotFound(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setLong("LAST_FAIL", System.currentTimeMillis());
    }

    @Shadow
    private static void writeNBTFound(NBTTagCompound nbt, BlockPos pos) {
    }

    @Shadow
    private static void writeNBTNotFound(NBTTagCompound nbt) {
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return new ActionResult(EnumActionResult.PASS, stack);
        }else if (nbt.hasKey("LAST_FAIL") && (System.currentTimeMillis() - nbt.getLong("LAST_FAIL")) <= TimeUnit.MINUTES.toMillis(5)) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        } else if (nbt.getBoolean("found")) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        } else if (nbt.hasKey("searchStarted") && world.getWorldTime() - nbt.getLong("searchStarted") < 100L) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        } else if (!nbt.hasKey("biomeIDToFind")) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        } else {
            Biome biomeToFind = Biome.getBiome(nbt.getInteger("biomeIDToFind"));
            writeNBTSearching(nbt, world);
            if (world.isRemote) {
                return new ActionResult(EnumActionResult.PASS, stack);
            } else if (biomeToFind != null && biomeToFind.getRegistryName() != null) {
                String biomeName = WordUtils.capitalize(biomeToFind.getRegistryName().getPath());
                sendChatMessage(player, "biome_finder.searching", biomeName, TextFormatting.DARK_PURPLE);
                BlockPos pos = BiomeUtils.spiralOutwardsLookingForBiome(world, biomeToFind, player.posX, player.posZ);
                if (pos == null) {
                    sendChatMessage(player, "biome_finder.not_found", biomeName, TextFormatting.RED);
                    writeNBTNotFound(nbt);
                } else {
                    sendChatMessage(player, "biome_finder.found", biomeName, TextFormatting.GREEN);
                    writeNBTFound(nbt, pos);
                }

                return new ActionResult(EnumActionResult.PASS, stack.copy());
            } else {
                return new ActionResult(EnumActionResult.PASS, stack);
            }
        }
    }
}
