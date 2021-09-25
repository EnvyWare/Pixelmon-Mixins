package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.client.render.IHasTexture;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityStatue.class)
public abstract class MixinEntityStatue extends EntityLiving implements IHasTexture, IAnimals {

    public MixinEntityStatue(World worldIn) {
        super(worldIn);
    }

    @Redirect(
            method = "Lcom/pixelmonmod/pixelmon/entities/pixelmon/EntityStatue;func_70636_d()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/pixelmonmod/pixelmon/entities/pixelmon/EntityStatue;func_191986_a(FFF)V"
            ),
            remap = false
    )
    public void onTravelCall(EntityStatue entityStatue, float strafe, float vertical, float forward) {
    }
}
