package org.oep.crawler.tile;

import android.graphics.Rect;

public class Terrain {
	public static final int TYPE_GRASS = 0xFF00FF00;
	public static final int TYPE_ROCK = 0xFFCCCCCC;
	public static final int TYPE_SAND = 0xFFCCCC33;
	public static final int TYPE_WATER = 0xFF0000FF;
	public static final int TYPE_ICE = 0xFFFFFFFF;
	public static final int TYPE_SKITTLES = 0xFFF0F0F0;
	
	public static final int TILE_SIZE = 35;
	
	/**
	 * Convert a tile coordinate to a world coordinate.
	 * @param tile coordinate
	 * @return world coordinate
	 */
	public static int toWorld(int tile) {
		return tile * TILE_SIZE + TILE_SIZE / 2;
	}
	
	/**
	 * Convert a world coordinate to a tile coordinate.
	 * @param world coordinate
	 * @return tile coordinate
	 */
	public static int toTile(int world) {
		return world / TILE_SIZE;
	}
	
	/**
	 * Get a Rect representing the tile in world coordinates.
	 * @param i, x-index of the tile
	 * @param j, y-index of the tile
	 * @return Rect representing the tile.
	 */
	public static Rect getRect(int i, int j) {
		return new Rect(i * Terrain.TILE_SIZE,
				j * Terrain.TILE_SIZE,
				(i + 1) * Terrain.TILE_SIZE,
				(j + 1) * Terrain.TILE_SIZE);
	}
}
