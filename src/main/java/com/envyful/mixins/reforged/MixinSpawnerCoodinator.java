package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Timer;

@Mixin(SpawnerCoordinator.class)
public class MixinSpawnerCoodinator {

    @Shadow private boolean active;

    @Shadow public SpawnerCoordinator.Processor processor;

    /**
     * @author danorris709
     */
    @Overwrite(remap = false)
    public SpawnerCoordinator activate() {
        (new Timer()).scheduleAtFixedRate(this.processor = new SpawnerCoordinator.Processor(), 0L, 200L);
        MinecraftForge.EVENT_BUS.register(this);
        this.active = true;
        return (SpawnerCoordinator) ((Object) this);
    }

}
