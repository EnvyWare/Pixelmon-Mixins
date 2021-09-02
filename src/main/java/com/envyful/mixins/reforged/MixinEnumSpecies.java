package com.envyful.mixins.reforged;

import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(EnumSpecies.class)
public class MixinEnumSpecies {

    @Shadow(remap = false) @Final private static EnumSpecies[] VALUES;
    @Shadow(remap = false) @Final public String name;

    private static final Set<String> LEGENDS = Sets.newHashSet(
            "Articuno", "Zapdos", "Moltres", "Mewtwo", "Mew", "Raikou", "Entei", "Suicune", "Lugia", "Ho-Oh",
            "Celebi", "Regirock", "Regice", "Registeel", "Latias", "Latios", "Kyogre", "Groudon", "Rayquaza", "Jirachi",
            "Deoxys", "Uxie", "Mesprit", "Azelf", "Dialga", "Palkia", "Heatran", "Regigigas", "Giratina", "Cresselia",
            "Phione", "Manaphy", "Darkrai", "Shaymin", "Arceus", "Cobalion", "Terrakion", "Virizion", "Tornadus",
            "Thundurus", "Landorus", "Reshiram", "Zekrom", "Kyurem", "Keldeo", "Meloetta", "Genesect", "Victini",
            "Xerneas", "Yveltal", "Zygarde", "Diancie", "Hoopa", "Volcanion", "TypeNull", "Silvally", "TapuKoko",
            "TapuLele", "TapuBulu", "TapuFini", "Cosmog", "Cosmoem", "Solgaleo", "Lunala", "Necrozma", "Marshadow",
            "Magearna", "Zeraora", "Meltan", "Melmetal", "Zacian", "Zamazenta", "Eternatus", "Kubfu", "Urshifu",
            "Zarude", "Regieleki", "Regidrago", "Glastrier", "Spectrier", "Calyrex"
    );

    private static final Set<String> ULTRA_BEASTS = Sets.newHashSet(
            "Nihilego", "Buzzwole", "Pheromosa", "Xurkitree", "Celesteela", "Kartana", "Guzzlord",
            "Blacephalon", "Poipole", "Naganadel", "Stakataka", "Kartana"
    );

    private transient Boolean legendary = null;
    private transient Boolean ultrabeast = null;

    /**
     * @author
     */
    @Overwrite(remap = false)
    public boolean isLegendary() {
        if (this.legendary == null) {
            this.legendary = LEGENDS.contains(this.name);
        }

        return this.legendary;
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public boolean isUltraBeast() {
        if (this.ultrabeast == null) {
            this.ultrabeast = ULTRA_BEASTS.contains(this.name);
        }

        return this.ultrabeast;
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static EnumSpecies randomPoke(boolean canBeLegendary) {
        boolean isValid = false;
        EnumSpecies randomPokemon = EnumSpecies.Bulbasaur;
        while (!isValid) {
            int pick = RandomHelper.rand.nextInt(VALUES.length);
            randomPokemon = VALUES[pick];

            isValid = true;
            if (!canBeLegendary && randomPokemon.isLegendary()) {
                isValid = false;
            } else if (!PixelmonConfig.allGenerationsDisabled()
                    && !PixelmonConfig.isGenerationEnabled(randomPokemon.getGeneration())) {
                isValid = false;
            } else if (randomPokemon == EnumSpecies.MissingNo) {
                isValid = false;
            }
        }
        return randomPokemon;
    }
}
