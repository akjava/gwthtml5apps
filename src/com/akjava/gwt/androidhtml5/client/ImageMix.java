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
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ImageMix extends Html5DemoEntryPoint {

	private Canvas mixedCanvas;
	private HorizontalPanel linkFolder;
	
	//must use canvas,if use img,can't tall image correctly in preview
	//private Canvas image1Canvas;
	//private Canvas image2Canvas;

	
	private Image img1;
	private Image img2;
	private CheckBox expand2Check;
	@Override
	public Panel initializeWidget() {
		DropDockRootPanel root=new DropDockRootPanel(Unit.PX,true){
			@Override
			public void callback(File file, String parent) {
				//do nothing
			}
		};
		
		
		
		
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(4);
		
		//HorizontalPanel bottomControler=new HorizontalPanel();
		
		topPanel.setStylePrimaryName("bg1");
		
		
		topPanel.add(createTitleWidget());
		
		root.addNorth(topPanel, 30);
		
		Anchor help=new Anchor("Help","imagemix_help.html");
		topPanel.add(help);
		
		mixedPanel = new TopBarPanel();
		
		TopBarPanel image1Panel=new TopBarPanel();
		
		image1Uploader = FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				new ImageElementLoader().load(value, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						CanvasUtils.clear(mixedCanvas);
						if(expand1Check.getValue()){
							CanvasUtils.drawExpandCenter(mixedCanvas, element);	
						}else{
						CanvasUtils.drawFitCenter(mixedCanvas, element);
						}
						//image1Canvas.getContext2d().drawImage(element, 0, 0);//TODO resize
						img1.setUrl(mixedCanvas.toDataUrl());
						img1.setVisible(true);
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
		
		expand1Check = new CheckBox("Expand");
		image1Panel.getTopPanel().add(expand1Check);
		
		image1Panel.getTopPanel().add(image1Uploader);
		
		
		
		TopBarPanel image2Panel=new TopBarPanel();
		FileUploadForm image2Uploader=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				
				new ImageElementLoader().load(value, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						CanvasUtils.clear(mixedCanvas);
						
						//LogUtils.log("uploaded:");
						//image2Canvas.getContext2d().drawImage(element, 0, 0);//TODO resize
						if(expand2Check.getValue()){
							CanvasUtils.drawExpandCenter(mixedCanvas, element);	
						}else{
						CanvasUtils.drawFitCenter(mixedCanvas, element);
						}
						img2.setUrl(mixedCanvas.toDataUrl());
						img2.setVisible(true);
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
		expand2Check = new CheckBox("Expand");
		image2Panel.getTopPanel().add(expand2Check);
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
		
		
		img1 = new Image();
		img1.setStylePrimaryName("transparent_bg");//or bg
		img1.setSize(canvasWidth+"px",canvasHeight+"px");
		img1.setVisible(false);
		image1Panel.add(img1);
		
		img2 = new Image();
		img2.setSize(canvasWidth+"px",canvasHeight+"px");
		img2.setVisible(false);
		image2Panel.add(img2);
		img2.setStylePrimaryName("transparent_bg");//or bg
		/*
		image1Canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		image1Panel.add(image1Canvas);
		image2Canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		image2Panel.add(image2Canvas);
		*/
		
		mixedCanvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);

		
		mixedImage = new Image();
		mixedImage.setStylePrimaryName("transparent_bg");//or bg
		mixedPanel.add(mixedImage);
		mixedPanel.setSize(canvasWidth+"px", (canvasHeight+40)+"px");
		GWTHTMLUtils.addFloatLeftStyle(mixedPanel);
		flow.add(mixedPanel);
		mixedPanel.setVisible(false);
		
		GWTHTMLUtils.addFloatLeftStyle(image1Panel);
		GWTHTMLUtils.addFloatLeftStyle(image2Panel);
		
		image1Panel.setSize(canvasWidth+"px", (canvasHeight+40)+"px");
		
		image2Panel.setSize(canvasWidth+"px", (canvasHeight+40)+"px");
		flow.add(image1Panel);
		flow.add(image2Panel);
		
		
		
		
		
		mixedImage.setSize(canvasWidth+"px", canvasWidth+"px");
		mixedImage.setVisible(false);
		
		return root;
	}
	
	
	private void createDownloadLink(){
		linkFolder.clear();
		if(GWTUtils.isIOS()){
			//iOS not support download
			linkFolder.add(new Label("long press to below image to download"));
		}else if(GWTUtils.isIE()){
			//IE is horrible to support it
			linkFolder.add(new Label("right-click context menu and select save as"));
		}else{
		
		Anchor anchor=CanvasUtils.generateDownloadImageLink(mixedCanvas, true, "mixed.png", "Push here to download image", true);
		anchor.setStylePrimaryName("bt");
		linkFolder.add(anchor);
		}
	}
	
	private int canvasWidth=400;
	private int canvasHeight=400;
	private Image mixedImage;

	private TopBarPanel mixedPanel;
	private FileUploadForm image1Uploader;
	private ListBox transparentBox;
	private HorizontalPanel topPanel;
	private CheckBox expand1Check;
	
	private void updateMixedImage() {
		int transparent=ValuesUtils.toInt(transparentBox.getItemText(transparentBox.getSelectedIndex()), 50);
		double tp=1-(0.01*transparent);
		CanvasUtils.clear(mixedCanvas);
		
		if(img1.isVisible()){
			CanvasUtils.drawFitCenter(mixedCanvas, ImageElement.as(img1.getElement()));
		}
		
		//mixedCanvas.getContext2d().drawImage(image1Canvas.getCanvasElement(), 0, 0);
		mixedCanvas.getContext2d().setGlobalAlpha(tp);
		//mixedCanvas.getContext2d().drawImage(image2Canvas.getCanvasElement(), 0, 0);
		
		if(img2.isVisible()){
			CanvasUtils.drawFitCenter(mixedCanvas, ImageElement.as(img2.getElement()));
		}
		mixedCanvas.getContext2d().setGlobalAlpha(1);
		
		mixedImage.setUrl(mixedCanvas.toDataUrl());
		mixedImage.setVisible(true);
		mixedPanel.setVisible(true);
		
		createDownloadLink();
	}

	@Override
	public String getAppName() {

		return "ImageMix";
	}

	@Override
	public String getAppVersion() {
		return "1.0";
	}

	@Override
	public String getAppUrl() {
		return "http://android.akjava.com/html5apps/index.html#"+getAppName().toLowerCase();
	}

	

	@Override
	public Panel getLinkContainer() {

		return topPanel;
	}

}
