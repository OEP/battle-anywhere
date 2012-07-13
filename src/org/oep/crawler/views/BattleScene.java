package org.oep.crawler.views;


import java.util.ArrayList;
import java.util.Random;

import org.oep.crawler.game.Creature;
import org.oep.crawler.tile.Camera;
import org.oep.crawler.tile.Feature;
import org.oep.crawler.tile.Terrain;
import org.oep.crawler.tile.TileManager;
import org.oep.crawler.util.Vector2;
import org.oep.crawler.views.Refreshable.RefreshHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

public class BattleScene extends Refreshable implements OnTouchListener, OnKeyListener {
	public static final String TAG = "BattleScene";
	
	
	private int mCameraOffsetX, mCameraOffsetY;
	
	private Camera mCamera = new Camera();
	private TileManager mTileManager = new TileManager();
	private ArrayList<Creature> mCreatures = new ArrayList<Creature>();
	private ArrayList<Feature> mFeatures = new ArrayList<Feature>();
	
	private int mHeightMap[][] = new int[MAX_WIDTH][MAX_HEIGHT];
	private int mTypeMap[][] = new int[MAX_WIDTH][MAX_HEIGHT];
	private int mTileMask[][] = new int[MAX_WIDTH][MAX_HEIGHT];
	private Vector2 mMovementPaths [][][] = new Vector2[MAX_WIDTH][MAX_HEIGHT][];
	
	private int mType;
	private int mWaterLevel;
	private long mSeed = System.currentTimeMillis();
	
	/** What kind of action is this player taking? */
	public static final int ACTION_NIL = 0,
		ACTION_MOVE = ACTION_NIL + 1,
		ACTION_STANDARD = ACTION_NIL + 2;
	
	public static final int STATE_NIL = 0,
		STATE_SELECT = STATE_NIL + 1,
		STATE_ANIMATE = STATE_NIL + 2;
	
	private int mAction = ACTION_NIL;
	private int mState = STATE_NIL;
	
	private boolean mMoved = false;
	private boolean mStandard = false;
	
	/** The current Creature whose turn it is... */
	private Creature mCurrent;
	
	/** The index of the creature whose turn it is */
	private int mCurrentIndex = 0;
	

	public BattleScene(Context context, AttributeSet attributes) {
		super(context, attributes);
		setup();
	}
	
	private void setup() {
		destroyHighlight();
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		mCamera.setFraction(1, 5);
		mRefreshHandler = new RefreshHandler(this);
	}
	
	/**
	 * Updates the game logic.
	 */
	@Override
	public void update() {
		mCamera.interpolate();
		
		if(mAction == ACTION_NIL && mState == STATE_NIL) {
			Camera.lookAt(mCamera, mCurrent.getX() + mCameraOffsetX, mCurrent.getY() + mCameraOffsetY);
		}
		
		if(mAction == ACTION_MOVE && mState == STATE_ANIMATE && mCurrent.move()) {
			mState = STATE_NIL;
			mAction = ACTION_NIL;
			nextTurn();
		}
		
		mRefreshHandler.sleep(1000 / 24);
	}
	
	@Override
	public void onDraw(Canvas c) {
		Paint p = new Paint();
	
		long start = System.currentTimeMillis();
		drawTerrain(c,p);
		drawCreatures(c,p);
		long stop = System.currentTimeMillis();
		
//		System.out.printf("Took %d millis to draw...\n", stop - start);
	}
	
	private void drawCreatures(Canvas c, Paint p) {
		for(Creature creature : mCreatures) {
			drawCreature(c,p,creature);
		}
	}
	
	private void drawCreature(Canvas canvas, Paint paint, Creature creature) {
		Camera cam = mCamera;
		
		// Calculate screen coordinates
		int sx = creature.getX() - (cam.x - getWidth() / 2);
		int sy = creature.getY() - (cam.y - getHeight() / 2);
		
		// Don't draw it if it is out of frame
		if(sx < 0 || sy < 0 || sx > getWidth() || sy > getHeight()) return;
		
		paint.setColor(Color.RED);
		canvas.drawCircle(sx, sy, Terrain.TILE_SIZE / 4, paint);
	}
	
