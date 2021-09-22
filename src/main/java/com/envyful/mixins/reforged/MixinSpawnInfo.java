package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.conditions.RarityMultiplier;
import com.pixelmonmod.pixelmon.config.BetterSpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Mixin(SpawnInfo.class)
public class MixinSpawnInfo {

    @Shadow public float rarity;
    @Shadow public ArrayList<RarityMultiplier> rarityMultipliers;
    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);

    private Float rarityCache = null;
    private Long lastUpdate = null;


    /**
     * @author danorris709
     * @reason allocations
     */
    @Overwrite(remap = false)
    public float getAdjustedRarity(AbstractSpawner spawner, SpawnLocation spawnLocation) {
        if (this.rarityCache != null && this.lastUpdate != null) {
            if ((System.currentTimeMillis() - this.lastUpdate) <= ONE_MINUTE) {
                return this.rarityCache;
            }
        }

        float rarity = this.rarity;
        Iterator var4;
        RarityMultiplier rarityMultiplier;
        if (this.rarityMultipliers != null && !this.rarityMultipliers.isEmpty()) {
            for(var4 = this.rarityMultipliers.iterator(); var4.hasNext(); rarity = rarityMultiplier.apply((SpawnInfo) ((Object) this), spawnLocation, rarity)) {
                rarityMultiplier = (RarityMultiplier)var4.next();
            }
        }

        for(var4 = spawner.rarityMultipliers.iterator(); var4.hasNext(); rarity = rarityMultiplier.apply((SpawnInfo) ((Object) this), spawnLocation, rarity)) {
            rarityMultiplier = (RarityMultiplier)var4.next();
        }

        for(var4 = BetterSpawnerConfig.INSTANCE.globalRarityMultipliers.iterator(); var4.hasNext(); rarity = rarityMultiplier.apply((SpawnInfo) ((Object) this), spawnLocation, rarity)) {
            rarityMultiplier = (RarityMultiplier)var4.next();
        }

        this.rarityCache = rarity;
        this.lastUpdate = System.currentTimeMillis();
        return rarity;
    }
}
