package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.items.LureItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(value = PlayerPartyStorage.class, remap = false)
public abstract class MixinPlayerPartyStorage extends PartyStorage {

    @Shadow @Nullable public abstract LureItem getLure();

    public MixinPlayerPartyStorage(UUID uuid) {
        super(uuid);
    }

    /**
     * @author Daniel Norris
     * @reason Efficiency
     */
    @Overwrite
    public float getMultiplier(AbstractSpawner spawner, SpawnInfo spawnInfo, float sum, float rarity) {
        float modifiedValue = 1.0F;
        if (this.getLure() != null) {
            modifiedValue *= this.getLure().getMultiplier(spawner, spawnInfo, sum, rarity);
        }

        var first = this.findFirst();

        if (first != null) {
            modifiedValue *= first.getAbility().getMultiplier(spawner, spawnInfo, sum, rarity);
        }

        return modifiedValue;
    }

    /**
     *
     * Adds an override for getAll
     *
     * @param condition
     * @return
     */
    @Override
    public Pokemon findOne(Predicate<Pokemon> condition) {
        for(var pokemon : this.inTemporaryMode() ? this.tempParty : this.party) {
            if (pokemon != null && condition.test(pokemon)) {
                return pokemon;
            }
        }

        return null;
    }

    private Pokemon findFirst() {
        for(var pokemon : this.inTemporaryMode() ? this.tempParty : this.party) {
            if (pokemon != null) {
                return pokemon;
            }
        }

        return null;
    }
}
