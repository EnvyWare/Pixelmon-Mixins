package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.StatChangeAction;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BattleStats.class)
public abstract class MixinBattleStats {

    @Shadow(remap = false) public abstract int[] getBattleStats();

    @Shadow(remap = false) public int attackStat;

    @Shadow(remap = false) public int defenseStat;

    @Shadow(remap = false) public int specialAttackStat;

    @Shadow(remap = false) public int specialDefenseStat;

    @Shadow(remap = false) public int speedStat;

    @Shadow(remap = false) private PixelmonWrapper pixelmon;

    /**
     * @author Daniel
     * @reason NPE
     */
    @Overwrite(remap = false)
    public void setBattleStatsForCurrentForm() {
        int[] statsBefore = this.getBattleStats().clone();
        this.attackStat = this.pixelmon.getStats().calculateStat(BattleStatsType.ATTACK, this.pixelmon.getNature(), this.pixelmon.getForm(), this.pixelmon.getPokemonLevelNum());
        this.defenseStat = this.pixelmon.getStats().calculateStat(BattleStatsType.DEFENSE, this.pixelmon.getNature(), this.pixelmon.getForm(), this.pixelmon.getPokemonLevelNum());
        this.specialAttackStat = this.pixelmon.getStats().calculateStat(BattleStatsType.SPECIAL_ATTACK, this.pixelmon.getNature(), this.pixelmon.getForm(), this.pixelmon.getPokemonLevelNum());
        this.specialDefenseStat = this.pixelmon.getStats().calculateStat(BattleStatsType.SPECIAL_DEFENSE, this.pixelmon.getNature(), this.pixelmon.getForm(), this.pixelmon.getPokemonLevelNum());
        this.speedStat = this.pixelmon.getStats().calculateStat(BattleStatsType.SPEED, this.pixelmon.getNature(), this.pixelmon.getForm(), this.pixelmon.getPokemonLevelNum());

        if (this.pixelmon != null && this.pixelmon.bc != null && this.pixelmon.bc.battleLog != null) {
            this.pixelmon.bc.battleLog.logEvent(new StatChangeAction(this.pixelmon.bc.battleTurn, this.pixelmon, statsBefore, this.getBattleStats()));
        }
    }

}
