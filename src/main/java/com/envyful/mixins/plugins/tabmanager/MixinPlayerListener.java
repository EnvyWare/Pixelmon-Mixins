package com.envyful.mixins.plugins.tabmanager;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import uk.co.proxying.tabmanager.TabManager;
import uk.co.proxying.tabmanager.listeners.PlayerListener;
import uk.co.proxying.tabmanager.tabObjects.BaseTab;
import uk.co.proxying.tabmanager.tabObjects.TabGroup;
import uk.co.proxying.tabmanager.utils.ScoreHandler;
import uk.co.proxying.tabmanager.utils.Utilities;

@Mixin(PlayerListener.class)
public class MixinPlayerListener {

    /**
     * @author danorris709
     */
    @Overwrite(remap = false)
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        Utilities.scheduleSyncTask(() -> {
            Utilities.checkAndUpdateName(player, true);
            if (TabManager.getInstance().getPlayerGroups().get(player.getUniqueId()) != null) {
                BaseTab baseTab = TabManager.getInstance().getPlayerGroups().get(player.getUniqueId());

                if (baseTab instanceof TabGroup) {
                    Utilities.updateGroupPlayerName(player, (TabGroup) baseTab);
                }
            }
        }, 1);
    }

    /**
     * @author danorris709
     */
    @Overwrite(remap = false)
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Root Player player) {
        Utilities.scheduleAsyncTask(() -> {
            TabManager.getInstance().getPlayerGroups().remove(player.getUniqueId());
            ScoreHandler.getInstance().removeFromTeam(player);
        }, 1);
    }
}
