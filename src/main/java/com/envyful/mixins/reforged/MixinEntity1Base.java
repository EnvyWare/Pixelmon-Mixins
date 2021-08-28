package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base;
import com.pixelmonmod.pixelmon.entities.pixelmon.PathNavigateGroundLarge;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Entity1Base.class)
public abstract class MixinEntity1Base extends EntityTameable {

    @Shadow public abstract Pokemon getPokemonData();

    private EnumBossMode bossModeCache = null;

    public MixinEntity1Base(World worldIn) {
        super(worldIn);
    }

    @Shadow(remap = false) public abstract Map<String, DataParameter<?>> getDataWatcherMap();

    @Shadow public abstract EnumSpecies getSpecies();

    @Shadow @Final private static DataParameter<Integer> dwSpecies;

    @Shadow @Final private static DataParameter<Byte> dwGrowth;

    @Inject(method = "setPokemon", at = @At("RETURN"), remap = false)
    public void onSetPokemon(Pokemon pokemon, CallbackInfo callbackInfo) {
        if (pokemon.getGrowth().scaleOrdinal > 5) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Inject(method = "func_70037_a(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"), remap = false)
    public void onReadEntityFromNBT(NBTTagCompound nbt, CallbackInfo callbackInfo) {
        if (this.getPokemonData().getGrowth().scaleOrdinal > 5) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Inject(method = "func_184206_a(Lnet/minecraft/network/datasync/DataParameter;)V", at = @At("RETURN"), remap = false)
    public void onNotifyDataManagerChange(DataParameter<?> key, CallbackInfo callbackInfo) {
        if ((key.getId() == dwSpecies.getId() || key.getId() == dwGrowth.getId()) && this.getPokemonData().getGrowth().scaleOrdinal > 5) {
            this.navigator = new PathNavigateGroundLarge(this, this.world);
        }
    }

    @Inject(method = "getBossMode", at = @At("RETURN"), remap = false)
    public void onGetBossModeRETURN(CallbackInfoReturnable<EnumBossMode> cir) {
        if (this.bossModeCache == null) {
            this.bossModeCache = cir.getReturnValue();
        }
    }

    @Inject(method = "getBossMode", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGetBossModeHEAD(CallbackInfoReturnable<EnumBossMode> cir) {
        if (this.bossModeCache != null) {
            cir.setReturnValue(this.bossModeCache);
            cir.cancel();
        }
    }

    @Inject(method = "setBoss", at = @At("RETURN"), remap = false)
    public void onSetBoss(EnumBossMode mode, CallbackInfo ci) {
        this.bossModeCache = mode;
    }
}
