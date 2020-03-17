package com.github.fernthedev.chunkswapper.command;

import com.github.fernthedev.chunkswapper.Constants;
import com.github.fernthedev.chunkswapper.file.ChunkSwapConfig;
import com.github.fernthedev.chunkswapper.file.ChunkSwapFileData;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernapi.universal.api.CommandSender;
import com.github.fernthedev.fernapi.universal.api.IFConsole;
import com.github.fernthedev.fernapi.universal.api.UniversalCommand;
import com.github.fernthedev.fernapi.universal.data.chat.BaseMessage;
import com.github.fernthedev.fernapi.universal.data.chat.ChatColor;
import com.github.fernthedev.fernapi.universal.data.chat.TextMessage;
import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.google.common.base.Stopwatch;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class FileCommand extends UniversalCommand {

    private ChunkSwapConfig chunkSwapConfig;

    /**
     * Construct a new command.
     */
    public FileCommand() {
        super(Constants.COMMAND_NAME, Constants.COMMAND_PERMISSION_MAIN);

        Argument loadArgument = new Argument("load", (sender, args) -> {
            if (sender.hasPermission(Constants.COMMAND_PERMISSION_LOAD)) {
                if(args.length == 0) {
                    sender.sendMessage(new TextMessage("Please provide name of fileName which must be in plugin directory and fileName extension of " + ChunkSwapFileData.FILE_EXTENSION + " \nNote do not provide the fileName extension in the command"));
                    return;
                }

                String fileName = args[0] + ChunkSwapFileData.FILE_EXTENSION;

                File file = new File(Universal.getMethods().getDataFolder(), fileName);

                if (!file.exists()) {
                    sender.sendMessage(new TextMessage("&cFile " + fileName + " does not exist. Did you type it correctly?"));
                    return;
                }

                ChunkSwapConfig config;
                try {
                    log(sender, new TextMessage("&aLoading fileName " + fileName + " now"));
                    Stopwatch stopWatch = Stopwatch.createStarted();
                    config = new ChunkSwapConfig(
                            new ChunkSwapFileData("", new HashMap<>()),
                            file);
                    stopWatch.stop();
                    log(sender, new TextMessage("&aFinished loading. Took " + stopWatch.elapsed(TimeUnit.MILLISECONDS) + "ms"));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    sender.sendMessage(new TextMessage("&cError reading file " + fileName + ". Error: &4" + e.getMessage() + ". &cCheck log for more details"));
                    return;
                }

               this.chunkSwapConfig = config;
            } else {
                sender.sendMessage(new TextMessage(Universal.getLocale().noPermission(Constants.COMMAND_PERMISSION_LOAD, this)));
            }
        });

        Argument startArgument = new Argument("start", (sender, args) -> {

            if (!sender.hasPermission(Constants.COMMAND_PERMISSION_SAVE)) {
                sender.sendMessage(new TextMessage(Universal.getLocale().noPermission(Constants.COMMAND_PERMISSION_SAVE, this)));
                return;
            }

            if (chunkSwapConfig == null) {
                sender.sendMessage(new TextMessage("You must first load a file using /chunkswap load {filename}"));
                return;
            }

            ThreadUtils.runAsync(() -> {
                log(sender, new TextMessage("&aStarting chunk swapping now"));
                Stopwatch stopwatch = Stopwatch.createStarted();

                try {
                    Constants.chunkSwapUtil.parallelChunkSwap(chunkSwapConfig.getConfigData()).awaitFinish(30);
                } catch (Exception e) {
                    e.printStackTrace();
                    log(sender, new TextMessage("&cUnable to finish chunk swap. Error: &4" + e.getMessage() +"\n&cMore details in console"));
                }


                stopwatch.stop();
                log(sender, new TextMessage("&aFinished chunk swap. Taken " + NumberFormat.getNumberInstance().format(stopwatch.elapsed(TimeUnit.MILLISECONDS)) + "ms"));
            }, ThreadUtils.ThreadExecutors.CACHED_THREADS.getExecutorService());

        });

        Argument reloadConfig = new Argument("reload", (sender, args) -> {
            if (!sender.hasPermission(Constants.COMMAND_PERMISSION_RELOAD)) {
                sender.sendMessage(new TextMessage(Universal.getLocale().noPermission(Constants.COMMAND_PERMISSION_RELOAD, this)));
                return;
            }


            Stopwatch stopwatch = Stopwatch.createStarted();
            Constants.reloadConfig();
            stopwatch.stop();

            log(sender, new TextMessage("&aFinished reload. Taken " + NumberFormat.getNumberInstance().format(stopwatch.elapsed(TimeUnit.MILLISECONDS)) + "ms"));

        });

        addArgument(loadArgument);
        addArgument(startArgument);
        addArgument(reloadConfig);
    }

    private void log(CommandSender sender, BaseMessage baseMessage) {
        if (!(sender instanceof IFConsole)) {
            sender.sendMessage(baseMessage);
        }
        Universal.getMethods().getLogger().info(ChatColor.translateAlternateColorCodes('&', baseMessage.toLegacyText()));
    }

    /**
     * Called when executing the command
     *
     * @param sender The source
     * @param args   The arguments provided
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextMessage("&aRunning plugin &2" + Constants.NAME + " at version &6" + Constants.VERSION + " created by &a" + Constants.DEV));
        } else {
            executeArguments(sender, args);
        }
    }
}
