package com.envyful.mixins.plugins.pixelextras;

import com.envyful.api.concurrency.UtilConcurrency;
import com.pixelextras.commands.PC;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityPC;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreen;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientChangeOpenPC;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientInitializePC;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PC.class)
public abstract class MixinPC extends CommandBase {

    /**
     * @author danorris709
     */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilConcurrency.runAsync(() -> {
            try {
                EntityPlayerMP player = getPlayer(server, sender, sender.getName());
                PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(player);

                if (pStorage == null) {
                    return;
                }

                if (pStorage.guiOpened) {
                    CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "You can't do that right now!");
                } else if (args.length == 1) {
                    if (sender.canUseCommand(0, "pixelextras.pc.openbox")) {
                        int box = PixelmonConfig.computerBoxes;
                        int arg1 = parseInt(args[0]);
                        if (arg1 >= 1 && arg1 <= box) {
                            PCStorage pc = Pixelmon.storageManager.getPC(player, (TileEntityPC) null);
                            pc.setLastBox(arg1 - 1);
                            Pixelmon.network.sendTo(new ClientInitializePC(pc), player);
                            Pixelmon.network.sendTo(new ClientChangeOpenPC(pc.uuid), player);
                            pc.sendContents(player);
                            OpenScreen.open(player, EnumGuiScreen.PC);
                        } else {
                            CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Number must be between 1 and " + box, new Object[0]);
                        }
                    } else {
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "You do not have the permission to use this command", new Object[0]);
                    }
                } else {
                    PCStorage pc = Pixelmon.storageManager.getPC(player, null);
                    Pixelmon.network.sendTo(new ClientChangeOpenPC(pc.uuid), player);
                    OpenScreen.open(player, EnumGuiScreen.PC);
                }
            } catch (CommandException e) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Player doesn't exist!");
            }
        });
    }
}
