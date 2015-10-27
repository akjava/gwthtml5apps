package com.akjava.gwt.androidhtml5.client;

import java.util.List;

import com.akjava.gwt.androidhtml5.client.inpaint.MaskData;
import com.akjava.gwt.androidhtml5.client.inpaint.MaskDataEditor;
import com.akjava.gwt.androidhtml5.client.inpaint.MaskDataEditor.MaskDataEditorDriver;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.inpaint.client.InPaint;
import com.akjava.gwt.inpaint.client.InpaintEngine;
import com.akjava.gwt.inpaint.client.InpaintEngine.InpaintListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.LoggingImageElementLoader;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.Benchmark;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.base.Optional;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class Inpaint extends Html5DemoEntryPoint {

	
	MaskDataEditorDriver maskDataDriver = GWT.create(MaskDataEditorDriver.class);

	private InputRangeWidget radiuseRange;

	

	private Button updateMaskBt;
	
	
	
	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	
	
	private DropDockDataUrlRootPanel createRootAndTop(){

		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false) {

			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
				
					
					ImageElementUtils.createWithLoader(dataUrl, new ImageElementListener() {
						
						@Override
						public void onLoad(ImageElement element) {
							if(element.getWidth()+element.getHeight()>150){
								Window.alert("hey this is dev mode,made your pc freeze.use on production mode");
							}
							uploadImage(element);
						}
						
						@Override
						public void onError(String url, ErrorEvent event) {
							LogUtils.log(event.getNativeEvent());
						}
					});
				}
			}
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		dock = new DockLayoutPanel(Unit.PX);
		//dock.setSize("100%", "100%");
		root.add(dock);
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setStylePrimaryName("bg1");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(1);
		dock.addNorth(topPanel,30);
		
		
		topPanel.add(createTitleWidget());
		
		topPanel.add(new Anchor("Help", "inpaint_help.html"));
		return root;
	}
	
	private ImageElement maskImageElement;
	
	private void createWestPanel(){
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		//EAST TOP
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(4);
		HorizontalPanel h1Panel=new HorizontalPanel();
		controler.add(h1Panel);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file,final String value) {
				if(file!=null){
					new LoggingImageElementLoader(){

						@Override
						public void onLoad(ImageElement imageElement) {
							uploadImage(imageElement);
						}
						
					}.load(value);
				
				}
				//loadFile(file, value);
			}
		}, true);
		h1Panel.add(upload);
		eastPanel.addNorth(controler, 280);
		//
		
	    
	 
	    
		
		
		
		
		HorizontalPanel h1=new HorizontalPanel();
		controler.add(h1);
		h1.setSpacing(4);
		
		
	
		
		
		final Label radiusLabel=new Label("radius:5");
		h1.add(radiusLabel);
		radiuseRange = HTML5InputRange.createInputRange(1, 40, 5);
		radiuseRange.setWidth("100px");
		h1.add(radiuseRange);
		radiuseRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				//updateImage();
			}
		});
		radiuseRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				radiusLabel.setText("radius:"+((double)radiuseRange.getValue()));
				//updateImage();
			}
		});
		h1.add(new Label("Better to keep value under 5(or freeze)"));
		
		
		HorizontalPanel h4=new HorizontalPanel();
		controler.add(h4);
		updateBt = new Button("Inpaint",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(maskImageButton.getValue()){
					doInPaint(selectedElement,maskImageElement);
				}else{
					doInPaint(selectedElement);
				}
				
			}
		});
		h4.add(updateBt);
		updateBt.setEnabled(false);
		
		
		controler.add(new Label("Mask Mode"));
		
		HorizontalPanel switcher=new HorizontalPanel();
		controler.add(switcher);
		switcher.setWidth(eastWidth+"px");
		
		
		VerticalPanel transparentPanel=new VerticalPanel();
		
		RadioButton transparentButton=new RadioButton("mode","Image's transparent");
		transparentButton.setValue(true);
		transparentButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					editor.setVisible(true);
				}
			}
		});
		transparentPanel.add(transparentButton);
		
		switcher.add(transparentPanel);
		
		maskImageButton = new RadioButton("mode","Mask Image(Keep black area)");
		maskImageButton.setEnabled(false);
		maskImageButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					editor.setVisible(false);
				}
			}
		});
		
		
		VerticalPanel maskImagesPanel=new VerticalPanel();
	
		final Label imageLabel=new Label();
		switcher.add(maskImagesPanel);
		maskImagesPanel.add(maskImageButton);
		
		final Button repreviewButton=new Button("Re preview Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doRepreview();
			}
		});
		maskImagesPanel.add(repreviewButton);
		repreviewButton.setEnabled(false);
		
		FileUploadForm maskUpload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				imageLabel.setText(file.getFileName());
				new LoggingImageElementLoader(){

					@Override
					public void onLoad(ImageElement imageElement) {
						maskImageElement=imageElement;
						maskImageButton.setEnabled(true);
						maskImageButton.setValue(true,true);
						repreviewButton.setEnabled(true);
						doRepreview();
					}}.load(text);
			}
		});
		maskImagesPanel.add(maskUpload);
		
		maskImagesPanel.add(imageLabel);
		
		
		//test
		HorizontalPanel masks=new HorizontalPanel();
		masks.add(new Label("Mask-image:"));
		//controler.add(masks);
		
		
		
		
		
		updateMaskBt = new Button("Preview Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doMask(selectedElement);
			}
		});
		updateMaskBt.setEnabled(false);
		transparentPanel.add(updateMaskBt);
		
		Button addMaskBt = new Button("add New Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				MaskData data=new MaskData();
				maskDataTableSet.addItem(data);
				maskDataTableSet.setSelected(data, true);
			}
		});
		//h4.add(addMaskBt);
		
		Button removeMaskBt = new Button("Remove Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(maskDataTableSet.getSelection()==null){
					return;
				}
				
				maskDataTableSet.removeItem(maskDataTableSet.getSelection());
				
			}
		});
		//h4.add(removeMaskBt);
		
		
		//controler.add(new Label("Current MaskData"));
		editor = new MaskDataEditor();    
	    maskDataDriver.initialize(editor);
	    controler.add(editor);
		
		//scroll
		ScrollPanel scroll=new ScrollPanel();
		scroll.setSize("100%", "100%");
		//create east-main
		
		SimpleCellTable<MaskData> table=new SimpleCellTable<MaskData>(999) {
			@Override
			public void addColumns(CellTable<MaskData> table) {
				 TextColumn<MaskData> fileInfoColumn = new TextColumn<MaskData>() {
				      public String getValue(MaskData value) {
				    	  String data="MaskData(";
				    	  if(value.isTransparent()){
				    		  data+="Transparent Mode";
				    	  }else{
				    		  if(value.isSimilarColor()){
				    			  data+="SimilarColor Mode";
				    		  }else{
				    			  data+="Color Mode";
				    		  }
				    		  data+=":"+value.getColor();
				    	  }
				    	  data+=")";
				    	  return data;
				    	  //return value.toString();
				      }
				    };
				    table.addColumn(fileInfoColumn,"Name");
			}
		};
		
		maskDataTableSet = new EasyCellTableObjects<MaskData>(table,false) {
			@Override
			public void onSelect(MaskData selection) {
				doEdit(selection);
			}
		};
		scroll.add(table);
		
		
		MaskData  maskData = new MaskData(); //initial data
		
		maskDataDriver.edit(maskData);
		
		maskDataTableSet.addItem(maskData);
		maskDataTableSet.setSelected(maskData, true);
		
		//cell list
		
		//eastPanel.add(scroll);
		
		dock.addWest(eastPanel, eastWidth);
	}
	protected void doRepreview() {
		LogUtils.log("doRepreview1");
		int margin=InpaintEngine.INPAINT_MARGIN;
		if(maskImageElement==null){
			 Window.alert("no mask element");
			 return;
		}
		LogUtils.log("doRepreview1");
		ImageElementUtils.copytoCanvasWithMargin(maskImageElement, sharedCanvas,true,margin,true);
		
		Canvas grayscale=CanvasUtils.convertToGrayScale(sharedCanvas, null);
		CanvasUtils.copyTo(grayscale, sharedCanvas);
		
		
		ImageData maskData=CanvasUtils.getImageData(sharedCanvas);
		
		greyScaleMaskPanel.clear();
		
		addToPanel(greyScaleMaskPanel,maskData);
		
		mainTab.selectTab(1);
		LogUtils.log("doRepreview4");
		//CanvasUtils.copyTo(maskData,sharedCanvas);
		//String dataUrl=sharedCanvas.toDataUrl();
		//listener.createGreyScaleMaks(maskData);
	}

	private int eastWidth=400;
	
	
	
	private MaskData selectionMaskData;
	private void doEdit(MaskData selection){
		
		maskDataDriver.flush();//finish last editing
		maskDataTableSet.update();
		
		if(selection==null){
			editor.setEnabled(false);
			maskDataDriver.edit(new MaskData());
		}else{
			editor.setEnabled(true);
			maskDataDriver.edit(selection);
		}
		
		selectionMaskData=selection;
		updateCanvas();
	}
	
	private void updateCanvas() {
		//show layer or something
		
	}


	@Override
	public Panel initializeWidget() {
		DropDockDataUrlRootPanel root=createRootAndTop();
		createWestPanel();
		
		//create main
		if(!InPaint.exists()){
			Window.alert("not found inpaint.js");
			return new VerticalPanel();
		}
		
		
		/*
		new ImageElementLoader().load("gridPaint.png", new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				doInPaint(element);
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
		
		
		
		
		
		
		
		mainTab = new TabLayoutPanel(30, Unit.PX);
		dock.add(mainTab);
		
		
		
		
		//main = new VerticalPanel();
		
		ScrollPanel scroll=new ScrollPanel();
		mainTab.add(scroll,"Loaded Image");
		loadedPanel = new VerticalPanel();
		
		scroll.add(loadedPanel);
		
		ScrollPanel maskScroll=new ScrollPanel();
		mainTab.add(maskScroll,"Grayscale Mask");
		greyScaleMaskPanel=new VerticalPanel();
		maskScroll.add(greyScaleMaskPanel);
		
		ScrollPanel inpaintMaskScroll=new ScrollPanel();
		mainTab.add(inpaintMaskScroll,"inPaint Mask");
		inpaintMaskPanel=new VerticalPanel();
		inpaintMaskScroll.add(inpaintMaskPanel);
		
		
		ScrollPanel inpaintScroll=new ScrollPanel();
		mainTab.add(inpaintScroll,"Raw Inpaint");
		inpaintPanel=new VerticalPanel();
		inpaintScroll.add(inpaintPanel);
		
		ScrollPanel mixedScroll=new ScrollPanel();
		mainTab.add(mixedScroll,"Final Mixed");
		mixedPanel=new VerticalPanel();
		mixedScroll.add(mixedPanel);
		
		mainTab.selectTab(0);
		sharedCanvas=Canvas.createIfSupported();
		sharedCanvas.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				ImageData data=sharedCanvas.getContext2d().getImageData(event.getX(), event.getY(), 1, 1);
				int r=data.getRedAt(0, 0);
				int g=data.getGreenAt(0, 0);
				int b=data.getBlueAt(0, 0);
				LogUtils.log(r+","+g+","+b);
				String hex=ColorUtils.toCssColor(r,g,b);
				LogUtils.log(hex);
				editor.getColorEditor().setValue(hex);
				//maskData.setColor(hex);
				//updateMaskData();
			}
		});
		
		return root;
	}
	
	
	/*
	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				uploadImageElement=element;
				
				
				//final ImageElementData data=new ImageElementData(file.getFileName(),element,asStringText);
				
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				Window.alert(event.toDebugString());
			}
			
		});
		
		//doSelecct(data);//only way to update on Android Chrome
		}catch (Exception e) {
			e.printStackTrace();
			LogUtils.log(e.getMessage());
		}
		
		
	}
	*/
	

	private Canvas sharedCanvas;
	
	private ImageElement selectedElement;
	private Button updateBt;
	private VerticalPanel loadedPanel;
	private VerticalPanel greyScaleMaskPanel;
	private VerticalPanel inpaintPanel;
	private VerticalPanel mixedPanel;
	private VerticalPanel inpaintMaskPanel;
	

	private MaskDataEditor editor;

	private EasyCellTableObjects<MaskData> maskDataTableSet;

	
	/**
	 * watch out create image based on canvas size.
	 * @param maskByte
	 * @param panel
	 */
	private void createAndInsertImage(Uint8Array maskByte,Panel panel){
	
		ImageData maskData=CanvasUtils.getImageData(sharedCanvas,false);
		//LogUtils.log(maskByte.length()+","+maskData.getWidth()+"x"+maskData.getHeight());
		//InPaint.createImageDataFromMask(maskData,maskByte,0,0,0,255,true);//for black & white
		InPaint.createImageDataFromMaskAsGray(maskData,maskByte);
		
		CanvasUtils.copyTo(maskData,sharedCanvas);
		String dataUrl=sharedCanvas.toDataUrl();
		Image img=new Image(dataUrl);
		panel.add(img);
	}
	
	
	Image loadedImage;
	private void uploadImage(final ImageElement element){
		loadedPanel.clear();
		
		loadedImage=new Image(element.getSrc());
		loadedPanel.add(loadedImage);
		
		ImageElementUtils.copytoCanvas(element, sharedCanvas);
		//loadedPanel.add(sharedCanvas);
		
		//add alpha?
		updateBt.setEnabled(true);
		updateMaskBt.setEnabled(true);
		this.selectedElement=element;
		mainTab.selectTab(0);
		
		//can't drop convert anymore somewhere need wait loading.
		//mainTab.selectTab(3);
		//temporaly doing.,how to drop convert?
		//doInPaint(element,maskImageElement);
		
	}
	
	
	protected void doMask(final ImageElement element) {

		maskDataDriver.flush();//flush current data
		maskDataTableSet.update();//update label
		
		if(element==null){
			return;//not selected yet
		}
		 
		
		greyScaleMaskPanel.clear();
		inpaintMaskPanel.clear();
		
		ImageElementUtils.copytoCanvas(selectedElement, sharedCanvas);
		Uint8Array merged=createMaskData(sharedCanvas);
		createAndInsertImage(merged,greyScaleMaskPanel);
		
		
		ImageData maskData=CanvasUtils.getImageData(sharedCanvas,true);//maybe remain last used data
		Uint8Array newByte=InPaint.createMaskByColor(maskData, 0,0,0,false);//not 0 is mask
		
		//return 0 or 1 need to x 255 to rgb
		for(int i=0;i<newByte.length();i++){
			newByte.set(i, newByte.get(i)*255);
		}
		
		createAndInsertImage(newByte,inpaintMaskPanel);
		
		
		
		mainTab.selectTab(1);//mask tab
		
	}
	
	private Uint8Array createMaskData(final Canvas canvas){
		//LogUtils.log(maskData.toString());
		
		//ImageElementUtils.copytoCanvas(element, sharedCanvas);
		
		ImageData imageData=CanvasUtils.getImageData(canvas,true);
		
		Uint8Array merged=null;
		List<MaskData> maskDatas=maskDataTableSet.getDatas();
		for(MaskData maskData:maskDatas){
			Uint8Array bt;
			Uint8Array grayByte;
		if(maskData.isTransparent()){
			bt=InPaint.createMaskByAlpha(imageData);
		}else{
			int rgb[]=ColorUtils.toRGB(maskData.getColor());
			
			
			if(maskData.isSimilarColor()){
				bt=InPaint.createMaskBySimilarColor(imageData, rgb[0], rgb[1], rgb[2],maskData.getMaxLength());
			}else{
				bt=InPaint.createMaskByColor(imageData, rgb[0], rgb[1], rgb[2]);
			}
		}
		//LogUtils.log("bt:"+bt.length());
		Uint8Array expanded=InPaint.expandMaskByte(bt,  imageData.getWidth(),maskData.getExpand());
		
		//LogUtils.log("expanded:"+expanded.length());
		grayByte=InPaint.expandMaskByteAsGray(expanded,  imageData.getWidth(),maskData.getFade());
		//LogUtils.log("grayByte:"+grayByte.length());
		if(merged==null){
			merged=grayByte;
		}else{
			merge(merged,grayByte);
		}
		
		
		}
		//LogUtils.log("merged:"+merged.length());
		//createAndInsertImage(merged,resultPanel);
		return merged;
	}
	
	//set larger
	public void merge(Uint8Array array1,Uint8Array array2){
		if(array1.length()!=array2.length()){
			throw new IllegalArgumentException("not same length");
		}
		
		for(int i=0;i<array1.length();i++){
			int v1=array1.get(i);
			int v2=array2.get(i);
			if(v2>v1){
				array1.set(i, v2);//over write
			}
		}
		
	}
	private synchronized void addToPanel(final Panel panel,ImageData imageData){
		ImageDataUtils.rezieAndImageData(imageData, sharedCanvas);
		new LoggingImageElementLoader(){

			@Override
			public void onLoad(ImageElement imageElement) {
				panel.add(new Image(imageElement.getSrc()));
			}
			
		}.load(sharedCanvas.toDataUrl());
	}
	
	protected void doInPaint(final ImageElement element,final ImageElement maskImage) {
		InpaintEngine engine=new InpaintEngine();
		updateBt.setEnabled(false);
		
		greyScaleMaskPanel.clear();
		inpaintMaskPanel.clear();
		inpaintPanel.clear();
		mixedPanel.clear();
		
		engine.doInpaint(element, radiuseRange.getValue(), maskImage, new InpaintListener(){
			private Canvas canvas=Canvas.createIfSupported();//shared canvas possible conflict
			@Override
			public void createMixedImage(ImageData imageData) {
				updateBt.setEnabled(true);
				addToPanel(mixedPanel,imageData);
				mainTab.selectTab(3);//final tab
			}

			@Override
			public void createInpaintImage(ImageData imageData) {
				addToPanel(inpaintPanel,imageData);
			}

			@Override
			public void createGreyScaleMaks(ImageData imageData) {
				addToPanel(greyScaleMaskPanel,imageData);
			}

			@Override
			public void createInpainteMaks(ImageData imageData) {
				addToPanel(inpaintMaskPanel,imageData);
			}
			
			private synchronized void addToPanel(final Panel panel,ImageData imageData){
				ImageDataUtils.rezieAndImageData(imageData, canvas);
				new LoggingImageElementLoader(){

					@Override
					public void onLoad(ImageElement imageElement) {
						panel.add(new Image(imageElement.getSrc()));
					}
					
				}.load(canvas.toDataUrl());
			}
			
			
		});
		
		
		
		
		
		/*
		final MaskData data=maskDataDriver.flush();
		this.selectedElement=element;
		
		Timer timer=new Timer(){
			public void run(){
				
				Benchmark.start("total");
				loadedPanel.clear();
				greyScaleMaskPanel.clear();
				inpaintMaskPanel.clear();
				inpaintPanel.clear();
				mixedPanel.clear();
				
				//loadedPanel.add(sharedCanvas);
				sharedCanvas.setVisible(false);
				
				
				//resultPanel.add(new Image(element.getSrc()));
				//use edge case
				int margin=2;
				
				Benchmark.start("expand");
				if(data.getColor()=="#000000" || data.isTransparent()){//should not expand
				//	margin=0;//not use
				}
				
				//canvas and sharedCanvas is same
				ImageElementUtils.copytoCanvasWithMargin(element, sharedCanvas,true,margin,true);
				ImageData expandedImageData=CanvasUtils.getImageData(sharedCanvas,true);
				
				
				Benchmark.endAndLog("expand");
				Uint8Array array=null;
				
				//Uint8Array grayByte=null;
				
				
				//created by maskData
				Uint8Array merged=null;
				
				
				Uint8Array expanded=InPaint.expandMaskByte(merged,  imageData.getWidth(),data.getExpand());
				createAndInsertImage(expanded,resultPanel);
				
				grayByte=InPaint.expandMaskByteAsGray(expanded,  imageData.getWidth(),data.getFade());
				
				
				
				
				int w=element.getWidth()+margin*2;
				int h=element.getHeight()+margin*2;
				CanvasUtils.createCanvas(sharedCanvas, w, h);
				
				sharedCanvas.getContext2d().drawImage(maskImage, margin, margin);
				String dataUrl=sharedCanvas.toDataUrl();
				Image img=new Image(dataUrl);
				greyScaleMaskPanel.add(img);//add mask
				ImageData maskData=ImageDataUtils.copyFrom(sharedCanvas);
				
				Uint8Array newByte=InPaint.createMaskByColor(maskData, 0,0,0,false);//not 0 is mask
				
				//return 0 or 1
				
				array=newByte;
				
				
				
				
				LogUtils.log("imageData:"+expandedImageData.getWidth()+"x"+expandedImageData.getHeight());
				Benchmark.start("inpaint");
				InPaint.inpaint(expandedImageData, array, radiuseRange.getValue());
				
				//somehow(maybe transparent problem) expanded to result should keep same size
				
				
				sharedCanvas.setCoordinateSpaceWidth(expandedImageData.getWidth()-4);
				sharedCanvas.setCoordinateSpaceHeight(expandedImageData.getHeight()-4);
				sharedCanvas.getContext2d().putImageData(expandedImageData,-2,-2);
				
				
				//CanvasUtils.copyTo(imageData,sharedCanvas);
				//CanvasUtils.copyTo(imageData,sharedCanvas);
				
				String inpaintDataUrl=sharedCanvas.toDataUrl();
				Image inpaintImage=new Image(inpaintDataUrl);//this image larged.
				inpaintPanel.add(inpaintImage);
				
				Anchor inpaintAnchor=HTML5Download.get().generateBase64DownloadLink(inpaintDataUrl, "image/png", "inpaingRaw.png", "Download", true);
				inpaintPanel.add(inpaintAnchor);
				
				//createAndInsertImage use sizes
				sharedCanvas.setCoordinateSpaceWidth(expandedImageData.getWidth());
				sharedCanvas.setCoordinateSpaceHeight(expandedImageData.getHeight());
				
				//for support mergin
				
				
				
				//create grayscale later
				
				Uint8Array drawByte=Uint8Array.createUint8(newByte.length());
				//better to do last ?
				for(int i=0;i<newByte.length();i++){
					drawByte.set(i, newByte.get(i)*255);
				}
				createAndInsertImage(drawByte,inpaintMaskPanel);
					
				
				
				Benchmark.endAndLog("inpaint");
				
				Benchmark.start("mix");
				CanvasUtils.copyTo(expandedImageData,sharedCanvas);//sharedcanvas broken by last create-image
				ImageData paintedData=CanvasUtils.getImageData(sharedCanvas, true);
				CanvasUtils.drawImage(sharedCanvas,element,margin,margin);
				
				if(merged!=null){
				CanvasUtils.copyAlpha(paintedData,merged);
				Canvas paintedCanvas=CanvasUtils.createCanvas(null, paintedData);
				CanvasUtils.drawImage(sharedCanvas,paintedCanvas);
				}
				updateBt.setEnabled(true);
				
				
				//cut off margin
				String lastImage=CanvasUtils.toDataUrl(sharedCanvas, sharedCanvas, margin, margin, sharedCanvas.getCoordinateSpaceWidth()-margin*2, sharedCanvas.getCoordinateSpaceHeight()-margin*2);
				
				
				//resultPanel.add(sharedCanvas);
				
				//final mixed
				
				mixedPanel.add(new Image(lastImage));
				Benchmark.endAndLog("mix");
				Benchmark.endAndLog("total");
				ImageElementUtils.copytoCanvas(element, sharedCanvas);
				sharedCanvas.setVisible(true);
				
				downloadArea.clear();
				Anchor anchor=HTML5Download.get().generateBase64DownloadLink(lastImage, "image/png", "inpaing.png", "Download", true);
				anchor.setName("bottom");
				
				downloadArea.add(anchor);
				
				
				
				
				if(loadedImage!=null){
					loadedPanel.add(loadedImage);//no need?
				}
				
				mixedPanel.add(downloadArea);
				
				
				
				mainTab.selectTab(3);//final tab
			}
		};
		timer.schedule(50);*/
	
		
	}
	
	protected void doInPaint(final ImageElement element) {
		final MaskData data=maskDataDriver.flush();
		
		
		updateBt.setEnabled(false);
	
		this.selectedElement=element;
		
		Timer timer=new Timer(){
			public void run(){
				
				Benchmark.start("total");
				loadedPanel.clear();
				greyScaleMaskPanel.clear();
				inpaintMaskPanel.clear();
				inpaintPanel.clear();
				mixedPanel.clear();
				
				//loadedPanel.add(sharedCanvas);
				sharedCanvas.setVisible(false);
				
				
				//resultPanel.add(new Image(element.getSrc()));
				//use edge case
				int margin=InpaintEngine.INPAINT_MARGIN;
				
				Benchmark.start("expand");
				if(data.getColor()=="#000000" || data.isTransparent()){//should not expand
				//	margin=0;//not use
				}
				
				//canvas and sharedCanvas is same
				ImageElementUtils.copytoCanvasWithMargin(element, sharedCanvas,true,margin,true);
				ImageData expandedImageData=CanvasUtils.getImageData(sharedCanvas,true);
				
				
				Benchmark.endAndLog("expand");
				Uint8Array array=null;
				
				//Uint8Array grayByte=null;
				
				
				//created by maskData
				Uint8Array merged=createMaskData(sharedCanvas);
				
				/*
				Uint8Array expanded=InPaint.expandMaskByte(merged,  imageData.getWidth(),data.getExpand());
				createAndInsertImage(expanded,resultPanel);
				
				grayByte=InPaint.expandMaskByteAsGray(expanded,  imageData.getWidth(),data.getFade());
				*/
				
				ImageData maskData=CanvasUtils.getImageData(sharedCanvas,false);
				InPaint.createImageDataFromMaskAsGray(maskData,merged);
				
				
				CanvasUtils.copyTo(maskData,sharedCanvas);
				String dataUrl=sharedCanvas.toDataUrl();
				Image img=new Image(dataUrl);
				greyScaleMaskPanel.add(img);//add mask
				LogUtils.log("gray:"+img.getWidth()+"x"+img.getHeight());
				
				
				
				
				
				Uint8Array newByte=InPaint.createMaskByColor(maskData, 0,0,0,false);//not 0 is mask
				
				//return 0 or 1
				
				array=newByte;
				
				
				
				
				LogUtils.log("imageData:"+expandedImageData.getWidth()+"x"+expandedImageData.getHeight());
				Benchmark.start("inpaint");
				InPaint.inpaint(expandedImageData, array, radiuseRange.getValue());
				
				//somehow(maybe transparent problem) expanded to result should keep same size
				
				
				sharedCanvas.setCoordinateSpaceWidth(expandedImageData.getWidth()-4);
				sharedCanvas.setCoordinateSpaceHeight(expandedImageData.getHeight()-4);
				sharedCanvas.getContext2d().putImageData(expandedImageData,-2,-2);
				
				
				//CanvasUtils.copyTo(imageData,sharedCanvas);
				//CanvasUtils.copyTo(imageData,sharedCanvas);
				
				String inpaintDataUrl=sharedCanvas.toDataUrl();
				Image inpaintImage=new Image(inpaintDataUrl);//this image larged.
				inpaintPanel.add(inpaintImage);
				
				Anchor inpaintAnchor=HTML5Download.get().generateBase64DownloadLink(inpaintDataUrl, "image/png", "inpaingRaw.png", "Download", true);
				inpaintPanel.add(inpaintAnchor);
				
				//createAndInsertImage use sizes
				sharedCanvas.setCoordinateSpaceWidth(expandedImageData.getWidth());
				sharedCanvas.setCoordinateSpaceHeight(expandedImageData.getHeight());
				
				//for support mergin
				
				
				
				//create grayscale later
				
				Uint8Array drawByte=Uint8Array.createUint8(newByte.length());
				//better to do last ?
				for(int i=0;i<newByte.length();i++){
					drawByte.set(i, newByte.get(i)*255);
				}
				createAndInsertImage(drawByte,inpaintMaskPanel);
					
				
				
				Benchmark.endAndLog("inpaint");
				
				Benchmark.start("mix");
				CanvasUtils.copyTo(expandedImageData,sharedCanvas);//sharedcanvas broken by last create-image
				ImageData paintedData=CanvasUtils.getImageData(sharedCanvas, true);
				CanvasUtils.drawImage(sharedCanvas,element,margin,margin);
				
				if(merged!=null){
				CanvasUtils.copyAlpha(paintedData,merged);
				Canvas paintedCanvas=CanvasUtils.createCanvas(null, paintedData);
				CanvasUtils.drawImage(sharedCanvas,paintedCanvas);
				}
				updateBt.setEnabled(true);
				
				
				//cut off margin
				String lastImage=CanvasUtils.toDataUrl(sharedCanvas, sharedCanvas, margin, margin, sharedCanvas.getCoordinateSpaceWidth()-margin*2, sharedCanvas.getCoordinateSpaceHeight()-margin*2);
				
				
				//resultPanel.add(sharedCanvas);
				
				//final mixed
				
				mixedPanel.add(new Image(lastImage));
				Benchmark.endAndLog("mix");
				Benchmark.endAndLog("total");
				ImageElementUtils.copytoCanvas(element, sharedCanvas);
				sharedCanvas.setVisible(true);
				
				downloadArea.clear();
				Anchor anchor=HTML5Download.get().generateBase64DownloadLink(lastImage, "image/png", "inpaing.png", "Download", true);
				anchor.setName("bottom");
				
				downloadArea.add(anchor);
				
				
				
				
				if(loadedImage!=null){
					loadedPanel.add(loadedImage);//no need?
				}
				
				mixedPanel.add(downloadArea);
				
				
				
				mainTab.selectTab(4);//final tab
			}
		};
		timer.schedule(50);
	
		
	}
	
	VerticalPanel downloadArea=new VerticalPanel();

	private TabLayoutPanel mainTab;

	private RadioButton maskImageButton;


	@Override
	public String getAppName() {
		return "Inpaint";
	}

	/**
	 * version history
	 * 1.0.1 fixed imageelement loading problem
	 */
	@Override
	public String getAppVersion() {
		return "1.1";
	}
	
	@Override
	public Panel getLinkContainer() {
		return topPanel;
	}

	@Override
	public String getAppUrl() {
		return "http://android.akjava.com/html5apps/index.html#inpaint";
	}
	
}
