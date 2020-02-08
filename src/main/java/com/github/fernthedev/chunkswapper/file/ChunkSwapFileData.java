package com.github.fernthedev.chunkswapper.file;

import lombok.Getter;

import java.util.Map;

public class ChunkSwapFileData {
    public static final String FILE_EXTENSION = ".csf"; // ChunkSwapFileData

    @Getter
    private String world;

    @Getter
    private final Map<ChunkSwap, ChunkSwap> chunkSwapMap; // The key (first parameter) gets swapped with the value (second parameter)


    public ChunkSwapFileData(String world, Map<ChunkSwap, ChunkSwap> chunkSwapMap) {
        this.world = world;
        this.chunkSwapMap = chunkSwapMap;
    }
}
