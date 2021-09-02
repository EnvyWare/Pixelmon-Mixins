package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PokemonStorage;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(PartyStorage.class)
public abstract class MixinPartyStorage extends PokemonStorage {

    @Shadow(remap = false) protected Pokemon[] party;

    public MixinPartyStorage(UUID uuid) {
        super(uuid);
    }

    private transient List<Pokemon> partyAsList = new ArrayList<>();

    @Inject(method = "getTeam", at = @At("HEAD"), cancellable = true, remap = false)
    public void onGetTeamHead(CallbackInfoReturnable<List<Pokemon>> callbackInfoReturnable) {
        if (!this.getShouldSave()) {
            callbackInfoReturnable.setReturnValue(this.partyAsList);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = "getTeam", at = @At("RETURN"), remap = false)
    public void onGetTeamReturn(CallbackInfoReturnable<List<Pokemon>> callbackInfoReturnable) {
        this.partyAsList = callbackInfoReturnable.getReturnValue();
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"), remap = false)
    public void onReadFromNBT(NBTTagCompound nbt, CallbackInfoReturnable<PartyStorage> callbackInfoReturnable) {
        this.setHasChanged(true);
    }

    /**
     *
     * ArrayIndexOutOfBoundsException fix
     *
     * @author
     */
    @Nullable
    @Overwrite(remap = false)
    public Pokemon get(StoragePosition position)
    {
        if (this.party.length <= position.order) {
            return null;
        }

        return party[position.order];
    }
}
