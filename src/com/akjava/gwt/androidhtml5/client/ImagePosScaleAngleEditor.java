package com.akjava.gwt.androidhtml5.client;

import javax.annotation.Nullable;

import com.akjava.gwt.androidhtml5.client.ImagePosScaleAngleEditor.PositionScaleAngleData;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.CursorUtils;
import com.akjava.gwt.lib.client.experimental.ImageScaleRangeConverter;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImagePosScaleAngleEditor extends VerticalPanel implements LeafValueEditor<PositionScaleAngleData>{


	
	public static class PositionScaleAngleData{
		private double scale=1;
		private double positionX;
		private double positionY;
		private double angle;
		public PositionScaleAngleData(){}
		public PositionScaleAngleData(double x,double y,double s,double a){
			this.positionX=x;
			this.positionY=y;
			this.angle=a;
			this.scale=s;
		}
		public double getScale() {
			return scale;
		}
		public void setScale(double scale) {
			this.scale = scale;
		}
		public double getPositionX() {
			return positionX;
		}
		public void setPositionX(double positionX) {
			this.positionX = positionX;
		}
		public double getPositionY() {
			return positionY;
		}
		public void setPositionY(double positionY) {
			this.positionY = positionY;
		}
		public double getAngle() {
			return angle;
		}
		public void setAngle(double angle) {
			this.angle = angle;
		}
	}
	private Canvas canvas;
	private InputRangeWidget scaleRange;
	private InputRangeWidget angleRange;
	private int offsetX;
	private int offsetY;
	public ImagePosScaleAngleEditor(){
		this(null);
	}

	
	public ImagePosScaleAngleEditor(@Nullable Canvas canvas){
		if(canvas==null){
			canvas=Canvas.createIfSupported();
		}
		this.canvas=canvas;
	
		VerticalPanel controler=new VerticalPanel();
		controler.setHeight("60px");
		add(controler);

		HorizontalPanel h1=new HorizontalPanel();
		controler.add(h1);
		final Label scaleLabel=new Label("Scale"+":1.0");
		h1.add(scaleLabel);//TODO ondemand scale
		scaleLabel.setWidth("60px");
		
		scaleRange = HTML5InputRange.createInputRange(-99, 90, 0);
		scaleRange.setWidth("250px");
		h1.add(scaleRange);
		scaleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				updateScaleValue(newValue);
			}
		});
		scaleRange.addInputRangeListener(new InputRangeListener() {
			public String toLabel(double value){
				String v=""+value;
				return v.substring(0,Math.min(v.length(), 4));
			}
			@Override
			public void changed(int newValue) {
				scaleLabel.setText("Scale"+":"+toLabel(getScale()));
				
			}
		});
		
	Button sminus=new Button("-",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int value=scaleRange.getValue()-1;
				
				scaleRange.setValue(value);
			}
		});
		h1.add(sminus);
	Button splus=new Button("+",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int value=scaleRange.getValue()+1;
				
				scaleRange.setValue(value);
			}
		});
		h1.add(splus);
		
		Button scale1=new Button("1.0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				scaleRange.setValue(0);
			}
		});
		h1.add(scale1);
		
		
		HorizontalPanel h2=new HorizontalPanel();
		controler.add(h2);
		final Label turnLabel=new Label("Angle"+":0");
		h2.add(turnLabel);//TODO ondemand scale
		turnLabel.setWidth("60px");
		
		angleRange = HTML5InputRange.createInputRange(-180, 180, 0);
		angleRange.setWidth("250px");
		h2.add(angleRange);
		angleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				int angle=angleRange.getValue();
				updateAngleValue(angle);
			}
		});
		angleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				
				turnLabel.setText("Angle"+":"+(angleRange.getValue()));
				
				
			}
		});
	
		Button minus=new Button("-",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int value=angleRange.getValue()-1;
				if(value<-180){
					value=value+360;
				}
				angleRange.setValue(value);
			}
		});
		h2.add(minus);
	Button plus=new Button("+",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int value=angleRange.getValue()+1;
				if(value>180){
					value=value-360;
				}
				angleRange.setValue(value);
			}
		});
		h2.add(plus);
		Button leftTurn=new Button("-90",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(-90);
			}
		});
		h2.add(leftTurn);
		Button zeroTurn=new Button("0",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(0);
			}
		});
		h2.add(zeroTurn);
		Button rightTurn=new Button("90",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(90);
			}
		});
		h2.add(rightTurn);
		
		
		CanvasDragMoveControler moveControler=new CanvasDragMoveControler(canvas,new MoveListener() {
			
			@Override
			public void start(int sx, int sy) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void end(int sx, int sy) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
				offsetX+=vectorX;
				offsetY+=vectorY;
				
				//offsetX+=(vectorX*(1.0/scale));
				//offsetY+=(vectorY*(1.0/scale));
				updateImage();
				updatePosition(offsetX,offsetY);
			}
		});
		this.add(canvas);
		

		updateCursor();
	}
	
	private void updateCursor(){
		CursorUtils.setCursor(canvas, Cursor.MOVE);
	}
	
	@Ignore
	public Canvas getCanvas() {
		return canvas;
	}
	public void updatePosition(int offsetX2, int offsetY2) {
		
	}


	private double getScale(){
		int v=scaleRange.getValue();
		
		if(v==0){
			return 1;
		}
		if(v>0){
			return 1.0+v*0.1;
		}else if(v<0){
			
			return 1.0+v*0.01;
		}
		return 1;
	}
	
	private int scaleToRangeValue(double scale){
		return new ImageScaleRangeConverter().reverse().convert(scale);
		
	}
	
	
	public void updateScaleValue(int value){
		
	}
	public void updateAngleValue(int value){
		
	}
	
	
	public void updateImage(){
		if(getValue()==null){
			return;//not yet ready
		}
		
		CanvasUtils.clear(canvas);
		if(imageElement!=null){
			CanvasUtils.drawCenter(canvas, imageElement,offsetX,offsetY,getScale(),getScale(),getAngle(),1);
		}//possible null on initialize
		
		doOverLayer(canvas);
	}
	
	public void doOverLayer(Canvas canvas) {
		
	}
	public void setCanvasSize(int w,int h){
		CanvasUtils.createCanvas(canvas,w, h);
	}
	

	private PositionScaleAngleData value;
	@Override
	public void setValue(PositionScaleAngleData value) {
		this.value=value;
		offsetX=(int)value.getPositionX();
		offsetY=(int)value.getPositionY();
		angleRange.setValue((int) value.getAngle());
		scaleRange.setValue((int) scaleToRangeValue(value.getScale()));
		
		updateImage();
	}
	
	private ImageElement imageElement;
	

	public ImageElement getImageElement() {
		return imageElement;
	}

	public void setImageElement(ImageElement imageElement) {
		this.imageElement = imageElement;
	}

	@Override
	public PositionScaleAngleData getValue() {
		if(value==null){
			return null;
		}
		value.setPositionX(offsetX);
		value.setPositionY(offsetY);
		value.setScale(getScale());
		value.setAngle(getAngle());
		return value;
	}

	private double getAngle() {
		return angleRange.getValue();
	}
}
