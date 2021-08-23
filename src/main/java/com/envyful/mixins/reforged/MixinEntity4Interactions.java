package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity3HasStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity4Interactions;
import com.pixelmonmod.pixelmon.entities.pixelmon.PathNavigateGroundLarge;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity4Interactions.class)
public abstract class MixinEntity4Interactions extends Entity3HasStats {

    public MixinEntity4Interactions(World par1World) {
        super(par1World);
    }

    @Shadow(remap = false) protected abstract boolean isFlying();

    /**
     * @author
     */
    @Overwrite(remap = false)
    protected void updateEntityActionStateAlt() {
/*        if (this.getBaseStats().getSpecies() == EnumSpecies.Groudon && this.getEntityBoundingBox() != null) {
            System.out.println("AVG " + this.getEntityBoundingBox().getAverageEdgeLength());
        }*/

        if (this.getEntityBoundingBox().getAverageEdgeLength() > 5) {
            if (!(this.navigator instanceof PathNavigateGroundLarge)) {
                this.navigator = new PathNavigateGroundLarge(
                        (EntityLiving) this.getEntityWorld().getEntityByID(this.getEntityId()), this.getEntityWorld());
            }
        }

        ++this.idleTime;
        this.despawnEntity();
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

        this.moveHelper.onUpdateMoveHelper();
        if (!this.isFlying()) {
            this.getLookHelper().onUpdateLook();
            this.jumpHelper.doJump();
        }

    }
}
