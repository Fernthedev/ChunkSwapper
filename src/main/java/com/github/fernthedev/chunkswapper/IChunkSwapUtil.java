package com.github.fernthedev.chunkswapper;

import com.github.fernthedev.chunkswapper.file.ChunkSwapFileData;
import com.github.fernthedev.fernutils.thread.multiple.TaskInfoList;

public interface IChunkSwapUtil {

    boolean checkIfWorldExists(String world);

    TaskInfoList parallelChunkSwap(ChunkSwapFileData chunkSwapFileData);

}
