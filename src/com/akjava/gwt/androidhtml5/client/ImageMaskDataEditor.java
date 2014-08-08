package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.androidhtml5.client.ImageMaskDataEditor.ImageMaskData;
import com.akjava.gwt.androidhtml5.client.TransparentIt.XYPoint;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageMaskDataEditor extends VerticalPanel implements LeafValueEditor<ImageMaskData>{
private Canvas canvas,overlayCanvas;
//private SimpleEditor<ImageData> imageDataEditor;
//private SimpleEditor<ImageElement> imageElementEditor;
private XYPoint lastPoint;
private double currentScale=1;//TODO support scale
private boolean mouseDown;
public ImageMaskDataEditor(){
	this(null,null);
}
/**
 * possible share canvas,but not complete because of some event listener
 * @param canvas
 * @param overlayCanvas
 */
public ImageMaskDataEditor(@Nullable Canvas canvas,@Nullable Canvas overlayCanvas){
	this.canvas=canvas;
	this.overlayCanvas=overlayCanvas;
	
	if(canvas==null){
		this.canvas=Canvas.createIfSupported();
		this.canvas.setStylePrimaryName("transparent_bg");//TODO move outside
	}
	if(overlayCanvas==null){
		this.overlayCanvas=Canvas.createIfSupported();
	}
	
	//labe.getValue();
	
	//imageDataEditor=SimpleEditor.of();
	//imageElementEditor=SimpleEditor.of();
	
	VerticalPanel controler=new VerticalPanel();
	controler.setHeight("60px");
	HorizontalPanel sizes=new HorizontalPanel();
	sizes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	controler.add(sizes);
	//create widget
	Label penSizeLabel=new Label("Pen-Size");
	sizes.add(penSizeLabel);
	
	sizeListBox = new ValueListBox<Integer>(new Renderer<Integer>() {

		@Override
		public String render(Integer object) {
			
			return ""+object;
		}

		@Override
		public void render(Integer object, Appendable appendable) throws IOException {
			
		}
	});
	
	List<Integer> sizeList=Lists.newArrayList(1,2,3,4,5,6,8,12,16,24,32,48,64);
	sizeListBox.setValue(penSize);
	sizeListBox.setAcceptableValues(sizeList);
	sizeListBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

		@Override
		public void onValueChange(ValueChangeEvent<Integer> event) {
			penSize=event.getValue();
		}
		
	});
	sizes.add(sizeListBox);
	
	Button resetBt=new Button("Reset",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			resetCanvas();
			
		}
	});
	sizes.add(resetBt);
	
	HorizontalPanel pens=new HorizontalPanel();
	pens.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	controler.add(pens);
	eraseR = new RadioButton("pens");
	pens.add(eraseR);
	eraseR.setValue(true);
	eraseR.addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			penMode=MODE_ERASE;
		}
	});
	pens.add(new Label("Erase"));
	uneraseR = new RadioButton("pens");
	uneraseR.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			penMode=MODE_UNERASE;
		}
	});
	pens.add(uneraseR);
	pens.add(new Label("unerase"));
	controler.add(pens);
	
	CanvasDragMoveControler dragControler=new CanvasDragMoveControler(this.canvas, new MoveListener() {


		@Override
		public void start(int x, int y) {
			x/=currentScale;
			y/=currentScale;
			
			mouseDown=true;
			lastPoint=mouseToXYPoint(x,y);
		}
		
		@Override
		public void end(int sx, int sy) {//is end is up?
			if(!mouseMoved){
				if(lastPoint!=null){
					
				
				XYPoint dummyPt=new XYPoint(lastPoint.getX()+1, lastPoint.getY()+1);
				
				switch(penMode){
				case MODE_ERASE:
					erase(lastPoint,dummyPt);
					break;
				case MODE_UNERASE:
					unerase(lastPoint,dummyPt);
					break;
				}
				}
			}
			if(mouseDown){
			updateCanvas();
			}
			
			mouseMoved=false;
			mouseDown=false;
			lastPoint=null;
		}
		
		@Override
		public void dragged(int startX, int startY, int mx, int my, int vectorX, int vectorY) {

			mx/=currentScale;
			my/=currentScale;
			
			
		
			if(mouseDown){
			
			mouseMoved=true;
			int x=mx*zoomSize;
			int y=my*zoomSize;
			XYPoint newPoint=new XYPoint(x,y);
			
			
			switch(penMode){
			case MODE_ERASE:
				erase(lastPoint,newPoint);
				break;
			case MODE_UNERASE:
				unerase(lastPoint,newPoint);
				break;
			}
			
			
			lastPoint=newPoint;
			updateCanvas();
		}
		}
	});
	
	this.add(controler);
	this.add(this.canvas);
	
}
public void updateCanvas() {
	
}
public Canvas getCanvas() {
	return canvas;
}
private int zoomSize=1;
private int penSize=16;
public static final int MODE_ERASE=0;
public static final int MODE_UNERASE=4;
private int penMode=MODE_ERASE;
private boolean mouseMoved;
private ValueListBox<Integer> sizeListBox;
private RadioButton eraseR;
private RadioButton uneraseR;


