package com.github.fernthedev.chunkswapper;

import com.github.fernthedev.chunkswapper.file.ChunkSwap;
import com.github.fernthedev.chunkswapper.file.ChunkSwapFileData;
import com.github.fernthedev.fernutils.thread.multiple.TaskInfoForLoop;

public interface IChunkSwapUtil {

    boolean checkIfWorldExists(String world);

    TaskInfoForLoop<ChunkSwap> parallelChunkSwap(ChunkSwapFileData chunkSwapFileData);

}
