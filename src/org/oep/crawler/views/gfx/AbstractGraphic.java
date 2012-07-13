package org.oep.crawler.views.gfx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class AbstractGraphic extends View {
	protected boolean mAnimating = false;
	
	protected int mCurrentFrame = 0;
	protected int mFrames = 1;
	protected boolean mReversed = false;
	protected boolean mLoop = true;
	
	public AbstractGraphic(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void draw(Canvas c) {
		
	}
	
	public void nextFrame() {
		if(mReversed && mLoop) {
			mCurrentFrame--;
			int abs = Math.abs(mCurrentFrame);
			abs %= mFrames;
			mCurrentFrame = mFrames - abs;
			return;
		}
		else if(mReversed && !mLoop) {
			mCurrentFrame = Math.max(mCurrentFrame - 1, 0);
			return;
		}
		else if(!mReversed && mLoop) {
			mCurrentFrame = (mCurrentFrame + 1) % mFrames;
			return;
		}
		else if(!mReversed && !mLoop) {
			mCurrentFrame = Math.min(mFrames - 1, mCurrentFrame + 1);
			return;
		}
	}
	
	public void setReversed(boolean b) {
		mReversed = b;
	}
	
	public void setCurrentFrame(int frame) {
		mCurrentFrame = Math.abs(frame) % mFrames;
	}
	
	public void toStartFrame() {
		mCurrentFrame = 0;
	}
	
	public void toEndFrame() {
		mCurrentFrame = mFrames - 1;
	}
	
	public void setFrames(int frames) {
		mFrames = Math.max(1, Math.abs(frames));
	}
	
	public boolean isAnimating()  {
		return mAnimating;
	}
}
