package org.oep.crawler.tile;

import org.oep.crawler.game.Creature;

public class Camera {
	public int x, y;
	public int sx, sy;
	private int mTop = 1, mBottom = 1;
	
	public Camera() {
		this(0,0);
	}
	
	public Camera(int x, int y) {
		this(x,y,x,y);
	}
	
	public Camera(int x, int y, int sx, int sy) {
		this(x,y,sx,sy,1,1);
	}
	
	public Camera(int x, int y, int sx, int sy, int top, int bottom) {
		this.x = x;
		this.y = y;
		this.sx = sx;
		this.sy = sy;
		setFraction(top,bottom);
	}
	
	public void setFraction(int top, int bottom) {
		mBottom = Math.max(1, Math.abs(bottom));
		mTop = Math.max(1, Math.min(mBottom, Math.abs(top)));
	}
	
	public void interpolate() {
		int dx = sx - x;
		int dy = sy - y;
		
		x += dx * mTop / mBottom;
		y += dy * mTop / mBottom;
	}
	
	public static void lookAt(Camera cam, Creature creep) {
		cam.sx = creep.getX();
		cam.sy = creep.getY();
	}
	
	public static void lookAt(Camera cam, int x, int y) {
		cam.sx = x;
		cam.sy = y;
	}
}
