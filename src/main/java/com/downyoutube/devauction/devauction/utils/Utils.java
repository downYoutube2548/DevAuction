package com.downyoutube.devauction.devauction.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String colorize(String s) {
        if (s == null || s.equals(""))
            return "";
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(s);
        while (match.find()) {
            String hexColor = s.substring(match.start(), match.end());
            s = s.replace(hexColor, net.md_5.bungee.api.ChatColor.of(hexColor).toString());
            match = pattern.matcher(s);
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String Format(Double input) {
        DecimalFormat df = new DecimalFormat("#,####.##");
        return df.format(input);
    }

    public static String getItemName(ItemStack item) {
        Material material = item.getType();
        StringBuilder result = new StringBuilder();
        boolean makeNextCharUpperCase = true;

        for (char c : material.name().toCharArray()) {
            if (c == '_') {
                makeNextCharUpperCase = true;
                result.append(" ");
            } else {
                if (makeNextCharUpperCase) {
                    result.append(Character.toUpperCase(c));
                    makeNextCharUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }

    public static String durationFormat(long seconds) {
        if (seconds < 0) seconds = 0;

        if (seconds == 0) { return "0 "+ConfigManager.getMessage("message.time-format.second", false) ; }

        // calculate the duration between start and end
        Duration duration = Duration.ofSeconds(seconds);

        // extract the days, hours, and minutes from the duration
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long second = duration.getSeconds() % 60;

        // format the duration as a string
        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (hours > 0) {
            sb.append(hours).append(" ").append(ConfigManager.getMessage("message.time-format.hour", false)).append(" ");
            i++;
        }
        if (minutes > 0) {
            if (i < 1) {
                sb.append(minutes).append(" ").append(ConfigManager.getMessage("message.time-format.minute", false)).append(" ");
                i++;
            }
        }
        if (second > 0) {
            if (i < 1) {
                sb.append(second).append(" ").append(ConfigManager.getMessage("message.time-format.second", false)).append(" ");
            }
        }


        return sb.substring(0, Math.max(sb.length()-1, 0));
    }
}
