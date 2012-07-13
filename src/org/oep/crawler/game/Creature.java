package org.oep.crawler.game;

import java.util.Random;

import org.oep.battle.R;
import org.oep.crawler.tile.Terrain;
import org.oep.crawler.util.Vector2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Creature {
	/** Movement speed of the player. */
	public static final int MOVE_SPEED = 5;
	
	/** Standard points-per-level */
	public static final int PPL_HERO = 3, PPL_BOSS = 5, PPL_CREEP = 1;
	
	/** Standard starting base attributes */
	public static final int ATTRIBUTE_STRONG = 18, ATTRIBUTE_MID = 15, ATTRIBUTE_WEAK = 10;
	
	/** Base core attributes. These remain unchanging. */
	private int mBaseStrong;

	private int mBaseQuick;

	private int mBaseSmart;
	
	/** Fluid core attributes. These reflect the "current" state */
	private int mStrong, mQuick, mSmart;
	
	/** Attribute points gained per level */
	private int mPPL = 1;
	
	private int mHealth, mHealthMax;
	
	/** Creature experience points */
	private int mExperience;
	
	/** Creature's seed used for generating the creature */
	private long mSeed;
	
	/** Destination X, Y indices */
	private int mDestX, mDestY;
	
	/** A path to follow, if any */
	private Vector2 mPath[];
	
	/** An index to the path */
	private int mPathIndex = 0;
	
	/** Fine X, Y world coordinates of this creature */
	private int mX, mY;
	
	/** Is the creature a player? (Oh yeah he is...) */
	private boolean mPlayer = false;
	
	/** Bitmap of the creature */
	private Bitmap mBitmap;
	
	/** Resource ID for this image */
	private int mImageId;
	
	/** Resources resovler for the bitmap */
	private Resources mResources;

	private static final String TAG = "Creature";
	
	/**
	 * Constructor to build a random creature based on this id.
	 * @param id to use as a seed
	 */
	public Creature(String id, int ppl) {
		buildCreature(id);
		mSeed = Long.decode("0x" + id);
		mPPL = ppl;
		syncLevel();
	}
	

	/**
	 * Alias constructor which uses the current time as a seed.
	 * @param strong, starting attribute for strong
	 * @param quick, starting attribute for quick
	 * @param smart, starting attribute for smart
	 * @param exp, the experience of this creature
	 */
	public Creature(int strong, int quick, int smart, int exp, int ppl) {
		this(strong,quick,smart,exp,ppl,System.currentTimeMillis());
	}

	/**
	 * Alias constructor which uses the current time as a seed.
	 * @param strong, starting attribute for strong
	 * @param quick, starting attribute for quick
	 * @param smart, starting attribute for smart
	 * @param exp, the experience of this creature
	 * @param seed, the seed we should use to generate this creature
	 */
	Creature(int strong, int quick, int smart, int exp, int ppl, long seed) {
		mBaseStrong = strong;
		mBaseQuick = quick;
		mBaseSmart = smart;
		mExperience = exp;
		mSeed = seed;
		mPPL = ppl;
		
		syncLevel();
	}
	
	/**
	 * Add experience to the creature's total amount.
	 * @param exp, amount of experience to add
	 */
	public void addExperience(int exp) {
		mExperience += exp;
		syncLevel();
	}	
	
	/**
	 * Temporary method to implement the damage formula for a basic attack.
	 * @param baddie, the creature to attack
	 * @param atk the attack to inflict
	 * @return amount of damage incurred
	 */
	public int attack(Creature baddie, Attack atk) {
		int attack = 0, defense = 0;
		
		switch(atk.getAttack()) {
		case Strong: attack = getStrong(); break;
		case Quick: attack = getQuick(); break;
		case Smart: attack = getSmart(); break;
		}
		
		switch(atk.getDefense()) {
		case Strong: defense = baddie.getStrong(); break;
		case Quick: defense = baddie.getQuick(); break;
		case Smart: defense = baddie.getSmart(); break;
		}
		
		
		int dmg = atk.calculateDamage(attack, defense);
		baddie.takeDamage(dmg);
		return dmg;
	}
	
	/**
	 * Perform a bounced subtraction from a creatures HP.
	 * @param dmg
	 */
	public void takeDamage(int dmg) {
		mHealth = Math.max(0, Math.min(mHealth - dmg, mHealthMax));
	}
	
	/**
	 * Implicit function that tells the creature to move to destination.
	 * @return true if we have reached the destination
	 */
	public boolean move() {
		Log.d(TAG, "In move()");
		int x, y;
		
		if(mPath == null) {
			Log.d(TAG, "\tNo path found");
			x = Terrain.toWorld(mDestX);
			y = Terrain.toWorld(mDestY);
		}
		else {
			int i = mPathIndex;
			
			// Normalize index
			i = (i >= 0 && i < mPath.length) ? i : mPath.length - 1;
			
			// Update world coordinates
			x = Terrain.toWorld( mPath[i].x );
			y = Terrain.toWorld( mPath[i].y );
			
			Log.d(TAG, String.format("Moving toward %d, %d in path", x, y));
		}
		
		int dx = Math.min(MOVE_SPEED, Math.abs(x - mX));
		int dy = Math.min(MOVE_SPEED, Math.abs(y - mY));
		
		mX = (mX > x) ? mX - dx : mX + dx;
		mY = (mY > y) ? mY - dy : mY + dy;
		
		// Find the next point if necessary
		if(mPath != null && mX == x && mY == y && mPathIndex < mPath.length - 1) {
			mPathIndex++;
			x = Terrain.toWorld(mPath[mPathIndex].x);
			y = Terrain.toWorld(mPath[mPathIndex].y);
		}
		
		// We're done if we're at the final point, and we either have no path, or are at the end of it.
		return mX == x && mY == y && (mPath == null || (mPath != null && mPathIndex == mPath.length - 1));
	}

	/**
	 * Return the seed that generates this creature.
	 * @return seed of creature
	 */
	public long getSeed() {
		return mSeed;
	}
	
	/**
	 * Return the seed of this creature as a hexadecimal String.
	 * @return seed in a hexadecimal string
	 */
	public String getSeedString() {
		String right = Long.toHexString(mSeed).toUpperCase();
		StringBuffer left = new StringBuffer();
		
		for(int i = 0; i < 12 - right.length(); i++) {
			left.append('0');
		}
		
		return left.toString() + right;
	}
	
	/**
	 * Get the net strong score of this creature.
	 * @return the sum of the strong score
	 */
	public int getStrong() {
		return mStrong + getStrongModifier();
	}
	
	/**
	 * Get net quick rating of this creature.
	 * @return sum of quick score.
	 */
	public int getQuick() {
		return mQuick + getQuickModifier();
	}
	
	/**
	 * Get net smart rating of this creature.
	 * @return sum of strong score
	 */
	public int getSmart() {
		return mSmart + getSmartModifier();
	}
	
	public int getStrongModifier() {
		// TODO: Add equipment effects and stuff here.
		return 0;
	}
	
	public int getQuickModifier() {
		// TODO: Add equipment effects and stuff here.
		return 0;
	}
	
	public int getSmartModifier() {
		// TODO: Add equipment effects and stuff here.
		return 0;
	}
	
	/**
	 * Calculates the level of this creature from the experience.
	 * @return the level of this creature.
	 */
	public int getLevel() {
		return Creature.calcLevel(mExperience);
	}
	
	public int getMovement() {
		return Creature.calcMovement(getQuick());
	}
	
	public int getRank() {
		return getSmart() + getQuick() + getStrong();
	}
	
	public int getHP() {
		return mHealth;
	}
	
	public int getXP() {
		return mExperience;
	}
	
	public int getPPL() {
		return mPPL;
	}
	
	public int getMaxHP() {
		return mHealthMax;
	}
	
	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
	
	public int getTileX() {
		return mDestX;
	}
	
	public int getTileY() {
		return mDestY;
	}
	
	public boolean canJump(int dh) {
		return (getQuick() / 4) >= Math.abs(dh);
	}
	
	public void setImageResource(int resid, Context ctx) {
		setImageResource(resid, ctx.getResources());
	}
	
	public void setImageResource(int resId, Resources r) {
		if(mImageId == resId && mResources == r) return;
		
		mImageId = resId;
		mResources = r;
		mBitmap = null;
	}
	
	private void resolveBitmap() {
		if(mBitmap != null) return;
		
		Log.i(TAG, String.format("Resolving resource %d\n", mImageId));
		mBitmap = BitmapFactory.decodeResource(mResources, mImageId);
	}
	
	public Bitmap getBitmap() {
		resolveBitmap();
		return mBitmap;
	}
	
	public int getImageId() {
		return mImageId;
	}
	
	public void setPosition(int x, int y) {
		mX = x;
		mY = y ;
	}
	
	public void setTilePosition(int i, int j) {
		mX = Terrain.toWorld(i);
		mY = Terrain.toWorld(j);
		setDestination(i,j);
	}
	
	public void setDestination(int i, int j) {
		mDestX = i;
		mDestY = j;
	}
	
	public void setPath(Vector2 path[]) {
		mPath = path;
	}
	
	public void setPlayer(boolean b) {
		mPlayer = b;
	}
	
	public boolean isDead() {
		return mHealth == 0;
	}
	
	private void buildCreature(String mac) {
		// Isolate the first 6 hex digits and use as the seed
		long seed = Long.decode( String.format("0x%s%s", mac.substring(0,6), "00000000") );
		Random rng = new Random(seed);
		
		int a = rng.nextInt(3), b = rng.nextInt(2);
		
		// Start off all at weak
		mBaseStrong = mBaseQuick = mBaseSmart = ATTRIBUTE_WEAK;
		
		switch(a) {
		case 0:
			mBaseStrong = ATTRIBUTE_STRONG;
			switch(b) {
			case 0: mBaseQuick = ATTRIBUTE_MID; break;
			case 1: mBaseSmart = ATTRIBUTE_MID; break;
			}
			break;
			
		case 1:
			mBaseQuick = ATTRIBUTE_STRONG;
			switch(b) {
			case 0: mBaseStrong = ATTRIBUTE_MID; break;
			case 1: mBaseSmart = ATTRIBUTE_MID; break;
			}
			break;
			
		case 2:
			mBaseSmart = ATTRIBUTE_STRONG;
			
			switch(b) {
			case 0: mBaseQuick = ATTRIBUTE_MID; break;
			case 1: mBaseStrong = ATTRIBUTE_MID; break;
			}
			break;
		}
	}
	
	/**
	 * Levels up the creature
	 */
	private void syncLevel() {
		Random rng = new Random(mSeed);
		mStrong = mBaseStrong;
		mQuick = mBaseQuick;
		mSmart = mBaseSmart;
			
		
		int sum = mStrong + mQuick + mSmart;
		int level = getLevel();
		int oldmax = mHealthMax;
		
		
		// Reset the max health
		mHealthMax = 0;
		
		for(int i = 1; i < level; i++) {
			
			for(int j = 0; j < mPPL; j++) {
				int r = rng.nextInt(sum);
				
				if(r < mStrong) {
					mStrong++;
				}
				else if(r >= mStrong && r < mStrong + mQuick) {
					mQuick++;
				}
				else {
					mSmart++;
				}
				
				// Add the random health bonus
				mHealthMax += 1 + rng.nextInt(getStrong() / 4);
			}
			
		}
		
		// Add the flat health bonus
		mHealthMax += level * getStrong() / 2;
		
		// Gain some HP in case the max changed
		mHealth += mHealthMax - oldmax;
	}
	
	public String toString() {
		return String.format("%s: [HP: %d/%d, Strong: %d, Quick: %d, Smart: %d, %d xp, level %d]",
				getSeedString(), mHealth, mHealthMax, getStrong(), getQuick(), getSmart(), mExperience, getLevel()); 
	}
	
	public static int calcBattleXP(int plevel, int elevel, int prank, int erank) {
		return (int) ((int) 250. *  Math.pow(2, (double) (elevel - plevel) / 2)) * erank / prank;
	}
	
	public static int calcExperience(int n) {
		return (n*n - n) * 500;
	}
	
	public static int calcLevel(int exp) {
		return ((int) Math.sqrt(5 * exp + 625) + 25) / 50;
	}

	public static int calcMovement(int quick) {
		return quick / 2;
	}
	
	public static int calcHeightPenalty(int dh) {
		return Math.abs(dh);
	}
}
