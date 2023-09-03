package com.downyoutube.devauction.devauction;

import com.downyoutube.devauction.devauction.commands.core;
import com.downyoutube.devauction.devauction.events.InvClick;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DevAuction extends JavaPlugin {

    public static DevAuction main;
    public static Economy economy = null;
    private static PlaceholderHook placeholderHook;

    @Override
    public void onEnable() {
        main = this;
        //loadResource(this, "config.yml");
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
