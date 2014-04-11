package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;

import com.akjava.gwt.androidhtml5.client.data.ImageUrlData;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.lib.client.CanvasResizer;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class SimpleResize extends Html5DemoEntryPoint {

	private Canvas canvas;
	
	private Image mainImage;
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableObjects<ImageUrlDataResizeInfo> EasyCellTableObjects;

	private ValueListBox<String> sizesList;
	


	private CheckBox highQualityCheck;
	
	public static final int TYPE_SAME=0;
	public static final int TYPE_PNG=1;
	public static final int TYPE_JPEG=2;
	public static final int TYPE_WEBP=3;
	private int exportType;

	private RadioButton widthButton;

	private RadioButton heightButton;
	
	private final int SIZE_WIDTH=0;
	private final int SIZE_HEIGHT=1;
	private int sizeMode;
	
	@Override
	public Panel initializeWidget() {
		
DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					SimpleResize.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		
		
		canvas = Canvas.createIfSupported();
		//canvas.setSize("100%", "100%");
		
		
		
		dock = new DockLayoutPanel(Unit.PX);
		//dock.setSize("100%", "100%");
		root.add(dock);
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(4);
		dock.addNorth(topPanel,40);
		
		
		topPanel.add(createTitleWidget());
		
		sizesList = new ValueListBox<String>(new Renderer<String>() {
			@Override
			public String render(String value) {
				return value;
			}

			@Override
			public void render(String object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
			}
		});
		
		canvas.setCoordinateSpaceHeight(360);
		canvas.setCoordinateSpaceWidth(360);
		
		VerticalPanel controler=new VerticalPanel();
		
		HorizontalPanel panel1=new HorizontalPanel();
		controler.add(panel1);
		panel1.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		panel1.setSpacing(2);
		//topPanel.add(controler);
		
		//panel1.add(new Label("Width:"));
		
		widthButton = new RadioButton("size","width");
		widthButton.setValue(true);
		widthButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(widthButton.getValue()){
					sizeMode=SIZE_WIDTH;
				}
			}
		});
		panel1.add(widthButton);
		
		heightButton = new RadioButton("size","height");
		heightButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(heightButton.getValue()){
					sizeMode=SIZE_HEIGHT;
				}
			}
		});
		panel1.add(heightButton);
		
		
		sizesList.setValue("360");
		sizesList.setAcceptableValues(Lists.newArrayList("x 0.1","x 0.25","x 0.5","x 1","x 2","x 4","90","180","360","480","640","720","800","920","1280"));
		panel1.add(sizesList);
		sizesList.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				try{
					
				//canvas.setCoordinateSpaceHeight(event.getValue());
				//canvas.setCoordinateSpaceWidth(event.getValue());
				
				}catch(Exception e){
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		});
		
		HorizontalPanel panel2=new HorizontalPanel();
		controler.add(panel2);
		
		highQualityCheck = new CheckBox("HQ");
		highQualityCheck.setValue(true);//defaut true
		panel2.add(highQualityCheck);
		
		final RadioButton typeSame=new RadioButton("type","same");
		typeSame.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(typeSame.getValue()){
					exportType=TYPE_SAME;
				}
			}
		});
		typeSame.setValue(true);
		panel2.add(typeSame);
		
		final RadioButton typePng=new RadioButton("type","png");
		panel2.add(typePng);
		typePng.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(typePng.getValue()){
					exportType=TYPE_PNG;
				}
			}
		});
		
		final RadioButton typeJPG=new RadioButton("type","jpg");
		panel2.add(typeJPG);
		typeJPG.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(typeJPG.getValue()){
					exportType=TYPE_JPEG;
				}
			}
		});
		
		final RadioButton typeWebp=new RadioButton("type","webp");
		panel2.add(typeWebp);
		typeWebp.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(typeWebp.getValue()){
					exportType=TYPE_WEBP;
				}
			}
		});
		
		topPanel.add(new Anchor("Help", "resize_help.html"));
		
		//dock.add(canvas);
		//canvas.removeFromParent();
		//dock.add(canvas);
		//RootLayoutPanel.get().add(dock);
		
		FileUploadForm fileUp=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String asStringText) {
				loadFile(file,asStringText);
			}
		}, false);
		panel1.add(fileUp);

		
		SimpleCellTable<ImageUrlDataResizeInfo> cellTable = new SimpleCellTable<ImageUrlDataResizeInfo>(999) {
			@Override
			public void addColumns(CellTable<ImageUrlDataResizeInfo> table) {
				 ButtonColumn<ImageUrlDataResizeInfo> removeBtColumn=new ButtonColumn<ImageUrlDataResizeInfo>() {
						@Override
						public void update(int index, ImageUrlDataResizeInfo object,
								String value) {
								EasyCellTableObjects.removeItem(object);
						}
						@Override
						public String getValue(ImageUrlDataResizeInfo object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					
					HtmlColumn<ImageUrlDataResizeInfo> downloadColumn=new HtmlColumn<ImageUrlDataResizeInfo> (new SafeHtmlCell()){
						@Override
						public String toHtml(ImageUrlDataResizeInfo data) {
							return data.getDownloadLink().toString();
						}
						
					};
					table.addColumn(downloadColumn);
					
				    TextColumn<ImageUrlDataResizeInfo> fileInfoColumn = new TextColumn<ImageUrlDataResizeInfo>() {
					      public String getValue(ImageUrlDataResizeInfo value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					    /*
					    TextColumn<ImageUrlDataResizeInfo> sizeModeColumn = new TextColumn<ImageUrlDataResizeInfo>() {
						      public String getValue(ImageUrlDataResizeInfo value) {
						    	  
						    	  if(value.getSizeMode()==SIZE_WIDTH){
						    		  return "W";
						    	  }else if(value.getSizeMode()==SIZE_HEIGHT){
						    		  return "H";
						    	  }else{
						    		  return "";
						    	  }
						      }
						    };
						    table.addColumn(sizeModeColumn,"");
						    */
						    
					    
					    TextColumn<ImageUrlDataResizeInfo> widthColumn = new TextColumn<ImageUrlDataResizeInfo>() {
						      public String getValue(ImageUrlDataResizeInfo value) {
						    	  String header="";
						    	  if(value.getSizeMode()==SIZE_WIDTH){
						    		  header= "W";
						    	  }else if(value.getSizeMode()==SIZE_HEIGHT){
						    		  header= "H";
						    	  }else{
						    		  header= "";
						    	  }
						    	 
						    	  
						    	  return header+" "+value.getWidthLabel();
						      }
						    };
						    table.addColumn(widthColumn,"size");
					    
			}
		};
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 60);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		EasyCellTableObjects=new EasyCellTableObjects<ImageUrlDataResizeInfo>(cellTable,false) {
			@Override
			public void onSelect(ImageUrlDataResizeInfo selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		mainImage = new Image();
		mainImage.setVisible(false);
		
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setWidth("100%");
		scroll.setHeight("100%");
		dock.add(scroll);
		scroll.add(mainImage);
		
		
		return root;
	}
	
	
	
	public boolean isScaleSize(String size){
		return size.indexOf("x")!=-1;
	}
	public double getScale(String size){
		int ind=size.indexOf("x");
		return ValuesUtils.toDouble(size.substring(ind+1), 1);
	}
	
	protected void loadFile(File file, String asStringText) {
		try{
		boolean hasWidth=sizeMode==SIZE_WIDTH;
			//TODO create method
		ImageElement element=ImageElementUtils.create(asStringText);
		String dataUrl;
		//boolean isJpeg=false;
		String exportMime="image/png";
		int size=0;
		double scale=0;
		String widthValue=sizesList.getValue();
		
		// something like x 2.0
		if(isScaleSize(widthValue)){
			scale=getScale(widthValue);
			if(sizeMode==SIZE_WIDTH){
				size=(int) (scale*element.getWidth());
			}else{
				size=(int) (scale*element.getHeight());
			}
			
			
		}else{//normal pixel value
			if(sizeMode==SIZE_WIDTH){
				size=ValuesUtils.toInt(widthValue,element.getWidth());
			}else{
				size=ValuesUtils.toInt(widthValue,element.getHeight());
			}	
		}
		
		if(exportType==TYPE_SAME){
			String type=FileNames.getImageType(file.getFileName());
			if(type.equals(FileNames.TYPE_JPEG)){
				exportMime="image/jpeg";
			}
		}else if(exportType==TYPE_JPEG){
			exportMime="image/jpeg";
		}else if(exportType==TYPE_WEBP){
			exportMime="image/webp";
		}
		
		boolean smallScale=false;
		if(sizeMode==SIZE_WIDTH){
			smallScale=(scale<1 || size<element.getWidth());
		}else{
			smallScale=(scale<1 || size<element.getHeight());
		}	
		
		boolean useHighQualith=highQualityCheck.getValue() && smallScale;
		
		
		if(useHighQualith){
			ImageElementUtils.copytoCanvas(asStringText, canvas);
			
			double convertScale;
			if(sizeMode==SIZE_WIDTH){
				convertScale=scale!=0?scale:(double)size/element.getWidth();
			}else{
				convertScale=scale!=0?scale:(double)size/element.getHeight();
			}	
			
			
			
			ImageData imgData=JSDownScale.downScaleCanvas(canvas.getCanvasElement(), convertScale);//TODO control it
			
			if(sizeMode==SIZE_WIDTH){
				size=imgData.getWidth();
			}else{
				size=imgData.getHeight();
			}
			
			
			
			CanvasUtils.copyTo(imgData, canvas);
			
			dataUrl=canvas.toDataUrl(exportMime);
			
		}else{
			
		if(exportType==TYPE_JPEG){
			if(sizeMode==SIZE_WIDTH){
				dataUrl=CanvasResizer.on(canvas).image(element).width(size).toJpegDataUrl();
			}else{
				dataUrl=CanvasResizer.on(canvas).image(element).height(size).toJpegDataUrl();
			}
			
		}else if(exportType==TYPE_WEBP){
			if(sizeMode==SIZE_WIDTH){
				dataUrl=CanvasResizer.on(canvas).image(element).width(size).toWebpDataUrl();
			}else{
				dataUrl=CanvasResizer.on(canvas).image(element).height(size).toWebpDataUrl();
			}
			
		}else{
			if(sizeMode==SIZE_WIDTH){
				dataUrl=CanvasResizer.on(canvas).image(element).width(size).toPngDataUrl();
			}else{
				dataUrl=CanvasResizer.on(canvas).image(element).height(size).toPngDataUrl();
			}
			
		}
		
		
		
		
		}
		
		
		
		//create new high quality image
		
		/*
		ImageElementUtils.copytoCanvas(asStringText, sharedCanvas);
		
		ImageData imgData=JSDownScale.downScaleCanvas(sharedCanvas.getCanvasElement(), 0.25);//TODO control it
		LogUtils.log("hq");
		CanvasUtils.copyTo(imgData, sharedCanvas);
		
		String dataUrl=sharedCanvas.toDataUrl();
		*/
		
		
		//mainImage.setUrl("resize/clear.cache.gif");
		//mainImage.setVisible(true);
		
		String newName;
		String type;
		if(exportType==TYPE_JPEG){
			type="jpeg";
			newName=FileNames.asSlash().getChangedExtensionName(file.getFileName(), "jpg");
		}else if(exportType==TYPE_WEBP){
			type="webp";
			newName=FileNames.asSlash().getChangedExtensionName(file.getFileName(), "webp");
		}
		else{
			type="png";
			newName=FileNames.asSlash().getChangedExtensionName(file.getFileName(), "png");
		}
		
		final ImageUrlDataResizeInfo data=new ImageUrlDataResizeInfo(newName, dataUrl,sizeMode,size,widthValue,type);
	
		EasyCellTableObjects.addItem(data);
		//updateList();
		
		
		//stack on mobile,maybe because of called async method
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				EasyCellTableObjects.setSelected(data, true);
			}
		});
		
		//doSelecct(data);//only way to update on Android Chrome
		}catch (Exception e) {
			e.printStackTrace();
			LogUtils.log(e.getMessage());
		}
	}

	public void clearImage(){
		//GWT.getModuleName()+"/"
	}
	
	public void doSelect(ImageUrlDataResizeInfo selection) {
		if(selection==null){
			mainImage.setVisible(false);
			mainImage.setUrl("");
		}else{
		mainImage.setUrl(selection.getDataUrl());
		mainImage.getElement().removeAttribute("style");
		if(selection.getSizeMode()==SIZE_WIDTH){
			mainImage.setWidth(selection.getWidth()+"px");
		}else{
			mainImage.setHeight(selection.getWidth()+"px");
		}
		LogUtils.log(mainImage);
		
		mainImage.setVisible(true);
		}
		//LogUtils.log("set-visible");
	}

	/*
	public void updateList(){
		ImageUrlDataWithWidthCellList.setData(datas);
		//ImageUrlDataWithWidthCellList.getCellTable().setFocus(true);
		//ImageUrlDataWithWidthCellList.getCellTable().redraw();
		//LogUtils.log("updateList-focus-redraw");
		//dock.forceLayout();
		//ImageUrlDataWithWidthCellList.getCellTable().flush();
		//LogUtils.log("layout");
	}
	*/
	public abstract class HtmlColumn<T> extends Column<T,SafeHtml>{

		public HtmlColumn(Cell<SafeHtml> cell) {
			super(cell);
		}

		public abstract String toHtml(T object);
		
		@Override
		public SafeHtml getValue(T object) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
	    	 sb.appendHtmlConstant(toHtml(object));
	    	 return sb.toSafeHtml();
		}
		
	}
	public static class ImageUrlDataResizeInfo extends ImageUrlData implements Supplier<Anchor>{
		private String widthLabel;
		private String type;
		private int sizeMode;
		
		public int getSizeMode() {
			return sizeMode;
		}

		public void setSizeMode(int sizeMode) {
			this.sizeMode = sizeMode;
		}
		private Supplier<Anchor> downloadLink=Suppliers.memoize(this); //if ==null return;
		public String getWidthLabel() {
			return widthLabel;
		}
		private int width;
		public int getWidth() {
			return width;
		}
		
		public ImageUrlDataResizeInfo(String fileName, String url,int sizeMode,int width,String widthLabel,String type) {
			super(fileName, url);
			this.sizeMode=sizeMode;
			this.width = width;
			this.widthLabel = widthLabel;
			this.type=type;
		}
		@Override
		public Anchor get() {
			LogUtils.log("generate:"+getFileName());
			Anchor a=HTML5Download.get().generateBase64DownloadLink(this.getDataUrl(), "image/"+type, this.getFileName(), "Download", false);
			a.setStylePrimaryName("bt");
			return a;
		}
		
		
		public Anchor getDownloadLink(){
			return downloadLink.get();
		}
		
		
	}
	/*
	public class ImageData{
		public ImageData(String fileName, String dataUrl) {
			super();
			this.fileName = fileName;
			this.dataUrl = dataUrl;
		}
		private int width;
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		private String fileName;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getDataUrl() {
			return dataUrl;
		}
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		private String dataUrl;
	}
*/


	@Override
	public String getAppName() {
		return "SimpleResize";
	}

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
		return "http://android.akjava.com/html5apps/index.html#simpleresize";
	}
	
}
