package com.github.fernthedev.chunkswapper.spigot;

import com.github.fernthedev.chunkswapper.Constants;
import com.github.fernthedev.chunkswapper.command.FileCommand;
import com.github.fernthedev.chunkswapper.file.ConfigData;
import com.github.fernthedev.config.gson.GsonConfig;
import com.github.fernthedev.fernapi.server.spigot.FernSpigotAPI;
import com.github.fernthedev.fernapi.universal.Universal;

import java.io.File;

public final class SpigotChunkSwapper extends FernSpigotAPI {

    @Override
    public void onEnable() {
        super.onEnable();
        Constants.VERSION = getDescription().getVersion();
        Constants.DEV = getDescription().getAuthors();
        Constants.NAME = getDescription().getName();
        Constants.chunkSwapUtil = new SpigotChunkSwapUtil();
        // Plugin startup logic

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.json");

        Constants.CONFIG = new GsonConfig<>(new ConfigData(), configFile);
        Constants.reloadConfig();

//        getLogger().info("Config file exist status: " + configFile.getAbsolutePath() + " " + configFile.exists());

        getLogger().info("Command name: " + new FileCommand().getName());

        Universal.getCommandHandler().registerFernCommand(new FileCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
    }
}
