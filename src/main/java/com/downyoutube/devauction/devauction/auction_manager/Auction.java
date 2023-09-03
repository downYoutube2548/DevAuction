package com.downyoutube.devauction.devauction.auction_manager;

import com.downyoutube.devauction.devauction.DevAuction;
import com.downyoutube.devauction.devauction.utils.ConfigManager;
import com.downyoutube.devauction.devauction.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Auction {

    public static ItemStack auction_item = null;
    public static List<Double> auction_diamond = new ArrayList<>();
    public static float auction_scale;
    public static List<String> auction_master = new ArrayList<>();
    public static long auction_duration_remain;
    public static long auction_duration;
    private static BukkitTask countdownTask = null;

    public static void start(ItemStack item, double price, float scale, long duration) {
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) return;

        auction_item = item;
        auction_diamond.add(price);
        auction_scale = scale;
        auction_duration_remain = duration;
        auction_duration = duration;

        if (countdownTask != null) countdownTask.cancel();
        countdownTask = startCountdown();
    }

    public static void bid(Player player, double price) {
        auction_master.add(player.getName());
        auction_diamond.add(price);

        end(auction_duration);
    }

    public static void end(long duration) {
        if (duration <= 0) {
            countdownTask.cancel();
            countdownTask = null;
            auction_item = null;
            auction_master.clear();
            auction_scale = 0;
            auction_diamond.clear();
            auction_duration = 0;
            auction_duration_remain = 0;
        } else {
            auction_duration_remain = duration;
        }
    }

    private static BukkitTask startCountdown() {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(DevAuction.main, ()->{
            if (auction_item.getItemMeta() == null) {
                Bukkit.broadcastMessage(ConfigManager.getMessage("message.system-error", true));
                end(0);
            }

            if (auction_duration_remain > 0) {
                auction_duration_remain--;

                if (!auction_master.isEmpty()) {
                    Player master = Bukkit.getPlayer(auction_master.get(auction_master.size() - 1));
                    if (master != null) {
                        if (DevAuction.economy.getBalance(master) < auction_diamond.get(auction_diamond.size() - 1)) {
                            Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-master-not-enough-money", true));
                            auction_master.remove(auction_master.size() - 1);
                            auction_diamond.remove(auction_diamond.size() - 1);
                            end(auction_duration);
                        }
                        master.sendTitle(ConfigManager.getMessage("message.auction-master-title.title", false), ConfigManager.getMessage("message.auction-master-title.sub-title", false).replace("{item}", auction_item.getItemMeta().getDisplayName().equals("") ? Utils.getItemName(auction_item) : auction_item.getItemMeta().getDisplayName()).replace("{time}", Utils.durationFormat(auction_duration_remain)), 0, 30, 10);
                    } else {
                        Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-master-offline", true));
                        auction_master.remove(auction_master.size() - 1);
                        auction_diamond.remove(auction_diamond.size() - 1);
                        end(auction_duration);
                    }
                }

                if (ConfigManager.getConfig().getIntegerList("auction.message-at-interval").contains((int) auction_duration_remain)) {
                    Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-count-down", true)
                            .replace("{item}", auction_item.getItemMeta().getDisplayName().equals("") ? Utils.getItemName(auction_item) : auction_item.getItemMeta().getDisplayName())
                            .replace("{time}", Utils.durationFormat(auction_duration_remain))
                    );
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (String sounds : ConfigManager.getConfig().getStringList("auction.auction-count-down-sound")) {
                            String[] sound = sounds.split(";");
                            player.playSound(player.getLocation(), Sound.valueOf(sound[0]), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                        }
                    }
                } else if (List.of(5,4,3,2,1,0).contains((int) auction_duration_remain)) {
                    Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-few-count-down", true)
                            .replace("{time}", Utils.durationFormat(auction_duration_remain))
                    );
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (String sounds : ConfigManager.getConfig().getStringList("auction.auction-few-count-down-sound")) {
                            String[] sound = sounds.split(";");
                            player.playSound(player.getLocation(), Sound.valueOf(sound[0]), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                        }
                    }
                }
            } else {
                if (auction_master.isEmpty()) {
                    Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-end", true));
                    Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-no-bidder", true));
                    end(0);
                } else {
                    Player master = Bukkit.getPlayer(auction_master.get(auction_master.size() - 1));
                    if (master != null) {
                        if (DevAuction.economy.getBalance(master) < auction_diamond.get(auction_diamond.size()-1)) {
                            Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-master-not-enough-money", true));
                            auction_master.remove(auction_master.size()-1);
                            auction_diamond.remove(auction_diamond.size()-1);
                            end(auction_duration);
                        } else {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                for (String sounds : ConfigManager.getConfig().getStringList("auction.auction-end-sound")) {
                                    String[] sound = sounds.split(";");
                                    player.playSound(player.getLocation(), Sound.valueOf(sound[0]), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                }
                            }

                            Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-end", true));
                            Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-winner", true)
                                    .replace("{player}", auction_master.get(auction_master.size() - 1))
                                    .replace("{price}", Utils.Format(auction_diamond.get(auction_diamond.size() - 1)))
                            );

                            master.resetTitle();
                            for (String sounds : ConfigManager.getConfig().getStringList("auction.auction-winner-sound")) {
                                String[] sound = sounds.split(";");
                                master.playSound(master.getLocation(), Sound.valueOf(sound[0]), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                            }
                            for (String commands : ConfigManager.getConfig().getStringList("auction.auction-winner-commands")) {
                                Bukkit.getScheduler().runTask(DevAuction.main, ()-> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("{player}", master.getName())));
                            }

                            DevAuction.economy.withdrawPlayer(master, auction_diamond.get(auction_diamond.size()-1));
                            master.getInventory().addItem(auction_item);

                            end(0);
                        }
                    } else {
                        Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-master-offline", true));
                        auction_master.remove(auction_master.size()-1);
                        auction_diamond.remove(auction_diamond.size()-1);
                        end(auction_duration);
                    }
                }
            }
        }, 20, 20);
    }

    public static void view(Player player) {
        Inventory inventory = Bukkit.createInventory(new AuctionHolder(), ConfigManager.getConfig().getInt("view-auction.size"), ConfigManager.getMessage("view-auction.title", false));
        for (int i = 0; i < ConfigManager.getConfig().getInt("view-auction.size"); i++) {
            ItemStack border = new ItemStack(Material.valueOf(ConfigManager.getConfig().getString("view-auction.border.type")), 1);
            ItemMeta borderMeta = border.getItemMeta();
            if (borderMeta != null) {
                borderMeta.setCustomModelData(ConfigManager.getConfig().getInt("view-auction.border.model"));
                borderMeta.setDisplayName(" ");
                border.setItemMeta(borderMeta);
            }

            inventory.setItem(i, border);
        }

        inventory.setItem(ConfigManager.getConfig().getInt("view-auction.auction-item-slot"), auction_item);
        player.openInventory(inventory);
    }

    public static class AuctionHolder implements InventoryHolder {
        @NotNull
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
