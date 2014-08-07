package com.akjava.gwt.androidhtml5.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;

public class Poisson {

	public  final native String doIt(String srcImgName,String dstImgName,String maskImgName,Context2d ctx,int iteration,int offx,int offy)/*-{
	$wnd.Poisson.load(srcImgName, dstImgName, maskImgName, function() {
  	var result = $wnd.Poisson.blend(iteration, offx, offy);
  		ctx.putImageData(result, 0, 0);
	});
	}-*/;
	
	public static  final native void setImageDatas(ImageData srcImgData,ImageData dstImgData,ImageData maskImgData,ImageData copyOfDstImgData)/*-{
	$wnd.Poisson.set(srcImgData, dstImgData, maskImgData,copyOfDstImgData);
	}-*/;
	
	public static final native ImageData blend(int iteration,int offx,int offy)/*-{
	return $wnd.Poisson.blend(iteration, offx, offy);
	}-*/;
}
