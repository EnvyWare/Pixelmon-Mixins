package com.envyful.mixins.sponge;

import co.aikar.timings.Timing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.block.TickBlockEvent;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.event.ShouldFire;
import org.spongepowered.common.event.tracking.PhaseTracker;
import org.spongepowered.common.event.tracking.TrackingUtil;
import org.spongepowered.common.interfaces.IMixinChunk;
import org.spongepowered.common.interfaces.block.IMixinBlockEventData;
import org.spongepowered.common.interfaces.block.tile.IMixinTileEntity;
import org.spongepowered.common.interfaces.world.IMixinWorldServer;
import org.spongepowered.common.world.SpongeLocatableBlockBuilder;
import org.spongepowered.common.world.WorldUtil;

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Mixin(TrackingUtil.class)
public class MixinTrackingUtil {

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static void tickTileEntity(IMixinWorldServer mixinWorldServer, ITickable tile) {
        checkArgument(tile instanceof TileEntity, "ITickable %s is not a TileEntity!", tile);
        checkNotNull(tile, "Cannot capture on a null ticking tile entity!");
        final net.minecraft.tileentity.TileEntity tileEntity = (net.minecraft.tileentity.TileEntity) tile;
        final IMixinTileEntity mixinTileEntity = (IMixinTileEntity) tile;
        final BlockPos pos = tileEntity.getPos();
        final IMixinChunk chunk = ((IMixinTileEntity) tile).getActiveChunk();
        if (!mixinTileEntity.shouldTick()) {
            return;
        }

        mixinTileEntity.setIsTicking(true);
        try (Timing timing = mixinTileEntity.getTimingsHandler().startTiming()) {
            tile.update();
        }
        // We delay clearing active chunk if TE is invalidated during tick so we must remove it after
        if (tileEntity.isInvalid()) {
            mixinTileEntity.setActiveChunk(null);
        }

        mixinTileEntity.setIsTicking(false);
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static boolean fireMinecraftBlockEvent(WorldServer worldIn, BlockEventData event) {
        IBlockState currentState = worldIn.getBlockState(event.getPosition());
        final IMixinBlockEventData blockEvent = (IMixinBlockEventData) event;

        Object source = blockEvent.getTickBlock() != null ? blockEvent.getTickBlock() : blockEvent.getTickTileEntity();
        if (source == null) {
            // No source present which means we are ignoring the phase state
            return currentState.onBlockEventReceived(worldIn, event.getPosition(), event.getEventID(), event.getEventParameter());
        }

        return currentState.onBlockEventReceived(worldIn, event.getPosition(), event.getEventID(), event.getEventParameter());
    }

    /**
     * @author
     */
    @Overwrite(remap = false)
    public static void randomTickBlock(PhaseTracker phaseTracker, IMixinWorldServer mixinWorld, Block block,
                                       BlockPos pos, IBlockState state, Random random) {
        final WorldServer world = WorldUtil.asNative(mixinWorld);
        final World apiWorld = WorldUtil.fromNative(world);

        if (ShouldFire.TICK_BLOCK_EVENT) {
            final BlockSnapshot currentTickBlock = mixinWorld.createSpongeBlockSnapshot(state, state, pos, BlockChangeFlags.NONE);
            final TickBlockEvent
                    event =
                    SpongeEventFactory.createTickBlockEventRandom(Sponge.getCauseStackManager().getCurrentCause(), currentTickBlock);
            SpongeImpl.postEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }

        final LocatableBlock locatable = new SpongeLocatableBlockBuilder().world(apiWorld).position(pos.getX(), pos.getY(), pos.getZ()).state((BlockState) state).build();

        block.randomTick(world, pos, state, random);
    }
}
