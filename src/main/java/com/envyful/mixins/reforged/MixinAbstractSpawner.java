package com.envyful.mixins.reforged;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mixin(AbstractSpawner.class)
public class MixinAbstractSpawner {

    @Shadow public transient HashMap<Biome, List<SpawnInfo>> cacheSets;

    @Shadow public List<SpawnSet> spawnSets;

    /**
     * @author
     */
    @Overwrite(remap = false)
    public ArrayList<SpawnInfo> getSuitableSpawns(SpawnLocation spawnLocation) {
        if (cacheSets == null) {
            this.cacheSets = Maps.newHashMap();
        }
        List<SpawnInfo> spawnInfos = this.cacheSets.get(spawnLocation.biome);

        if (spawnInfos != null) {
            return (ArrayList<SpawnInfo>) spawnInfos;
        }

        ArrayList<SpawnInfo> suitableSpawns = new ArrayList<>();

        for (SpawnInfo spawnInfo : cacheSets.get(spawnLocation.biome)) {
            if (spawnInfo.fits((AbstractSpawner) ((Object) this), spawnLocation)) {
                suitableSpawns.add(spawnInfo);
            }
        }

        this.cacheSets.put(spawnLocation.biome, suitableSpawns);
        return suitableSpawns;
    }
}
