package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Pokemon.class)
public abstract class MixinPokemon extends PokemonBase {

    @Shadow public abstract void setForm(int form);

    /**
     * @author
     */
    @Overwrite(remap = false)
    public boolean isLegendary() {
        return this.getSpecies().isLegendary();
    }


    @Redirect(
            method = "Lcom/pixelmonmod/pixelmon/api/pokemon/Pokemon;initialize([Lcom/pixelmonmod/pixelmon/api/pokemon/EnumInitializeCategory;)Lcom/pixelmonmod/pixelmon/api/pokemon/Pokemon;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"
            ),
            remap = false
    )
    public boolean onContains(ArrayList arrayList, Object o) {
        if (arrayList.size() > 25) {
            return this.isLegendary();
        } else {
            return this.species.isUltraBeast();
        }
    }

    @Inject(method = "readFromByteBuffer", at = @At("RETURN"), remap = false)
    public void onReadFromByteBuffer(ByteBuf buf, EnumUpdateType[] data, CallbackInfoReturnable<Pokemon> cir) {
        this.setForm(this.form);
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = false)
    public void onReadFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
        this.setForm(this.form);
    }
}
