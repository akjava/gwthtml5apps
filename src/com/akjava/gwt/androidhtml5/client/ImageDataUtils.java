package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;

public class ImageDataUtils {



public static ImageData create(Canvas canvas,ImageElement element){
	if(canvas==null){
		canvas=Canvas.createIfSupported();
	}
	ImageElementUtils.copytoCanvas(element, canvas, true);
	return CanvasUtils.getImageData(canvas, true);
	}


public static ImageData copy(Canvas canvas,ImageData data){
	return copy(canvas.getContext2d(),data);
	}

public static ImageData copy(Context2d ctx,ImageData data){
	ImageData newData=ctx.createImageData(data);
	for(int i=0;i<data.getData().getLength();i++){
		newData.getData().set(i, data.getData().get(i));//maybe slow copy
	}
	return newData;
	}


}
