package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.quests.QuestProgress;
import com.pixelmonmod.pixelmon.quests.objectives.IObjective;
import com.pixelmonmod.pixelmon.quests.objectives.Objective;
import com.pixelmonmod.pixelmon.quests.objectives.objectives.meta.DateObjective;
import com.pixelmonmod.pixelmon.quests.quest.Arguments;
import com.pixelmonmod.pixelmon.quests.quest.Context;
import com.pixelmonmod.pixelmon.quests.quest.Stage;
import com.pixelmonmod.pixelmon.storage.playerData.QuestData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.time.LocalDate;
import java.time.ZoneId;

@Mixin(DateObjective.class)
public abstract class MixinDateObjective implements IObjective {

    private static transient ZoneId zone = ZoneId.systemDefault();

    /**
     *
     * Changed to use ZoneId cache because {@link ZoneId#systemDefault()} is slow
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    public boolean test(Stage stage, QuestData data, QuestProgress progress, Objective objective, Arguments arguments, Context context) {
        LocalDate date = LocalDate.now(zone);
        return date.isAfter(arguments.value(0, progress)) && date.isBefore(arguments.value(1, progress));
    }
}
