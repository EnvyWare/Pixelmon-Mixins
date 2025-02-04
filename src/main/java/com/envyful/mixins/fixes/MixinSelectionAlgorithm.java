package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.RarityTweak;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.calculators.SelectionAlgorithm;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = SelectionAlgorithm.class, remap = false)
public interface MixinSelectionAlgorithm {

    @Shadow @Nullable
    SpawnInfo chooseViaPercentage(AbstractSpawner spawner, List<SpawnInfo> spawnInfos);

    /**
     * @author Daniel
     * @reason Use Object2FloatMap to reduce boxing and unboxing
     */
    @Overwrite
    default SpawnInfo choose(AbstractSpawner spawner, SpawnLocation spawnLocation, List<SpawnInfo> spawnInfos) {
        if (!spawnInfos.isEmpty() && (spawnInfos.size() != 1 || !((spawnInfos.get(0)).rarity <= 0.0F))) {
            SpawnInfo percentSelection = this.chooseViaPercentage(spawner, spawnInfos);
            if (percentSelection != null) {
                return percentSelection;
            } else if (spawnInfos.size() == 1) {
                return spawnInfos.get(0);
            } else {
                Object2FloatMap<SpawnInfo> finalRarities = new Object2FloatOpenHashMap<>(spawnInfos.size());
                float raritySum = 0.0F;

                for(SpawnInfo spawnInfo : spawnInfos) {
                    float rarity = spawnInfo.getAdjustedRarity(spawner, spawnLocation);
                    finalRarities.put(spawnInfo, rarity);
                    raritySum += rarity;
                }

                for(SpawnInfo spawnInfo : spawnInfos) {
                    float rarity = finalRarities.getFloat(spawnInfo);

                    for(RarityTweak rarityTweak : spawner.rarityTweaks) {
                        rarity *= rarityTweak.getMultiplier(spawner, spawnInfo, raritySum, rarity);
                        raritySum += rarity - finalRarities.getFloat(spawnInfo);
                    }

                    finalRarities.put(spawnInfo, rarity);
                }

                if (raritySum <= 0.0F) {
                    return null;
                } else {
                    float selected = RandomHelper.getRandomNumberBetween(0.0F, raritySum);
                    raritySum = 0.0F;

                    for(var spawnInfo : spawnInfos) {
                        if ((raritySum += finalRarities.getFloat(spawnInfo)) >= selected && finalRarities.getFloat(spawnInfo) != 0.0F) {
                            return spawnInfo;
                        }
                    }

                    Pixelmon.LOGGER.log(Level.WARN, "Unable to choose a SpawnInfo based on rarities. This shouldn't be possible.");
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}
