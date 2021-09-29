package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pokeballs.EnumPokeBallMode;
import com.pixelmonmod.pixelmon.entities.pokeballs.captures.CaptureBase;
import com.pixelmonmod.pixelmon.entities.pokeballs.captures.CaptureBeastBall;
import com.pixelmonmod.pixelmon.enums.forms.EnumSpecial;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CaptureBeastBall.class)
public abstract class MixinCaptureBeastBall extends CaptureBase {

    public MixinCaptureBeastBall(EnumPokeballs pokeball) {
        super(pokeball);
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public double getBallBonus(EnumPokeballs type, EntityPlayer thrower, Pokemon p2, EnumPokeBallMode mode) {
        if (p2.getSpecies().isUltraBeast()) {
            return 5.0D;
        } else {
            return p2.getSpecies().getPossibleForms(true).contains(EnumSpecial.Alien) ? 1.0D : 0.1D;
        }
    }
}
