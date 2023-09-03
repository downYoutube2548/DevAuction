package com.downyoutube.devauction.devauction.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class auction implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player player) {

            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (player.hasPermission("devauction.start")) {

                    }
                } else if (args[0].equalsIgnoreCase("end")) {
                    if (player.hasPermission("devauction.end")) {

                    }
                } else if (args[0].equalsIgnoreCase("bid")) {
                    if (player.hasPermission("devauction.bid")) {

                    }
                } else if (args[0].equalsIgnoreCase("view")) {
                    if (player.hasPermission("devauction.view")) {

                    }
                }
            }

        } else {
            sender.sendMessage(ChatColor.RED+"You must be player to execute this command.");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
