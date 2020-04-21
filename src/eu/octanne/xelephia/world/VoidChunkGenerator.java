package eu.octanne.xelephia.world;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class VoidChunkGenerator extends ChunkGenerator {

	private boolean hasPlatform;

	public VoidChunkGenerator(boolean spawnPlatform) {
		hasPlatform = spawnPlatform;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		ChunkData chunk = createChunkData(world);
		if (hasPlatform) {
			if (chunkX == 0 && chunkZ == 0) {
				for (int X = 0; X < 8; X++) {
					for (int Z = 0; Z < 8; Z++) {
						chunk.setBlock(X, 64, Z, Material.SANDSTONE);
					}
				}
			}
		}
		if(hasPlatform == false) super.generateChunkData(world, random, chunkX, chunkZ, biome);
		return chunk;
	}
}
