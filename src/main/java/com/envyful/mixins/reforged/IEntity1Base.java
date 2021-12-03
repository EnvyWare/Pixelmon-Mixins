package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base;
import net.minecraft.network.datasync.DataParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity1Base.class)
public interface IEntity1Base {

    @Accessor(remap = false)
    DataParameter<Byte> getDwBossMode();

}
