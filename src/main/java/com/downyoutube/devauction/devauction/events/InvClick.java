package com.downyoutube.devauction.devauction.events;

import com.downyoutube.devauction.devauction.auction_manager.Auction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InvClick implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof Auction.AuctionHolder) {
            e.setCancelled(true);
        }
    }
}
