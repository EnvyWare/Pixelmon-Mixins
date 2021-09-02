package com.envyful.mixins.plugins.pixelextras;

import com.envyful.api.concurrency.UtilConcurrency;
import com.pixelextras.commands.PokeExp;
import com.pixelextras.config.PixelExtrasConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PokeExp.class)
public abstract class MixinPokeExp extends CommandBase {


        /**
         * @author danorris709
         */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilConcurrency.runAsync(() -> {
            if (args.length == 2 && sender instanceof EntityPlayerMP) {
                EntityPlayerMP player = null;
                try {
                    player = getCommandSenderAsPlayer(sender);
                } catch (PlayerNotFoundException e) {
                    e.printStackTrace();
                }
                int slot = Integer.parseInt(args[0]) - 1;
                int exp = Integer.parseInt(args[1]);
                int expToUse = (int)((double)exp * PixelExtrasConfig.expConversionRate);
                int playerTotalXP = player.experienceTotal;
                float playerXP = player.experience;

                if (expToUse > playerTotalXP) {
                    CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "Not enough experience!");
                    if (playerXP > 0.0F) {
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, playerXP + " exp is left!");
                    }
                    return;
                }

                PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player);
                if (storage == null) {
                    return;
                }

                List<Pokemon> party = storage.getTeam();
                Pokemon pixelmon = party.get(slot);
                pixelmon.getLevelContainer().awardEXP(exp);
                removeExperience(player, -((int)((double)exp * PixelExtrasConfig.expConversionRate)));
            } else {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage(sender));
            }
        });
    }

    @Shadow(remap = false)
    private static void removeExperience(EntityPlayerMP player, int i) {}
}
