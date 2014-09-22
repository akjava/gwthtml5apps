package com.akjava.gwt.androidhtml5.client;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.androidhtml5.client.cifar10.Cifar10Data;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ImageBuilder;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Cifar10 extends AbstractDropArrayEastDemoEntryPoint{
private Canvas sharedCanvas=Canvas.createIfSupported();
private VerticalPanel mainPanel;
private List<Cifar10Data> datas=new ArrayList<Cifar10Data>();

	
	//TODO cyfar10 package
	

	//TODO move common
	  private static final int UNSIGNED_MASK = 0xFF;
	  public static int toInt(byte value) {
		    return value & UNSIGNED_MASK;
		  }
	  
	@Override
	public void doDropFile(File file, Uint8Array array) {
		//TODO set name;
		
		byte[] bytes=array.toByteArray();
		checkState(bytes.length==30730000,file.getFileName()+" invalid byte-data:length="+bytes.length);
		for(int i=0;i<1000;i++){
			String name=FileNames.getRemovedExtensionName(file.getFileName())+"_"+(i+1);
			
			//LogUtils.log("making:"+i);
			Cifar10Data data=new Cifar10Data();
			data.setName(name);
			int index=3073*i;
			
			data.setClassNumber(bytes[index]);
			ImageData imageData=ImageDataUtils.createWithSharedCanvas(32,32);
			//red
			for(int ry=0;ry<32;ry++){
				for(int rx=0;rx<32;rx++){
					int redIndex=(index+1)+ry*32+rx;
					int red=toInt(bytes[redIndex]);
					
					imageData.setRedAt(red, rx, ry);
					imageData.setAlphaAt(255, rx, ry);
				}
			}
			
			//green
			for(int gy=0;gy<32;gy++){
				for(int gx=0;gx<32;gx++){
					int greenIndex=(index+1)+1024+gy*32+gx;
					imageData.setGreenAt(toInt(bytes[greenIndex]), gx, gy);
				}
			}
			//blue
			for(int by=0;by<32;by++){
				for(int bx=0;bx<32;bx++){
					int blueIndex=(index+1)+1024+1024+by*32+bx;
					imageData.setBlueAt(toInt(bytes[blueIndex]), bx, by);
				}
			}
			data.setImageData(imageData);
			
			
			
			//cat only test
			
			addData(data);
			
		}
	}
	public void addData(Cifar10Data data){
		CanvasUtils.copyTo(data.getImageData(), sharedCanvas);
		if(data.getClassNumber()==3){
		Image img=new Image(sharedCanvas.toDataUrl());
		img.setWidth("128px");
		mainPanel.add(img);
		mainPanel.add(new Label(data.getName()));
		}
		
		datas.add(data);
	}

	@Override
	public Predicate<File> getDoDropFilePredicate() {
		// TODO Auto-generated method stub
		return new Predicate<File>(){
			@Override
			public boolean apply(File input) {
				return input.getFileName().endsWith(".bin");
			}
			
		};
	}

	@Override
	public int getEastPanelWidth() {
		
		return 200;
	}

	@Override
	public Panel getEastPanel() {
		VerticalPanel panel=new VerticalPanel();
		final VerticalPanel linkPanel=new VerticalPanel();
		
		ExecuteButton execButton=new ExecuteButton("extract") {
			
			@Override
			public void executeOnClick() {
				List<String> infoNames=new ArrayList<String>();
				List<String> bgNames=new ArrayList<String>();
				linkPanel.clear();
				final JSZip zip=JSZip.newJSZip();
				final JSZip zip2=JSZip.newJSZip();
				LogUtils.log("size:"+datas.size());
				for(Cifar10Data data:datas){
					String pngData=ImageBuilder.from(CanvasUtils.createCanvas(sharedCanvas, data.getImageData())).onPng().toDataUrl();
					
					/*
					CanvasUtils.createCanvas(sharedCanvas, 400, 400);
					CanvasUtils.fillRect(sharedCanvas, "#fff");
					CanvasUtils.drawCenter(sharedCanvas, ImageElementUtils.create(pngData));
					
					pngData=sharedCanvas.toDataUrl();//test big image;
					*/
					
					//LogUtils.log(pngData);
					String name=data.getName()+".png";
					
					if(data.getClassNumber()==3){//TODO
					//LogUtils.log("name:"+data.getName());
						zip.base64UrlFile(name, pngData);
						infoNames.add(name+" 1 0 0 32 32");
						//infoNames.add(name+" 1 4 4 28 28");	//24x24
					}else{
						bgNames.add(name);
						zip2.base64UrlFile(name, pngData);
					}
				}
				
				zip.file("info.txt", Joiner.on("\n").join(infoNames));
				zip2.file("bg.txt", Joiner.on("\n").join(bgNames));
				
				
				Blob blob=zip.generateBlob(null);
				Blob blob2=zip2.generateBlob(null);
				Anchor a=new HTML5Download().generateDownloadLink(blob,"application/zip","ok.zip","download ok",true);
				Anchor a2=new HTML5Download().generateDownloadLink(blob2,"application/zip","bg.zip","download ng",true);
				linkPanel.add(a);
				linkPanel.add(a2);
			}
		};
		panel.add(execButton);
		
		
		panel.add(linkPanel);
		
		return panel;
	}
	

	@Override
	public Panel getCenterPanel() {
		mainPanel = new VerticalPanel();
		ScrollPanel scroll=new ScrollPanel(mainPanel);
		
		mainPanel.setSpacing(4);
		
		
		return scroll;
	}

	@Override
	public String getHelpUrl() {
		return "";
	}

	@Override
	public String getAppName() {
		// TODO Auto-generated method stub
		return "Cyfar-10 extractor";
	}

	@Override
	public String getAppVersion() {
		return "1.0";
	}

	@Override
	public String getAppUrl() {
		return "";
	}

}
