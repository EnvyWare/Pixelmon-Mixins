package com.envyful.mixins.plugins.pixelextras;

import com.envyful.api.concurrency.UtilConcurrency;
import com.mojang.authlib.GameProfile;
import com.pixelextras.commands.CompSee;
import com.pixelextras.config.PixelExtrasConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(CompSee.class)
public abstract class MixinCompSee extends CommandBase {

    @Shadow(remap = false)
    protected abstract void getBoxList(int box, UUID player, ICommandSender sender);

        /**
         * @author danorris709
         */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilConcurrency.runAsync(() -> {
            if (args.length < 2) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage(sender));
            } else {
                try {
                    Integer box = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
                    if (box <= 0 || box > PixelmonConfig.computerBoxes) {
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "That box does not exist! Must be 1-" + PixelmonConfig.computerBoxes + "!");
                        return;
                    }

                    if (PixelExtrasConfig.allowOfflineCheck) {
                        GameProfile gp = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);
                        if (gp == null) {
                            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "This player doesn't exist.");
                            return;
                        }

                        UUID uuid = gp.getId();
                        if (Pixelmon.storageManager.getPCForPlayer(uuid).countPokemon() == 0) {
                            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "This player doesn't exist or don't have any pokemon in his PC.");
                            return;
                        }

                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.AQUA, args[0] + "'s Computer Box " + box + ": (Mouse over for more info)");
                        this.getBoxList(box - 1, uuid, sender);
                    } else {
                        EntityPlayerMP player = getPlayer(server, sender, args[0]);
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.AQUA, player.getDisplayNameString() + "'s Computer Box " + box + ": (Mouse over for more info)");
                        this.getBoxList(box - 1, player.getUniqueID(), sender);
                    }
                } catch (CommandException e) {
                    CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Invalid Name! Try again!");
                }
            }
        });
    }
}
