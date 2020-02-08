package com.github.fernthedev.chunkswapper;

import com.github.fernthedev.chunkswapper.file.ConfigData;
import com.github.fernthedev.config.common.Config;
import com.github.fernthedev.fernapi.universal.Universal;

import java.util.List;

public class Constants {

    public static IChunkSwapUtil chunkSwapUtil;
    public static Config<ConfigData> CONFIG;

    public static void reloadConfig() {
        Constants.CONFIG.load();

        Universal.setDebug(Constants.CONFIG.getConfigData().isVerbose());
    }

    public static final int MAX_BUILD_LIMIT = 256;

    public static final String COMMAND_NAME = "chunkswap";
    public static final String COMMAND_PERMISSION_MAIN = "chunkswap";
    public static final String COMMAND_PERMISSION_LOAD = "chunkswap.load";
    public static final String COMMAND_PERMISSION_SAVE = "chunkswap.save";
    public static final String COMMAND_PERMISSION_RELOAD = "chunkswap.reload";

    public static String VERSION;
    public static List<String> DEV;
    public static String NAME;

}
