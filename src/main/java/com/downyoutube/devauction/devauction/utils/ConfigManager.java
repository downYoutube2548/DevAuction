package com.downyoutube.devauction.devauction.utils;

import com.downyoutube.devauction.devauction.DevAuction;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    public static String getMessage(String path, boolean prefix) {
        return Utils.colorize(prefix ? DevAuction.main.getConfig().getString("message.prefix")+DevAuction.main.getConfig().getString(path) : DevAuction.main.getConfig().getString(path));
    }

    public static FileConfiguration getConfig() {
        return DevAuction.main.getConfig();
    }
}
