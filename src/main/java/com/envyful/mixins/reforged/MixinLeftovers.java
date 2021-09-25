package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.enums.heldItems.EnumHeldItems;
import com.pixelmonmod.pixelmon.items.ItemHeld;
import com.pixelmonmod.pixelmon.items.heldItems.ItemLeftovers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemLeftovers.class)
public abstract class MixinLeftovers extends ItemHeld {

    public MixinLeftovers(EnumHeldItems heldItemType, String itemName) {
        super(heldItemType, itemName);
    }

    /**
     *
     *
     * @author danorris709
     * @reason Fixes a waiting glitch where the fainted pokemon attempts to heal itself
     */
    @Overwrite(remap = false)
    public void applyRepeatedEffect(PixelmonWrapper pw) {
        if (pw.isFainted()) {
            return;
        }

        if (!pw.hasFullHealth() && !pw.hasStatus(StatusType.HealBlock)) {
            int par1 = (int)((float)pw.getMaxHealth() * 0.0625F);
            pw.healEntityBy(par1);
            if (pw.bc != null) {
                pw.bc.sendToAll("pixelmon.helditems.leftovers", new Object[]{pw.getNickname()});
            }
        }

    }
}
