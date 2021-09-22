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

    /**
     * @author
     */
    @Overwrite(remap = false)
    public List<Pokemon> getTeam() {
        if (this.partyAsList == null || this.getShouldSave()) {
            List<Pokemon> team = new ArrayList();
            Pokemon[] var2 = this.party;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Pokemon pokemon = var2[var4];
                if (pokemon != null && !pokemon.isEgg()) {
                    team.add(pokemon);
                }
            }

            this.partyAsList = team;
        }

        return this.partyAsList;
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
