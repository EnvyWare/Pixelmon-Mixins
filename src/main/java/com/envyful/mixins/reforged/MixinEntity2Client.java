package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.client.render.IHasTexture;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity1Base;
import com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumGastrodon;
import com.pixelmonmod.pixelmon.enums.forms.EnumMagikarp;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity2Client.class)
public abstract class MixinEntity2Client extends Entity1Base implements IHasTexture {
    public MixinEntity2Client(World par1World) {
        super(par1World);
    }

    private transient EnumSpecies priorSpecies = null;
    private transient IEnumForm priorForm = null;

    @Inject(method = "evolve", at = @At("HEAD"), remap = false)
    public void onEvolveHEAD(PokemonSpec evolveTo, CallbackInfo ci) {
        this.priorSpecies = this.getPokemonData().getSpecies();
        this.priorForm = this.getPokemonData().getFormEnum();
    }

    @Inject(method = "evolve", at = @At("RETURN"), remap = false)
    public void onEvolveRETURN(PokemonSpec evolveTo, CallbackInfo ci) {
        if (this.priorSpecies == EnumSpecies.Magikarp) {
            if (this.priorForm == EnumMagikarp.ROASTED) {
                getPokemonData().setForm(EnumSpecial.Zombie);
            }
        }
        if (this.priorSpecies == EnumSpecies.Shellos) {
            if (this.priorForm.getForm() % 2 == 0) {
                getPokemonData().setForm(EnumGastrodon.East);
            } else {
                getPokemonData().setForm(EnumGastrodon.West);
            }
        }
    }
}
