package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.ShopKeeperPacket;
import com.pixelmonmod.pixelmon.entities.npcs.registry.EnumBuySell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShopKeeperPacket.class)
public interface IShopKeeperPacket {

    @Accessor(remap = false)
    EnumBuySell getBuySell();

    @Accessor(remap = false)
    String getItemID();

    @Accessor(remap = false)
    int getAmount();

    @Accessor(remap = false)
    int getShopKeeperID();
}

