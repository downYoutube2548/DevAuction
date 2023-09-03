package com.downyoutube.devauction.devauction;

import com.downyoutube.devauction.devauction.auction_manager.Auction;
import com.downyoutube.devauction.devauction.utils.ConfigManager;
import com.downyoutube.devauction.devauction.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "devauction";
    }

    @Override
    public @NotNull String getAuthor() {
        return "downYoutube2548";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (params.equalsIgnoreCase("has_auction")) {
            return Auction.auction_item == null ? "no" : "yes";
        }
        if (params.equalsIgnoreCase("auction_master")) {
            if (Auction.auction_item == null) return "";
            if (Auction.auction_master.isEmpty()) return ConfigManager.getMessage("message.placeholder-no-bidder", false);
            return Auction.auction_master.get(Auction.auction_master.size()-1);
        }
        if (params.equalsIgnoreCase("auction_price")) {
            if (Auction.auction_item == null) return "";
            return Utils.Format(Auction.auction_diamond.get(Auction.auction_diamond.size()-1));
        }
        if (params.equalsIgnoreCase("auction_next_price")) {
            if (Auction.auction_item == null) return "";
            double price = Auction.auction_diamond.get(Auction.auction_diamond.size()-1);
            return Utils.Format(price + (price * Auction.auction_scale/100));
        }
        if (params.equalsIgnoreCase("auction_remain")) {
            if (Auction.auction_item == null) return "";
            return Utils.durationFormat(Auction.auction_duration_remain);
        }
        if (params.equalsIgnoreCase("auction_item")) {
            if (Auction.auction_item == null) return "";
            if (Auction.auction_item.getItemMeta() == null) return "";
            return Auction.auction_item.getItemMeta().getDisplayName();
        }

        return "";
    }
}
