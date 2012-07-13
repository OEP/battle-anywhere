package org.oep.crawler.tile;

import java.util.HashMap;
import java.util.Random;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class TileManager {
	public static final int FUNCTION_RESOLUTION = 10;
	
	private HashMap<Integer, Bitmap> mTiles = new HashMap<Integer,Bitmap>();
	
	private float mAmplitudes[] = new float [FUNCTION_RESOLUTION];
	private float mFrequencies[] = new float [FUNCTION_RESOLUTION];
	private float mPhases[] = new float[FUNCTION_RESOLUTION];
	
	// A note: tiles to not necessarily need to be reproduced.
	private Random mRNG;
	
	public TileManager() {
		this(System.currentTimeMillis());
	}
	
	public TileManager(long seed) {
		mRNG = new Random(seed);
	}
	
	public Bitmap getTile(int color) {
		// Ignore the alpha channel.
		color |= 0xFF000000;
		
		Bitmap bm = mTiles.get(color);
		
		if(bm == null) {
			bm = cacheTile(color);
		}
		
		return bm;
	}
	
	private void makeFunction() {
		for(int i = 0; i < FUNCTION_RESOLUTION; i++) {
			mAmplitudes[i] = mRNG.nextFloat();
			mFrequencies[i] = (float) (20 * Math.PI * mRNG.nextFloat());
			mPhases[i] = (float) (2 * Math.PI * mRNG.nextFloat());
		}
	}
	
	private float noise(float x) {
		float sum = 0.0f;
		
		for(int i = 0; i < FUNCTION_RESOLUTION; i++) {
			sum += mAmplitudes[i] * Math.sin(mFrequencies[i] * x + mPhases[i]);
		}
		
		// Average the results of the periodic functions
		sum /= FUNCTION_RESOLUTION;
		
		return sum;
	}
	
	private float noise(float x, float y) {
		return (noise(x) + noise(y)) / 2;
	}

	private Bitmap cacheTile(int color) {
		// Ignore alpha channel.
		color |= 0xFF000000;
		
		// To make each tile look a little different
		makeFunction();
		
		Bitmap bm = Bitmap.createBitmap(Terrain.TILE_SIZE, Terrain.TILE_SIZE, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bm);
		Paint p = new Paint();
		
		p.setColor(color);
		p.setAlpha(128);
		canvas.drawRect(0, 0, Terrain.TILE_SIZE, Terrain.TILE_SIZE, p);
		
		for(int i = 0; i < Terrain.TILE_SIZE; i++) {
			for(int j = 0; j < Terrain.TILE_SIZE; j++) {
				int alpha = mRNG.nextInt(256);
				p.setColor(color);
				p.setAlpha(alpha);
				canvas.drawPoint(i, j, p);
			}
		}
		
		mTiles.put(color, bm);
		return bm;
	}
}
