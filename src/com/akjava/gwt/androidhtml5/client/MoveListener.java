package com.akjava.gwt.androidhtml5.client;
public  interface MoveListener{
	public void moved(int startX,int startY,int endX,int endY,int vectorX,int vectorY);
	public void start(int sx,int sy);
	public void end(int sx,int sy);
}