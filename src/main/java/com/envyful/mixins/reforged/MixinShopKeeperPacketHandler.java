package com.envyful.mixins.reforged;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.events.ShopkeeperEvent;
import com.pixelmonmod.pixelmon.comm.packetHandlers.ISyncHandler;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.ShopKeeperPacket;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import com.pixelmonmod.pixelmon.entities.npcs.EntityNPC;
import com.pixelmonmod.pixelmon.entities.npcs.NPCShopkeeper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.EnumBuySell;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.items.ItemPokeball;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(ShopKeeperPacket.Handler.class)
public abstract class MixinShopKeeperPacketHandler implements ISyncHandler<ShopKeeperPacket> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @Shadow protected abstract void updateTransaction(EntityPlayerMP p, NPCShopkeeper npc);

    @Shadow protected abstract boolean areItemsEqual(ItemStack item1, ItemStack item2);

    /**
     * @author
     */
    @Overwrite(remap = false)
    public void onSyncMessage(ShopKeeperPacket message, MessageContext ctx) {
        EntityPlayerMP p = ctx.getServerHandler().player;
        Optional<NPCShopkeeper> npcOptional = EntityNPC.locateNPCServer(p.world, ((IShopKeeperPacket) message).getShopKeeperID(), NPCShopkeeper.class);

        if (!npcOptional.isPresent() || ((IShopKeeperPacket) message).getAmount() <= 0) {
            return;
        }

        IPixelmonBankAccount account = (IPixelmonBankAccount) Pixelmon.moneyManager.getBankAccount(p).orElse(null);

        if (account == null) {
            return;
        }

        NPCShopkeeper npc = npcOptional.get();

        EXECUTOR_SERVICE.execute(() -> {
            ArrayList itemList;
            Iterator var8;
            ShopItemWithVariation shopItem;
            ItemStack buyStack;
            int initialAmount;
            int actualAmount;
            if (((IShopKeeperPacket) message).getBuySell() == EnumBuySell.Buy) {
                itemList = npc.getItemList();
                var8 = itemList.iterator();

                while (var8.hasNext()) {
                    shopItem = (ShopItemWithVariation) var8.next();
                    if (shopItem.getBaseShopItem().id.equals(((IShopKeeperPacket) message).getItemID())) {
                        if (((IShopKeeperPacket) message).getAmount() > PixelmonConfig.getShopMaxStackSize(shopItem.getItemStack())) {
                            return;
                        }

                        if (account.getMoney() >= shopItem.getBuyCost() * ((IShopKeeperPacket) message).getAmount()) {
                            if (shopItem.getBuyCost() * ((IShopKeeperPacket) message).getAmount() < 0) {
                                return;
                            }

                            ItemStack item = shopItem.getItemStack();
                            buyStack = item.copy();
                            initialAmount = ((IShopKeeperPacket) message).getAmount();
                            buyStack.setCount(((IShopKeeperPacket) message).getAmount());
                            if (Pixelmon.EVENT_BUS.post(new ShopkeeperEvent.Purchase(p, npc, buyStack, EnumBuySell.Buy))) {
                                return;
                            }

                            if (p.addItemStackToInventory(buyStack)) {
                                Item buyItem = buyStack.getItem();
                                if (buyItem instanceof ItemPokeball && ((ItemPokeball) buyItem).type == EnumPokeballs.PokeBall && ((IShopKeeperPacket) message).getAmount() >= 10) {
                                    ItemStack premierBall = new ItemStack(PixelmonItemsPokeballs.premierBall, 1);
                                    p.addItemStackToInventory(premierBall);
                                }

                                account.changeMoney(-shopItem.getBuyCost() * ((IShopKeeperPacket) message).getAmount());
                                this.updateTransaction(p, npc);
                                return;
                            }

                            if (initialAmount > buyStack.getCount()) {
                                actualAmount = initialAmount - buyStack.getCount();
                                account.changeMoney(-shopItem.getBuyCost() * actualAmount);
                                this.updateTransaction(p, npc);
                                return;
                            }
                        }
                    }
                }

            } else {
                itemList = npc.getSellList(p);
                var8 = itemList.iterator();

                while (true) {
                    int count;
                    do {
                        do {
                            if (!var8.hasNext()) {
                                return;
                            }

                            shopItem = (ShopItemWithVariation) var8.next();
                        } while (!shopItem.getBaseShopItem().id.equals(((IShopKeeperPacket) message).getItemID()));

                        count = 0;
                        buyStack = shopItem.getItemStack();

                        for (initialAmount = 0; initialAmount < p.inventory.mainInventory.size(); ++initialAmount) {
                            ItemStack item = (ItemStack) p.inventory.mainInventory.get(initialAmount);
                            if (this.areItemsEqual(item, buyStack)) {
                                count += item.getCount();
                            }
                        }

                        ItemStack copy = buyStack.copy();
                        copy.setCount(((IShopKeeperPacket) message).getAmount());
                        if (Pixelmon.EVENT_BUS.post(new ShopkeeperEvent.Sell(p, npc, EnumBuySell.Sell, copy))) {
                            return;
                        }
                    } while (count < ((IShopKeeperPacket) message).getAmount());

                    actualAmount = shopItem.getSellCost();
                    count = ((IShopKeeperPacket) message).getAmount();

                    for (int i = 0; i < p.inventory.mainInventory.size(); ++i) {
                        ItemStack item = (ItemStack) p.inventory.mainInventory.get(i);
                        if (this.areItemsEqual(item, buyStack)) {
                            if (item.getCount() >= count) {
                                item.setCount(item.getCount() - count);
                                count = 0;
                            } else {
                                count -= item.getCount();
                                item.setCount(0);
                            }

                            if (item.getCount() == 0) {
                                p.inventory.mainInventory.set(i, ItemStack.EMPTY);
                            }
                        }

                        if (count <= 0) {
                            break;
                        }
                    }

                    account.changeMoney(actualAmount * ((IShopKeeperPacket) message).getAmount());
                    this.updateTransaction(p, npc);
                }
            }
        });
    }
}
