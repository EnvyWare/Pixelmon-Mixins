package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.attackAnimations.AttackAnimation;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.battles.attacks.EffectBase;
import com.pixelmonmod.pixelmon.battles.attacks.ZMove;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.attackModifiers.AttackModifierBase;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.attackModifiers.CriticalHit;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.attackModifiers.MultipleHit;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.basic.*;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.multiTurn.MultiTurnCharge;
import com.pixelmonmod.pixelmon.battles.attacks.specialAttacks.multiTurn.MultiTurnSpecialAttackBase;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.log.AttackResult;
import com.pixelmonmod.pixelmon.battles.controller.log.MoveResults;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.GlobalStatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.KeenEye;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.ParentalBond;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.Unaware;
import com.pixelmonmod.pixelmon.enums.EnumType;
import com.pixelmonmod.pixelmon.enums.battle.AttackCategory;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(Attack.class)
public abstract class MixinAttack {
        @Shadow(remap = false)
        public AttackBase baseAttack;
        @Shadow(remap = false)
        public AttackBase overrideAttack;
        @Shadow(remap = false)
        public int pp;
        @Shadow(remap = false)
        public int ppLevel;
        @Shadow(remap = false)
        public int movePower;
        @Shadow(remap = false)
        public int overridePower;
        @Shadow(remap = false)
        public int moveAccuracy;
        @Shadow(remap = false)
        public boolean cantMiss;
        @Shadow(remap = false)
        public boolean disabled;
        @Shadow(remap = false)
        public MoveResults moveResult;
        @Shadow(remap = false)
        public float damageResult;
        @Shadow(remap = false)
        public AttackBase savedAttack;
        @Shadow(remap = false)
        public int savedPower;
        @Shadow(remap = false)
        public int savedAccuracy;
        @Shadow(remap = false)
        public Integer overridePPMax;
        @Shadow(remap = false)
        public AttackCategory overrideAttackCategory;
        @Shadow(remap = false)
        public EnumType overrideType;
        @Shadow(remap = false)
        public transient boolean hasPlayedAnimationOnce;
        @Shadow(remap = false)
        public transient boolean isZ;
        @Shadow(remap = false)
        public transient boolean isMax;
        @Shadow(remap = false)
        public transient Attack originalMove;
        @Shadow(remap = false)
        public transient boolean fromDancer;

    public MixinAttack() {
        this.overrideAttack = null;
        this.overridePower = -1;
        this.overridePPMax = null;
        this.overrideAttackCategory = null;
        this.overrideType = null;
        this.hasPlayedAnimationOnce = false;
        this.isZ = false;
        this.isMax = false;
        this.originalMove = null;
        this.fromDancer = false;
    }

        @Shadow(remap = false)
        public abstract AttackBase getActualMove();

        @Shadow(remap = false)
        public abstract void overrideAttackCategory(final AttackCategory p0);

        @Shadow(remap = false)
        public abstract boolean checkSkyBattle(final BattleControllerBase p0);

        @Shadow(remap = false)
        public abstract boolean canHitNoTarget();

        @Shadow(remap = false)
        public abstract boolean canHit(final PixelmonWrapper p0, final PixelmonWrapper p1);

        @Shadow(remap = false)
        public abstract AttackBase getMove();

        @Shadow(remap = false)
        public abstract void applySelfStatusMove(final PixelmonWrapper p0, final MoveResults p1);

        @Shadow(remap = false)
        public abstract AttackCategory getAttackCategory();

        @Shadow(remap = false)
        public abstract void onMiss(final PixelmonWrapper p0, final PixelmonWrapper p1, final MoveResults p2, final Object p3);

        @Shadow(remap = false)
        public abstract boolean hasNoEffect(final PixelmonWrapper p0, final PixelmonWrapper p1);

        @Shadow(remap = false)
        public abstract boolean cantMiss(final PixelmonWrapper p0);

        @Shadow(remap = false)
        public abstract void playAnimation(final PixelmonWrapper p0, final PixelmonWrapper p1);

        @Shadow(remap = false)
        public abstract void executeAttackEffects(final PixelmonWrapper p0, final PixelmonWrapper p1, final MoveResults p2, final EffectBase p3, final float p4);

