package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.ability.Ability;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbilityRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.ability.abilities.ComingSoon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.Moveset;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.ImmutableAttack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(Moveset.class)
public abstract class MixinMoveset {

    @Shadow public abstract void clear();

    @Shadow public abstract boolean add(Attack a);

    @Shadow protected String tempAbility;

    @Shadow protected Ability ability;

    @Shadow @Final private List<ImmutableAttack> reminderMoves;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void readFromNBT(CompoundNBT nbt) {
        this.clear();
        ListNBT list = nbt.getList("Moveset", 10);
        Iterator var3 = list.iterator();

        INBT base;
        while(var3.hasNext()) {
            base = (INBT)var3.next();
            CompoundNBT compound = (CompoundNBT)base;
            String moveID = compound.getString("MoveID");
            Optional<ImmutableAttack> attack = AttackRegistry.getAttackBase(moveID);
            short movePP;
            if (!attack.isPresent()) {
                movePP = compound.getShort("MoveID");
                attack = AttackRegistry.fromAttackIndex(movePP);
            }

            if (attack.isPresent()) {
                movePP = compound.getShort("MovePP");
                int movePPLevel = compound.getInt("MovePPLevel");
                Attack mutableAttack = (attack.get()).ofMutable();
                mutableAttack.pp = movePP;
                mutableAttack.ppLevel = movePPLevel;
                this.add(mutableAttack);
            }
        }

        if (nbt.contains("TempAbility")) {
            this.tempAbility = nbt.getString("TempAbility");
        }

        if (nbt.contains("Ability")) {
            this.ability = AbilityRegistry.getAbility(nbt.getString("Ability")).orElse(ComingSoon.noAbility);
        }

        this.reminderMoves.clear();
        if (nbt.contains("RelrnMoves")) {
            list = nbt.getList("RelrnMoves", 8);
            var3 = list.iterator();

            while(var3.hasNext()) {
                base = (INBT)var3.next();
                Optional var10000 = AttackRegistry.getAttackBase(base.getAsString());
                List var10001 = this.reminderMoves;
                var10000.ifPresent(var10001::add);
            }
        }
    }
}
