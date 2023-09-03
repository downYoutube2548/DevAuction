package com.downyoutube.devauction.devauction;

import com.downyoutube.devauction.devauction.commands.core;
import com.downyoutube.devauction.devauction.events.InvClick;
import com.google.common.io.ByteStreams;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public final class DevAuction extends JavaPlugin {

    public static DevAuction main;
    public static Economy economy = null;
    private static PlaceholderHook placeholderHook;

    @Override
    public void onEnable() {
        main = this;
        loadResource(this, "config.yml");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        placeholderHook = new PlaceholderHook();

        getServer().getPluginManager().registerEvents(new InvClick(), this);
        Objects.requireNonNull(getCommand("auction")).setExecutor(new core());

        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderHook.register();
        }
    }

    @Override
    public void onDisable() {
        placeholderHook.unregister();
    }

    private static File loadResource(Plugin plugin, String resource) {
        File folder = plugin.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File resourceFile = new File(folder, resource);
        try {
            //if (!resourceFile.exists()) {
            resourceFile.createNewFile();
            try (InputStream in = plugin.getResource(resource);
                 OutputStream out = new FileOutputStream(resourceFile)) {
                ByteStreams.copy(in, out);
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceFile;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
