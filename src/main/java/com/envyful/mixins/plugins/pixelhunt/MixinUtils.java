package com.envyful.mixins.plugins.pixelhunt;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.xpgaming.pixelhunt.PixelHuntForge;
import com.xpgaming.pixelhunt.Storage;
import com.xpgaming.pixelhunt.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Utils.class)
public class MixinUtils {

    /**
     * @author
     */
    @Overwrite(remap = false)
    public int isInHunt(Pokemon pokemon) {
        if (pokemon == null) {
            return 0;
        }

        if (pokemon.getSpecies() == Storage.pokemon.get(0).getSpecies()) {
            return 1;
        } else if (pokemon.getSpecies() == Storage.pokemon.get(1).getSpecies()) {
            return 2;
        } else if (pokemon.getSpecies() == Storage.pokemon.get(2).getSpecies()) {
            return 3;
        } else {
            return pokemon.getSpecies() == Storage.pokemon.get(3).getSpecies() ? 4 : 0;
        }
    }

    @Inject(method = "randomisePokemon", at = @At("RETURN"), remap = false)
    public void onRandomisePokemon(int slot, CallbackInfo ci) {
        switch (slot) {
            case 1 :
                Storage.pokemon.set(0, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon1)));
                break;
            case 2 :
                Storage.pokemon.set(1, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon2)));
                break;
            case 3 :
                Storage.pokemon.set(2, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon3)));
                break;
            case 4 :
                Storage.pokemon.set(3, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon4)));
                break;
        }
    }

    @Inject(method = "initialisePokemon", at = @At("RETURN"), remap = false)
    public void onInitializePokemon(CallbackInfo ci) {
        for (int slot = 1; slot <= 4; slot++) {
            switch (slot) {
                case 1:
                    Storage.pokemon.set(0, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon1)));
                    break;
                case 2:
                    Storage.pokemon.set(1, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon2)));
                    break;
                case 3:
                    Storage.pokemon.set(2, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon3)));
                    break;
                case 4:
                    Storage.pokemon.set(3, Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(PixelHuntForge.pokemon4)));
                    break;
            }
        }
    }
}
