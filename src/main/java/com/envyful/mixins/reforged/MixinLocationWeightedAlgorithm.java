package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.IRarityTweak;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.algorithms.selection.LocationWeightedAlgorithm;
import com.pixelmonmod.pixelmon.api.spawning.calculators.ISelectionAlgorithm;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Mixin(LocationWeightedAlgorithm.class)
public abstract class MixinLocationWeightedAlgorithm implements ISelectionAlgorithm {

    private static final long ONE_MINUTE = TimeUnit.SECONDS.toMillis(10);

    private Long lastUpdate = null;
    private SpawnInfo cached = null;

    @Override
    public SpawnInfo choose(AbstractSpawner spawner, SpawnLocation spawnLocation, ArrayList<SpawnInfo> spawnInfos) {
        if (spawnInfos.isEmpty() || (spawnInfos.size() == 1 && spawnInfos.get(0).rarity <= 0)) {
            return null;
        }

        if (this.lastUpdate != null && this.cached != null) {
            if ((System.currentTimeMillis() - this.lastUpdate) <= ONE_MINUTE) {
                return this.cached;
            }
        }

        SpawnInfo percentSelection = this.chooseViaPercentage(spawner, spawnInfos);

        if (percentSelection != null) {
            return percentSelection;
        }

        if (spawnInfos.size() == 1)
            return spawnInfos.get(0);

        HashMap<SpawnInfo, Float> finalRarities = new HashMap<>();

        float raritySum = 0;
        for (SpawnInfo spawnInfo : spawnInfos)
        {
            float rarity = spawnInfo.getAdjustedRarity(spawner, spawnLocation);
            finalRarities.put(spawnInfo, rarity);
            raritySum += rarity;
        }

        for (SpawnInfo spawnInfo : spawnInfos)
        {
            float rarity = finalRarities.get(spawnInfo);

            for (IRarityTweak rarityTweak : spawner.rarityTweaks)
            {
                rarity = rarity * rarityTweak.getMultiplier(spawner, spawnInfo, raritySum, rarity);
                raritySum += (rarity - finalRarities.get(spawnInfo));
            }

            finalRarities.put(spawnInfo, rarity);
        }

        if (raritySum <= 0)
            return null;

        float selected = RandomHelper.getRandomNumberBetween(0, raritySum);
        raritySum = 0;
        for (SpawnInfo spawnInfo : spawnInfos) {
            if ((raritySum += finalRarities.get(spawnInfo)) >= selected && finalRarities.get(spawnInfo) != 0) {
                this.cached = spawnInfo;
                this.lastUpdate = System.currentTimeMillis();
                return spawnInfo;
            }
        }

        Pixelmon.LOGGER.log(Level.WARN, "Unable to choose a SpawnInfo based on rarities. This shouldn't be possible.");
        return null;
    }
}
