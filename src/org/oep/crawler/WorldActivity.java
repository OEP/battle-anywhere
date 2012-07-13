package org.oep.crawler;

import java.util.ArrayList;
import java.util.List;

import org.oep.battle.R;
import org.oep.crawler.game.Creature;
import org.oep.crawler.game.GameState;
import org.oep.crawler.views.CreatureListAdapter;
import org.oep.net.MACHandler;
import org.oep.net.MACReceiver;
import org.oep.net.MACResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class WorldActivity extends Activity implements MACReceiver {
	
	@Override
	public void onCreate(Bundle icicle) { 
		super.onCreate(icicle);
		setup();
	}
	
	private void setup() {
		GameState gs = GameState.getInstance();
		this.setContentView(R.layout.world_browser);
		
		gs.setHandler( MACHandler.getWirelessHandler(this, this) );
		gs.setContext(this);
		
		// Add a dummy
		ListView list = (ListView) findViewById(R.id.world_enemy_list);
		ArrayList<Creature> creeps = new ArrayList<Creature>();
		Creature creep = new Creature("FFFFFFFF0000", Creature.PPL_CREEP);
		creep.setImageResource(R.drawable.creature_router, this);
		creeps.add(creep);
		list.setAdapter(new CreatureListAdapter(this, creeps));
		
		setupListeners();
	}
	
	
	private void setupListeners() {
		GameState gs = GameState.getInstance();
		final MACHandler handler = gs.getHandler();
		((Button)findViewById(R.id.world_scan)).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(handler == null) {
							System.out.println("ERROR: No handler");
							return;
						}
						
						if(handler.startScan() == false) {
							AlertDialog.Builder builder = new AlertDialog.Builder(WorldActivity.this);
							builder.setTitle("Fail")
							.setMessage("You fail at life.")
							.show();
						}
					}
				}
			);
		
		/**
		 * Code for when an item has been clicked. Launch the battle
		 * activity passing in the necessary arguments.
		 */
		final ListView list = (ListView) findViewById(R.id.world_enemy_list);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> v, View view, int position, long id) {
				GameState gs = GameState.getInstance();
				Creature enemy = (Creature) list.getItemAtPosition(position);
				gs.getHero().setImageResource(R.drawable.viking, WorldActivity.this);
				gs.setEnemy(enemy);
				
				Intent i = new Intent(WorldActivity.this, BattleActivity.class);
				WorldActivity.this.startActivity(i);
			}
		});
		
	}

	@Override
	public void onReceive(List<MACResult> results) {
				ListView v = (ListView) findViewById(R.id.world_enemy_list);
				ArrayList<Creature> creatures = new ArrayList<Creature>();
				
				for(MACResult result : results) {
					Creature c = new Creature(result.MAC, Creature.PPL_CREEP);
					c.setImageResource(result.ResId, WorldActivity.this);
					creatures.add(c);
				}
				
				v.setAdapter(new CreatureListAdapter(this, creatures));
	}

}
