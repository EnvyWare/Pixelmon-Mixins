package com.envyful.mixins.plugins.pokeloots;

import com.pixelmonmod.pixelmon.blocks.BlockPokeChest;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityPokeChest;
import fr.pokepixel.pokeloots.HandlerMC;
import fr.pokepixel.pokeloots.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(HandlerMC.class)
public class MixinHandlerMC {

    @Shadow public static ArrayList<String> list;

    @Shadow boolean remove;
    @Shadow int z;
    @Shadow int y;
    @Shadow int x;

    /**
     * @author
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        TileEntity tiles = event.getWorld().getTileEntity(event.getPos());

        if (tiles == null || event.getEntityPlayer().canUseCommand(0, "pokeloots.remove")) {
            return;
        }

        Block block = tiles.getBlockType();

        if (!(block instanceof BlockPokeChest)) {
            return;
        }

        Configuration config = CommonProxy.config;
        String whatdo = config.getCategory("general").get("whatyouwanttodo").getString();
        boolean override = config.getCategory("general").get("customlootreplace").getBoolean();
        TileEntityPokeChest pokeChest = (TileEntityPokeChest) tiles;

        if (override || !pokeChest.isCustomDrop() && whatdo.equalsIgnoreCase("remove")) {
            this.x = event.getPos().getX();
            this.y = event.getPos().getY();
            this.z = event.getPos().getZ();
            this.remove = true;
            list.add(event.getEntityPlayer().getName());
        }
    }
}
