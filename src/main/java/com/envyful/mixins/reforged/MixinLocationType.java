package com.envyful.mixins.reforged;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.spawning.conditions.LocationType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(LocationType.class)
public class MixinLocationType {

    private static final Map<Block, List<LocationType>> CACHED_TYPES = Maps.newConcurrentMap();

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static ArrayList<LocationType> getPotentialTypes(IBlockState state) {
        List<LocationType> locationTypes = CACHED_TYPES.getOrDefault(state.getBlock(), Collections.emptyList());

        if (!locationTypes.isEmpty()) {
            return (ArrayList<LocationType>) locationTypes;
        }

        ArrayList<LocationType> types = new ArrayList();
        Iterator var2 = locationTypes.iterator();

        while(var2.hasNext()) {
            LocationType type = (LocationType)var2.next();
            if (state != null && type.baseBlockCondition.test(state)) {
                types.add(type);
            }
        }

        if (!types.isEmpty()) {
            CACHED_TYPES.put(state.getBlock(), types);
        }

        return types;
    }
}
