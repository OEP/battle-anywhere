package org.oep.crawler.views.gfx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class TouchBubble extends AbstractGraphic {
	
	private boolean mDying = false;
	
	private int mBackgroundColor = Color.WHITE;

	private int mOutlineColor = Color.BLACK;
	
	private int mBorderSize = 5;
	
	public TouchBubble(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void draw(Canvas c) {
		int w = resizeRadius(getWidth());
		int h = resizeRadius(getHeight());
		int r = Math.min(w, h) / 2;
		
		Paint p = new Paint();
		
		if(mBorderSize > 0) {
			p.setColor(mOutlineColor);
			c.drawCircle(w / 2, h / 2, r, p);
		}
		
		p.setColor(mBackgroundColor);
		c.drawCircle(w/2, h/2, r, p);
		
		p.setColor(Color.WHITE);
		c.drawText(new String("HELO"), 0, 0, p);
		
		c.drawRect(0,0,getWidth(),getHeight(), p);
	}
	
	public void setDying(boolean b) {
		mDying = b;
	}
	
	public void setBorderSize(int size) {
		mBorderSize = Math.max(0,size);
	}
	
	private int resizeRadius(int r) {
		double x = 1.57735 * mCurrentFrame / mFrames;
		return (int) ( r * (-1.5 * (x-1) * (x-1) + 1.5));
	}
}
