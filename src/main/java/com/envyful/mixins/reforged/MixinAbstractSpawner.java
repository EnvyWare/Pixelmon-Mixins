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
import java.util.Iterator;
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
        ArrayList<SpawnInfo> suitableSpawns = new ArrayList();
        Iterator var3;
        if (this.cacheSets != null) {
            if (!this.cacheSets.containsKey(spawnLocation.biome)) {
                return new ArrayList();
            }

            var3 = ((List)this.cacheSets.get(spawnLocation.biome)).iterator();

            while(var3.hasNext()) {
                SpawnInfo spawnInfo = (SpawnInfo)var3.next();
                if (spawnInfo.fits((AbstractSpawner) ((Object) this), spawnLocation)) {
                    suitableSpawns.add(spawnInfo);
                }
            }
        } else {
            var3 = this.spawnSets.iterator();

            while(var3.hasNext()) {
                SpawnSet set = (SpawnSet)var3.next();
                suitableSpawns.addAll(set.suitableSpawnsFor((AbstractSpawner) ((Object) this), spawnLocation));
            }
        }

        return suitableSpawns;
    }
}