	private void drawTerrain(Canvas c, Paint p) {
		Camera cam = mCamera;
		
		int w = this.getWidth();
		int h = this.getHeight();
		
		Rect screen = new Rect(0, 0, w, h);
		
		for(int i = 0; i < MAX_WIDTH; i++) {
			for(int j = 0; j < MAX_HEIGHT; j++) {
				Rect r = Terrain.getRect(i, j);
				
				r.offset( - (cam.x - w / 2), - (cam.y - h / 2));
				
				if(screen.contains(r) || Rect.intersects(screen, r)) {
					int color = mTypeMap[i][j];
					Bitmap bm = mTileManager.getTile(color);
					p.setAlpha(0xFF);
					c.drawBitmap(bm, r.left, r.top, p);
					
					if(mTileMask[i][j] != 0) {
						p.setColor(mTileMask[i][j]);
						c.drawRect(r, p);
					}
					
					if(mHeightMap[i][j] != 0) {
						p.setColor(0xFF000000 | ~color );
						c.drawText(String.valueOf(mHeightMap[i][j]), r.centerX(), r.centerY(), p);
					}
				}
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Put in integer-ready variables...
		int sx = (int) event.getX(),
			sy = (int) event.getY();
		
		// Transform to world coordinates.
		int wx = (mCamera.x - getWidth() / 2 + sx),
			wy = (mCamera.y - getHeight() / 2 + sy);
		
		int cx = mCurrent.getX(),
			cy = mCurrent.getY();
		
		if(mAction == ACTION_NIL && mState == STATE_NIL) {
			// Did the user click on the guy?
			if(Math.hypot(wx - cx, wy - cy) < Terrain.TILE_SIZE && event.getAction() == MotionEvent.ACTION_DOWN) {
				Log.d(TAG, "Detected touching of the player...");
				onMoveSelected();
			}
			
			// Assume the user is trying to "look" in this direction
			else if(event.getAction() != MotionEvent.ACTION_UP) {
				// Calculate how far off from center we are and set the camera offset
				mCameraOffsetX = sx - (getWidth() / 2);
				mCameraOffsetY = sy - (getHeight() / 2);
			}
			
			// Clear away the camera offset
			else {
				mCameraOffsetX = mCameraOffsetY = 0;
			}
			
			this.invalidate();
			return true;
		}
			
		
		if(mAction == ACTION_MOVE && mState == STATE_SELECT && event.getAction() == MotionEvent.ACTION_DOWN) {
			// Calculate the tile they clicked on...
			int tx = Terrain.toTile(wx), ty = Terrain.toTile(wy);
			
			// User wants to stop moving...
			if(event.getAction() == MotionEvent.ACTION_DOWN && Math.hypot(wx - cx, wy - cy) < Terrain.TILE_SIZE) {
				// Return to previous state..
				mState = STATE_NIL;
				mAction = ACTION_NIL;
				destroyHighlight();
			}
			
			// User wants to move dammit!
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				onMoveTileSelected(tx,ty);	
			}
			
			this.invalidate();
			return true;
		}
		
		Log.d(TAG, "Losing touch event");
		return false;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		int action = event.getAction();
		
		Log.d(TAG, "Saw onKey()");
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			mCameraOffsetY--; return true;
			
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mCameraOffsetY--; return true;
		}
		
		return false;
	}
	
	public void onMoveSelected() {
		// We ignore this in case called in invalid state.
		if(mMoved == true || mState != STATE_NIL || mAction != ACTION_NIL) return;
		
		Log.d(TAG, "Called onMoveSelected()");
		
		mAction = ACTION_MOVE;
		mState = STATE_SELECT;
		this.fillMovement(mCurrent);
	}
	
	public void onMoveTileSelected(int tx, int ty) {
		Log.d(TAG, String.format("Tile selected: (%d, %d)", tx, ty));
		if(tx < 0 || ty < 0 || tx >= MAX_WIDTH || ty >= MAX_WIDTH ||
				mMovementPaths[tx][ty] == null) return;
		
		Log.d(TAG, "Called onMoveTileSelected()");
	
		destroyHighlight();
		mCurrent.setPath(mMovementPaths[tx][ty]);
		deletePaths();
		
		mState = STATE_ANIMATE;
		mMoved = true;
	}
	
	public Camera getCamera() {
		return mCamera;
	}

