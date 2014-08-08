package com.akjava.gwt.androidhtml5.client;

import com.akjava.lib.common.graphics.Rect;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;

public class CanvasTools {

	private CanvasTools(Canvas canvas){
		this.canvas=canvas;
	}
	private Canvas canvas;
	
	public static CanvasTools from(Canvas canvas){
		return new CanvasTools(canvas);
	}
	
	public Rect getTransparentArea(int transparentValue){
		return getTransparentArea(transparentValue,0,0);
	}
	public Rect getTransparentArea(int transparentValue,int expandW,int expandH){
		int minX=canvas.getCoordinateSpaceWidth();
		int minY=canvas.getCoordinateSpaceHeight();
		int maxX=0;
		int maxY=0;
		
		ImageData data=canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		
		for(int x=1;x<data.getWidth();x++){
			for(int y=1;y<data.getHeight();y++){
				int alpha=data.getAlphaAt(x, y);
				if(alpha<=transparentValue){
					if(minX>x){
						minX=x;
					}
					if(minY>y){
						minY=y;
					}
					if(maxX<x){
						maxX=x;
					}
					if(maxY<y){
						maxY=y;
					}
				}
			}
		}
		
		if(minX<=maxX && minY<=maxY){
			return new Rect(minX,minY,maxX-minX,maxY-minY);
		}else{
			return new Rect(0,0,0,0);//invalid case.
		}
	}
	public ImageData getImageData(Rect rect){
		return  canvas.getContext2d().getImageData(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	public void putImageData(Rect rect,ImageData data){
		//TODO should i check rect.w & data.w ?
		  canvas.getContext2d().putImageData(data, rect.getX(), rect.getY());
	}
}
