package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityPokeChest;
import com.pixelmonmod.pixelmon.util.LootClaim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

@Mixin(TileEntityPokeChest.class)
public class MixinTileEntityPokeChest {

    @Shadow private ArrayList<LootClaim> claimed;

    /**
     * @author danorris709
     */
    @Overwrite(remap = false)
    public LootClaim getLootClaim(UUID playerID) {
        Iterator<LootClaim> var2 = this.claimed.iterator();

        while (var2.hasNext()) {
            LootClaim next = var2.next();

            if (Objects.equals(next.getPlayerID(), playerID)) {
                return next;
            }
        }

        return null;
    }

}