	/**
	 * Add creature to the turn cycler. This method uses an insertion
	 * sort according to the creature's quick score.
	 * @param creature
	 * @return index of the creature added
	 */
	public int addCreature(Creature creature) {
		// If nobody is here, they are the first in line.
		if(mCreatures.size() == 0) {
			mCreatures.add(creature);
			mCurrent = creature;
			return 0;
		}
		
		for(int i = 0; i < mCreatures.size(); i++) {
			Creature dude = mCreatures.get(0);
			
			// Add it before dude if he's quicker
			if(creature.getQuick() > dude.getQuick()) {
				mCreatures.add(i, creature);
				mCurrentIndex = 0;
				mCurrent = mCreatures.get(0);
				return i;
			}
		}
		
		// He's really effin' slow. Add him to the end.
		mCreatures.add(creature);
		mCurrentIndex = 0;
		mCurrent = mCreatures.get(0);
		return mCreatures.size() - 1;
	}
	
	public void addFeature(Feature feature) {
		mFeatures.add(feature);
	}
	
	public void setSeed(long seed) {
		mSeed = seed;
	}
	
	public void setType(int type) {
		mType = type;
	}
	
	public void nextTurn() {
		mCurrentIndex = (mCurrentIndex + 1) % mCreatures.size();
		mCurrent = mCreatures.get(mCurrentIndex);
		
		// Reset all the state elements.
		mMoved = false;
		mStandard = false;
		mState = STATE_NIL;
		mAction = ACTION_NIL;
		
	}

	public void generate() {
		Random rng = new Random(mSeed);
		for(int i = 0; i < mTypeMap.length; i++)
			for(int j = 0; j < mTypeMap[i].length; j++) {
				mTypeMap[i][j] = mType;
			}
		
		long start = System.currentTimeMillis();
		
		generateLand(rng);
		placeCreatures(rng);
		
		long stop = System.currentTimeMillis();
		
		System.out.printf("Took %d millis to generate...\n", stop - start);
	}
	
	private void placeCreatures(Random rng) {
		if(mCreatures.size() == 0)
			return;
		
		for(Creature c : mCreatures) {
			c.setTilePosition(rng.nextInt(MAX_WIDTH), rng.nextInt(MAX_HEIGHT));
		}
	}

	private void generateLand(Random rng) {
		if(mFeatures == null) return;
		
		for(Feature feature : mFeatures) {
			int x = rng.nextInt(MAX_WIDTH);
			int y = rng.nextInt(MAX_HEIGHT);
			int h = feature.Height;
			
			fillLand(x, y, feature.Color, feature.ColorLife, rng);
			
			while(h --> 0) {
				fillHeight(x, y, 0, feature.HeightLife - h, rng);
			}
		}
	}
	
	private void fillHeight(int x, int y, int height, int life, Random rng) {
		if(life <= 0 || rng.nextInt(life) == 0 ||
				x < 0 || x >= MAX_WIDTH || y < 0 || y >= MAX_HEIGHT ||
				mHeightMap[x][y] > height) return;
		
		mHeightMap[x][y]++;
		fillHeight(x - 1, y - 1, height + 1, life - 1, rng);
		fillHeight(x, y - 1, height + 1, life - 1, rng);
		fillHeight(x + 1, y - 1, height + 1, life - 1, rng);
		fillHeight(x - 1, y, height + 1, life - 1, rng);
		fillHeight(x + 1, y, height + 1, life - 1, rng);
		fillHeight(x - 1, y + 1, height + 1, life - 1, rng);
		fillHeight(x, y + 1, height + 1, life - 1, rng);
		fillHeight(x + 1, y + 1, height + 1, life - 1, rng);
	}
	
	private void fillLand(int x, int y, int type, int life, Random rng) {
		// Stop if we have no life left or our life was randomly cut short
		// Also make sure we are inside the bounds of the array
		// and that the current land type isn't the subtype
		if(life <= 0 || rng.nextInt(life) == 0 ||
				x < 0 || x >= MAX_WIDTH || y < 0 || y >= MAX_HEIGHT ||
				mTypeMap[x][y] == type) return;
		
		mTypeMap[x][y] = type;
		
		// Use the flood fill algorithm to continue the growth
		fillLand(x - 1, y - 1, type, life - 1, rng);
		fillLand(x, y - 1, type, life - 1, rng);
		fillLand(x + 1, y - 1, type, life - 1, rng);
		fillLand(x - 1, y, type, life - 1, rng);
		fillLand(x + 1, y, type, life - 1, rng);
		fillLand(x - 1, y + 1, type, life - 1, rng);
		fillLand(x, y + 1, type, life - 1, rng);
		fillLand(x + 1, y + 1, type, life - 1, rng);
	}
	
