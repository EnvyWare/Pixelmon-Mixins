package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base;
import com.pixelmonmod.pixelmon.entities.pixelmon.PathNavigateGroundLarge;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity1Base.class)
public abstract class MixinEntity1Base extends EntityTameable {

    @Shadow public abstract Pokemon getPokemonData();

    public MixinEntity1Base(World worldIn) {
        super(worldIn);
    }

    @Shadow @Final private static DataParameter<Integer> dwSpecies;

    @Shadow @Final private static DataParameter<Byte> dwGrowth;

    @Shadow public abstract EnumBossMode getBossMode();

    @Inject(method = "setPokemon", at = @At("RETURN"), remap = false)
    public void onSetPokemon(Pokemon pokemon, CallbackInfo callbackInfo) {
        if (pokemon.getGrowth().scaleOrdinal > 5 || (this.getBossMode() != null && this.getBossMode().scaleFactor > 1.0F)) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Inject(method = "func_70037_a(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"), remap = false)
    public void onReadEntityFromNBT(NBTTagCompound nbt, CallbackInfo callbackInfo) {
        if (this.getPokemonData().getGrowth().scaleOrdinal > 5 || (this.getBossMode() != null && this.getBossMode().scaleFactor > 1.0F)) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Inject(method = "func_184206_a(Lnet/minecraft/network/datasync/DataParameter;)V", at = @At("RETURN"), remap = false)
    public void onNotifyDataManagerChange(DataParameter<?> key, CallbackInfo callbackInfo) {
        if ((key.getId() == dwSpecies.getId() || key.getId() == dwGrowth.getId()) && this.getPokemonData().getGrowth().scaleOrdinal > 5) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }

        if (this instanceof IEntityPixelmon && key.getId() == ((IEntityPixelmon) this).getDwBossMode().getId() &&
                this.getBossMode() != null && this.getBossMode().scaleFactor > 1.0F) {

        }
    }
}
