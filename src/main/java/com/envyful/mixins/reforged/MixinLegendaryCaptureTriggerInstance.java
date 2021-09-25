package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.advancements.triggers.LegendaryCaptureTrigger;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LegendaryCaptureTrigger.Instance.class)
public class MixinLegendaryCaptureTriggerInstance extends AbstractCriterionInstance {

    @Shadow(remap = false) EnumSpecies pokemon;
    @Shadow(remap = false) String name;

    public MixinLegendaryCaptureTriggerInstance(ResourceLocation criterionIn) {
        super(criterionIn);
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public boolean test(EnumSpecies pokemon) {
        if (this.pokemon == null && this.name.equalsIgnoreCase("legendary") && pokemon.isLegendary()) {
            return true;
        } else {
            return this.pokemon == pokemon;
        }
    }
}
