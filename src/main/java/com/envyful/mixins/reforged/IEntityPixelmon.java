package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.network.datasync.DataParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPixelmon.class)
public interface IEntityPixelmon {

    @Accessor(remap = false)
    DataParameter<Byte> getDwBossMode();

}
