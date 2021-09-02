package com.envyful.mixins.plugins.pixelextras;

import com.envyful.api.concurrency.UtilConcurrency;
import com.pixelextras.commands.Wiki;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Wiki.class)
public abstract class MixinWiki extends CommandBase {


        /**
         * @author danorris709
         */
    @Overwrite
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilConcurrency.runAsync(() -> {
            if (args.length == 0) {
                CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage(sender), new Object[0]);
            } else {
                String name = args[0];
                String infoType;
                Pokemon pokemon;
                if (args.length == 2) {
                    infoType = args[1];
                    if (EnumSpecies.hasPokemonAnyCase(name)) {
                        pokemon = Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(name));
                        this.sendInfo(pokemon, sender, infoType);
                    } else {
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, "%s is not a valid Pixelmon name", new Object[]{name});
                    }
                } else if (args.length == 3) {
                    infoType = args[2];
                    pokemon = Pixelmon.pokemonFactory.create(EnumSpecies.getFromNameAnyCase(name));
                    if (args[1].contains("type")) {
                        CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage(sender), new Object[0]);
                        return;
                    }

                    PokemonSpec.from(new String[]{args[1]}).apply(pokemon);
                    this.sendInfo(pokemon, sender, infoType);
                } else {
                    CommandChatHandler.sendFormattedChat(sender, TextFormatting.RED, this.getUsage(sender), new Object[0]);
                }
            }
        });
    }

    @Shadow(remap = false)
    protected abstract void sendInfo(Pokemon pokemon, ICommandSender sender, String infoType);
}
