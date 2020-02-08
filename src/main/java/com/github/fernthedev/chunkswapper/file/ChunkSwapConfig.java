package com.github.fernthedev.chunkswapper.file;

import com.github.fernthedev.chunkswapper.Constants;
import com.github.fernthedev.config.common.Config;
import lombok.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkSwapConfig extends Config<ChunkSwapFileData> {

    public ChunkSwapConfig(ChunkSwapFileData configData, @NonNull File file) {
        super(configData, file);
    }


    /**
     * Should return a String representation of the file {@link #configData}. This string representation should be the way that it is read in {@link #parseConfigFromData(List)}
     *
     * @return String representation of {@link #configData} that is read by {@link #parseConfigFromData(List)}
     */
    @Override
    protected String configToFileString() {
        StringBuilder builder = new StringBuilder();

        Map<ChunkSwap, ChunkSwap> chunkSwapMap = configData.getChunkSwapMap();

        for (ChunkSwap chunkSwapKey : chunkSwapMap.keySet()) {
            ChunkSwap chunkSwapValue = chunkSwapMap.get(chunkSwapKey);
            builder.append(chunkToString(chunkSwapKey))
                    .append("@")
                    .append(chunkToString(chunkSwapValue))
                    .append("\n");
        }

        return builder.toString();
    }

    protected static String chunkToString(ChunkSwap chunkSwap) {
        return chunkSwap.getX() + ":" + chunkSwap.getZ();
    }

    /**
     * Returns the object instance of {@link #configData} parsed from the file which is saved by {@link #configToFileString()}
     *
     * @param data The String data from the file.
     * @return The object instance.
     */
    @Override
    protected ChunkSwapFileData parseConfigFromData(@NonNull List<String> data) {
        Map<ChunkSwap, ChunkSwap> chunkSwapMap = new HashMap<>();

        String worldData = data.get(0);
        int index = 0;

        while (worldData.trim().equals("") || worldData.trim().isBlank() || worldData.trim().isEmpty())  {
            index++;
            worldData = data.get(index);
            if(!(worldData.trim().equals("") || worldData.trim().isBlank() || worldData.trim().isEmpty())) break;
        }

        String world;

        String worldStart = "world(\""; // world("
        String worldEnd = "\")"; // ")

        if (worldData.startsWith(worldStart) && worldData.endsWith(worldEnd)) {

            world = worldData.substring(
                    worldStart.length(),
                    worldData.indexOf(worldEnd));
        } else {
            throw new IllegalArgumentException("The first line must contain a world statement. Example: world(\"world\")");
        }

        boolean worldExist = Constants.chunkSwapUtil.checkIfWorldExists(world);

        if(!worldExist) throw new IllegalArgumentException("World " + world + " not recognized or does not exist. Please make sure it is typed correctly");



        for (String line : Arrays.copyOfRange(data.toArray(new String[0]), index, data.size())) {
            if(line.trim().equals("") || line.trim().isBlank() || line.trim().isEmpty() || line.startsWith(worldStart)) continue;

            String[] chunkSwapArray = line.split("@", 2); // Splits 1..3 as example into {1,3} array

            try {
                ChunkSwap chunkKey = fromArray(chunkSwapArray[0]);
                ChunkSwap chunkVal = fromArray(chunkSwapArray[1]); // Gets the second element, the value
                chunkSwapMap.put(chunkKey, chunkVal);
            } catch (Exception e) {
                throw new RuntimeException("The string is " + line + " chunkswaparray: " + Arrays.toString(chunkSwapArray), e);
            }
        }

        return new ChunkSwapFileData(world, chunkSwapMap);
    }

    private ChunkSwap fromArray(String data) {
        String[] splitChunk = data.split(":", 2);
        return new ChunkSwap(splitChunk[0], splitChunk[1]); // Gets the first element, the key
    }
}
