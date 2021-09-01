package com.envyful.mixins.reforged;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.quests.QuestProgress;
import com.pixelmonmod.pixelmon.quests.exceptions.InvalidQuestArgsException;
import com.pixelmonmod.pixelmon.quests.objectives.IObjective;
import com.pixelmonmod.pixelmon.quests.objectives.Objective;
import com.pixelmonmod.pixelmon.quests.objectives.objectives.entity.TileEntityVicinityObjective;
import com.pixelmonmod.pixelmon.quests.quest.Stage;
import com.pixelmonmod.pixelmon.storage.playerData.QuestData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(TileEntityVicinityObjective.class)
public abstract class MixinTileEntityVicinityObjective implements IObjective {

    private static final Map<Class<?>, String> CLASS_NAME_CACHE = Maps.newHashMap();

    /**
     *
     * Changes looping through the entire world's tile entities to expanding the bounding box
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    public boolean test(Stage stageIn, QuestData dataIn, QuestProgress progressIn, Objective objectiveIn, Object[] objectiveArgsIn, Object... argsIn) throws InvalidQuestArgsException {
        Object arg = objectiveArgsIn[0];
        int distance = (int) objectiveArgsIn[1];
        WorldServer world = dataIn.getPlayer().getServerWorld();
        String type = (String) arg;

        Iterator var11 = this.getTileEntitiesWithinAABB(world,
                dataIn.getPlayer().getEntityBoundingBox().expand(distance, distance, distance)).iterator();
        TileEntity te;
        do {
            if (!var11.hasNext()) {
                return false;
            }

            te = (TileEntity)var11.next();
        } while(!(dataIn.getPlayer().getDistance((double)te.getPos().getX(), (double)te.getPos().getY(), (double)te.getPos().getZ()) <= (double)distance) || !te.getClass().getSimpleName().equalsIgnoreCase(type));

        return true;
    }

    public List<TileEntity> getTileEntitiesWithinAABB(World world, AxisAlignedBB aabb) {
        int j2 = MathHelper.floor((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
        int k2 = MathHelper.ceil((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
        int l2 = MathHelper.floor((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
        int i3 = MathHelper.ceil((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
        List<TileEntity> list = Lists.newArrayList();

        for (int j3 = j2; j3 < k2; ++j3)
        {
            for (int k3 = l2; k3 < i3; ++k3)
            {
                if (world.getChunkProvider().getLoadedChunk(j3, k3) != null)
                {
                    list.addAll(world.getChunk(j3, k3).getTileEntityMap().values());
                }
            }
        }

        return list;
    }
}
