package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.MagicCoat;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.enums.battle.AttackCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MagicCoat.class)
public abstract class MixinMagicCoat extends StatusBase {

    private static final String[] BLOCKED = new String[] {
            "Sketch",
            "Bestow", "Curse", "Guard Swap", "Heart Swap", "Lock-On", "Memento", "Mimic", "Power Swap", "Psych Up",
            "Psycho Shift", "Role Play", "Skill Swap", "Snatch", "Switcheroo", "Transform", "Trick", "Extreme Evoboost"
    };

    @Overwrite(remap = false)
    public static boolean reflectMove(Attack a, PixelmonWrapper pokemon, PixelmonWrapper user, String message) {
        if (a.getAttackCategory() != AttackCategory.STATUS || a.isAttack(BLOCKED)
                || a.getMove().getTargetingInfo().hitsAll && a.getMove().getTargetingInfo().hitsSelf) {
            return false;
        } else {
            user.bc.sendToAll(message, pokemon.getNickname());
            pokemon.targetIndex = 0;
            if (!pokemon.getBattleAbility().allowsOutgoingAttack(pokemon, user, a)) {
                return true;
            } else if (a.hasNoEffect(pokemon, user)) {
                user.bc.sendToAll("pixelmon.battletext.noeffect", user.getNickname());
                return true;
            } else {
                a.applyAttackEffect(pokemon, user);
                return true;
            }
        }
    }
}
