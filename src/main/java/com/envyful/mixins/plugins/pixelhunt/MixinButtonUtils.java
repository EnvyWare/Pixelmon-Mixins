package com.envyful.mixins.plugins.pixelhunt;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.xpgaming.pixelhunt.Storage;
import com.xpgaming.pixelhunt.utils.ButtonUtils;
import com.xpgaming.pixelhunt.utils.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.List;

@Mixin(ButtonUtils.class)
public class MixinButtonUtils {

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static List<Button> getHuntButtons(EntityPlayerMP player) {
        List<Button> huntList = new ArrayList();
        Pokemon pokemon1 = Storage.pokemon.get(0);
        Pokemon pokemon2 = Storage.pokemon.get(1);
        Pokemon pokemon3 = Storage.pokemon.get(2);
        Pokemon pokemon4 = Storage.pokemon.get(3);
        Button pokeButton1 = GooeyButton.builder().display(Utils.getPokemonPhoto(pokemon1)).title(Utils.regex("&6#1 &e" + pokemon1.getSpecies().getPokemonName())).lore(Utils.getHuntInfo("nature1", "reward1", "expiry1")).build();
        Button pokeButton2 = GooeyButton.builder().display(Utils.getPokemonPhoto(pokemon2)).title(Utils.regex("&6#2 &e" + pokemon2.getSpecies().getPokemonName())).lore(Utils.getHuntInfo("nature2", "reward2", "expiry2")).build();
        Button pokeButton3 = GooeyButton.builder().display(Utils.getPokemonPhoto(pokemon3)).title(Utils.regex("&6#3 &e" + pokemon3.getSpecies().getPokemonName())).lore(Utils.getHuntInfo("nature3", "reward3", "expiry3")).build();
        Button pokeButton4 = GooeyButton.builder().display(Utils.getPokemonPhoto(pokemon4)).title(Utils.regex("&6#4 &e" + pokemon4.getSpecies().getPokemonName())).lore(Utils.getHuntInfo("nature4", "reward4", "expiry4")).build();
        huntList.add(pokeButton1);
        huntList.add(pokeButton2);
        huntList.add(pokeButton3);
        huntList.add(pokeButton4);
        return huntList;
    }
}
