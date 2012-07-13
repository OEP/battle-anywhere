package org.oep.crawler.tile;

import java.util.Random;


/**
 * This class represents a "land feature". The terrain generator
 * assumes that we'd like to do things like couple a raise in the 
 * land height with a specific land feature type.
 * 
 * This object allows us to couple together a land type, a land height
 * as well as how prominent any specific land feature is.
 * @author OEP
 *
 */
public class Feature {
	
	
	/**
	 * This constructor shall construct all land features.
	 * @param height the maximum height of the land
	 * @param heightLife how far this maximum height may extend
	 * @param color the color of this land feature
	 * @param colorLife the "life" of this land feature
	 */
	public Feature(int height, int heightLife, int color, int colorLife) {
		Height = height;
		HeightLife = heightLife;
		Color = color;
		ColorLife = colorLife;
	}
	
	/** We'll need this Random object */
	private static Random mRNG = new Random();
	
	/** How high the maximum point of the land can be. */
	public int Height;
	
	/** How far the maximum height could extend */
	public int HeightLife;
	
	/** What color is this feature */
	public int Color;
	
	/** How far out can this color extend */
	public int ColorLife;
	
	public static Feature getRandom(int height, int heightLife, int colorLife) {
		height = Math.abs(height);
		heightLife = Math.abs(heightLife);
		colorLife = Math.abs(colorLife);
		
		return new Feature(mRNG.nextInt(height), mRNG.nextInt(heightLife), 0xFF000000 | mRNG.nextInt(0xFFFFFF),
				mRNG.nextInt(colorLife));
	}
	
	public static final Feature HILL = new Feature(6, 24, 0, 0);
	public static final Feature ROCKY_MOUNTAIN = new Feature(24, 24, Terrain.TYPE_ROCK, 26);
	public static final Feature SAND_PIT = new Feature(-6, 8, Terrain.TYPE_SAND, 8);
}
