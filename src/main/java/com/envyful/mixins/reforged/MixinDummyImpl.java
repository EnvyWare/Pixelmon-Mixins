package com.envyful.mixins.reforged;

import com.google.common.collect.Sets;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnLocationEvent;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.calculators.ICalculateSpawnLocations;
import com.pixelmonmod.pixelmon.api.spawning.conditions.LocationType;
import com.pixelmonmod.pixelmon.api.world.BlockCollection;
import com.pixelmonmod.pixelmon.api.world.MutableLocation;
import com.pixelmonmod.pixelmon.config.BetterSpawnerConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Set;

@Mixin(ICalculateSpawnLocations.DummyImpl.class)
public class MixinDummyImpl implements ICalculateSpawnLocations {

    @Override
    public ArrayList<SpawnLocation> calculateSpawnableLocations(BlockCollection collection) {
        ArrayList<SpawnLocation> spawnableLocations = new ArrayList<>();

        World world = collection.world;
        int minX = collection.minX + MIN_DIAMETER;
        int minY = collection.minY + MIN_DIAMETER;
        int minZ = collection.minZ + MIN_DIAMETER;
        int maxX = collection.maxX - MIN_DIAMETER;
        int maxY = collection.maxY - MIN_DIAMETER;
        int maxZ = collection.maxZ - MIN_DIAMETER;

        for (int baseX = minX; baseX <= maxX; baseX++) {
            for (int baseZ = minZ; baseZ <= maxZ; baseZ++) {
                boolean canSeeSky = true;
                for (int skyY = 254; skyY >= maxY + 1; skyY--) {
                    if (!BetterSpawnerConfig.doesBlockSeeSky(collection.getBlockState(baseX, skyY + 1, baseZ))) {
                        canSeeSky = false;
                        break;
                    }
                }

                yLoop:
                for (int baseY = maxY - 1; baseY >= minY; baseY--) {
                    IBlockState state = collection.getBlockState(baseX, baseY + 1, baseZ);
                    if (state == null)
                        break;
                    if (canSeeSky && !BetterSpawnerConfig.doesBlockSeeSky(state))
                        canSeeSky = false;
                    ArrayList<LocationType> types = LocationType.getPotentialTypes(collection.getBlockState(baseX, baseY, baseZ));
                    if (types.isEmpty())
                        continue;

                    BlockPos base = new BlockPos(baseX, baseY + 1, baseZ);

                    int r = 0;
                    int diameter = 0;

                    rLoop:
                    while (diameter <= getMaxSpawnLocationDiameter()) {
                        int y = base.getY() + r;
                        for (int sign : new int[] { -1, 1 }) {
                            for (int x : new int[] { base.getX() + r * sign, base.getX() }) {
                                for (int z : new int[] { base.getZ() + r * sign, base.getZ() }) {
                                    if (x > maxX || x < minX || y > maxY || y < minY || z > maxZ || z < minZ) // Going out of bounds of the block collection
                                    {
                                        if (r <= MIN_DIAMETER) // And we haven't reached the threshold, trash this location
                                            continue yLoop;
                                        else // We reached the boundary but this is big enough to qualify as a location
                                            break rLoop;
                                    }

                                    IBlockState rstate = collection.getBlockState(x, y, z);
                                    if (rstate == null) // Void - we're done here
                                        break rLoop;

                                    if (diameter <= MIN_DIAMETER) // If we're below the minimum then if we run out of types it's game over, trash the location
                                    {
                                        types.removeIf(type -> !type.surroundingBlockCondition.test(rstate));
                                        if (types.isEmpty())
                                            continue yLoop;
                                    } else // If not then we'll stop expanding once we are starting to disqualify types. Maximises location potency.
                                        for (LocationType type : types)
                                            if (!type.surroundingBlockCondition.test(rstate))
                                                break rLoop;

                                    if (r == 0) // No need to check the base block - we just did.
                                        break;
                                }
                                if (r == 0) // No need to check the base block - we just did.
                                    break;
                            }
                            diameter++;
                        }
                        r++;
                    }

                    int searchRad = getMaxSpawnLocationDiameter();
                    Set<Block> uniqueBlocks = Sets.newHashSet();
                    for (int x = baseX - searchRad; x < baseX + searchRad; x++) {
                        if (x > collection.maxX || x < collection.minX)
                            continue;

                        for (int y = baseY - searchRad; y <= baseY + searchRad; y++) {
                            if (y > collection.maxY || y < collection.minY)
                                continue;

                            for (int z = baseZ - searchRad; z <= baseZ + searchRad; z++) {
                                if (z > collection.maxZ || z < collection.minZ)
                                    continue;

                                state = collection.getBlockState(x, y, z);
                                if (state != null)
                                    uniqueBlocks.add(state.getBlock());
                            }
                        }
                    }
                    final boolean fCanSeeSky = canSeeSky;

                    Set<LocationType> finalTypes = Sets.newHashSet();
                    for (LocationType type : types)
                        if ((type.seesSky == null || fCanSeeSky == type.seesSky.booleanValue())
                                && (type.neededNearbyBlockCondition == null || type.neededNearbyBlockCondition.test(uniqueBlocks)))
                            finalTypes.add(type);

                    if (finalTypes.isEmpty())
                        continue yLoop;

                    MutableLocation loc = new MutableLocation(world, baseX, baseY + 1, baseZ);

                    SpawnLocation spawnLocation = new SpawnLocation(
                            collection.cause,
                            loc,
                            finalTypes,
                            collection.getBlockState(baseX, baseY, baseZ).getBlock(),
                            uniqueBlocks,
                            collection.getBiome(baseX, baseZ),
                            canSeeSky,
                            diameter,
                            collection.getLight(baseX, baseY + 1, baseZ)
                    );

                    SpawnLocationEvent event = new SpawnLocationEvent(spawnLocation);
                    if (Pixelmon.EVENT_BUS.post(event))
                        continue;
                    spawnableLocations.add(event.getSpawnLocation());
                }
            }
        }

        return spawnableLocations;
    }

}
