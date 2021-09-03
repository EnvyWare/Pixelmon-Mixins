package com.envyful.mixins.plugins.activetime;

import com.mcsimonflash.sponge.activetime.managers.Storage;
import com.mcsimonflash.sponge.activetime.managers.Util;
import com.mcsimonflash.sponge.activetime.objects.ConfigHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Storage.class)
public abstract class MixinStorage {

    @Shadow private static ConfigHolder players;

    @Shadow private static ConfigHolder current;

    @Shadow
    protected static void syncCurrentDate() {
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static void save() {
        Util.createTask("ActiveTime SaveConfig Sync Processor", (t) -> {
            players.save();
            current.save();
            syncCurrentDate();
        }, 0, true);
    }
}
