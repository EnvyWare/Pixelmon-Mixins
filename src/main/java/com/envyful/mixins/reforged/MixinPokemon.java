package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.pokemon.EnumInitializeCategory;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.ComingSoon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.*;
import com.pixelmonmod.pixelmon.enums.*;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import com.pixelmonmod.pixelmon.util.helpers.CollectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@Mixin(Pokemon.class)
public abstract class MixinPokemon extends PokemonBase {
    /**
     * @author
     */
    @Overwrite(remap = false)
    public boolean isLegendary() {
        return this.getSpecies().isLegendary();
    }


    @Redirect(
            method = "Lcom/pixelmonmod/pixelmon/api/pokemon/Pokemon;initialize([Lcom/pixelmonmod/pixelmon/api/pokemon/EnumInitializeCategory;)Lcom/pixelmonmod/pixelmon/api/pokemon/Pokemon;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"
            ),
            remap = false
    )
    public boolean onContains(ArrayList arrayList, Object o) {
        if (arrayList.size() > 25) {
            return this.isLegendary();
        } else {
            return this.species.isUltraBeast();
        }
    }
}
