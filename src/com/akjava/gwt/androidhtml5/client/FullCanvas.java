package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;

public class FullCanvas implements EntryPoint {

	private Canvas canvas;

	@Override
	public void onModuleLoad() {
		canvas = Canvas.createIfSupported();
		canvas.setSize("100%", "100%");
		
		
		
		DockLayoutPanel dock=new DockLayoutPanel(Unit.PX);
		
		HorizontalPanel top=new HorizontalPanel();
		dock.addNorth(top,40);
		
	
		
		
		final ValueListBox<Integer> sizesList=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer value) {
				return ""+value;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		canvas.setCoordinateSpaceHeight(100);
		canvas.setCoordinateSpaceWidth(100);
		sizesList.setValue(100);
		sizesList.setAcceptableValues(Lists.newArrayList(100,200,400,800,1600,3200));
		top.add(sizesList);
		sizesList.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				try{
				canvas.setCoordinateSpaceHeight(event.getValue());
				canvas.setCoordinateSpaceWidth(event.getValue());
				redraw();
				}catch(Exception e){
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		});
		
		dock.add(canvas);
		//canvas.removeFromParent();
		//dock.add(canvas);
		RootLayoutPanel.get().add(dock);
		
		final HorizontalPanel dlPanel=new HorizontalPanel();
		
		Button download=new Button("download",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				try{
					dlPanel.clear();
				Anchor a=CanvasUtils.generateDownloadImageLink(canvas, true, "fullcanvas.png", "Download", true);
				dlPanel.add(a);
				}catch (Exception e) {
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		});
		top.add(download);
		
		top.add(dlPanel);
		
		Button dummy=new Button("dummy",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				try{
					Canvas c=CanvasUtils.createCanvas(sizesList.getValue(), sizesList.getValue());
					c.getContext2d().setFillStyle("#fff");
					c.getContext2d().fillRect(0, 0, c.getCoordinateSpaceWidth(), c.getCoordinateSpaceHeight());
				}catch (Exception e) {
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		});
		top.add(dummy);
		
		FileUploadForm fileUp=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String asStringText) {
				try{
				ImageElement element=Document.get().createImageElement();
				element.setSrc(asStringText);
				Canvas c=CanvasUtils.createCanvas(sizesList.getValue(), sizesList.getValue());
				CanvasUtils.drawFitCenter(c, element);
				canvas.getContext2d().drawImage(c.getCanvasElement(), 0, 0);
				}catch (Exception e) {
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		}, false);
		top.add(fileUp);
		
		redraw();
	}

	private void redraw(){
		canvas.getContext2d().setFillStyle("#080");
		canvas.getContext2d().fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		canvas.getContext2d().setFillStyle("#000");
		canvas.getContext2d().fillRect(0, 0, 50,50);
	}
	
}
