package com.akjava.gwt.androidhtml5.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.androidhtml5.client.ImageMaskDataEditor.ImageMaskData;
import com.akjava.gwt.androidhtml5.client.ImagePosScaleAngleEditor.PositionScaleAngleData;
import com.akjava.gwt.androidhtml5.client.PoissonImage.LabeledInputRange;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.inpaint.client.InPaint;
import com.akjava.gwt.inpaint.client.InpaintEngine;
import com.akjava.gwt.inpaint.client.InpaintEngine.InpaintListener;
import com.akjava.gwt.inpaint.client.MaskData;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.AsyncMultiCaller;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.LoggingImageElementLoader;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public  class PoissonImage extends AbstractDropEastDemoEntryPoint implements PoissonExecuter{

	 interface Driver extends SimpleBeanEditorDriver< PoissonImageData,  PoissonImageDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	//private VerticalPanel containers;
	
	public  static class SimpleMixCanvas extends TabPanel{
		private Canvas canvas;
		private Canvas originalCanvas;
		private boolean showOrigin=true;
		public boolean isShowOrigin() {
			return showOrigin;
		}
		public void setShowOrigin(boolean showOrigin) {
			this.showOrigin = showOrigin;
		}

		private ImageElement origin;
		private ImageElement poissonedImage;
		
		private Canvas maskCanvas;
		private boolean useMask;
		
		public Canvas getMaskCanvas() {
			return maskCanvas;
		}
		public void setMaskCanvas(Canvas maskCanvas) {
			this.maskCanvas = maskCanvas;
		}
		public boolean isUseMask() {
			return useMask;
		}
		public void setUseMask(boolean useMask) {
			this.useMask = useMask;
		}
		public ImageElement getPoissonedImage() {
			return poissonedImage;
		}
		public void setPoissonedImage(ImageElement poissonedImage) {
			this.poissonedImage = poissonedImage;
		}
		public SimpleMixCanvas(){
			canvas=CanvasUtils.createCanvas(100, 100);
			
			originalCanvas=CanvasUtils.createCanvas(100,100);
			
			/*
			canvas.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					showOrigin=!showOrigin;
					update();
				}
			});
			CanvasUtils.disableSelection(canvas);
			*/
			
			add(originalCanvas,"Original-dest");
			add(canvas,"Poisonned");
		}
		public void setDest(ImageElement origin){
			ImageElementUtils.copytoCanvas(origin, canvas, false);
			ImageElementUtils.copytoCanvas(origin, originalCanvas, true);
			this.origin=origin;
			update();
		}
		/*
		private Canvas srcCanvas;
		private ImageData destImage;
		public void setMixed(Canvas srcCanvas,ImageData destImage){
			this.srcCanvas=srcCanvas;
			this.destImage=destImage;
		}
		
		 //CanvasTools.from(canvas).clear().put().drawxx();
				CanvasUtils.clear(canvas);
				if(destImage!=null && srcCanvas!=null){
				//CanvasUtils.putImageData(canvas,destImage)
				canvas.getContext2d().putImageData(destImage, 0, 0);
				//CanvasUtils.drawImageOnce(canvas,src,mode);
				canvas.getContext2d().save();
				canvas.getContext2d().setGlobalCompositeOperation(Composite.DESTINATION_OVER);
				canvas.getContext2d().drawImage(srcCanvas.getCanvasElement(), 0, 0);
				canvas.getContext2d().restore();
				}
		 */
		
		public void update(){
			CanvasUtils.clear(canvas);
			if(showOrigin){
				
				//CanvasTools.from(canvas).clear().ignoreNull().drawImage(origin)
			if(origin!=null){
			originalCanvas.getContext2d().drawImage(origin, 0, 0);
			}
			selectTab(0);
			}else{
				selectTab(1);
				if(poissonedImage!=null){
					if(useMask){
						
						ImageData data=sourceInImageData(ImageDataUtils.create(null, poissonedImage),ImageDataUtils.copyFrom(maskCanvas));
						ImageDataUtils.putImageData(data,canvas);
						
						/*canvas.getContext2d().drawImage(maskCanvas.getCanvasElement(),0,0);
						canvas.getContext2d().save();
						canvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_IN);
						canvas.getContext2d().drawImage(poissonedImage,0,0);
						canvas.getContext2d().restore();*/
						
					}else{
						canvas.getContext2d().drawImage(poissonedImage,0,0);
					}
				}
			}
		}
	}


	public  static class PoissonImageData{
		private ImageElement overrayImage;
		public ImageElement getOverrayImage() {
			return overrayImage;
		}

		public void setOverrayImage(ImageElement overrayImage) {
			this.overrayImage = overrayImage;
		}

		public PoissonImageData(ImageElement image,ImageElement overrayImage){
			imageMaskData=new ImageMaskData();
			imageMaskData.setImageElement(image);
			this.overrayImage=overrayImage;
			
			posScaleAngleData=new PositionScaleAngleData();
		}
		private ImageMaskData imageMaskData;
		private PositionScaleAngleData posScaleAngleData;

		public PositionScaleAngleData getPosScaleAngleData() {
			return posScaleAngleData;
		}

		public void setPosScaleAngleData(PositionScaleAngleData posScaleAngleData) {
			this.posScaleAngleData = posScaleAngleData;
		}

		public ImageMaskData getImageMaskData() {
			return imageMaskData;
		}

		public void setImageMaskData(ImageMaskData imageMaskData) {
			this.imageMaskData = imageMaskData;
		}
	}
	
	
	
	//TODO move to
	public abstract class LabeledInputRange extends HorizontalPanel{
		private InputRangeWidget inputRange;
		public LabeledInputRange(final String header,int min,int max,int current,String headerWidth,String rangeWidth){
			
			
			final Label scaleLabel=new Label();
			this.add(scaleLabel);
			scaleLabel.setWidth(headerWidth);
			
			inputRange = HTML5InputRange.createInputRange(min, max, current);
			inputRange.setWidth(rangeWidth);
			this.add(inputRange);
			inputRange.addInputRangeListener(new InputRangeListener() {
				@Override
				public void changed(int newValue) {
					scaleLabel.setText(header+generateValueText(newValue));
					onValueChanged(newValue);
				}
			});
			
			scaleLabel.setText(header+generateValueText(current));
		}
		public abstract void onValueChanged(int newValue);
		public abstract String generateValueText(int newValue);
		public int getValue(){
			return inputRange.getValue();
		}
	}
	
	
	
	
	public static class PoissonImageDataEditor extends VerticalPanel implements Editor<PoissonImageData>,ValueAwareEditor<PoissonImageData>{
		ImageMaskDataEditor imageMaskDataEditor;
		ImagePosScaleAngleEditor posScaleAngleDataEditor;
		
		@Ignore
		public ImagePosScaleAngleEditor getPosScaleAngleDataEditor() {
			return posScaleAngleDataEditor;
		}
		SimpleEditor<ImageElement> overrayImageEditor;
		private Canvas layerCanvas=Canvas.createIfSupported();
		private int possitionIteration=0;
		public int getPossitionIteration() {
			return possitionIteration;
		}
		public void setPossitionIteration(int possitionIteration) {
			this.possitionIteration = possitionIteration;
		}
		public PoissonImageDataEditor(final PoissonExecuter executer){
			
			HorizontalPanel h=new HorizontalPanel();
			add(h);
			imageMaskDataEditor=new ImageMaskDataEditor(){
				@Override
				public void updateCanvas() {
					//copy size
					if(layerCanvas.getCoordinateSpaceWidth()!=getCanvas().getCoordinateSpaceWidth() || layerCanvas.getCoordinateSpaceHeight()!=getCanvas().getCoordinateSpaceHeight()){
						CanvasUtils.createCanvas(layerCanvas, getCanvas().getCoordinateSpaceWidth(), getCanvas().getCoordinateSpaceHeight());
					}
					CanvasUtils.fillRect(layerCanvas, "rgba(0,0,0,1)");
					layerCanvas.getContext2d().save();
					layerCanvas.getContext2d().setGlobalCompositeOperation(Composite.XOR);
					layerCanvas.getContext2d().drawImage(getCanvas().getCanvasElement(), 0, 0);
					layerCanvas.getContext2d().restore();
					posScaleAngleDataEditor.updateImage(); //call do overlay
				}
				
			}
			;
			h.add(imageMaskDataEditor);
			
			posScaleAngleDataEditor=new ImagePosScaleAngleEditor(){
				public void doOverLayer(Canvas canvas) {
					imageMaskDataEditor.backgroundDataUrl=canvas.toDataUrl();
					CanvasUtils.setBackgroundImage(imageMaskDataEditor.getCanvas(), imageMaskDataEditor.backgroundDataUrl,imageMaskDataEditor.getScaledCanvasWidth(),imageMaskDataEditor.getScaledCanvasHeight());//link image
					
					canvas.getContext2d().save();
					canvas.getContext2d().setGlobalAlpha(0.5);
					canvas.getContext2d().drawImage(layerCanvas.getCanvasElement(), 0, 0);
					canvas.getContext2d().restore();
					if(possitionIteration!=0){
					executer.doPoisson(possitionIteration,false);//for preview,//this make slow
					}
				}
			};
			h.add(posScaleAngleDataEditor);
			
			overrayImageEditor=SimpleEditor.of();
		}
		@Override
		public void setDelegate(EditorDelegate<PoissonImageData> delegate) {
			//no error check
		}
		@Override
		public void flush() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onPropertyChange(String... paths) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void setValue(PoissonImageData value) {
			
			//based on image size  & set value
			
			ImageElement baseImage=value.getImageMaskData().getImageElement();
			posScaleAngleDataEditor.setCanvasSize(baseImage.getWidth(), baseImage.getHeight());
			posScaleAngleDataEditor.setImageElement(value.getOverrayImage());
			
			//because of heavy imageupdate on posscaleeditor,set value abandoned 
			overrayImageEditor.setValue(value.getOverrayImage());
		}
	}
	
	
	@Override
	public String getAppName() {
		return "PoisonImageEditor";
	}

	@Override
	public String getAppVersion() {
		return "1.0.1";
	}

	@Override
	public String getAppUrl() {
		return "http://android.akjava.com/html5apps/index.html#poison";
	}

	@Override
	public void doDropFile(File file, String dataUrl) {
		//add to cell
	}

	@Override
	public Predicate<File> getDoDropFilePredicate() {
		return FilePredicates.getImageExtensionOnly();
	}

	@Override
	public int getEastPanelWidth() {
		return 200;
	}

	@Override
	public Panel getEastPanel() {
		VerticalPanel panel=new VerticalPanel();
		//cell & cell
		return panel;
	}
	private LabeledInputRange previewIterationRange;
	
	private ImageElement destImageElement,srcImageElement;
	private CheckBox maskSrcCheck;

	private CheckBox mixSrcCheck;

	private LabeledInputRange mixSrcRange;
	@Override
	public Panel getCenterPanel() {
		
		
		ScrollPanel scroll=new ScrollPanel();
		final VerticalPanel panel=new VerticalPanel();
		scroll.add(panel);
		
		
		
		editor = new PoissonImageDataEditor(this);    
		driver.initialize(editor);
		
		VerticalPanel editorPanel=new VerticalPanel();
		
		final String destImage="vinci_dst.png";
		final String srcImage="vinci_src.png";
		
		new ImageElementLoader().load(destImage, new ImageElementListener() {
			
			@Override
			public void onLoad(final ImageElement element) {
				destImageElement=element;
				mixCanvas.setDest(destImageElement);
				new ImageElementLoader().load(srcImage, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element2) {
						srcImageElement=element2;
						driver.edit(new PoissonImageData(element,element2));
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						LogUtils.log("load-faild:"+url);
					}
				});
				
				
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				LogUtils.log("load-faild:"+url);
			}
		});
		
		
		HorizontalPanel h=new HorizontalPanel();
		panel.add(h);
		h.setSpacing(16);
		h.add(new Label("dest"));
		
		FileUploadForm destUpLoad=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				
				ImageElementUtils.createWithLoader(text, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						destImageElement=element;
						mixCanvas.setDest(destImageElement);
						PoissonImageData newData=new PoissonImageData(destImageElement,srcImageElement);
						
						driver.edit(newData);
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		}, true);
		h.add(destUpLoad);
		
		h.add(new Label("src"));
		
		FileUploadForm srcUpLoad=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, final String text) {
				ImageElementUtils.createWithLoader(text, new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						ImageMaskData maskData=null;
						try{
							PoissonImageData oldData=driver.flush();
							maskData=oldData.getImageMaskData();
						}catch (Exception e) {
							//it happen on initial
						}
						srcImageElement=element;
						PoissonImageData newData=new PoissonImageData(destImageElement,srcImageElement);
						
						if(maskData!=null){
							newData.getImageMaskData().setImageData(ImageDataUtils.copy(sharedCanvas,maskData.getImageData()));
						}
						driver.edit(newData);
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
				
				
			}
		}, true);
		h.add(srcUpLoad);
		
		
		
		editorPanel.add(editor);
		
		int initialValue=GWT.isScript()?5:0;
		
		previewIterationRange = new LabeledInputRange("Preview-Iteration:",0,100,initialValue,"140px","400px") {
			
			@Override
			public void onValueChanged(int newValue) {
				editor.setPossitionIteration(newValue);
			}
			
			@Override
			public String generateValueText(int newValue) {
				return ""+newValue;
			}
		};
		editorPanel.add(previewIterationRange);
		editor.setPossitionIteration(initialValue);
		
		/**
		 * over iteration around 500,seems no effects.
		 */
		updateIterationRange = new LabeledInputRange("Update-Iteration:",1,600,100,"140px","600px") {
			
			@Override
			public void onValueChanged(int newValue) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String generateValueText(int newValue) {
				return ""+newValue;
			}
		};
		editorPanel.add(updateIterationRange);
		
		
		//containers = new VerticalPanel();
		
			
	    	
	    	Button updateBt=new ExecuteButton("Update"){
				@Override
				public void executeOnClick() {
					doPoisson(updateIterationRange.getValue(),inpaintCheck.getValue());
				}
	    	};
	    editorPanel.add(updateBt);
	    
	    inpaintCheck = new CheckBox("use inpaint on src");
	    inpaintCheck.setTitle("poisson not good at clipeed image on edge.so do inpaint to try erase edge.");
	    editorPanel.add(inpaintCheck);
	    
	    maskSrcCheck = new CheckBox("mask src only");
	    maskSrcCheck.setTitle("mask area only");
	    editorPanel.add(maskSrcCheck);
	    
	    HorizontalPanel mixSrcPanel=new HorizontalPanel();
	    mixSrcPanel.setSpacing(2);
	    mixSrcCheck = new CheckBox("mix src image:");
	    mixSrcCheck.setTitle("draw masked src image with global alpha");
	    mixSrcPanel.add(mixSrcCheck);
	    
	    mixSrcRange = new LabeledInputRange("global-alpha:",1,100,50,"100px","200px") {
			
			@Override
			public void onValueChanged(int newValue) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String generateValueText(int newValue) {
				return ""+newValue;
			}
		};
		mixSrcPanel.add(mixSrcRange);
	    
	    
	    editorPanel.add(mixSrcPanel);
	    
	    panel.add(editorPanel);
	    
	    HorizontalPanel h2=new HorizontalPanel();
		panel.add(h2);
		
		mixCanvas = new SimpleMixCanvas();
		
		h2.add(mixCanvas);
		
		
		TabPanel tab2=new TabPanel();
		
		previewCanvas=Canvas.createIfSupported();
		tab2.add(previewCanvas,"Preview");
		tab2.selectTab(0);
	    h2.add(tab2);
	    
	    return scroll;
	}
	

	boolean possing;
	private SimpleMixCanvas mixCanvas;
	private LabeledInputRange updateIterationRange;
	private PoissonImageDataEditor editor;
	
	/*
	public static interface ButtonExecuteListener {
		public void executeOnClick();
	}
	*/

	
	public abstract class AsyncMultiImageElementLoader extends AsyncMultiCaller<String>{
		private List<ImageElement> imageElements=Lists.newArrayList();
		private List<String> faildPaths=Lists.newArrayList();
		public AsyncMultiImageElementLoader(List<String> datas) {
			super(datas);
		}
		@Override
		public void onStart(){
			imageElements.clear();
			faildPaths.clear();
		}
		public void onFaild(String data){
			faildPaths.add(data);
		}
		
		@Override
		public void execAsync(final String data) {
			new ImageElementLoader().load(data, new ImageElementListener() {
				
				@Override
				public void onLoad(ImageElement element) {
					done(data,true);
					imageElements.add(element);
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					
					done(data,false);
				}
			});
			
		}
		
		@Override
		public void doFinally(boolean cancelled) {
			if(!cancelled){
				onAllImageLoaded(Optional.<List<ImageElement>>of(Lists.newArrayList(imageElements)),Lists.newArrayList(faildPaths));
			}else{
				onAllImageLoaded(Optional.<List<ImageElement>>absent(),Lists.newArrayList(faildPaths));
			}
		}
		
		public abstract void onAllImageLoaded(Optional<List<ImageElement>> imageElements,List<String> faildPaths);
	};
	
	private Canvas previewCanvas;
	private Canvas sharedCanvas=Canvas.createIfSupported();
	private CheckBox inpaintCheck;
	public void doPoisson(final int iteration,boolean doInpaint){
		
		if(possing){
			LogUtils.log("poissing.quit");
			return;
		}
		LogUtils.log("poison:"+iteration);
		try{
		possing=true;

		
		
		
		
		PoissonImageData poissonData=driver.flush();//possible called before data set and make that broken
		
		if(poissonData.getOverrayImage()==null){//not initialized
			LogUtils.log("overrayImage is null");
			possing=false;
			return;
		}
		
		CanvasTools canvasTools=CanvasTools.from(editor.imageMaskDataEditor.getCanvas());
		
		final Rect transParentAreaRect=canvasTools.getTransparentArea(128, 16, 16);
		//LogUtils.log(transParentAreaRect);
		
		if(transParentAreaRect.getWidth()==0 || transParentAreaRect.getHeight()==0){
			LogUtils.log("no rect,quit");
			possing=false;
			return;
		}
		
		ImageData maskBaseImageData=canvasTools.getImageData(transParentAreaRect);
		
		
		
		//extremlly slow
		
		Uint8Array masks=InPaint.createMaskByAlpha(maskBaseImageData);
		//TODO method
		Canvas maskCanvas=CanvasUtils.createCanvas(sharedCanvas,maskBaseImageData.getWidth(), maskBaseImageData.getHeight());
		CanvasUtils.clear(maskCanvas);//clear here
		final ImageData maskData=CanvasUtils.getImageData(maskCanvas, false);
		InPaint.createImageDataFromMask(maskData, masks, 255, 255, 255, 255, false);
		
		/*//add mask to show
		maskCanvas.getContext2d().putImageData(newData, 0, 0);
		maskCanvas.getElement().getStyle().setBackgroundColor("#000");
		containers.add(maskCanvas);//mask
		*/
		
		
		/*
		Canvas canvas=CanvasUtils.copyTo(data.getImageMaskData().getImageData(), null);
		canvas.getElement().getStyle().setBackgroundColor("#000");
		containers.add(canvas);//created
		*/
		
		//CanvasUtils.createCanvas(ImageElement)
		ImageElementUtils.copytoCanvas(poissonData.getImageMaskData().getImageElement(), previewCanvas);
		final CanvasTools resultCanvas=CanvasTools.from(previewCanvas);
		final ImageData destData=resultCanvas.getImageData(transParentAreaRect);
		final ImageData resultData=resultCanvas.getImageData(transParentAreaRect);
		
		
		//set
		//create method size only?
		final Canvas srcCanvas=CanvasUtils.createCanvas(poissonData.getImageMaskData().getImageElement().getWidth(),poissonData.getImageMaskData().getImageElement().getHeight());
		PositionScaleAngleData psa=poissonData.getPosScaleAngleData();
		CanvasUtils.drawCenter(srcCanvas, poissonData.getOverrayImage(),(int)psa.getPositionX(),(int)psa.getPositionY(),psa.getScale(),psa.getScale(),psa.getAngle(),1);
		
		if(doInpaint){
			
			
			new LoggingImageElementLoader(){
				@Override
				public void onLoad(ImageElement imageElement) {
					InpaintEngine engine=new InpaintEngine();
					int fade=0;
					int expand=0;
					int inpaintRadius=1;
					List<MaskData> inpaintmasks=Lists.newArrayList(new MaskData().fade(fade).expand(expand));
					engine.doInpaint(imageElement, inpaintRadius, inpaintmasks, new InpaintListener() {
						
						@Override
						public void createMixedImage(ImageData dataUrl) {
							
							Canvas inpaintedCanvas=CanvasUtils.copyTo(dataUrl, null);
							
							//TODO make method
							ImageData srcData=CanvasTools.from(inpaintedCanvas).getImageData(transParentAreaRect);
							
							/*
							ImageData srcData=CanvasUtils.getImageData(editor.getPosScaleAngleDataEditor().getCanvas(),true);
							CanvasUtils.copyTo(srcData, srcCanvas);
							*/
							//containers.add(srcCanvas);
							
							//mixCanvas.setMixed(srcCanvas,data.getImageMaskData().getImageData());
						
							
							//data.getPosScaleAngleData()
							
							//what is problem?
							//CanvasUtils.createCanvas(ImageData imageData);
							/*
							containers.add(CanvasUtils.createCanvas(null, srcData));
							containers.add(CanvasUtils.createCanvas(null, destData));
							containers.add(CanvasUtils.createCanvas(null, maskData));
							containers.add(CanvasUtils.createCanvas(null, resultData));
							*/
							
							Poisson.setImageDatas(srcData,destData, maskData,resultData);
							ImageData poisoned=Poisson.blend(iteration, 0, 0);
							
							//containers.add(CanvasUtils.createCanvas(null, poisoned));
							
							resultCanvas.putImageData(transParentAreaRect, poisoned);
							
							
							//CanvasUtils.copyTo(poisoned, pCanvas);
							
							if(iteration==updateIterationRange.getValue()){
							mixCanvas.setPoissonedImage(ImageElementUtils.create(previewCanvas.toDataUrl()));
							mixCanvas.setShowOrigin(false);
							mixCanvas.setUseMask(maskSrcCheck.getValue());
							mixCanvas.setMaskCanvas(srcCanvas);
							mixCanvas.update();
							}else{
								//only preview -mode
							}
							//containers.add(previewCanvas);
							
							possing=false;
						}
						
						@Override
						public void createInpainteMaks(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void createInpaintImage(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void createGreyScaleMaks(ImageData dataUrl) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				
			}.load(srcCanvas);
			//new LoggingImageElementLoader(){}.load("url");
			
			
			
			
			
			
		}else{
			//LogUtils.log("not inpaint");
			ImageData srcData=CanvasTools.from(srcCanvas).getImageData(transParentAreaRect);
			
			/*
			ImageData srcData=CanvasUtils.getImageData(editor.getPosScaleAngleDataEditor().getCanvas(),true);
			CanvasUtils.copyTo(srcData, srcCanvas);
			*/
			//containers.add(srcCanvas);
			
			//mixCanvas.setMixed(srcCanvas,data.getImageMaskData().getImageData());
		
			
			//data.getPosScaleAngleData()
			
			//what is problem?
			//CanvasUtils.createCanvas(ImageData imageData);
			/*
			containers.add(CanvasUtils.createCanvas(null, srcData));
			containers.add(CanvasUtils.createCanvas(null, destData));
			containers.add(CanvasUtils.createCanvas(null, maskData));
			containers.add(CanvasUtils.createCanvas(null, resultData));
			*/
			
			Poisson.setImageDatas(srcData,destData, maskData,resultData);
			ImageData poisoned=Poisson.blend(iteration, 0, 0);
			
			//containers.add(CanvasUtils.createCanvas(null, poisoned));
			
			
			
			if(mixSrcCheck.getValue()){
			
				for(int x=0;x<maskData.getWidth();x++){
					for(int y=0;y<maskData.getHeight();y++){
						
						srcData.setAlphaAt(maskData.getAlphaAt(x, y), x, y);
					}
				}
				
				
				double globalAlpha=(double)mixSrcRange.getValue()/100;
				Canvas srcCanvas2=CanvasUtils.createFrom(srcData);
				Canvas poisonedCanvas=CanvasUtils.createFrom(poisoned);
				poisonedCanvas.getContext2d().setGlobalAlpha(globalAlpha);
				poisonedCanvas.getContext2d().drawImage(srcCanvas2.getCanvasElement(), 0, 0);
				poisoned=ImageDataUtils.copyFrom(poisonedCanvas);
				}
			
			
			
			resultCanvas.putImageData(transParentAreaRect, poisoned);
			
			//TODO methods createFrom()
			//Canvas destCanvas=CanvasUtils.createCanvas(destData.getWidth(), destData.getHeight());
			//destCanvas.getContext2d().putImageData(destData, 0, 0);
			
			//white is mask
			//copy to alpha
			
			//CanvasUtils.copyTo(poisoned, pCanvas);
			
			if(iteration==updateIterationRange.getValue()){
				new LoggingImageElementLoader(){
					@Override
					public void onLoad(ImageElement imageElement) {
						mixCanvas.setPoissonedImage(imageElement);
						mixCanvas.setUseMask(maskSrcCheck.getValue());
						mixCanvas.setMaskCanvas(srcCanvas);
						mixCanvas.setShowOrigin(false);
						mixCanvas.update();
					}
					
				}.load(previewCanvas);
			}else{
				//only preview -mode
			}
			//containers.add(previewCanvas);
			
			possing=false;
		}
		
		
		
		}catch (Exception e) {
			LogUtils.log(e.getMessage());
			possing=false;
		}
	}
	
	public static class PoissonWithInpaintExecuter{
		private ImageData srcData;
		private Canvas canvas;
		public PoissonWithInpaintExecuter(ImageData srcData, ImageData destData, ImageData maskData){
			this(srcData, destData, maskData, null);
		}
		public PoissonWithInpaintExecuter(ImageData srcData, ImageData destData, ImageData maskData,@Nullable ImageData resultData) {
			super();
			this.srcData = checkNotNull(srcData,"PoissonWithInpaintExecuter:need src");
			this.destData = checkNotNull(destData,"PoissonWithInpaintExecuter:need destData");
			this.maskData = checkNotNull(maskData,"PoissonWithInpaintExecuter:need maskData");;
			this.resultData = resultData!=null?resultData:ImageDataUtils.copy(getCanvas(), destData);
		}

		private Canvas getCanvas() {
			if(canvas==null){
				canvas=Canvas.createIfSupported();
			}
			return canvas;
		}

		private ImageData destData;
		private ImageData maskData;
		private ImageData resultData;
		private boolean inpaintSrc;
		private boolean inpaintDesc;
		private boolean readySrc;
		private boolean readyDest;
		private boolean inpainting;
		
		public void execute(final PoissonListener listener,final int iteration){
			readySrc=inpaintSrc?false:true;
			readyDest=inpaintDesc?false:true;
			inpainting=false;
			Timer timer=new Timer(){

				@Override
				public void run() {
					if(inpainting){
						return;
					}
					
					if(!readySrc){
						inpainting=true;
						inPaintTransparentArea(srcData,1,0,0,new InpaintListener() {
							
							@Override
							public void createMixedImage(ImageData imageData) {
								srcData=imageData;
								readySrc=true;
								inpainting=false;
							}
							
							@Override
							public void createInpainteMaks(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void createInpaintImage(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void createGreyScaleMaks(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
						});
						return;
					}else if(!readyDest){
						inpainting=true;
						inPaintTransparentArea(destData,1,0,0,new InpaintListener() {
							
							@Override
							public void createMixedImage(ImageData imageData) {
								destData=imageData;
								readyDest=true;
								inpainting=false;
							}
							
							@Override
							public void createInpainteMaks(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void createInpaintImage(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void createGreyScaleMaks(ImageData imageData) {
								// TODO Auto-generated method stub
								
							}
						});
						return;
						
						
						
					}
					
					Poisson.setImageDatas(srcData,destData, maskData,resultData);
					ImageData poisoned=Poisson.blend(iteration, 0, 0);
					
					
					
					listener.onPoissoned(poisoned);
					cancel();
					
				}
				
			};
			timer.scheduleRepeating(50);
		}
	}
	
	//TODO move to imageData?
	/**
	 * draw only not-alpha on maskdata
	 * @param imageData
	 * @param maskData
	 * @return
	 */
	public static ImageData sourceInImageData(ImageData imageData,ImageData maskData){
		//check & warn
		Canvas imageCanvas=CanvasUtils.copyTo(imageData, null);
		Canvas maskCanvas=CanvasUtils.copyTo(maskData, null);
		
		Canvas canvas=CanvasUtils.copyToSizeOnly(imageCanvas, null);
		canvas.getContext2d().drawImage(maskCanvas.getCanvasElement(), 0, 0);
	
		canvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_IN);
		canvas.getContext2d().drawImage(imageCanvas.getCanvasElement(),0,0);
		
		imageCanvas=null;
		maskCanvas=null;
		
		ImageData data=ImageDataUtils.copyFrom(canvas);
		canvas=null;
		return data;
	}
	
	public static  void inPaintTransparentArea(ImageData data,final int inpaintRadius,final int expand,final int fade,final InpaintListener listener){
		Canvas canvas=CanvasUtils.copyTo(data, null);
		new LoggingImageElementLoader(){
			@Override
			public void onLoad(ImageElement imageElement) {
				InpaintEngine engine=new InpaintEngine();
				
				List<MaskData> inpaintmasks=Lists.newArrayList(new MaskData().fade(fade).expand(expand));
				engine.doInpaint(imageElement, inpaintRadius, inpaintmasks,listener);
			}
		}.load(canvas);
	}
	
	public static interface PoissonListener{
		public void onPoissoned(ImageData data);
	}
	

	public ImageData from(Canvas canvas,ImageElement element){
		ImageElementUtils.copytoCanvas(element, canvas);
		return canvas.getContext2d().getImageData(0, 0, element.getWidth(), element.getHeight());
	}
	
	@Override
	public String getHelpUrl() {
		return "poison_help.html";
	}
	

}
