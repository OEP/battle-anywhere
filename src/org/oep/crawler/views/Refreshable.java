package org.oep.crawler.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class Refreshable extends View {
	
	/**
	 * An overloaded class that repaints this view in a separate thread.
	 * Calling PongView.update() should initiate the thread.
	 * @author OEP
	 *
	 */
	class RefreshHandler extends Handler {
		private boolean mUpdate = true;
		private Refreshable mView;
		
		public RefreshHandler(Refreshable view) {
			mView = view;
		}
		
		@Override
		public void handleMessage(Message msg) {
			mView.update();
			mView.invalidate();
		}
		
		public void sleep(long delay) {
			this.removeMessages(0);
			
			if(mUpdate) {
				this.sendMessageDelayed(obtainMessage(0), delay);
			}
		}
		
		public void setUpdate(boolean b) {
			mUpdate = b;
		}
	}
	
	protected RefreshHandler mRefreshHandler;
	
	public Refreshable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void update() {
		System.out.println("SUPACLASS");
	}
	
	public void setUpdate(boolean b) {
		mRefreshHandler.setUpdate(b);
	}
}
