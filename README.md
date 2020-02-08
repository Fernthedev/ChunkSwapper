# ChunkSwapper
Swaps chunks in Minecraft Spigot

It is fully asynchronous. Each chunk pair (the two chunks that are swapping) has it's own thread that creates 256 unique threads that will swap two blocks in the chunk pairs.

Downloads can be found in [releases](https://github.com/Fernthedev/ChunkSwapper/releases)

# Usage:
/chunkswap -> main command

In the ChunkSwap plugin directory, create a file with the file extension .csf. Example: test.csf
In the file, you specify the world and the chunks.
## Format:
```
world("world") // The name of the world according to the server. **THIS MUST BE THE FIRST LINE AND IS REQUIRED AND CANNOT BE SPECIFIED MULTIPLE TIMES.**

x1:z1@x2:z2 // X1:Z1 are the chunk coordinates of the first chunk, same for X2:Z2 for the second chunk
```

After the file is created, you must load it with `/chunkswap load {file_name}`. The file name cannot use the file extension. If the name is test.csf, you use `/chunkswap load test`

After it is loaded, use `/chunkswap start`
