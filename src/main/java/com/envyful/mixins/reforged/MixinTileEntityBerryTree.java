package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.blocks.BlockBerryTree;
import com.pixelmonmod.pixelmon.blocks.tileEntities.ISpecialTexture;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityBerryTree;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.enums.EnumBerry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityBerryTree.class)
public abstract class MixinTileEntityBerryTree extends TileEntity implements ITickable, ISpecialTexture {

    @Shadow private byte stage;
    @Shadow long lastWorldTime;
    @Shadow private short nextGrowthStage;

    @Shadow protected abstract void calculateNextGrowthStage();

    @Shadow private static boolean DEBUG;

    @Shadow public abstract byte getStage();

    @Shadow public boolean isGenerated;

    @Shadow private byte typeOrdinal;
    @Shadow private short projectedYield;
    @Shadow private short hours;
    @Shadow private boolean isGrowthBoosted;
    @Shadow private int hoursTillDeath;
    @Shadow public int timesReplanted;

    @Shadow public abstract void replant();

    private transient BlockBerryTree cache = null;
    private transient EnumBerry typeCache = null;

    /**
     *
     * Reduces the number of calls to World#getBlockState as it's slow as
     *
     * @author danorris709
     */
    @Overwrite()
    public void update() {
        if (!this.world.isRemote) {
            if (this.stage < 0) {
                this.world.setBlockToAir(this.pos);
                return;
            }

            long currentWorldTime = PixelmonConfig.useSystemTimeForBerries ? System.currentTimeMillis() : this.world.getTotalWorldTime();
            if (this.lastWorldTime == -1L || this.lastWorldTime > currentWorldTime) {
                this.lastWorldTime = currentWorldTime;
            }

            if (this.lastWorldTime < 1576392266000L && PixelmonConfig.useSystemTimeForBerries) {
                this.lastWorldTime = currentWorldTime;
            }

            if (this.nextGrowthStage == -1) {
                this.calculateNextGrowthStage();
            }

            int ticksPerHour = DEBUG && Pixelmon.devEnvironment ? 10 : (int)(36000.0F / PixelmonConfig.berryTreeGrowthMultiplier);
            long multiplier = PixelmonConfig.useSystemTimeForBerries ? 50L : 1L;
            byte originalStage = this.getStage();

            while(!this.isGenerated && currentWorldTime - this.lastWorldTime > (long)ticksPerHour * multiplier && this.getBlock() != null) {
                this.onHour();
                this.lastWorldTime += (long)ticksPerHour * multiplier;
                if (this.getStage() < originalStage) {
                    break;
                }
            }
        }

    }

    /**
     *
     * Uses cached type because Enum#values creates a new array every single time
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    public void onHour() {
        boolean markForUpdate = false;
        EnumBerry type = this.getType();
        if (this.projectedYield != type.minYield) {
            IBlockState state = this.world.getBlockState(this.getPos().down());
            boolean isWatered = false;
            if (state.getBlock() == Blocks.FARMLAND && (Integer)state.getValue(BlockFarmland.MOISTURE) == 7) {
                isWatered = true;
            }

            if (!isWatered) {
                this.projectedYield = (short)((int)((double)this.projectedYield - Math.ceil((double)type.maxYield / 5.0D)));
                if (this.projectedYield < type.minYield) {
                    this.projectedYield = (short)type.minYield;
                }
            }
        }

        if (++this.hours > this.nextGrowthStage && this.stage != 5) {
            this.calculateNextGrowthStage();
            if (this.isGrowthBoosted) {
                ++this.projectedYield;
                this.isGrowthBoosted = false;
            }

            this.getBlock().growStage(this.world, this.world.rand, this.getPos(), this.world.getBlockState(this.getPos()));
            markForUpdate = true;
            if (this.stage == 5) {
                this.hoursTillDeath = (int)((double)type.growthTime * (0.5D + (double)this.world.rand.nextFloat()));
            }
        } else if (this.hoursTillDeath != -1 && this.hours >= this.hoursTillDeath) {
            markForUpdate = true;
            if (this.timesReplanted < 9) {
                this.getBlock().replant(this.world, this.getPos(), this.world.getBlockState(this.getPos()));
                this.replant();
            } else {
                this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        if (markForUpdate && this.hasWorld()) {
            ((WorldServer)this.world).getPlayerChunkMap().markBlockForUpdate(this.pos);
        }

    }

    /**
     *
     * Adds cache because World#getBlockState is slow and this tile entity CANNOT move
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    private BlockBerryTree getBlock() {
        if (this.cache != null) {
            return this.cache;
        }

        Block block = world.getBlockState(this.getPos()).getBlock();
        if (block instanceof BlockBerryTree) {
            this.cache = (BlockBerryTree) block;
        } else {
            return null;
        }

        return this.cache;
    }

    /**
     *
     * Adds a cache for the type because Enum#values() creates a new array every call
     *
     * @author danorris709
     */
    @Overwrite(remap = false)
    public EnumBerry getType() {
        if (this.typeCache == null) {
            this.typeCache = EnumBerry.values()[typeOrdinal];
        }

        return this.typeCache;
    }

}
