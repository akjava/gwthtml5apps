package com.akjava.gwt.androidhtml5.client;

import java.util.List;

import com.akjava.gwt.androidhtml5.client.ImageMaskDataEditor.ImageMaskData;
import com.akjava.gwt.androidhtml5.client.ImagePosScaleAngleEditor.PositionScaleAngleData;
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
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.AsyncMultiCaller;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public  class PoissonImage extends AbstractDropEastDemoEntryPoint implements PoissonExecuter{

	 interface Driver extends SimpleBeanEditorDriver< PoissonImageData,  PoissonImageDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	private VerticalPanel containers;



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
		private int possitionIteration=20;
		public int getPossitionIteration() {
			return possitionIteration;
		}
		public void setPossitionIteration(int possitionIteration) {
			this.possitionIteration = possitionIteration;
		}
		public PoissonImageDataEditor(final PoissonExecuter executer){
			imageMaskDataEditor=new ImageMaskDataEditor(){
				@Override
				public void updateCanvas() {
					//copy size
					CanvasUtils.createCanvas(layerCanvas, getCanvas().getCoordinateSpaceWidth(), getCanvas().getCoordinateSpaceHeight());
					CanvasUtils.fillRect(layerCanvas, "rgba(0,0,0,1)");
					layerCanvas.getContext2d().save();
					layerCanvas.getContext2d().setGlobalCompositeOperation(Composite.XOR);
					layerCanvas.getContext2d().drawImage(getCanvas().getCanvasElement(), 0, 0);
					layerCanvas.getContext2d().restore();
					posScaleAngleDataEditor.updateImage();
				}
			}
			;
			this.add(imageMaskDataEditor);
			
			posScaleAngleDataEditor=new ImagePosScaleAngleEditor(){
				public void doOverLayer(Canvas canvas) {
					canvas.getContext2d().save();
					canvas.getContext2d().setGlobalAlpha(0.5);
					canvas.getContext2d().drawImage(layerCanvas.getCanvasElement(), 0, 0);
					canvas.getContext2d().restore();
					
					executer.doPoisson(possitionIteration);//for preview,//this make slow
				}
			};
			this.add(posScaleAngleDataEditor);
			
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
		return "1.0";
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
		return 400;
	}

	@Override
	public Panel getEastPanel() {
		VerticalPanel panel=new VerticalPanel();
		//cell & cell
		return panel;
	}
	private LabeledInputRange previewIterationRange;
	
	private ImageElement destImageElement,srcImageElement;
	@Override
	public Panel getCenterPanel() {
		ScrollPanel scroll=new ScrollPanel();
		final VerticalPanel panel=new VerticalPanel();
		scroll.add(panel);
		//editor & editor
		final PoissonImageDataEditor editor=new PoissonImageDataEditor(this);    
		driver.initialize(editor);
		
		VerticalPanel editorPanel=new VerticalPanel();
		
		final String destImage="vinci_dst.png";
		final String srcImage="test.png";
		
		new ImageElementLoader().load(destImage, new ImageElementListener() {
			
			@Override
			public void onLoad(final ImageElement element) {
				destImageElement=element;
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
		
		
		panel.add(new Label("dest"));
		
		FileUploadForm destUpLoad=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				destImageElement=ImageElementUtils.create(text);
				driver.edit(new PoissonImageData(destImageElement,srcImageElement));
			}
		}, true);
		panel.add(destUpLoad);
		
		panel.add(new Label("src"));
		
		FileUploadForm srcUpLoad=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				srcImageElement=ImageElementUtils.create(text);
				driver.edit(new PoissonImageData(destImageElement,srcImageElement));
			}
		}, true);
		panel.add(srcUpLoad);
		
		
		editorPanel.add(editor);
		
		
		previewIterationRange = new LabeledInputRange("Preview-Iteration:",1,100,20,"140px","400px") {
			
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
		
		final LabeledInputRange updateIterationRange = new LabeledInputRange("Update-Iteration:",20,400,100,"140px","400px") {
			
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
		
		
		containers = new VerticalPanel();
		
			
	    	
	    	Button updateBt=new ExecuteButton("Update"){
				@Override
				public void executeOnClick() {
					doPoisson(updateIterationRange.getValue());
				}
	    	};
	    	
	    
	    editorPanel.add(updateBt);
	    
	    panel.add(editorPanel);
	    
	    panel.add(containers);
	    
	    return scroll;
	}
	

	boolean possing;
	
	/*
	public static interface ButtonExecuteListener {
		public void executeOnClick();
	}
	*/
	public abstract class ExecuteButton extends Button{
		public ExecuteButton(String label){
			super(label);
			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					setEnabled(false);
					Timer timer=new Timer(){
						@Override
						public void run() {
							try{
								executeOnClick();
							}catch(Exception e){
								new RuntimeException(e);//i dont care
							}finally{
								setEnabled(true);
							}
						}
						
					};
					timer.schedule(20);
				}
			});
		}
		public abstract void executeOnClick();
	}
	
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
	
	
	public void doPoisson(int iteration){
		if(possing){
			return;
		}
		try{
		possing=true;

		containers.clear();
		
		
		PoissonImageData data=driver.flush();//possible called before data set and make that broken
		
		if(data.getOverrayImage()==null){
			LogUtils.log("overrayImage is null");
			possing=false;
			return;
		}
		
		ImageData imageData=data.getImageMaskData().getImageData();
		//extremlly slow
		LogUtils.log("create mask");
		Uint8Array masks=InPaint.createMaskByAlpha(imageData);
		//TODO method
		Canvas maskCanvas=CanvasUtils.createCanvas(imageData.getWidth(), imageData.getHeight());
		
		ImageData newData=CanvasUtils.getImageData(maskCanvas, false);
		LogUtils.log("create imagedata");
		InPaint.createImageDataFromMask(newData, masks, 255, 255, 255, 255, false);
		
		maskCanvas.getContext2d().putImageData(newData, 0, 0);
		maskCanvas.getElement().getStyle().setBackgroundColor("#000");
		//containers.add(maskCanvas);//mask
		
		
		Canvas canvas=CanvasUtils.copyTo(data.getImageMaskData().getImageData(), null);
		canvas.getElement().getStyle().setBackgroundColor("#000");
		//containers.add(canvas);//created
		
		
		
		Canvas pCanvas=Canvas.createIfSupported();
		
		ImageData resultData=ImageDataUtils.create(pCanvas,data.getImageMaskData().getImageElement());
		
		
		//set
		//create method size only?
		Canvas srcCanvas=CanvasUtils.createCanvas(data.getImageMaskData().getImageElement().getWidth(),data.getImageMaskData().getImageElement().getHeight());
		
		PositionScaleAngleData psa=data.getPosScaleAngleData();
		
		CanvasUtils.drawCenter(srcCanvas, data.getOverrayImage(),(int)psa.getPositionX(),(int)psa.getPositionY(),psa.getScale(),psa.getScale(),psa.getAngle(),1);
		
		ImageData srcData=CanvasUtils.getImageData(srcCanvas,true);
		
		/*
		ImageData srcData=CanvasUtils.getImageData(editor.getPosScaleAngleDataEditor().getCanvas(),true);
		CanvasUtils.copyTo(srcData, srcCanvas);
		*/
		//containers.add(srcCanvas);
		
		//data.getPosScaleAngleData()
		Poisson.setImageDatas(srcData,from(pCanvas,data.getImageMaskData().getImageElement()), newData,resultData);
		ImageData poisoned=Poisson.blend(iteration, 0, 0);
		CanvasUtils.copyTo(poisoned, pCanvas);
		containers.add(pCanvas);
		possing=false;
		}catch (Exception e) {
			LogUtils.log(e.getMessage());
			possing=false;
		}
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
