package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.quests.QuestProgress;
import com.pixelmonmod.pixelmon.quests.exceptions.InvalidQuestArgsException;
import com.pixelmonmod.pixelmon.quests.objectives.Objective;
import com.pixelmonmod.pixelmon.quests.objectives.objectives.meta.DateObjective;
import com.pixelmonmod.pixelmon.quests.quest.Stage;
import com.pixelmonmod.pixelmon.storage.playerData.QuestData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.time.LocalDate;
import java.time.ZoneId;

@Mixin(DateObjective.class)
public class MixinDateObjective {

    private static ZoneId zone = ZoneId.systemDefault();

    /**
     *
     * Changed to use ZoneId cache because {@link ZoneId#systemDefault()} is slow
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    public boolean test(Stage stageIn, QuestData dataIn, QuestProgress progressIn, Objective objectiveIn, Object[] objectiveArgsIn, Object... argsIn) throws InvalidQuestArgsException {
        LocalDate date = LocalDate.now(zone);
        return date.isAfter((LocalDate)objectiveArgsIn[0]) && date.isBefore((LocalDate)objectiveArgsIn[1]);
    }
}