	private void fillMovement(Creature c) {
		int move = c.getMovement();
		int x = c.getTileX();
		int y = c.getTileY();
		
		boolean visits[][] = new boolean[MAX_WIDTH][MAX_HEIGHT];
		
		mTileMask[x][y] = BattleScene.HIGHLIGHT_MOVEMENT;
		visits[x][y] = true;
		
		long start = System.currentTimeMillis();
		
		fillMovement(c, x, y - 1, x, y, visits, move);
		fillMovement(c, x - 1, y, x, y, visits, move);
		fillMovement(c, x + 1, y, x, y, visits, move);
		fillMovement(c, x, y + 1, x, y, visits, move);
		
		long stop = System.currentTimeMillis();
		
		System.out.printf("Took %d millis to solve...\n", stop - start);
	}
	
	private void fillMovement(Creature c, int x, int y, int px, int py, boolean [][] visits, int points) {
		// Stop if out of bounds.
		if(x < 0 || x >= MAX_WIDTH || y < 0 || y>= MAX_HEIGHT) return;
		
		// Stop if we've been here...
		if(visits[x][y] == true) return;
		
		// Calculate the base penalty plus the added penalty from height differential
		int h0 = mHeightMap[px][py];
		int h1 = mHeightMap[x][y];
		int penalty = 1 + Creature.calcHeightPenalty(h1 - h0);
		
		// If they can't make the jump or if the movement penalty is too great, terminate.
		if(!c.canJump(h1 - h0) || penalty > points) return;
		
		// Apply the penalty
		points -= penalty;
		
		// Set the tile mask for this tile
		mTileMask[x][y] = BattleScene.HIGHLIGHT_MOVEMENT;
		visits[x][y] = true;
		
		int oldLength = (mMovementPaths[x][y] == null) ? 0 : mMovementPaths[x][y].length;
		int newLength = (mMovementPaths[px][py] == null) ? 1 : mMovementPaths[px][py].length + 1;
		
		// Path is null. Make a path with one length.
		if(oldLength == 0) {
			Log.d(TAG, String.format("Adding possible movement square: (%d, %d)", x, y));
			mMovementPaths[x][y] = new Vector2[1];
		}
		
		// Current path is non-null. See if we can make it shorter.
		else if(newLength < oldLength) {
			mMovementPaths[x][y] = new Vector2 [mMovementPaths[px][py].length + 1];
		}

		// Decide how we should populate this new path.
		if(mMovementPaths[x][y].length == 1) {
			// The current path is simply the current point
			mMovementPaths[x][y][0] = new Vector2(x,y);
		}
		else {
			Vector2 currentPath[] = mMovementPaths[px][py];
			
			// Copy over the current path
			for(int i = 0; i < currentPath.length; i++) {
				mMovementPaths[x][y][i] = currentPath[i];
			}
			
			// Tack on the current x-y coordinate
			mMovementPaths[x][y][ mMovementPaths[x][y].length - 1 ] = new Vector2(x,y);
		}
		
		fillMovement(c, x, y - 1, x, y, visits, points);
		fillMovement(c, x - 1, y, x, y, visits, points);
		fillMovement(c, x + 1, y, x, y, visits, points);		
		fillMovement(c, x, y + 1, x, y, visits, points);

		visits[x][y] = false;
	}
	
	private void destroyHighlight() {
		Log.d(TAG, "Deleting hilights...");
		
		for(int i = 0; i < MAX_WIDTH; i++)
			for(int j = 0; j <  MAX_HEIGHT; j++) {
				mTileMask[i][j] = 0;
			}
	}
	
	private void deletePaths() {
		Log.d(TAG, "Deleting paths...");
		for(int i = 0; i < MAX_WIDTH; i++)
			for(int j = 0; j < MAX_HEIGHT; j++) {
				mMovementPaths[i][j] = null;
			}
	}
	
	
	
	public static final int HIGHLIGHT_MOVEMENT = 0x990000FF;
	
	public static final int MAX_WIDTH = 28;
	public static final int MAX_HEIGHT = 20;
}
