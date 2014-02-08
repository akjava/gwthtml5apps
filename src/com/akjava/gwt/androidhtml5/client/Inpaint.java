package com.akjava.gwt.androidhtml5.client;

import java.util.List;

import com.akjava.gwt.androidhtml5.client.inpaint.MaskData;
import com.akjava.gwt.androidhtml5.client.inpaint.MaskDataEditor;
import com.akjava.gwt.androidhtml5.client.inpaint.MaskDataEditor.MaskDataEditorDriver;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.inpaint.client.InPaint;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
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
import com.google.gwt.user.client.ui.ScrollPanel;
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

	
	private ImageElement uploadImageElement;
	private Button updateMaskBt;
	
	
	
	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	
	
	private DropDockDataUrlRootPanel createRootAndTop(){

		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,true) {

			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					if(!GWT.isProdMode()){
						ImageElement element=ImageElementUtils.create(dataUrl);
						if(element.getWidth()+element.getHeight()>150){
							Window.alert("hey this is dev mode,made your pc freeze.use on production mode");
						}else{
							uploadImage(element);
						}
					}else{
						ImageElement element=ImageElementUtils.create(dataUrl);
						uploadImage(element);
					}
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
	
	
	private void createWestPanel(){
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		//EAST TOP
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(4);
		HorizontalPanel h1Panel=new HorizontalPanel();
		controler.add(h1Panel);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				if(file!=null){
				uploadImage(ImageElementUtils.create(value));
				}
				//loadFile(file, value);
			}
		}, true);
		h1Panel.add(upload);
		eastPanel.addNorth(controler, 220);
		//
		editor = new MaskDataEditor();    
	    maskDataDriver.initialize(editor);
	    controler.add(editor);
	    
	  
	    
		
		
		
		
		HorizontalPanel h1=new HorizontalPanel();
		controler.add(h1);
	
		
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
		
		
		HorizontalPanel h4=new HorizontalPanel();
		controler.add(h4);
		updateBt = new Button("update",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doInPaint(selectedElement);
			}
		});
		h4.add(updateBt);
		updateBt.setEnabled(false);
		
		updateMaskBt = new Button("update Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doMask(selectedElement);
			}
		});
		h4.add(updateMaskBt);
		
		Button addMaskBt = new Button("add Mask",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				MaskData data=new MaskData();
				maskDataTableSet.addItem(data);
				maskDataTableSet.setSelected(data, true);
			}
		});
		h4.add(addMaskBt);
		
		
		
		//scroll
		ScrollPanel scroll=new ScrollPanel();
		scroll.setSize("100%", "100%");
		//create east-main
		
		SimpleCellTable<MaskData> table=new SimpleCellTable<MaskData>(999) {
			@Override
			public void addColumns(CellTable<MaskData> table) {
				 TextColumn<MaskData> fileInfoColumn = new TextColumn<MaskData>() {
				      public String getValue(MaskData value) {
				    	  
				    	  return value.toString();
				      }
				    };
				    table.addColumn(fileInfoColumn,"Name");
			}
		};
		
		maskDataTableSet = new EasyCellTableSet<MaskData>(table,false) {
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
		
		eastPanel.add(scroll);
		
		dock.addWest(eastPanel, 400);
	}
	
	
	
	private MaskData selectionMaskData;
	private void doEdit(MaskData selection){
		
		maskDataDriver.flush();//finish last editing
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
	public void initializeWidget() {
		createRootAndTop();
		createWestPanel();
		
		//create main
		if(!InPaint.exists()){
			Window.alert("not found inpaint.js");
			return;
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
		
		
		
		
		
		
		
		
		ScrollPanel scroll=new ScrollPanel();
		
		//main = new VerticalPanel();
		dock.add(scroll);
		
		resultPanel = new VerticalPanel();
		sharedCanvas=Canvas.createIfSupported();
		scroll.add(resultPanel);
		
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
		
		
	}
	
	
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
	

	private Canvas sharedCanvas;
	
	private ImageElement selectedElement;
	private Button updateBt;
	private VerticalPanel resultPanel;

	

	private MaskDataEditor editor;

	private EasyCellTableSet<MaskData> maskDataTableSet;

	
	
	private void createAndInsertImage(Uint8Array maskByte,Panel panel){
	
		ImageData maskData=CanvasUtils.getImageData(sharedCanvas,false);
		LogUtils.log(maskByte.length()+","+maskData.getWidth()+"x"+maskData.getHeight());
		//InPaint.createImageDataFromMask(maskData,maskByte,0,0,0,255,true);//for black & white
		InPaint.createImageDataFromMaskAsGray(maskData,maskByte);
		
		CanvasUtils.copyTo(maskData,sharedCanvas);
		String dataUrl=sharedCanvas.toDataUrl();
		Image img=new Image(dataUrl);
		panel.add(img);
	}
	
	private void uploadImage(final ImageElement element){
		resultPanel.clear();
		ImageElementUtils.copytoCanvas(element, sharedCanvas);
		resultPanel.add(sharedCanvas);
		
		//add alpha?
		updateBt.setEnabled(true);
		this.selectedElement=element;
	}
	
	//TODO support multiple
	protected void doMask(final ImageElement element) {

		maskDataDriver.flush();//flush current
		
		 
			resultPanel.clear();
			HorizontalPanel h=new HorizontalPanel();
			resultPanel.add(h);
			h.add(sharedCanvas);
			sharedCanvas.setVisible(false);
			
			
		
		Uint8Array merged=createMaskData(element);
		
		
	
		
		createAndInsertImage(merged,h);
		
		
		//redraw origin image
		ImageElementUtils.copytoCanvas(element, sharedCanvas);
		sharedCanvas.setVisible(true);
	}
	
	private Uint8Array createMaskData(final ImageElement element){
		//LogUtils.log(maskData.toString());
		
		
		ImageData imageData=CanvasUtils.getImageData(sharedCanvas,true);
		
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
	
	protected void doInPaint(final ImageElement element) {
		final MaskData data=maskDataDriver.flush();
		
		
		updateBt.setEnabled(false);
	
		this.selectedElement=element;
		
		Timer timer=new Timer(){
			public void run(){
				
				 Benchmark.start("total");
				resultPanel.clear();
				resultPanel.add(sharedCanvas);
				sharedCanvas.setVisible(false);
				
				
				//resultPanel.add(new Image(element.getSrc()));
				//use edge case
				int margin=2;
				
				Benchmark.start("expand");
				if(data.getColor()=="#000000" || data.isTransparent()){//should not expand
				//	margin=0;//not use
				}
				Canvas canvas=ImageElementUtils.copytoCanvasWithMargin(element, sharedCanvas,true,margin,true);
				
				ImageData imageData=CanvasUtils.getImageData(canvas,true);
				Benchmark.endAndLog("expand");
				Uint8Array array=null;
				
				//Uint8Array grayByte=null;
				
				
				//created by maskData
				Uint8Array merged=createMaskData(element);
				
				/*
				Uint8Array expanded=InPaint.expandMaskByte(merged,  imageData.getWidth(),data.getExpand());
				createAndInsertImage(expanded,resultPanel);
				
				grayByte=InPaint.expandMaskByteAsGray(expanded,  imageData.getWidth(),data.getFade());
				*/
				
				ImageData maskData=CanvasUtils.getImageData(canvas,false);
				InPaint.createImageDataFromMaskAsGray(maskData,merged);
				
				
				CanvasUtils.copyTo(maskData,sharedCanvas);
				String dataUrl=sharedCanvas.toDataUrl();
				Image img=new Image(dataUrl);
				resultPanel.add(img);
				
				
				Uint8Array newByte=InPaint.createMaskByColor(maskData, 0,0,0,false);//not 0 is mask
				createAndInsertImage(newByte,resultPanel);
				array=newByte;
				
				
				
				
				
				Benchmark.start("inpaint");
				InPaint.inpaint(imageData, array, radiuseRange.getValue());
				CanvasUtils.copyTo(imageData,sharedCanvas);
				String dataUrl3=sharedCanvas.toDataUrl();
				Image painted=new Image(dataUrl3);
				resultPanel.add(painted);
				Benchmark.endAndLog("inpaint");
				
				Benchmark.start("mix");
				ImageData paintedData=CanvasUtils.getImageData(sharedCanvas, true);
				CanvasUtils.drawImage(sharedCanvas,element,margin,margin);
				
				if(merged!=null){
				CanvasUtils.copyAlpha(paintedData,merged);
				Canvas paintedCanvas=CanvasUtils.createCanvas(null, paintedData);
				CanvasUtils.drawImage(sharedCanvas,paintedCanvas);
				}
				updateBt.setEnabled(true);
				
				
				//cut off margin
				String lastImage=CanvasUtils.toDataUrl(sharedCanvas, sharedCanvas, margin, margin, canvas.getCoordinateSpaceWidth()-margin*2, canvas.getCoordinateSpaceHeight()-margin*2);
				
				
				//resultPanel.add(sharedCanvas);
				
				//final mixed
				
				resultPanel.add(new Image(lastImage));
				Benchmark.endAndLog("mix");
				Benchmark.endAndLog("total");
				ImageElementUtils.copytoCanvas(element, sharedCanvas);
				sharedCanvas.setVisible(true);
			}
		};
		timer.schedule(50);
	
		
	}
	


	@Override
	public String getAppName() {
		return "Inpaint";
	}

	@Override
	public String getAppVersion() {
		return "1.0";
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
