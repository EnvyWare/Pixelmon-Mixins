package com.envyful.mixins.reforged;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.ExtraStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.extraStats.*;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.lang.reflect.Constructor;
import java.util.Map;

@Mixin(ExtraStats.class)
public class MixinExtraStats {

    private static final transient Map<EnumSpecies, Constructor<ExtraStats>> STATS_CONSTRUCTORS = Maps.newHashMap();

    static {
        getConstructor(EnumSpecies.Mew, MewStats.class);
        getConstructor(EnumSpecies.Azelf, LakeTrioStats.class);
        getConstructor(EnumSpecies.Uxie, LakeTrioStats.class);
        getConstructor(EnumSpecies.Mesprit, LakeTrioStats.class);
        getConstructor(EnumSpecies.Meltan, MeltanStats.class);
        getConstructor(EnumSpecies.Mareep, ShearableStats.class);
        getConstructor(EnumSpecies.Minior, MiniorStats.class);
        getConstructor(EnumSpecies.Wooloo, ShearableStats.class);
        getConstructor(EnumSpecies.Dubwool, ShearableStats.class);
    }

    @SuppressWarnings("unchecked")
    private static void getConstructor(EnumSpecies species, Class<? extends ExtraStats> clazz) {
        try {
            STATS_CONSTRUCTORS.put(species, (Constructor<ExtraStats>) clazz.getConstructor());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author danorris709
     * @reason Optimizes the reflection used in {@link ExtraStats}
     */
    @Overwrite(remap = false)
    public static ExtraStats getExtraStats(EnumSpecies species) {
        Constructor<ExtraStats> constructor = STATS_CONSTRUCTORS.get(species);

        if (constructor == null) {
            return null;
        }

        try {
            return constructor.newInstance();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        return null;
    }
}
