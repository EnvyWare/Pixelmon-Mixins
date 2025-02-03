package com.envyful.mixins.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.pixelmonmod.pixelmon.api.util.helpers.WorldHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ChunkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Mixin(value = WorldHelper.class, remap = false)
public class MixinWorldHelper {

    @Shadow @Final private static Map<String, Structure<?>> CACHED_STRUCTURES;
    private static final Map<ResourceLocation, Map<Pair<Integer, Integer>, List<StructureStart<?>>>> FASTER_STRUCTURES = Maps.newConcurrentMap();

    /**
     * @author Daniel
     * @reason Change map
     */
    @Overwrite(remap = false)
    public static boolean insideStructure(ServerWorld world, String structure, BlockPos pos) {
        Structure<?> value = CACHED_STRUCTURES.computeIfAbsent(structure.toLowerCase(Locale.ROOT), (s) -> Structure.STRUCTURES_REGISTRY.get(structure));
        if (value == null) {
            return false;
        } else {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            var dimensionStructures = FASTER_STRUCTURES.computeIfAbsent(world.dimension().location(), (unused) -> Maps.newHashMap());
            var structureStarts = dimensionStructures.get(Pair.of(chunkX, chunkZ));

            if (structureStarts != null && !structureStarts.isEmpty()) {
                for (var structureStart : structureStarts) {
                    if (Objects.equals(structureStart.getFeature(), value) && structureStart.getBoundingBox().isInside(pos)) {
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }
    }

    /**
     * @author Daniel
     * @reason Change map
     */
    @Overwrite
    public static void onLoad(ChunkEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            if (!event.getChunk().getAllStarts().isEmpty()) {
                Map<Pair<Integer, Integer>, List<StructureStart<?>>> dimensionStructures = FASTER_STRUCTURES.computeIfAbsent(((ServerWorld)event.getWorld()).dimension().location(), (unused) -> Maps.newHashMap());
                List<StructureStart<?>> structureStarts = dimensionStructures.computeIfAbsent(Pair.of(event.getChunk().getPos().x, event.getChunk().getPos().z), (___) -> Lists.newCopyOnWriteArrayList());
                structureStarts.addAll(event.getChunk().getAllStarts().values());
            }
        }
    }

    /**
     * @author Daniel
     * @reason Change map
     */
    @Overwrite
    public static void onUnload(ChunkEvent.Unload event) {
        if (event.getWorld() instanceof ServerWorld) {
            var dimensionStructures = FASTER_STRUCTURES.computeIfAbsent(((ServerWorld)event.getWorld()).dimension().location(), (unused) -> Maps.newHashMap());
            dimensionStructures.remove(Pair.of(event.getChunk().getPos().x, event.getChunk().getPos().z));
        }
    }
}
