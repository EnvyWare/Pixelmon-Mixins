package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.AI.AIFlying;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AIFlying.class)
public abstract class MixinAIFlying extends EntityAIBase {

    @Shadow private int flightTicks;

    @Shadow private boolean takingOff;

    @Shadow @Final private EntityPixelmon pixelmon;

    @Shadow private int targetHeight;

    @Shadow private double takeOffSpeed;

    @Shadow protected abstract void lookForOwnerEntity();

    @Shadow protected abstract void checkForLandingSpot();

    @Shadow private BlockPos currentFlightTarget;

    @Shadow protected abstract boolean hasLandingSpot();

    @Shadow private int wingBeatTick;

    @Shadow private int nextWingBeat;

    @Shadow public abstract void pickDirection(boolean useLastChangeDirection);

    /**
     * @author
     */
    @Overwrite
    public void updateTask() {
        ++this.flightTicks;
        if (this.flightTicks > 30 && this.takingOff || this.takingOff && this.pixelmon.posY >= (double)this.targetHeight) {
            this.takingOff = false;
            this.flightTicks = 0;
        }

        if (this.takingOff) {
            this.pixelmon.moveStrafing = this.pixelmon.getPokemonData().getStat(StatsType.Speed) / 500.0F;
            this.pixelmon.motionY = this.takeOffSpeed;
        }

        if (this.pixelmon.getOwner() == null) {
            this.lookForOwnerEntity();
        }

        this.checkForLandingSpot();
        AxisAlignedBB box = this.pixelmon.getEntityBoundingBox();
        RayTraceResult mop = this.pixelmon.world.rayTraceBlocks(new Vec3d(this.pixelmon.posX, box.minY, this.pixelmon.posZ), new Vec3d(this.pixelmon.posX + this.pixelmon.motionX * 100.0D, box.minY, this.pixelmon.posZ + this.pixelmon.motionZ * 100.0D));
        if (mop == null) {
            mop = this.pixelmon.world.rayTraceBlocks(new Vec3d(this.pixelmon.posX, box.maxY, this.pixelmon.posZ), new Vec3d(this.pixelmon.posX + this.pixelmon.motionX * 100.0D, box.maxY, this.pixelmon.posZ + this.pixelmon.motionZ * 100.0D));
        }

        if (this.hasLandingSpot()) {
            if (mop == null) {
                double d0 = (double)this.currentFlightTarget.getX() + 0.5D - this.pixelmon.posX;
                double d1 = (double)this.currentFlightTarget.getY() + 0.1D - this.pixelmon.posY;
                double d2 = (double)this.currentFlightTarget.getZ() + 0.5D - this.pixelmon.posZ;
                EntityPixelmon var10000 = this.pixelmon;
                var10000.motionX += (Math.signum(d0) * 1.0D - this.pixelmon.motionX) * 0.10000000149011612D;
                var10000 = this.pixelmon;
                var10000.motionY += (Math.signum(d1) * 0.699999988079071D - this.pixelmon.motionY) * 0.10000000149011612D;
                var10000 = this.pixelmon;
                var10000.motionZ += (Math.signum(d2) * 1.0D - this.pixelmon.motionZ) * 0.10000000149011612D;
                float f = (float)(Math.atan2(this.pixelmon.motionZ, this.pixelmon.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
                float f1 = MathHelper.wrapDegrees(f - this.pixelmon.rotationYaw);
                this.pixelmon.setMoveForward(0.5F);
                var10000 = this.pixelmon;
                var10000.rotationYaw += f1;
            }
        } else {
            this.maintainFlight(mop != null);
        }

        super.updateTask();
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    private void maintainFlight(boolean hasObstacle) {
        ++this.wingBeatTick;
        if (hasObstacle || this.wingBeatTick >= this.nextWingBeat) {
            this.pickDirection(hasObstacle);
            this.nextWingBeat = this.pixelmon.getFlyingParameters().flapRate + (int)(Math.random() * 0.4D * (double)this.pixelmon.getFlyingParameters().flapRate - 0.2D * (double)this.pixelmon.getFlyingParameters().flapRate);
            this.pixelmon.moveForward = 4.0F + (float)this.pixelmon.getPokemonData().getStat(StatsType.Speed) / 100.0F * this.pixelmon.getFlyingParameters().flySpeedModifier;
            this.pixelmon.motionY = (double)(this.pixelmon.getFlyingParameters().flapRate + 1) * 0.01D;
            this.wingBeatTick = 0;
        }

    }
}
