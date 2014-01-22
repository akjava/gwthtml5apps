package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.List;

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
import com.akjava.gwt.lib.client.io.GWTLineReader;
import com.akjava.lib.common.csv.CSVProcessor;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Joiner;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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
	//private Canvas image1Canvas;
	//private Canvas image2Canvas;

	private String appName="ImageMix";
	private String version="1.0";
	private Image img1;
	private Image img2;
	@Override
	public void onModuleLoad() {
		LogUtils.log("imageMix:version "+version);
		DockLayoutPanel root=new DockLayoutPanel(Unit.PX);
		
		RootLayoutPanel.get().add(root);
		
		
		
		
		topControler = new HorizontalPanel();
		topControler.setWidth("100%");
		topControler.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topControler.setSpacing(4);
		
		//HorizontalPanel bottomControler=new HorizontalPanel();
		
		topControler.setStylePrimaryName("bg1");
		
		Label label=new Label("akjava.com "+appName+" "+version);
		label.setStylePrimaryName("title");
		topControler.add(label);
		root.addNorth(topControler, 40);
		
		Anchor help=new Anchor("How to Use","http://android.akjava.com/");
		topControler.add(help);
		
		mixedPanel = new TopBarPanel();
		
		TopBarPanel image1Panel=new TopBarPanel();
		
		image1Uploader = FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				new ImageElementLoader().load(value, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						CanvasUtils.clear(mixedCanvas);
						CanvasUtils.drawFitCenter(mixedCanvas, element);
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
		image1Panel.getTopPanel().add(image1Uploader);
		//image1Uploader.setVisible(false);
		
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
						CanvasUtils.drawFitImage(mixedCanvas, element,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_MIDDLE);
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
		img1.setSize(canvasWidth+"px",canvasHeight+"px");
		img1.setVisible(false);
		image1Panel.add(img1);
		
		img2 = new Image();
		img2.setSize(canvasWidth+"px",canvasHeight+"px");
		img2.setVisible(false);
		image2Panel.add(img2);
		
		/*
		image1Canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		image1Panel.add(image1Canvas);
		image2Canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		image2Panel.add(image2Canvas);
		*/
		
		mixedCanvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);

		
		mixedImage = new Image();
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
		
		parseCsv();
	}
	
	private void parseCsv(){
		try {
			new RequestBuilder(RequestBuilder.GET, "/apps.csv").sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					
					List<List<String>> csvs;
					try {
						
						int size=CSVUtils.splitLinesWithGuava(response.getText()).size();
						
						
						csvs = GWTLineReader.wrap(response.getText()).readLines(new CSVProcessor(','));
						HorizontalPanel p1=new HorizontalPanel();
						
						p1.setSpacing(2);
						//p1.add(new Label("Apps:"));
						for(int i=0;i<csvs.size();i++){
							
							
							
							List<String> csv=csvs.get(i);
							LogUtils.log(Joiner.on(",").join(csv));
							LogUtils.log("csv-size:"+csv.size());
							Anchor a=null;
							if(csv.size()>1){
								a=new Anchor(""+csv.get(0)+"",csv.get(1));
								a.setStylePrimaryName("title");
								p1.add(a);
							}
							if(csv.size()>2){
								a.setTitle(csv.get(2));
							}
							
							if(i<csvs.size()-1){
								p1.add(new Label("|"));
							}
						}
						
						topControler.add(p1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					LogUtils.log("csv not found");
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private int canvasWidth=400;
	private int canvasHeight=400;
	private Image mixedImage;

	private TopBarPanel mixedPanel;
	private FileUploadForm image1Uploader;
	private ListBox transparentBox;
	private HorizontalPanel topControler;
	
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

}
