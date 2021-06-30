package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.Regenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Regenerator.class)
public abstract class MixinRegenerator extends AbilityBase {

    /**
     *
     *
     * @author danorris709
     * @reason Fixes a waiting glitch where the fainted pokemon attempts to heal itself
     */
    @Overwrite(remap = false)
    public void applySwitchOutEffect(PixelmonWrapper pw) {
        if (pw.isFainted()) {
            return;
        }

        pw.animateHP = true;
        pw.healByPercent(33.333332F);
        pw.update(EnumUpdateType.HP);
        this.sendActivatedMessage(pw);

        pw.animateHP = true;
    }
}