public KeyDownHandler getKeyDownHandler(){
	return new KeyDownHandler() {
		
		@Override
		public void onKeyDown(KeyDownEvent event) {
			int code=event.getNativeKeyCode();
			LogUtils.log(""+code);
			if(code=='1'){
				sizeListBox.setValue(1);
			}
			if(code=='2'){
				sizeListBox.setValue(4);
			}
			if(code=='3'){
				sizeListBox.setValue(8);
			}
			if(code=='4'){
				sizeListBox.setValue(16);
			}
			if(code=='5'){
				sizeListBox.setValue(32);
			}
			if(code=='6'){
				sizeListBox.setValue(64);
			}
			if(code=='7'){
				sizeListBox.setValue(128);
			}
			penSize=sizeListBox.getValue();
			
			if(code==KeyCodes.KEY_TAB){
				if(penMode==MODE_ERASE){
					uneraseR.setValue(true);
					penMode=MODE_UNERASE;
				}else{
					eraseR.setValue(true);
					penMode=MODE_ERASE;
				}
			}
		}
	};
}
private XYPoint mouseToXYPoint(int mx,int my){
	int x=mx*zoomSize;
	int y=my*zoomSize;
	XYPoint newPoint=new XYPoint(x,y);
	return newPoint;
}

private void erase(XYPoint p1,XYPoint p2){
	canvas.getContext2d().save();
	canvas.getContext2d().setLineWidth(penSize);
	canvas.getContext2d().setLineJoin(LineJoin.ROUND);
	canvas.getContext2d().setStrokeStyle("#000");
	canvas.getContext2d().setGlobalCompositeOperation("destination-out");
	
	canvas.getContext2d().beginPath();
	
	canvas.getContext2d().moveTo(p1.getX(),p1.getY());
	canvas.getContext2d().lineTo(p2.getX(),p2.getY());
	
	canvas.getContext2d().closePath();
	canvas.getContext2d().stroke();
	canvas.getContext2d().restore();
}

private void unerase(XYPoint p1,XYPoint p2){
	
	overlayCanvas.getContext2d().clearRect(0, 0, overlayCanvas.getCoordinateSpaceWidth(), overlayCanvas.getCoordinateSpaceHeight());
	
	overlayCanvas.getContext2d().save();
	overlayCanvas.getContext2d().setLineWidth(penSize+2);
	overlayCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
	overlayCanvas.getContext2d().setStrokeStyle("#000");
	overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
	overlayCanvas.getContext2d().beginPath();
	overlayCanvas.getContext2d().moveTo(p1.getX(),p1.getY());
	overlayCanvas.getContext2d().lineTo(p2.getX(),p2.getY());
	overlayCanvas.getContext2d().closePath();
	overlayCanvas.getContext2d().stroke();
	
	//TODO clip
	overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_IN);
	//overlayCanvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
	//overlayCanvas.getContext2d().scale(-1, 1);
	overlayCanvas.getContext2d().drawImage(imageElement, 0, 0);
	
	overlayCanvas.getContext2d().restore();
	
	canvas.getContext2d().save();
	canvas.getContext2d().drawImage(overlayCanvas.getCanvasElement(), 0, 0);
	canvas.getContext2d().restore();
}

public static class ImageMaskData{
	private ImageData imageData;
	private ImageElement imageElement;//must be loaded


	public ImageElement getImageElement() {
		return imageElement;
	}

	public void setImageElement(ImageElement imageElement) {
		this.imageElement = imageElement;
	}

	public ImageData getImageData() {
		return imageData;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}
}

private ImageElement imageElement;
private ImageMaskData value;

private void resetCanvas(){
	canvas.getContext2d().drawImage(value.getImageElement(), 0, 0);
	updateCanvas();
}
@Override
public void setValue(ImageMaskData value) {
	this.value=value;
	int w=value.getImageElement().getWidth();
	int h=value.getImageElement().getHeight();
	
	CanvasUtils.createCanvas(canvas, w, h);
	CanvasUtils.createCanvas(overlayCanvas, w, h);
	imageElement=value.getImageElement();
	
	if(value.getImageData()==null){
		resetCanvas();
		//ImageData imageData=canvas.getContext2d().getImageData(0,0,w, h);
		//imageDataEditor.setValue(imageData);//must be exist.
	}else{
		//imageDataEditor.setValue(value.getImageData());
		canvas.getContext2d().putImageData(value.getImageData(), 0, 0);
	}
	
	//imageElementEditor.setValue(value.getImageElement());
}
@Override
public ImageMaskData getValue() {
	//flush here?
	//
	value.setImageData(canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight()));
	
	
	return value;
}

}
