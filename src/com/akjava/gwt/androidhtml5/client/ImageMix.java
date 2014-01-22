package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.GWTUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ImageMix implements EntryPoint {

	private Canvas mixedCanvas;
	private HorizontalPanel linkFolder;
	
	//must use canvas,if use img,can't tall image correctly in preview
	private Canvas image1Canvas;
	private Canvas image2Canvas;

	private String version="1.0";
	//private Image img2;
	@Override
	public void onModuleLoad() {
		LogUtils.log("imageMix:version "+version);
		DockLayoutPanel root=new DockLayoutPanel(Unit.PX);
		
		RootLayoutPanel.get().add(root);
		
		/**
		 * use toppanel because on small device,without topbar touch url wrongly
		 */
		HorizontalPanel topControler=new HorizontalPanel();
		topControler.setWidth("100%");
		topControler.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topControler.setSpacing(4);
		
		//HorizontalPanel bottomControler=new HorizontalPanel();
		
		topControler.setStylePrimaryName("bg1");
		
		Label label=new Label("ImageMix "+version);
		label.setStylePrimaryName("title");
		topControler.add(label);
		root.addNorth(topControler, 40);
		
		mixedPanel = new TopBarPanel();
		
		TopBarPanel image1Panel=new TopBarPanel();
		
		image1Uploader = FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				new ImageElementLoader().load(value, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						CanvasUtils.clear(image1Canvas);
						CanvasUtils.drawFitCenter(image1Canvas, element);
						//image1Canvas.getContext2d().drawImage(element, 0, 0);//TODO resize
						//img1.setUrl(element.getSrc());
						//img1.setVisible(true);
						updateMixedImage();
					}
					


					@Override
					public void onError(String url, ErrorEvent event) {
						Window.alert("load faild:"+url);
					}
				});
			}
		}, false);
		image1Panel.getTopPanel().add(new Label("Background"));
		image1Panel.getTopPanel().add(image1Uploader);
		//image1Uploader.setVisible(false);
		
		TopBarPanel image2Panel=new TopBarPanel();
		FileUploadForm image2Uploader=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				
				new ImageElementLoader().load(value, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						CanvasUtils.clear(image2Canvas);
						//img2.setUrl(element.getSrc());
						//img2.setVisible(true);
						//LogUtils.log("uploaded:");
						//image2Canvas.getContext2d().drawImage(element, 0, 0);//TODO resize
						CanvasUtils.drawFitImage(image2Canvas, element,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_MIDDLE);
						updateMixedImage();
						//image1Uploader.setVisible(true);
					}
					


					@Override
					public void onError(String url, ErrorEvent event) {
						Window.alert("load faild:"+url);
					}
				});
			}
		}, false);
		image2Panel.getTopPanel().add(new Label("Layer"));
		image2Panel.getTopPanel().add(image2Uploader);
		
		
		/*
		Button downloadBt=new Button("Download",new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				linkFolder.clear();
				if(GWTUtils.isIOS()){
				//iOS not support download	
					linkFolder.add(new Label("long press to below to download"));
				}else{
				Anchor anchor=CanvasUtils.generateDownloadImageLink(mixedCanvas, true, "mixed.png", "Click Here to Download", true);
				linkFolder.add(anchor);
				}
				
			}
		});
		*/
		//topControler.add(downloadBt);
		linkFolder = new HorizontalPanel();
		linkFolder.setSpacing(2);
		
		transparentBox = new ListBox();
		transparentBox.addItem("100");
		transparentBox.addItem("90");
		transparentBox.addItem("80");
		transparentBox.addItem("70");
		transparentBox.addItem("60");
		transparentBox.addItem("50");
		transparentBox.addItem("40");
		transparentBox.addItem("30");
		transparentBox.addItem("20");
		transparentBox.addItem("10");
		transparentBox.addItem("0");
		transparentBox.setSelectedIndex(5);
		transparentBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateMixedImage();
			}
		});
		mixedPanel.getTopPanel().add(new Label("Layer-Transparent"));
		mixedPanel.getTopPanel().add(transparentBox);
		mixedPanel.getTopPanel().add(linkFolder);
		
		
		
		//topControler.add(new Label("hello"));
	
		
		ScrollPanel scroll=new ScrollPanel();
		
		scroll.setSize("100%", "100%");
		root.add(scroll);
		FlowPanel flow=new FlowPanel();
		
		scroll.setWidget(flow);
		
		/*
		img1 = new Image();
		img1.setWidth("480px");
		img1.setVisible(false);
		image1Panel.add(img1);
		
		img2 = new Image();
		img2.setWidth("480px");
		img2.setVisible(false);
		image2Panel.add(img2);
		*/
		image1Canvas = CanvasUtils.createCanvas(480, 480);
		image1Panel.add(image1Canvas);
		image2Canvas = CanvasUtils.createCanvas(480, 480);
		image2Panel.add(image2Canvas);
		
		mixedCanvas = CanvasUtils.createCanvas(480, 480);

		
		mixedImage = new Image();
		mixedPanel.add(mixedImage);
		mixedPanel.setSize("480px", "520px");
		GWTHTMLUtils.addFloatLeftStyle(mixedPanel);
		flow.add(mixedPanel);
		mixedPanel.setVisible(false);
		
		GWTHTMLUtils.addFloatLeftStyle(image1Panel);
		GWTHTMLUtils.addFloatLeftStyle(image2Panel);
		
		image1Panel.setSize("480px", "520px");
		
		image2Panel.setSize("480px", "520px");
		flow.add(image1Panel);
		flow.add(image2Panel);
		
		
		
		
		
		mixedImage.setSize("480px", "480px");
		mixedImage.setVisible(false);
		
	}
	
	private void createDownloadLink(){
		linkFolder.clear();
		if(GWTUtils.isIOS()){
		//iOS not support download	
			linkFolder.add(new Label("long press to below to download"));
		}else{
		Anchor anchor=CanvasUtils.generateDownloadImageLink(mixedCanvas, true, "mixed.png", "Push here to download image", true);
		anchor.setStylePrimaryName("bt");
		linkFolder.add(anchor);
		}
	}
	
	private int canvasWidth=480;
	private int canvasHeight=480;
	private Image mixedImage;
	//private Image img1;
	private TopBarPanel mixedPanel;
	private FileUploadForm image1Uploader;
	private ListBox transparentBox;
	
	private void updateMixedImage() {
		int transparent=ValuesUtils.toInt(transparentBox.getItemText(transparentBox.getSelectedIndex()), 50);
		double tp=1-(0.01*transparent);
		mixedCanvas.getContext2d().clearRect(0, 0, canvasWidth, canvasHeight);
		/*
		if(img1.isVisible()){
		CanvasUtils.drawFitCenter(mixedCanvas, ImageElement.as(img1.getElement()));
		}
		*/
		mixedCanvas.getContext2d().drawImage(image1Canvas.getCanvasElement(), 0, 0);
		mixedCanvas.getContext2d().setGlobalAlpha(tp);
		mixedCanvas.getContext2d().drawImage(image2Canvas.getCanvasElement(), 0, 0);
		/*
		if(img2.isVisible()){
		CanvasUtils.drawFitCenter(mixedCanvas, ImageElement.as(img2.getElement()));
		}*/
		mixedCanvas.getContext2d().setGlobalAlpha(1);
		
		mixedImage.setUrl(mixedCanvas.toDataUrl());
		mixedImage.setVisible(true);
		mixedPanel.setVisible(true);
		
		createDownloadLink();
	}

}
