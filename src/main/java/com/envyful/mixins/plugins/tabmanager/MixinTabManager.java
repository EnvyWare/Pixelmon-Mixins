package com.envyful.mixins.plugins.tabmanager;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import uk.co.proxying.tabmanager.TabManager;
import uk.co.proxying.tabmanager.utils.Utilities;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Mixin(TabManager.class)
public class MixinTabManager {

    @Shadow(remap = false)
    private int placeHolderUpdateIntervalSeconds;
    @Shadow(remap = false)
    private int groupUpdateIntervalSeconds;

    /**
     * @author danorris709
     */
    @Overwrite(remap = false)
    public void startUpdateTask() {
        this.cancelActiveRunningTasks();

        if (this.placeHolderUpdateIntervalSeconds > 1) {
            Task.builder().name("tabmanager-placeholder-updater").interval((long)this.placeHolderUpdateIntervalSeconds, TimeUnit.SECONDS).execute(() -> {
                Iterator var0 = Sponge.getServer().getOnlinePlayers().iterator();

                while(var0.hasNext()) {
                    Player player = (Player)var0.next();
                    Utilities.checkAndUpdateName(player, false);
                }

            }).async().submit(this);
        }

        if (this.groupUpdateIntervalSeconds > 1) {
            Task.builder().name("tabmanager-group-updater").interval((long)this.groupUpdateIntervalSeconds, TimeUnit.SECONDS).async().execute(() -> {
                Iterator var0 = Sponge.getServer().getOnlinePlayers().iterator();

                while(var0.hasNext()) {
                    Player player = (Player)var0.next();
                    Utilities.checkAndUpdateGroup(player);
                }

            }).async().submit(this);
        }

    }

    private void cancelActiveRunningTasks() {
        Iterator<Task> activePlaceholderTasks = Sponge.getScheduler().getTasksByName("tabmanager-placeholder-updater").iterator();

        while(activePlaceholderTasks.hasNext()) {
            Task task = activePlaceholderTasks.next();
            task.cancel();
        }

        Iterator<Task> activeGroupUpdaterTasks = Sponge.getScheduler().getTasksByName("tabmanager-group-updater").iterator();

        while(activeGroupUpdaterTasks.hasNext()) {
            Task task = activeGroupUpdaterTasks.next();
            task.cancel();
        }
    }
}
