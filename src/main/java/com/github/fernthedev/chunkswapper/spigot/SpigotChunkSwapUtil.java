package com.github.fernthedev.chunkswapper.spigot;

import com.github.fernthedev.chunkswapper.Constants;
import com.github.fernthedev.chunkswapper.IChunkSwapUtil;
import com.github.fernthedev.chunkswapper.file.ChunkSwap;
import com.github.fernthedev.chunkswapper.file.ChunkSwapFileData;
import com.github.fernthedev.fernapi.universal.Universal;
import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.fernutils.thread.multiple.TaskInfoList;
import kotlin.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SpigotChunkSwapUtil implements IChunkSwapUtil {
    @Override
    public boolean checkIfWorldExists(String world) {
        return Bukkit.getWorld(world) != null;
    }

    @Override
    public TaskInfoList parallelChunkSwap(ChunkSwapFileData chunkSwapFileData) {
        World world = Bukkit.getWorld(chunkSwapFileData.getWorld());

        if (world == null) throw new NullPointerException("World is null somehow");


        TaskInfoList taskInfoList = ThreadUtils.runForLoopAsync(new ArrayList<>(chunkSwapFileData.getChunkSwapMap().keySet()), (ChunkSwap chunkSwapKey) -> {
            ChunkSwap chunkSwapValue = chunkSwapFileData.getChunkSwapMap().get(chunkSwapKey);

            Chunk chunkKey = world.getChunkAt(chunkSwapKey.getX(), chunkSwapKey.getZ());
            Chunk chunkValue = world.getChunkAt(chunkSwapValue.getX(), chunkSwapValue.getZ());

            swapBlocksInChunk(world, chunkKey, chunkValue);

//            ThreadUtils.runAsync(taskInfo -> swapBlocksInChunk(world, chunkKey, chunkValue));
            return null;
        });

        try {
            taskInfoList.runThreads(ThreadUtils.ThreadExecutors.CACHED_THREADS.getExecutorService());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return taskInfoList;

//        for (ChunkSwap chunkSwapKey : chunkSwapFileData.getChunkSwapMap().keySet()) {
//            ChunkSwap chunkSwapValue = chunkSwapFileData.getChunkSwapMap().get(chunkSwapKey);
//
//            Chunk chunkKey = world.getChunkAt(chunkSwapKey.getX(), chunkSwapKey.getZ());
//            Chunk chunkValue = world.getChunkAt(chunkSwapValue.getX(), chunkSwapValue.getZ());
//
////            Map<Integer, List<Block>> blockLists = new HashMap<>(); // Lambda requires a mutable variable
//
//            ThreadUtils.runAsync(taskInfo -> swapBlocksInChunk(world, chunkKey, chunkValue));
//
////            TaskInfo taskInfoKey = ThreadUtils.runAsync(new TaskInfo(taskInfo -> blockLists.put(0, getBlocksInChunk(world, chunkKey))));
////            TaskInfo taskInfoVal = ThreadUtils.runAsync(new TaskInfo(taskInfo -> blockLists.put(1, getBlocksInChunk(world, chunkValue))));
////
////            taskInfoKey.awaitFinish(5);
////            taskInfoVal.awaitFinish(5);
////
////            List<Block> blockKey = blockLists.get(0);
////            List<Block> blockVal = blockLists.get(1);
////
////
////            ThreadUtils.runForLoopAsync(blockKey, blockVoidFunction);
//
//        }
    }

    public void swapBlocksInChunk(World world, Chunk chunkKey, Chunk chunkValue) {
        SpigotChunkSwapper spigotChunkSwapper = (SpigotChunkSwapper) Universal.getPlugin();

        Future<ChunkSwap> future = Bukkit.getServer().getScheduler().callSyncMethod(spigotChunkSwapper, new Callable<>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             *
             * @return computed result
             */
            @Override
            public ChunkSwap call() {
                world.loadChunk(chunkKey);
                world.loadChunk(chunkValue);
                Universal.debug("Loaded " + chunkKey.getX() + ":" + chunkKey.getZ() + " and " + chunkValue.getX() + ":" + chunkValue.getZ());
                return null;
            }
        });

        while (!future.isDone()) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }


//        final int[] blocksRequire = {16 * Constants.MAX_BUILD_LIMIT * 16}; //x*y*z
//        final int[] blocksDone = {0};

        List<Runnable> tasks = Collections.synchronizedList(new ArrayList<>());

        Map<Pair<Block, BlockData>, Pair<Block, BlockData>> blockMap = new HashMap<>();

        /////////////////////////////// X
//        ThreadUtils.runAsync(() -> {
        for (int xx = 0; xx < 16; xx++) {
            final int x = xx;

            ///////////////////////////////////// Z
//                ThreadUtils.runAsync(() -> {
            for (int zz = 0; zz < 16; zz++) {
                final int z = zz;

                ////////////////////////////////////// Y


                Runnable task = new Runnable() {
                    /**
                     * When an object implementing interface <code>Runnable</code> is used
                     * to create a thread, starting the thread causes the object's
                     * <code>run</code> method to be called in that separately executing
                     * thread.
                     * <p>
                     * The general contract of the method <code>run</code> is that it may
                     * take any action whatsoever.
                     *
                     * @see Thread#run()
                     */
                    @Override
                    public void run() {

                        // Used for optimization. This number is the
                        // highest y value with a non-air block. Once it is
                        // reached, the server skips the rest since they are air blocks
                        int maxHeight = -1; // -1 means value is undefined, to prevent setting the value multiple times when it is the same

                        for (int y = 0; y < Constants.MAX_BUILD_LIMIT && (maxHeight >= y || maxHeight == -1); y++) {

                            Block blockKey = chunkKey.getBlock(x, y, z);
                            Block blockValue = chunkValue.getBlock(x, y, z);

                            BlockData blockDataKey = blockKey.getBlockData();
                            BlockData blockDataValue = blockValue.getBlockData();

                            // If both blocks are air, swapping them is unnecessary.
                            // Might change this later to check if both are the same block,
                            // though blocks with data like chest and furnaces might be an issue
                            if (blockDataKey.getMaterial() == Material.AIR && blockDataValue.getMaterial() == Material.AIR) {
//                                    blocksRequire[0]--;
                                continue;
                            }

                            // Once the highest block is reached, skip the rest since it is air
                            if (maxHeight == -1) {
                                maxHeight = Math.max(world.getHighestBlockYAt(blockKey.getX(), blockKey.getZ()), world.getHighestBlockYAt(blockValue.getX(), blockValue.getZ()));
//                                    blocksRequire[0] -= Constants.MAX_BUILD_LIMIT - maxHeight;
                            }

                            Pair<Block, BlockData> keyPair = new Pair<>(blockKey, blockDataKey);
                            Pair<Block, BlockData> valuePair = new Pair<>(blockValue, blockDataValue);


                            blockMap.put(keyPair, valuePair);


                            Bukkit.getServer().getScheduler().callSyncMethod(spigotChunkSwapper, new Callable<>() {
                                /**
                                 * Computes a result, or throws an exception if unable to do so.
                                 *
                                 * @return computed result
                                 */
                                @Override
                                public Object call() {

                                    Universal.debug("Swapping " + toStringCoords(blockKey.getLocation()) + " " + toStringCoords(blockValue.getLocation()));

                                    blockKey.setBlockData(blockDataValue);
                                    blockValue.setBlockData(blockDataKey);
//                                        blocksDone[0]++;
//                                        Universal.debug("Done blocks " + blocksDone[0] + " missing to finish " + (blocksRequire[0] - blocksDone[0]));

                                    return null;
                                }
                            });


                        }
                        Universal.debug("Finished Y on " + x + ":" + z + " and task info is " + this.getClass().getName());
                        tasks.remove(this);
                    }
                };

                tasks.add(task);

                /////////////////////////////////////// Y
                Universal.debug("Finished Z on " + x);
            }
//                });
            ////////////////////// Z
            Universal.debug("Finished X");
        }
//        });
        //////////////////////////////////// X


        TaskInfoList taskInfoList = ThreadUtils.runAsyncList(tasks);

        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            taskInfoList.runThreads(executorService);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        taskInfoList.awaitFinish(1);

//        synchronized (tasks) {
//            for (Task taskInfo : tasks) {
//                Universal.debug("Waiting for task id: " + tasks.indexOf(taskInfo));
//                taskInfo.awaitFinish(5);
//                tasks.remove(taskInfo);
//            }
//        }

        Universal.debug("Finished waiting for chunk tasks");

        Bukkit.getServer().getScheduler().callSyncMethod(spigotChunkSwapper, new Callable<>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             *
             * @return computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            public ChunkSwap call() throws Exception {
                world.unloadChunk(chunkKey);
                world.unloadChunk(chunkValue);
                Universal.debug("Unloaded " + chunkKey.getX() + ":" + chunkKey.getZ() + " and " + chunkValue.getX() + ":" + chunkValue.getZ());
                return null;
            }
        });


//        Universal.debug("Waiting for blocks to finish now. Done:" + blocksDone[0] + " Left to finish: " + (blocksRequire[0] -blocksDone[0]));
//
//        while (blocksDone[0] != blocksRequire[0]) {
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
//            }
//        }


    }

    private static String toStringCoords(Location loc) {
        return "x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ();
    }
}
