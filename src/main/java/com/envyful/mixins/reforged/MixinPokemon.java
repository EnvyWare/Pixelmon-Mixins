package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pokemon.class)
public abstract class MixinPokemon extends PokemonBase {

    @Shadow public abstract void setForm(int form);

    @Inject(method = "readFromByteBuffer", at = @At("RETURN"), remap = false)
    public void onReadFromByteBuffer(ByteBuf buf, EnumUpdateType[] data, CallbackInfoReturnable<Pokemon> cir) {
        this.setForm(this.form);
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = false)
    public void onReadFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
        this.setForm(this.form);
    }
}
