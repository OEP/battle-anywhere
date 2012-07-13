package org.oep.crawler;

import org.oep.battle.R;
import org.oep.crawler.game.Attack;
import org.oep.crawler.game.Creature;
import org.oep.crawler.game.GameState;
import org.oep.crawler.views.Battlefield;
import org.oep.crawler.views.Battlefield.State;
import org.oep.net.MACHandler;
import org.oep.net.MACReceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BattleActivity extends Activity {
	
	private static final String TAG = "BattleActivity";
	private Creature mHero;
	private Creature mEnemy;
	
	private MACHandler mHandler;
	
	
	protected State mState;
	protected Action mPlayerAction, mOpponentAction;
	protected Attack mPlayerAttack, mOpponentAttack;
	
	
	enum State { SelectAction, SelectAttack, SelectDefense, SelectItem, Animate };
	enum Action { Attack, Defend, Item };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setupGame();
    }
    
    private void setupGame() {
    	// We were expecting some information to come through the intent

    	GameState gs = GameState.getInstance();
    	mHero = gs.getHero();
    	mEnemy = gs.getEnemy();
    	
    	if(mHero == null) {
    		Log.d(TAG, "Hero is null!");
    	}
    	
    	if(mEnemy == null) {
    		Log.d(TAG, "Enemy is null!");
    	}
    	
    	initUI();
    	Battlefield bf = (Battlefield) findViewById(R.id.battle_battlefield);
    	bf.setPlayer(mHero);
    	bf.setOpponent(mEnemy);
  
    	updateUI();
	}

	@Override
    public void onStart() {
    	super.onStart();
    	
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    }

    private void initUI() {
    	setContentView(R.layout.battle_scene);
    }
    
    private void updateUI() {
    	
    }
    
    private void setupListeners() {
    	findViewById(R.id.button_attack).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
    	});
    	
    	findViewById(R.id.button_attack).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
    	});
    	
    	findViewById(R.id.button_attack).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
    	});
    }
    

    public static final String
	PREFIX_ENEMY = "ENEMY_",
	PREFIX_HERO = "HERO_";

}