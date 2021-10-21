package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.comm.SetTrainerData;
import com.pixelmonmod.pixelmon.entities.npcs.EntityNPC;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NPCTrainer.class)
public abstract class MixinNPCTrainer extends EntityNPC {

    public MixinNPCTrainer(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "update", at = @At("RETURN"), remap = false)
    public void update(SetTrainerData p, CallbackInfo ci) {
        if (p.name != null && !p.name.isEmpty()) {
            this.setName(p.name);
        }
    }
}