        /**
         * @author
         */
        @Overwrite(remap = false)
        public boolean use(final PixelmonWrapper user, PixelmonWrapper target, final MoveResults moveResults, final ZMove zMove) {
        final boolean z = zMove != null;
        if (z) {
            this.isZ = true;
            final Optional<AttackBase> opt = (Optional<AttackBase>)AttackBase.getAttackBase(zMove.attackName);
            if (opt.isPresent()) {
                this.overrideAttack = opt.get();
                if (this.overrideAttack.getAttackCategory() != AttackCategory.STATUS) {
                    this.overrideAttackCategory(this.getActualMove().getAttackCategory());
                }
            }
        }
        this.moveResult = moveResults;
        this.damageResult = -1.0f;
        if (user.bc == null || target.bc == null) {
            return false;
        }
        if (!this.checkSkyBattle(user.bc)) {
            user.bc.sendToAll("pixelmon.effect.effectfailed", new Object[0]);
            moveResults.result = AttackResult.failed;
            return false;
        }
        final AbilityBase userAbility = user.getBattleAbility();
        final AbilityBase targetAbility = target.getBattleAbility();
        if (!this.canHit(user, target) && !this.canHitNoTarget()) {
            moveResults.result = AttackResult.notarget;
            return true;
        }
        if (user == target) {
            for (final PixelmonWrapper activePokemon : user.bc.getActiveUnfaintedPokemon()) {
                for (final StatusBase status : activePokemon.getStatuses()) {
                    if (status.stopsSelfStatusMove(activePokemon, user, (Attack)(Object) this)) {
                        moveResults.result = AttackResult.failed;
                        return true;
                    }
                }
            }
            if (!user.bc.simulateMode) {
                for (final AttackAnimation anim : this.getMove().animations) {
                    if ((!anim.usedOncePerTurn() || !this.hasPlayedAnimationOnce) && (user == target || user.entity.getDistance(target.entity) < 20.0f)) {
                        BattleControllerBase.currentAnimations.add(anim.instantiate(user, target, (Attack)(Object) this));
                    }
                }
            }
            this.applySelfStatusMove(user, moveResults);
            return true;
        }
        if (user.targets.size() == 1) {
            final ArrayList<PixelmonWrapper> opponents = (ArrayList<PixelmonWrapper>)user.bc.getOpponentPokemon(user.getParticipant());
            if (opponents.size() > 1) {
                for (final PixelmonWrapper pw : opponents) {
                    if (pw != target) {
                        for (final StatusBase status2 : pw.getStatuses()) {
                            if (status2.redirectAttack(user, pw, (Attack) (Object) this)) {
                                target = pw;
                                break;
                            }
                        }
                        if (pw.getBattleAbility().redirectAttack(user, pw, (Attack)(Object) this)) {
                            target = pw;
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        final ArrayList<EffectBase> effects = new ArrayList<EffectBase>(this.getMove().effects);
        if (z && !zMove.attackName.equals(this.getMove().getAttackName()) && this.getAttackCategory() != AttackCategory.STATUS) {
            effects.clear();
            effects.addAll(zMove.effects);
        }
        for (final EffectBase e : effects) {
            try {
                if (e.applyEffectStart(user, target) != AttackResult.proceed) {
                    return true;
                }
                continue;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        int livePower = this.getMove().getBasePower();
        if (z) {
            livePower = zMove.basePower;
        }
        else if (this.overridePower > -1) {
            livePower = this.overridePower;
        }
        int[] modifiedMoveStats = userAbility.modifyPowerAndAccuracyUser(livePower, this.getMove().getAccuracy(),
                                                                         user, target, (Attack) (Object) this);
        for (final PixelmonWrapper teammate : user.bc.getTeamPokemon(user)) {
            modifiedMoveStats = teammate.getBattleAbility().modifyPowerAndAccuracyTeammate(modifiedMoveStats[0], modifiedMoveStats[1], user, target, (Attack)(Object) this);
        }
        modifiedMoveStats = targetAbility.modifyPowerAndAccuracyTarget(modifiedMoveStats[0], modifiedMoveStats[1], user, target, (Attack)(Object) this);
        int saveAccuracy = 0;
        if (modifiedMoveStats[1] < 0) {
            saveAccuracy = modifiedMoveStats[1];
        }
        modifiedMoveStats = user.getUsableHeldItem().modifyPowerAndAccuracyUser(modifiedMoveStats, user, target, (Attack)(Object) this);
        modifiedMoveStats = target.getUsableHeldItem().modifyPowerAndAccuracyTarget(modifiedMoveStats, user, target, (Attack)(Object) this);
        for (int beforeSize = user.getStatuses().size(), i = 0; i < beforeSize; ++i) {
            final StatusBase status3 = user.getStatus(i);
            modifiedMoveStats = status3.modifyPowerAndAccuracyUser(modifiedMoveStats[0], modifiedMoveStats[1], user, target, (Attack)(Object) this);
            if (user.getStatuses().size() != beforeSize) {
                break;
            }
        }
        final Iterator<StatusBase> iterator8 = target.getStatuses().iterator();
        while (iterator8.hasNext()) {
            final StatusBase status3 = iterator8.next();
            modifiedMoveStats = status3.modifyPowerAndAccuracyTarget(modifiedMoveStats[0], modifiedMoveStats[1], user, target, (Attack)(Object) this);
        }
        final Iterator<GlobalStatusBase> iterator9 = user.bc.globalStatusController.getGlobalStatuses().iterator();
        while (iterator9.hasNext()) {
            final StatusBase status3 = iterator9.next();
            modifiedMoveStats = status3.modifyPowerAndAccuracyTarget(modifiedMoveStats[0], modifiedMoveStats[1], user, target, (Attack)(Object) this);
        }
        if (saveAccuracy < 0 && modifiedMoveStats[1] >= 0) {
            modifiedMoveStats[1] = saveAccuracy;
        }
        this.movePower = modifiedMoveStats[0];
        this.moveAccuracy = Math.min(modifiedMoveStats[1], 100);
        this.cantMiss = false;
        if (user.entity != null && target.entity != null) {
            user.entity.getLookHelper().setLookPositionWithEntity((Entity)target.entity, 0.0f, 0.0f);
        }
        double accuracy = this.moveAccuracy;
        if (this.moveAccuracy >= 0) {
            int evasion = target.getBattleStats().getEvasionStage();
            if (user.bc.globalStatusController.hasStatus(StatusType.Gravity)) {
                evasion = Math.max(-6, evasion - 2);
            }
            if (user.getBattleAbility() instanceof KeenEye) {
                evasion = Math.min(0, evasion);
            }
            if (this.getMove().hasEffect((Class)IgnoreDefense.class) || userAbility instanceof Unaware) {
                evasion = 0;
            }
            double combinedAccuracy = user.getBattleStats().getAccuracyStage() - evasion;
            if (combinedAccuracy > 6.0) {
                combinedAccuracy = 6.0;
            }
            else if (combinedAccuracy < -6.0) {
                combinedAccuracy = -6.0;
            }
            accuracy = this.moveAccuracy * (user.getBattleStats().GetAccOrEva(combinedAccuracy) / 100.0);
        }
        final ArrayList<StatusBase> allStatuses = new ArrayList<StatusBase>(target.getStatuses());
        allStatuses.addAll((Collection<? extends StatusBase>)target.bc.globalStatusController.getGlobalStatuses().stream().collect(Collectors.toList()));
        boolean shouldNotLosePP = false;
        for (int j = 0; j < effects.size(); ++j) {
            final EffectBase e2 = effects.get(j);
            if (e2 instanceof MultiTurnSpecialAttackBase) {
                shouldNotLosePP = ((MultiTurnSpecialAttackBase)e2).shouldNotLosePP(user);
            }
        }
        for (final StatusBase e3 : user.getStatuses()) {
            try {
                if (e3.stopsIncomingAttackUser(target, user)) {
                    return !shouldNotLosePP;
                }
                continue;
            }
            catch (Exception exc) {
                user.bc.battleLog.onCrash(exc, "Error calculating stopsIncomingAttack for " + e3.type.toString() + " for attack " + this.getMove().getLocalizedName());
            }
        }
        for (final StatusBase e3 : allStatuses) {
            try {
                if (e3.stopsIncomingAttack(target, user)) {
                    this.onMiss(user, target, moveResults, e3);
                    return !shouldNotLosePP;
                }
                continue;
            }
            catch (Exception exc) {
                user.bc.battleLog.onCrash(exc, "Error calculating stopsIncomingAttack for " + e3.type.toString() + " for attack " + this.getMove().getLocalizedName());
            }
        }
        boolean allowed = target.getBattleAbility(user).allowsIncomingAttack(target, user, (Attack)(Object) this);
        if (allowed) {
            allowed = target.getUsableHeldItem().allowsIncomingAttack(target, user, (Attack)(Object) this);
        }
        if (allowed) {
            for (final PixelmonWrapper ally : target.bc.getTeamPokemon(target)) {
                if (!ally.getBattleAbility().allowsIncomingAttackTeammate(ally, target, user, (Attack)(Object) this)) {
                    allowed = false;
                    break;
                }
            }
        }
        if (!allowed) {
            try {
                this.onMiss(user, target, moveResults, targetAbility);
                return !shouldNotLosePP;
            }
            catch (Exception exc2) {
                user.bc.battleLog.onCrash(exc2, "Error calculating allowsIncomingAttack for attack " + this.getMove().getLocalizedName());
            }
        }
        if (!user.getBattleAbility().allowsOutgoingAttack(user, target, (Attack)(Object) this)) {
            this.onMiss(user, target, moveResults, targetAbility);
            return !shouldNotLosePP;
        }
        if (this.hasNoEffect(user, target)) {
            this.onMiss(user, target, moveResults, EnumType.Mystery);
            return !shouldNotLosePP;
        }
        if (!shouldNotLosePP) {
            targetAbility.preProcessAttack(target, user, (Attack)(Object) this);
            userAbility.preProcessAttackUser(user, target, (Attack)(Object) this);
        }
        this.cantMiss = (z || this.cantMiss(user) || this.moveAccuracy < 0);
        if (user.bc.simulateMode) {
            this.moveResult.accuracy = this.moveAccuracy;
            accuracy = 100.0;
        }
        final AttackEvent.Use event = new AttackEvent.Use(user, target, this.getMove(), accuracy, this.cantMiss);
        Pixelmon.EVENT_BUS.post((Event)event);
        accuracy = event.accuracy;
        this.cantMiss = event.cantMiss;
        CriticalHit critModifier = null;
        if (this.cantMiss || RandomHelper.getRandomChance((int)accuracy)) {
            AttackResult finalResult = AttackResult.proceed;
            AttackResult applyEffectResult = AttackResult.proceed;
            for (final EffectBase e4 : effects) {
                try {
                    if (e4 instanceof AttackModifierBase) {
                        if (e4 instanceof CriticalHit) {
                            critModifier = (CriticalHit)e4;
                        }
                        else {
                            applyEffectResult = ((AttackModifierBase)e4).applyEffectDuring(user, target);
                            if (applyEffectResult != AttackResult.proceed) {
                                finalResult = applyEffectResult;
                            }
                        }
                    }
                    else if (e4 instanceof SpecialAttackBase) {
                        applyEffectResult = ((SpecialAttackBase)e4).applyEffectDuring(user, target);
                        if (applyEffectResult != AttackResult.proceed) {
                            finalResult = applyEffectResult;
                        }
                    }
                    else if (e4 instanceof MultiTurnSpecialAttackBase) {
                        applyEffectResult = ((MultiTurnSpecialAttackBase)e4).applyEffectDuring(user, target);
                        if (applyEffectResult != AttackResult.proceed) {
                            finalResult = applyEffectResult;
                            break;
                        }
                    }
                    if (finalResult == AttackResult.succeeded || finalResult == AttackResult.failed || finalResult == AttackResult.charging || finalResult == AttackResult.notarget) {
                        moveResults.result = finalResult;
                    }
                    else {
                        if (finalResult != AttackResult.hit) {
                            continue;
                        }
                        if (target.isAlive()) {
                            moveResults.result = AttackResult.hit;
                        }
                        else {
                            moveResults.result = AttackResult.killed;
                        }
                    }
                }
                catch (Exception exc3) {
                    user.bc.battleLog.onCrash(exc3, "Error in applyEffect for " + e4.getClass().toString() + " for attack " + this.getMove().getLocalizedName());
                }
            }
            if (moveResults.result.isSuccess() || moveResults.result == AttackResult.charging) {
                this.playAnimation(user, target);
            }
            if (applyEffectResult == AttackResult.proceed) {
                if (userAbility instanceof ParentalBond) {
                    user.inMultipleHit = true;
                    user.inParentalBond = true;
                    for (final EffectBase e4 : effects) {
                        if (e4 instanceof BeatUp || e4 instanceof Fling || e4 instanceof MultiTurnCharge || e4 instanceof MultipleHit || e4 instanceof TripleKick) {
                            user.inMultipleHit = false;
                            user.inParentalBond = false;
                        }
                        else {
                            if (!(e4 instanceof Assurance)) {
                                continue;
                            }
                            this.getMove().setBasePower(this.getMove().getBasePower() * 2);
                        }
                    }
                }
                this.playAnimation(user, target);
                this.hasPlayedAnimationOnce = true;
                this.executeAttackEffects(user, target, moveResults, (EffectBase)critModifier, 1.0f);
                if (user.inParentalBond && this.getAttackCategory() != AttackCategory.STATUS && target.isAlive() && user.isAlive() && user.targets.size() == 1) {
                    for (final EffectBase e4 : effects) {
                        if (e4 instanceof Assurance) {
                            this.getMove().setBasePower(this.getMove().getBasePower() * 2);
                        }
                    }
                    user.inMultipleHit = false;
                    user.inParentalBond = false;
                    this.executeAttackEffects(user, target, moveResults, (EffectBase)critModifier, 0.25f);
                    user.bc.sendToAll("multiplehit.times", new Object[] { user.getNickname(), 2 });
                }
                user.inParentalBond = false;
                for (final EffectBase e4 : effects) {
                    if (e4 instanceof SpecialAttackBase) {
                        ((SpecialAttackBase)e4).applyAfterEffect(user);
                    }
                }
                target.getBattleAbility().postProcessAttack(target, user, (Attack) (Object) this);
                user.getBattleAbility().postProcessAttackUser(user, target, (Attack)(Object) this);
                for (final PixelmonWrapper wrapper : user.bc.getActivePokemon()) {
                    wrapper.getBattleAbility().postProcessAttackOther(wrapper, user, target, (Attack)(Object) this);
                }
            }
        }
        else {
            this.onMiss(user, target, moveResults, null);
        }
        for (int k = 0; k < target.getStatusSize(); ++k) {
            final int sizeBefore = target.getStatusSize();
            target.getStatus(k).onAttackEnd(target);
            if (sizeBefore > target.getStatusSize()) {
                --k;
            }
        }
        if (!user.bc.simulateMode) {
            final EnumUpdateType[] updateTypes = { EnumUpdateType.HP, EnumUpdateType.Moveset };
            if (user.getPlayerOwner() != null) {
                user.update(updateTypes);
            }
            if (target.getPlayerOwner() != null) {
                target.update(updateTypes);
            }
        }
        if (target.isFainted()) {
            shouldNotLosePP = false;
        }
        try {
            final PlayerPartyStorage pps = Pixelmon.storageManager.getParty(user.pokemon.getOwnerPlayer());
            pps.getQuestData(true).receive("BATTLE_MOVE_USER", new Object[] { user.pokemon, target.pokemon, this, moveResults });
        }
        catch (Exception ex2) {}
        try {
            final PlayerPartyStorage pps = Pixelmon.storageManager.getParty(target.pokemon.getOwnerPlayer());
            pps.getQuestData(true).receive("BATTLE_MOVE_TARGET", new Object[] { user.pokemon, target.pokemon, this, moveResults });
        }
        catch (Exception ex3) {}
        return !shouldNotLosePP;
    }
}
