package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity6Moves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity6Moves.class)
public class MixinEntity6Moves {

    private Boolean flyingCache = null;

    @Inject(method = "getIsFlying", at = @At("RETURN"), remap = false)
    public void onGetIsFlyingRETURN(CallbackInfoReturnable<Boolean> cir) {
        if (this.flyingCache == null) {
            this.flyingCache = cir.getReturnValue();
        }
    }

    @Inject(method = "getIsFlying", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGetIsFlyingHEAD(CallbackInfoReturnable<Boolean> cir) {
        if (this.flyingCache != null) {
            cir.setReturnValue(this.flyingCache);
            cir.cancel();
        }
    }

    @Inject(method = "setIsFlying", at = @At("RETURN"), remap = false, cancellable = true)
    public void onSetIsFlying(boolean flying, CallbackInfo info) {
        this.flyingCache = flying;
    }
}
