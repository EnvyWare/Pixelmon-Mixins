package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.AI.AIFlyingPersistent;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AIFlyingPersistent.class)
public abstract class MixinAIFlyingPersistent extends EntityAIBase {


    @Shadow private EntityPixelmon pixelmon;

    @Shadow private int ticksToChangeDirection;

    @Shadow private int ticksToChangeSpeed;

    @Shadow public abstract void pickDirection(boolean useLastChangeDirection);

    @Shadow public abstract void pickSpeed();

    @Shadow private int directionChange;

    @Shadow private int speedChange;

    @Shadow private float movespeed;

    /**
     * @author
     */
    @Overwrite
    public boolean shouldExecute() {
        return this.pixelmon.battleController == null;
    }

    /**
     * @author
     */
    @Overwrite
    public boolean shouldContinueExecuting() {
        return true;
    }

    /**
     * @author
     */
    @Overwrite
    public void updateTask() {
        --this.ticksToChangeDirection;
        --this.ticksToChangeSpeed;
        boolean useLastChangeDirection = false;
        World var10000 = this.pixelmon.world;
        Vec3d var10001 = new Vec3d(this.pixelmon.posX, this.pixelmon.getEntityBoundingBox().minY, this.pixelmon.posZ);
        double var10004 = this.pixelmon.posX + this.pixelmon.motionX * 100.0D;
        double var10006 = this.pixelmon.posZ + this.pixelmon.motionZ * 100.0D;
        RayTraceResult mop = var10000.rayTraceBlocks(var10001, new Vec3d(var10004, this.pixelmon.getEntityBoundingBox().minY, var10006));
        if (mop == null) {
            var10000 = this.pixelmon.world;
            var10001 = new Vec3d(this.pixelmon.posX, this.pixelmon.getEntityBoundingBox().maxY, this.pixelmon.posZ);
            var10004 = this.pixelmon.posX + this.pixelmon.motionX * 100.0D;
            var10006 = this.pixelmon.posZ + this.pixelmon.motionZ * 100.0D;
            mop = var10000.rayTraceBlocks(var10001, new Vec3d(var10004, this.pixelmon.getEntityBoundingBox().maxY, var10006));
        }

        if (mop != null) {
            useLastChangeDirection = true;
            this.ticksToChangeDirection = 0;
        }

        if (this.ticksToChangeDirection <= 0) {
            this.pickDirection(useLastChangeDirection);
            this.ticksToChangeDirection = 25 + this.pixelmon.getRNG().nextInt(this.directionChange);
        }

        if (this.ticksToChangeSpeed <= 0) {
            this.pickSpeed();
            this.ticksToChangeSpeed = 50 + this.pixelmon.getRNG().nextInt(this.speedChange);
        }

        this.pixelmon.moveForward = this.movespeed;
    }
}
