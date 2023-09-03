package com.downyoutube.devauction.devauction.commands;

import com.downyoutube.devauction.devauction.DevAuction;
import com.downyoutube.devauction.devauction.auction_manager.Auction;
import com.downyoutube.devauction.devauction.utils.ConfigManager;
import com.downyoutube.devauction.devauction.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class core implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        try {
            if (sender instanceof Player player) {

                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("start")) {
                        if (player.hasPermission("devauction.start")) {

                            if (args.length >= 2) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir() || item.getItemMeta() == null) {
                                    sender.sendMessage(ConfigManager.getMessage("message.no-item", true));
                                    return false;
                                }
                                if (Auction.auction_item != null) {
                                    sender.sendMessage(ConfigManager.getMessage("message.auction-already-start", true));
                                    return false;
                                }

                                long duration = ConfigManager.getConfig().getLong("auction.default-duration");
                                float scale = (float) ConfigManager.getConfig().getDouble("auction.default-scale");
                                double price = Double.parseDouble(args[1]);

                                for (String arg : args) {
                                    if (arg.startsWith("duration:")) {
                                        duration = Long.parseLong(arg.replace("duration:", ""));
                                    } else if (arg.startsWith("scale:")) {
                                        scale = Float.parseFloat(arg.replace("scale:", ""));
                                    }
                                }

                                Auction.start(item, price, scale, duration);

                                Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-start", false)
                                        .replace("{item}", (item.getItemMeta().getDisplayName().equals("")) ? Utils.getItemName(item) : item.getItemMeta().getDisplayName())
                                        .replace("{price}", Utils.Format(price + (price * Auction.auction_scale / 100)))
                                );
                            } else {
                                sender.sendMessage(ConfigManager.getMessage("message.specify-price", true));
                            }
                        } else {
                            sender.sendMessage(ConfigManager.getMessage("message.no-permission", true));
                        }
                    } else if (args[0].equalsIgnoreCase("end")) {
                        if (player.hasPermission("devauction.end")) {
                            Auction.auction_duration_remain = args.length >= 2 ? Long.parseLong(args[1]) : 0;
                        } else {
                            sender.sendMessage(ConfigManager.getMessage("message.no-permission", true));
                        }
                    } else if (args[0].equalsIgnoreCase("bid")) {
                        if (player.hasPermission("devauction.bid")) {
                            if (args.length >= 2) {

                                if (Auction.auction_item == null) {
                                    sender.sendMessage(ConfigManager.getMessage("message.auction-not-start", true));
                                    return false;
                                }

                                if (!Auction.auction_master.isEmpty()) {
                                    String master = Auction.auction_master.get(Auction.auction_master.size() - 1);
                                    if (master.equals(player.getName())) {
                                        sender.sendMessage(ConfigManager.getMessage("message.auction-already-bid", true));
                                        return false;
                                    }
                                }

                                double price = Double.parseDouble(args[1]);

                                double last_price = Auction.auction_diamond.get(Auction.auction_diamond.size()-1);
                                if (price < last_price + (last_price * Auction.auction_scale/100)) {
                                    sender.sendMessage(ConfigManager.getMessage("message.auction-bid-price", true)
                                            .replace("{price}", Utils.Format(last_price + (last_price * Auction.auction_scale/100)))
                                    );
                                    return false;
                                }
                                if (DevAuction.economy.getBalance(player) < price) { sender.sendMessage(ConfigManager.getMessage("message.not-enough-money", true)); return false; }

                                Auction.bid(player, price);
                                Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-bid-success", true)
                                        .replace("{player}", player.getName())
                                        .replace("{price}", Utils.Format(price))
                                        .replace("{next price}", Utils.Format(price + (price * Auction.auction_scale/100)))
                                );
                            } else {
                                sender.sendMessage(ConfigManager.getMessage("message.syntax-error", true));
                            }
                        } else {
                            sender.sendMessage(ConfigManager.getMessage("message.no-permission", true));
                        }
                    } else if (args[0].equalsIgnoreCase("cancel")) {
                        if (player.hasPermission("devauction.cancel")) {
                            Bukkit.broadcastMessage(ConfigManager.getMessage("message.auction-cancelled", true));
                            Auction.end(0);
                        } else {
                            sender.sendMessage(ConfigManager.getMessage("message.no-permission", true));
                        }
                    } else if (args[0].equalsIgnoreCase("view")) {
                        if (player.hasPermission("devauction.view")) {

                            if (Auction.auction_item == null) {
                                sender.sendMessage(ConfigManager.getMessage("message.auction-not-start", true));
                                return false;
                            }

                            Auction.view(player);

                        } else {
                            sender.sendMessage(ConfigManager.getMessage("message.no-permission", true));
                        }
                    } else {
                        sender.sendMessage(ConfigManager.getMessage("message.syntax-error", true));
                    }
                }

            } else {
                sender.sendMessage(ChatColor.RED + "You must be player to execute this command.");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ConfigManager.getMessage("message.invalid-number", true));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> output = new ArrayList<>();

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("devauction.start")) completions.add("start");
            if (sender.hasPermission("devauction.view")) completions.add("view");
            if (sender.hasPermission("devauction.end")) completions.add("end");
            if (sender.hasPermission("devauction.cancel")) completions.add("cancel");
            if (sender.hasPermission("devauction.bid")) completions.add("bid");

            output = tabComplete(args[0], completions);
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("start")) {
                if (sender.hasPermission("devauction.start")) {
                    if (args.length == 2) {
                        output = List.of("PRICE");
                    } else {
                        List<String> completions = new ArrayList<>(List.of("scale:", "duration:"));
                        for (String arg : args) {
                            if (arg.startsWith("scale:")) completions.remove("scale:");
                            if (arg.startsWith("duration:")) completions.remove("duration:");
                        }
                        output = tabComplete(args[args.length - 1], completions);
                    }
                }
            } else if (args[0].equalsIgnoreCase("end")) {
                if (sender.hasPermission("devauction.end")) {
                    if (args.length == 2) {
                        output = List.of("DURATION");
                    }
                }
            } else if (args[0].equalsIgnoreCase("bid")) {
                if (sender.hasPermission("devauction.bid")) {
                    if (args.length == 2) {
                        output = List.of("PRICE");
                    }
                }
            }
        }

        return output;
    }

    public static List<String> tabComplete(String a, List<String> arg) {
        List<String> matches = new ArrayList<>();
        String search = a.toLowerCase(Locale.ROOT);
        for (String s : arg) {
            if (s.toLowerCase(Locale.ROOT).startsWith(search)) {
                matches.add(s);
            }
        }
        return matches;
    }
}
