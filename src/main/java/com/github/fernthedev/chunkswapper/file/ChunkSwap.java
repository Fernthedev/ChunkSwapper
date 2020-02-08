package com.github.fernthedev.chunkswapper.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public class ChunkSwap {

    private int x;
    private int z;

    public ChunkSwap(String x, String y) {
        this(Integer.parseInt(x), Integer.parseInt(y));
    }

}
