package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity8HoldsItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPixelmon.class)
public abstract class MixinEntityPixelmon extends Entity8HoldsItems {

    public MixinEntityPixelmon(World par1World) {
        super(par1World);
    }

    /**
     *
     * Sponge's is more efficient
     *
     * @author danorris709
     */
    @Overwrite(remap = true)
    protected void despawnEntity() {
        super.despawnEntity();
    }
}
