package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.battles.attacks.DamageTypeEnum;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.Poison;
import com.pixelmonmod.pixelmon.battles.status.StatusPersist;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.MagicGuard;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.PoisonHeal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Poison.class)
public abstract class MixinPoison extends StatusPersist {

    @Shadow(remap = false) protected abstract float getPoisonDamage(PixelmonWrapper pw);

    public MixinPoison(StatusType type) {
        super(type);
    }

    /**
     *
     *
     * @author danorris709
     * @reason Fixes a waiting glitch where the fainted pokemon attempts to heal itself
     */
    @Overwrite(remap = false)
    public void applyRepeatedEffect(PixelmonWrapper pw) {
        if (pw.isFainted()) {
            return;
        }

        AbilityBase ability = pw.getBattleAbility();
        if (!(ability instanceof MagicGuard)) {
            if (ability instanceof PoisonHeal) {
                if (pw.hasFullHealth() || pw.hasStatus(StatusType.HealBlock)) {
                    return;
                }

                pw.bc.sendToAll("pixelmon.abilities.poisonheal", pw.getNickname());
                pw.healByPercent(12.5F);
            } else {
                pw.bc.sendToAll("pixelmon.status.hurtbypoison", pw.getNickname());
                pw.doBattleDamage(pw, this.getPoisonDamage(pw), DamageTypeEnum.STATUS);
            }

        }
    }
}
