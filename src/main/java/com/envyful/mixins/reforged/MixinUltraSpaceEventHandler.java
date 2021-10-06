package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.EntityDen;
import com.pixelmonmod.pixelmon.entities.EntityPokestop;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.util.helpers.DimensionHelper;
import com.pixelmonmod.pixelmon.worldGeneration.dimension.ultraspace.UltraSpace;
import com.pixelmonmod.pixelmon.worldGeneration.dimension.ultraspace.UltraSpaceEventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(UltraSpaceEventHandler.class)
public class MixinUltraSpaceEventHandler {

    /**
     * @author
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.getWorldInfo().getTerrainType() == UltraSpace.WORLD_TYPE) {
            boolean modifyGravity = false;
            if (!entity.hasNoGravity() && !entity.isRiding()) {
                modifyGravity = true;
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                if (player.capabilities.isFlying || player.capabilities.isCreativeMode) {
                    modifyGravity = false;
                }
            }

            if (entity instanceof EntityPixelmon) {
                EntityPixelmon pixelmon = (EntityPixelmon)entity;
                if (pixelmon.isFlying || pixelmon.isBeingRidden()) {
                    modifyGravity = false;
                }
            }

            if (entity instanceof EntityPokestop || entity instanceof EntityDen) {
                modifyGravity = false;
            }

            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP)entity;
                if (entity.posY < -3.0D && entity.dimension == UltraSpace.DIM_ID) {
                    player.world.playSound((EntityPlayer)null, entity.getPosition(), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.5F, 1.0F);
                    double y;
                    double z;
                    int d;
                    double x;
                    if (player.getEntityData().hasKey("PortalX") && player.getEntityData().hasKey("PortalY") && player.getEntityData().hasKey("PortalZ") && player.getEntityData().hasKey("PortalD")) {
                        x = player.getEntityData().getDouble("PortalX");
                        y = player.getEntityData().getDouble("PortalY");
                        z = player.getEntityData().getDouble("PortalZ");
                        d = player.getEntityData().getInteger("PortalD");
                    } else {
                        d = player.getSpawnDimension();
                        WorldServer w = player.getServer().getWorld(d);
                        x = (double)w.getSpawnPoint().getX();
                        y = (double)w.getSpawnPoint().getY();
                        z = (double)w.getSpawnPoint().getZ();
                    }

                    DimensionHelper.teleport(player, d, x, y, z);
                } else if (entity.posY > 300.0D) {
                    if (player.world.getWorldTime() % 20L == 0L) {
                        player.attackEntityFrom(DamageSource.IN_WALL, 2.0F);
                    }
                } else if (entity.posY > 255.0D && player.world.getWorldTime() % 60L == 0L) {
                    player.attackEntityFrom(DamageSource.IN_WALL, 1.0F);
                }
            }
        }

    }

}
