package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.battles.BattleAIMode;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.events.SpawnPixelmonEntityForBattleEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraft.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(TrainerParticipant.class)
public abstract class MixinTrainerParticipant extends BattleParticipant {


    @Shadow(remap = false) public NPCTrainer trainer;

    @Shadow(remap = false) protected abstract void setPokemonInBattle();

    public MixinTrainerParticipant(int numControlledPokemon) {
        super(numControlledPokemon);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void startBattle() {
        this.controlledPokemon.clear();
        this.trainer.setBattleController(this.bc);
        PixelmonWrapper[] var1 = this.allPokemon;
        int var2 = var1.length;

        int var3;
        for(var3 = 0; var3 < var2; ++var3) {
            PixelmonWrapper pw = var1[var3];
            pw.bc = this.bc;
            if (pw.entity != null) {
                pw.entity.goalSelector.disableControlFlag(Goal.Flag.MOVE);
                pw.entity.goalSelector.disableControlFlag(Goal.Flag.JUMP);
                pw.entity.goalSelector.disableControlFlag(Goal.Flag.TARGET);
                pw.entity.setXxa(0);
                pw.entity.setZza(0);
                pw.entity.setYya(0);
                pw.entity.hasImpulse = true;
                pw.entity.getNavigation().stop();
            }
        }

        if (this.trainer.getBossTier().isBoss()) {
            int lvl = 1;
            Iterator var7 = this.bc.participants.iterator();

            while(var7.hasNext()) {
                BattleParticipant p = (BattleParticipant)var7.next();
                if (p.team != this.team && p instanceof PlayerParticipant) {
                    lvl = Math.max(lvl, ((PlayerParticipant)p).getHighestLevel());
                }
            }

            lvl = (int)((double)lvl * this.trainer.getBossTier().getLevelMultiplier());
            lvl += this.trainer.getBossTier().getExtraLevels();
            PixelmonWrapper[] var8 = this.allPokemon;
            var3 = var8.length;

            for(int var13 = 0; var13 < var3; ++var13) {
                PixelmonWrapper pw = var8[var13];
                pw.bc = this.bc;
                pw.setTempLevel(lvl);
            }
        }

        this.setPokemonInBattle();
        var1 = this.allPokemon;
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            PixelmonWrapper pw = var1[var3];
            if (pw.onBattlefield) {
                this.controlledPokemon.add(pw);
            }
        }

        if (!Pixelmon.EVENT_BUS.post(new SpawnPixelmonEntityForBattleEvent.Pre(((PixelmonWrapper)this.controlledPokemon.get(0)).pokemon))) {
            Iterator var9 = this.controlledPokemon.iterator();

            while(var9.hasNext()) {
                PixelmonWrapper pw = (PixelmonWrapper)var9.next();
                pw.entity = this.trainer.releasePokemon(pw.getPokemonUUID());

                if (pw.entity != null) {
                    pw.entity.goalSelector.disableControlFlag(Goal.Flag.MOVE);
                    pw.entity.goalSelector.disableControlFlag(Goal.Flag.JUMP);
                    pw.entity.goalSelector.disableControlFlag(Goal.Flag.TARGET);
                    pw.entity.setXxa(0);
                    pw.entity.setZza(0);
                    pw.entity.setYya(0);
                    pw.entity.hasImpulse = true;
                    pw.entity.getNavigation().stop();
                }
            }

            Pixelmon.EVENT_BUS.post(new SpawnPixelmonEntityForBattleEvent.Post(((PixelmonWrapper)this.controlledPokemon.get(0)).pokemon, ((PixelmonWrapper)this.controlledPokemon.get(0)).entity));
        }

        super.startBattle();
        var1 = this.allPokemon;
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            PixelmonWrapper  pw = var1[var3];
            pw.setHealth(pw.getMaxHealth());
            pw.enableReturnHeldItem();
        }

        BattleAIMode battleAIMode = this.trainer.getBattleAIMode();
        if (battleAIMode == BattleAIMode.DEFAULT) {
            battleAIMode = PixelmonConfigProxy.getBattle().getBattleAITrainer();
        }

        this.setBattleAI(battleAIMode.createAI(this));
        this.trainer.startBattle((BattleParticipant)this.bc.getOpponents(this).get(0));
    }


}
