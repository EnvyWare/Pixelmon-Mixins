package com.envyful.mixins.fixes;

import com.pixelmonmod.pixelmon.PixelmonEventHandler;
import com.pixelmonmod.pixelmon.api.events.moveskills.UseMoveSkillEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PixelmonEventHandler.class)
public class MixinPixelmonEventHandler {

    /**
     * @author daniel
     * @reason No more pichu
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onMoveSkillUsed(UseMoveSkillEvent event) {

    }

}
