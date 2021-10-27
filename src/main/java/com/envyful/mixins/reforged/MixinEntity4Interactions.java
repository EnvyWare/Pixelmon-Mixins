package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity3HasStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity4Interactions;
import com.pixelmonmod.pixelmon.entities.pixelmon.PathNavigateGroundLarge;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity4Interactions.class)
public abstract class MixinEntity4Interactions extends Entity3HasStats {

    public MixinEntity4Interactions(World par1World) {
        super(par1World);
    }

    @Shadow(remap = false) protected abstract boolean isFlying();

    @Shadow public int jumpTicks;

    @Inject(method = "resetAI", at = @At("RETURN"), remap = false)
    public void onResetAI(CallbackInfo ci) {
        if (this.getPokemonData().getGrowth().scaleOrdinal > 5 || (this.getBossMode() != null && this.getBossMode().scaleFactor > 1.0F)) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Override
    protected boolean isMovementBlocked() {
        return false;
    }

    /**
     * @author
     */
    @Overwrite
    public void onLivingUpdate() {
        if (!this.isFlying()) {
            if (this.newPosRotationIncrements > 0 && !this.canPassengerSteer()) {
                double d0 = this.posX + (this.interpTargetX - this.posX) / (double)this.newPosRotationIncrements;
                double d1 = this.posY + (this.interpTargetY - this.posY) / (double)this.newPosRotationIncrements;
                double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double)this.newPosRotationIncrements;
                double d3 = MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                --this.newPosRotationIncrements;
                this.setPosition(d0, d1, d2);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else if (!this.isServerWorld()) {
                this.motionX *= 0.98D;
                this.motionY *= 0.98D;
                this.motionZ *= 0.98D;
            }

            if (Math.abs(this.motionX) < 0.003D) {
                this.motionX = 0.0D;
            }

            if (Math.abs(this.motionY) < 0.003D) {
                this.motionY = 0.0D;
            }

            if (Math.abs(this.motionZ) < 0.003D) {
                this.motionZ = 0.0D;
            }

            this.world.profiler.startSection("ai");
        }

        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if (this.isServerWorld()) {
            this.world.profiler.startSection("newAi");
            this.updateEntityActionStateAlt();
            this.world.profiler.endSection();
        }

        this.world.profiler.endSection();
        this.world.profiler.startSection("jump");
        if (!this.isFlying()) {
            if (this.isJumping) {
                if (this.isInWater()) {
                    this.handleJumpWater();
                } else if (this.isInLava()) {
                    this.handleJumpLava();
                } else if (this.onGround && this.jumpTicks == 0) {
                    this.jump();
                    this.jumpTicks = 10;
                }
            }

            this.world.profiler.endSection();
            this.world.profiler.startSection("travel");
            this.moveStrafing *= 0.98F;
            this.moveForward *= 0.98F;
            this.randomYawVelocity *= 0.9F;
            this.travel(this.moveStrafing, this.moveVertical, this.moveForward);
        }

        this.world.profiler.endSection();
        this.world.profiler.startSection("push");
        this.collideWithNearbyEntities();
        this.world.profiler.endSection();
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    protected void updateEntityActionStateAlt() {
        ++this.idleTime;
        this.despawnEntity();
        if (!Double.isNaN(this.posX) && !Double.isNaN(this.posY) && !Double.isNaN(this.posZ)) {
            this.getEntitySenses().clearSensingCache();
            this.targetTasks.onUpdateTasks();
            this.tasks.onUpdateTasks();
            this.navigator.onUpdateNavigation();
            this.updateAITasks();
            if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving)this.getRidingEntity();
                entityliving.getNavigator().setPath(this.getNavigator().getPath(), 1.5D);
                entityliving.getMoveHelper().read(this.getMoveHelper());
            }

            /*this.moveHelper.onUpdateMoveHelper();*/
            if (!this.isFlying()) {
                this.getLookHelper().onUpdateLook();
                this.jumpHelper.doJump();
            }

        }
    }
}
