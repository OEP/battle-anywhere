package org.oep.crawler.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.oep.battle.R;
import org.oep.crawler.xml.AttackHandler;
import org.oep.net.MACHandler;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

public class GameState {
	private static final String TAG = "GameState";

	private HashMap<String, Vector<Attack>> mAttackDatabase;
	
	private Creature mHero, mEnemy;
	
	private Context mContext;
	
	private MACHandler mHandler;
	
	private GameState() {	}
	
	public void setHandler(MACHandler handler) {
		if(handler == null) return;
		mHandler = handler;
		loadHero();
	}
	
	public void setContext(Context ctx) {
		mContext = ctx;
		loadAttacks();
	}
	
	public void setEnemy(Creature creep) {
		mEnemy = creep;
	}
	
	public Creature getEnemy() {
		return mEnemy;
	}
	
	public Creature getHero() {
		if(mHero == null) loadHero();
		return mHero;
	}
	
	public MACHandler getHandler() {
		return mHandler;
	}
	
	public boolean hasHandler() {
		return mHandler != null;
	}
	
	public boolean hasContext() {
		return mContext != null;
	}
	
	private void loadAttacks() {
		if(mContext == null) {
			Log.w(TAG, "Could not load attacks: context not set.");
			return;
		}
		
		AttackHandler handler = new AttackHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;

		try {
			parser = factory.newSAXParser();
		} catch (Exception e) {
			return;
		}
		
		Resources r = mContext.getResources();
		try {
			parser.parse(r.openRawResource(R.xml.attacks), handler);
		} catch (NotFoundException e) {
			Log.w(TAG, "Could not load attacks: XML resource not found");
			return;
		} catch (SAXException e) {
			Log.w(TAG, "Could not load attacks: " + e.getMessage());
			return;
		} catch (IOException e) {
			Log.w(TAG, "Could not load attacks: I/O error");
			return;
		}
		
		mAttackDatabase = handler.getAttacks();
	}
	
	private void loadHero() {
		mHero = new Creature(mHandler.getMAC(), Creature.PPL_HERO);
	}
	
	public static GameState getInstance() {
		return sInstance;
	}
	
	private static GameState sInstance
		= new GameState();
}
