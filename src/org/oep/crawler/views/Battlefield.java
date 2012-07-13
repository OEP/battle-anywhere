package org.oep.crawler.views;

import org.oep.battle.R;
import org.oep.crawler.game.Creature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class Battlefield extends View {
	
	protected Creature mPlayer;
	protected Creature mOpponent;
	
	private boolean mAnimating = true;

	public Battlefield(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void draw(Canvas c) {
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Rect screen = new Rect(0, 0, getWidth(), getHeight());
		c.drawRect(screen, paint);
		drawBackground(c);
		drawCreatures(c);
	}
	
	private void drawBackground(Canvas c) {
		Drawable bg = getContext().getResources().getDrawable(R.drawable.bg_cyberspace);
//		int dh = getHeight() - bg.getIntrinsicHeight();
//		int dw = getWidth() - bg.getIntrinsicWidth();
		
		bg.setBounds(0, 0, getWidth(), getHeight());
		bg.draw(c);
	}
	
	private void drawCreatures(Canvas c) {
		Bitmap opponent, player;
		int w = getWidth();
		int h = getHeight();
		
		Paint paint = new Paint();
		
		if(mOpponent != null) {
			opponent = mOpponent.getBitmap();
			c.drawBitmap(opponent, w - opponent.getWidth(), 0, paint);
		}
		else {
			c.drawText("Creature 'enemy' not found", 0, 0, paint);
		}
		
		if(mPlayer != null) {
			player = mPlayer.getBitmap();
			c.drawBitmap(player, 0, h - player.getHeight(), paint);
		}
		else {
			c.drawText("Creature 'player' not found", 0, h - 30, paint);
		}
		
	}
	
	public void setPlayer(Creature c) {
		mPlayer = c;
	}
	
	public void setOpponent(Creature c) {
		mOpponent = c;
	}
}
