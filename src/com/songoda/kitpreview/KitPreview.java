package com.songoda.kitpreview;

import com.songoda.arconix.method.formatting.TextComponent;
import com.songoda.kitpreview.api.*;
import com.songoda.kitpreview.handlers.*;
import com.songoda.kitpreview.events.*;
import com.songoda.kitpreview.hooks.EssentialsHook;
import com.songoda.kitpreview.hooks.Hooks;
import com.songoda.kitpreview.hooks.UltimateCoreHook;
import com.songoda.kitpreview.kits.Kit;
import com.songoda.kitpreview.utils.ConfigWrapper;
import com.songoda.kitpreview.utils.Debugger;
import com.songoda.kitpreview.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class KitPreview extends JavaPlugin {
    public static CommandSender console = Bukkit.getConsoleSender();

    public ConfigWrapper langFile = new ConfigWrapper(this, "", "lang.yml");

    public References references = null;

    public Hooks hooks = null;
    public HologramHandler holo = null;
    public SettingsManager sm;

    public boolean v1_7 = Bukkit.getServer().getClass().getPackage().getName().contains("1_7");
    public boolean v1_8 = Bukkit.getServer().getClass().getPackage().getName().contains("1_8");

    private static KitPreview INSTANCE;

    public Map<Player, String> inEditor = new HashMap<>();
    public Map<Player, Block> editing = new HashMap<>();
    public Map<Player, Kit> editingKit = new HashMap<>();

    public Map<Player, Integer> page = new HashMap<>();

    public Map<Player, String> buy = new HashMap<>();
    public Map<String, String> kits = new HashMap<>();

    public Map<Player, Kit> inKit = new HashMap<>();

    public List<UUID> xyz = new ArrayList<>();
    public List<UUID> xyz2 = new ArrayList<>();
    public List<UUID> xyz3 = new ArrayList<>();

    public void onEnable() {
        INSTANCE = this;
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7KitPreview" + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &aEnabling&7..."));
        langFile.createNewFile("Loading language file", "KitPreview language file");
        loadLanguageFile();

        hook();

        references = new References();

        holo = new HologramHandler(this);
        new ParticleHandler(this);
        new DisplayItemHandler(this);
        sm = new SettingsManager(this);
        sm.updateSettings();
        setupConfig();

        try {
            Update update = new Update(this);
            update.start();
            Bukkit.getLogger().info("MCUpdate enabled and loaded");
        } catch (IOException e) {
            Bukkit.getLogger().info("Failed initialize MCUpdate");
        }

        if (!getConfig().getBoolean("Main.Enabled Custom Kits And Kit Commands")) {
            console.sendMessage(TextComponent.formatText("&7The &a/kit&7 and &a/kits &7features have been &cdisabled&7."));
        } else {
            registerCommandDynamically("kits", new CommandHandler(this));
            registerCommandDynamically("kit", new CommandHandler(this));
        }

        console.sendMessage(TextComponent.formatText("&a============================="));

        this.getCommand("KitPreview").setExecutor(new CommandHandler(this));
        this.getCommand("PreviewKit").setExecutor(new CommandHandler(this));

        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new ChatListeners(this), this);
        if (!v1_7) getServer().getPluginManager().registerEvents(new EntityListeners(this), this);
        getServer().getPluginManager().registerEvents(new InteractListeners(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
        getServer().getPluginManager().registerEvents(new QuitListeners(this), this);
    }

    private void registerCommandDynamically(String command, CommandExecutor executor) {
        try {
            Class<?> classCraftServer = Bukkit.getServer().getClass();
            Field fieldCommandMap = classCraftServer.getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) fieldCommandMap.get(Bukkit.getServer());

            Constructor<PluginCommand> constructorPluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructorPluginCommand.setAccessible(true);

            PluginCommand commandObject = constructorPluginCommand.newInstance(command, this);
            commandObject.setExecutor(executor);

            commandMap.register(command, commandObject);
        } catch (ReflectiveOperationException e) {
            Debugger.runReport(e);
        }
    }

    public void hook() {
        if (getServer().getPluginManager().getPlugin("Essentials") != null) {
            hooks = new EssentialsHook();
        } else if (getServer().getPluginManager().getPlugin("UltimateCore") != null) {
            hooks = new UltimateCoreHook();
        }
    }

    public void onDisable() {
        xyz.clear();
        xyz2.clear();
        xyz3.clear();
        console.sendMessage(TextComponent.formatText("&a============================="));
        console.sendMessage(TextComponent.formatText("&7KitPreview " + this.getDescription().getVersion() + " by &5Brianna <3!"));
        console.sendMessage(TextComponent.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(TextComponent.formatText("&a============================="));
    }

    private void setupConfig() {
        sm.updateSettings();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void loadLanguageFile() {
        Lang.setFile(langFile.getConfig());

        for (final Lang value : Lang.values()) {
            langFile.getConfig().addDefault(value.getPath(), value.getDefault());
        }

        langFile.getConfig().options().copyDefaults(true);
        langFile.saveConfig();
    }

    public void reload() {
        try {
            langFile.createNewFile("Loading language file", "KitPreview language file");
            loadLanguageFile();
            references = new References();
            reloadConfig();
            saveConfig();
            holo.updateHolograms();
        } catch (Exception ex) {
            Debugger.runReport(ex);
        }

    }

    public static KitPreview pl() {
        return INSTANCE;
    }

    public static KitPreview getInstance() {
        return INSTANCE;
    }

}

