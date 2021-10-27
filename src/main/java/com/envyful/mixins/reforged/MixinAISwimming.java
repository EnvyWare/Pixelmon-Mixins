package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.AI.AISwimming;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.Random;

@Mixin(AISwimming.class)
public abstract class MixinAISwimming extends EntityAIBase {

    @Shadow(remap = false) private EntityPixelmon pixelmon;

    @Shadow(remap = false) private int depthRangeStart;

    @Shadow(remap = false)
    float moveSpeed;

    @Shadow(remap = false)
    int ticksToRefresh;

    @Shadow(remap = false) public abstract void pickDirection(boolean useLastChangeDirection);

    @Shadow(remap = false) public abstract void pickSpeed();

    @Shadow private float swimSpeed;

    @Shadow private float decayRate;

    @Shadow private int depthRangeEnd;

    @Shadow private Random rand;

    public MixinAISwimming(EntityPixelmon entity) {
        if (entity.getSwimmingParameters() != null) {
            this.swimSpeed = entity.getSwimmingParameters().swimSpeed;
            this.decayRate = entity.getSwimmingParameters().decayRate;
            this.depthRangeStart = entity.getSwimmingParameters().depthRangeStart;
            this.depthRangeEnd = entity.getSwimmingParameters().depthRangeEnd;
            this.ticksToRefresh = 0;
        }

        if (Objects.equals(entity.getSpecies(), EnumSpecies.Magikarp)) {
            this.swimSpeed = 0.7f;
        }

        this.pixelmon = entity;
        this.rand = entity.getRNG();
    }

    /**
     * @author
     */
    @Overwrite
    public boolean shouldExecute() {
        if (!this.pixelmon.isInWater()) {
            return false;
        }

        if (this.pixelmon.battleController != null) {
            return false;
        }

        return this.depthRangeStart != -1;
    }

    /**
     * @author
     */
    @Overwrite
    public void updateTask() {
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
        }

        --this.ticksToRefresh;
        if (this.moveSpeed == 0.0F || useLastChangeDirection || this.pixelmon.motionX * this.pixelmon.motionX + this.pixelmon.motionZ * this.pixelmon.motionZ < (double) (this.moveSpeed / 4.0F)) {
            this.pickDirection(useLastChangeDirection);
            this.pickSpeed();
            this.pixelmon.moveForward = this.moveSpeed;
            /*this.pixelmon.travel(0.0F, 0.0F, this.moveSpeed);*/
        }
    }

}
