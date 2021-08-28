package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity3HasStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity4Interactions;
import com.pixelmonmod.pixelmon.entities.pixelmon.PathNavigateGroundLarge;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
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

    @Inject(method = "resetAI", at = @At("RETURN"), remap = false)
    public void onResetAI(CallbackInfo ci) {
        if (this.getPokemonData().getGrowth().scaleOrdinal > 5) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }
}
