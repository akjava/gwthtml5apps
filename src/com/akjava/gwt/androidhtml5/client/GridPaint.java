package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.jsgif.client.GifAnimeBuilder;
import com.akjava.gwt.lib.client.CanvasPaintUtils;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.ColorUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
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
public class GridPaint extends Html5DemoEntryPoint {

	protected static final int PICK_AND_GRID = 2;
	protected static final int PICK_COLOR = 1;
	protected static final int SELECT_GRID = 0;
	private int mode;



	private Canvas canvas;
	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableObjects<GridImageData> easyCellTableObjects;
	private DragMoveControler moveControler;
	private ValueListBox<Integer> qualityBox;
	private ValueListBox<Integer> speedBox;

	
	@Override
	public Panel initializeWidget() {
		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					GridPaint.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		canvas = Canvas.createIfSupported();
		dock = new DockLayoutPanel(Unit.PX);
		root.add(dock);
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setStylePrimaryName("bg1");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(1);
		dock.addNorth(topPanel,30);
		
		//title
		topPanel.add(createTitleWidget());
		
		topPanel.add(new Anchor("Help", "gridpaint_help.html"));
		
		topPanel.add(createSettingAnchor());
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		HorizontalPanel bh=new HorizontalPanel();
		controler.add(bh);
		bh.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		
		
		
		
		downloadArea = new VerticalPanel();
		downloadArea.setSpacing(2);
		
		bh.add(new Label("grid:"));
		HorizontalPanel topControler=new HorizontalPanel();
		topControler.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		topControler.setSpacing(1);
		controler.add(topControler);
		
		
		
		baseSizeListBox = new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer value) {
				return ""+value;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
			}
		});
		
		baseSizeListBox.setValue(10);
		baseSizeListBox.setAcceptableValues(Lists.newArrayList(4,5,8,10,16,20,30,40,50,64));
		bh.add(baseSizeListBox);
		
		//topPanel.add(controler);
		colorBox = new ColorBox();
		colorBox.setWidth("50px");//for IE
		bh.add(colorBox);
		
		
		circleCheck = new CheckBox("Circle");
		circleCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateCanvas();
			}
		});
		bh.add(circleCheck);
		
		
		modeBox = new ListBox();
		modeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				mode=modeBox.getSelectedIndex();
				if(mode==0){
					canvas.removeStyleName("pickcolor");
				}else{
					canvas.addStyleName("pickcolor");
				}
				
			}
		});
		modeBox.addItem("Grid");
		modeBox.addItem("Pick");
		modeBox.addItem("P&Gr");
		modeBox.setSelectedIndex(0);
		bh.add(modeBox);
		
		
		Button fillBt=new Button("Fill",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fillColor();
			}
		});
		bh.add(fillBt);
		fillBt.setTitle("replace all colored block to current color");
		
		stampCheck = new CheckBox("Stamp");
		stampCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				target=null;
			}
		});
		bh.add(stampCheck);
		
		
		
		
		
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
		topControler.add(fileUp);
		
		
		

		
		
		

		SimpleCellTable<GridImageData> cellTable = new SimpleCellTable<GridPaint.GridImageData>(999) {
			@Override
			public void addColumns(CellTable<GridImageData> table) {
				
				Column<GridImageData,GridImageData> actionColumn=new ActionCellGenerator<GridImageData>(){

					@Override
					public void executeAt(int index, GridImageData object) {
						if(index==0){
							easyCellTableObjects.removeItem(object);
						}
						else if(index==1){
							easyCellTableObjects.upItem(object);
						}else{
							easyCellTableObjects.downItem(object);
						}
						
					}}.generateColumn(Lists.newArrayList("X","UP","DOWN"));
					
				
				 
					
					/*
					 ButtonColumn<GridImageData> removeBtColumn=new ButtonColumn<GridImageData>() {
						@Override
						public void update(int index, GridImageData object,
								String value) {
								easyCellTableObjects.removeItem(object);
						}
						@Override
						public String getValue(GridImageData object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					 ButtonColumn<GridImageData> saveBtColumn=new ButtonColumn<GridImageData>() {
							@Override
							public void update(int index, GridImageData object,
									String value) {
									createDownloadImage();
							}
							
							@Override
							public String getValue(GridImageData object) {
								 return "Save";
							}
						};
					table.addColumn(saveBtColumn);
					table.setColumnWidth(saveBtColumn, "50px");
					*/
					
					/*
					 ButtonColumn<GridImageData> regridBtColumn=new ButtonColumn<GridImageData>() {
							@Override
							public void update(int index, GridImageData object,
									String value) {
									regrid();
							}
							
							@Override
							public String getValue(GridImageData object) {
								 return "RegGrid";
							}
						};
					table.addColumn(regridBtColumn);
					table.setColumnWidth(regridBtColumn, "50px");
					*/

					
					table.addColumn(actionColumn);
					table.setColumnWidth(actionColumn,"160px" );
					
					
					/*
					 * ButtonColumn<GridImageData> upBtColumn=new ButtonColumn<GridImageData>() {
						@Override
						public void update(int index, GridImageData object,
								String value) {
								
								easyCellTableObjects.upItem(object);
						}
						@Override
						public String getValue(GridImageData object) {
							 return "Up";
						}
					};
					table.addColumn(upBtColumn);
					 ButtonColumn<GridImageData> downBtColumn=new ButtonColumn<GridImageData>() {
							@Override
							public void update(int index, GridImageData object,
									String value) {
									easyCellTableObjects.downItem(object);
							}
							@Override
							public String getValue(GridImageData object) {
								 return "Down";
							}
						};
						table.addColumn(downBtColumn);
				*/
					
					
				    TextColumn<GridImageData> fileInfoColumn = new TextColumn<GridImageData>() {
					      public String getValue(GridImageData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					    
					    TextColumn<GridImageData> widthColumn = new TextColumn<GridImageData>() {
						      public String getValue(GridImageData value) {
						    	 return value.getCol()+"x"+value.getRow();
						    	
						      }
						    };
						    table.addColumn(widthColumn,"Grid");
					    
			}
		};
		
		
		HorizontalPanel gifPanel=new HorizontalPanel();
		controler.add(gifPanel);
		
		Button regrid=new Button("ReGrid",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				regrid();
			}
		});
		gifPanel.add(regrid);
		
		Button copyBt=new Button("Copy",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doCopy();
			}
		});
		gifPanel.add(copyBt);
		
		
		Button saveBt=new Button("Save",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createDownloadImage();
			}
		});
		gifPanel.add(saveBt);
		saveBt.setWidth("80px");
		
		
		
		makeBt = new Button("Make Gif",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				makeBt.setEnabled(false);
				Timer timer=new Timer(){

					@Override
					public void run() {
						List<ImageElement> elements=FluentIterable.from(easyCellTableObjects.getDatas()).transform(new DataToImageElement()).toList();
						
						
						gifUrl=GifAnimeBuilder.from(elements).setQuality(qualityBox.getValue()).loop().delay(speedBox.getValue()).toDataUrl();
						
						
						
						//set image
						showGif();
						
						
						
						
						//create buttons
						downloadArea.clear();
						Anchor a=HTML5Download.get().generateBase64DownloadLink(gifUrl, "image/gif", "gridanime.gif", "Download Gif", false);
						a.setStylePrimaryName("bt");
						downloadArea.add(a);
						makeBt.setEnabled(true);
						stopAndPreview.setVisible(true);
					}


					
				};
				timer.schedule(5);
				
				
				
				
				
			}
		});
		gifPanel.add(makeBt);
		
		stopAndPreview = new Button("stop",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				stopOrPreview();
			}
		});
		stopAndPreview.setVisible(false);
		gifPanel.add(stopAndPreview);
		
		
		topControler.add(downloadArea);//last bottom
		
		//makeBt.setEnabled(false);
		
		
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 100);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableObjects=new EasyCellTableObjects<GridPaint.GridImageData>(cellTable,false) {
			@Override
			public void onSelect(GridImageData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		canvas.setVisible(false);
		

		moveControler=new DragMoveControler(canvas,new MoveListener() {
			@Override
			public void moved(int sx, int sy, int ex,int ey,int vectorX, int vectorY) {
				LogUtils.log(sx+","+sy+" moved: vec="+vectorX+","+vectorY);
				
				if(mode==SELECT_GRID){
					
					String value=getValueMouseAt(ex,ey);
					
					String setValue=makeGridValue();
					
					if(value==null || !value.equals(setValue)){
						int atX=ex/selection.getGridSize();
						int atY=ey/selection.getGridSize();
						//doGrid(sx, sy);
						selection.setColor(atY,atX, setValue);
						updateAround(atX, atY);
					}
				}
			}

			@Override
			public void start(int sx, int sy) {
				doClick(sx,sy);
			}

			@Override
			public void end(int sx, int sy) {
				//do nothing
			}
		});
		
		VerticalPanel scrollMain=new VerticalPanel();
		scrollMain.add(canvas);
		gifImage = new Image();
		gifImage.setVisible(false);
		scrollMain.add(gifImage);
		
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setWidth("100%");
		scroll.setHeight("100%");
		dock.add(scroll);
		scroll.add(scrollMain);
		
		return root;
	}
	
	 public Panel createMainSettingPage(){
			VerticalPanel panel=new VerticalPanel();
			Label label0=new Label("Export Image Type");
			panel.add(label0);
			
			List<String> fileType=Lists.newArrayList("PNG","Jpeg","WebP");
			exportImageType = new ValueListBox<String>(new Renderer<String>() {
				@Override
				public String render(String object) {
					return object;
				}

				@Override
				public void render(String object, Appendable appendable) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
			exportImageType.setValue(getStorageValue(KEY_IMAGE_TYPE, "PNG"));
			exportImageType.setAcceptableValues(fileType);
			exportImageType.addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					setStorageValue(KEY_IMAGE_TYPE, event.getValue());
				}
				
			});
			panel.add(exportImageType);
			
			
			Label label=new Label("GifAnime");
			panel.add(label);
			
			HorizontalPanel h1=new HorizontalPanel();
			h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			panel.add(h1);
			
			h1.add(new Label("quality"));
			qualityBox = new ValueListBox<Integer>(new Renderer<Integer>() {

				@Override
				public String render(Integer value) {
					if(value==10){
						return "medium(10)";
					}
					
					if(value==1){
						return "High(1)";
					}
					
					if(value==20){
						return "low(20)";
					}
					
					return ""+value;
				}

				@Override
				public void render(Integer object, Appendable appendable) throws IOException {
					
				}
			});
			List<Integer> acceptableValues=Lists.newArrayList();
			for(int i=20;i>0;i--){
				acceptableValues.add(i);
			}
			qualityBox.setValue(storageControler.getValue(KEY_GIF_QUALITY, 20));//low 
			qualityBox.setAcceptableValues(acceptableValues);
			h1.add(qualityBox);
			qualityBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					setStorageValue(KEY_GIF_QUALITY, event.getValue());
				}
			});

			h1.add(new Label("speed"));
			speedBox = new ValueListBox<Integer>(new Renderer<Integer>() {

				@Override
				public String render(Integer value) {
					if(value==1000){
						return "slow(1000ms)";
					}
					
					if(value==50){
						return "first(50ms)";
					}
					
					if(value==500){
						return "medium(500ms)";
					}
					
					return ""+value;
				}

				@Override
				public void render(Integer object, Appendable appendable) throws IOException {
					
				}
			});
			List<Integer> acceptableValues2=Lists.newArrayList();
			for(int i=1;i<=20;i++){
				acceptableValues2.add(i*50);
			}
			speedBox.setValue(storageControler.getValue(KEY_GIF_DELAY, 300));//low 
			speedBox.setAcceptableValues(acceptableValues2);
			h1.add(speedBox);
			speedBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					setStorageValue(KEY_GIF_DELAY, event.getValue());
				}
			});
			return panel;
		}
	 
	 private static final String KEY_IMAGE_TYPE="gridpaint_image_type";
	 private static final String KEY_GIF_QUALITY="gridpaint_gif_quality";
	 private static final String KEY_GIF_DELAY="gridpaint_gif_delay";
	
	private void showGif() {
		gifImage.setUrl(gifUrl);
		easyCellTableObjects.unselect();
	}
	
	private String gifUrl;
	private boolean gifShowing;
	protected void stopOrPreview() {
		if(gifShowing){
			gifImage.setUrl("");
			if(easyCellTableObjects.getDatas().size()>0){
				//select first and stop gif
				easyCellTableObjects.setSelected(easyCellTableObjects.getDatas().get(0), true);
			}
		}else{
			showGif();
		}
	}

	protected void doCopy() {
		if(this.selection!=null){
			GridImageData newData=this.selection.copy();
			easyCellTableObjects.addItem(newData);
			easyCellTableObjects.setSelected(newData, true);
		}
	}

	private String getColor(){
		String color=colorBox.getValue();
		if(color.isEmpty()){
			return "#000000";
		}else{
			return color;
		}
	}
	
	public  void fillColor(){
		for(int y=0;y<selection.getRow();y++){
			for(int x=0;x<selection.getCol();x++){
				String value=selection.getValue(y, x);
				if(value!=null){
					if(GridImageData.isPointValue(value)){
						//ignore
					}else{
						if(GridImageData.isCircle(value)){
							selection.setColor(y,x,"circle"+getColor());
						}else{
							selection.setColor(y,x,getColor());
						}
						
						
					}
				}
			}
		}
		updateCanvas();
	}
	

	
	private void regrid(){
		if(this.selection!=null){
			int baseSplit=baseSizeListBox.getValue();
			int width=selection.getImageWidth();
			int height=selection.getImageHeight();
			int[] result=calcurateGrid(width,height,baseSplit);
			GridImageData newData=new GridImageData(selection.getFileName(), selection.getDataUrl(), result[0], result[1], result[2],width,height);
			easyCellTableObjects.addItem(newData);
			easyCellTableObjects.setSelected(newData, true);
		}
	}

	Blob blob;
	private void createDownloadImage() {
		if(selection==null){
			return;
		}
		downloadArea.clear();
		
		String type=exportImageType.getValue().toLowerCase();
		String mime="image/"+type;
		String extension=type.equals("jpeg")?"jpg":type;
		
		
		blob=Blob.createBase64Blob(canvas.toDataUrl(mime),mime);//for IE keep blob
		
		Anchor a=null;
		if(GWTUtils.isIE()){
			a=HTML5Download.get().generateDownloadLink(blob, mime,"gridPaint."+extension, "RightClickAndSaveAs",false);
			a.setTitle("to download right mouse button to show contextmenu and select save as by yourself");
		}else{
			//TODO support ios
			a=HTML5Download.get().generateDownloadLink(blob, "image/"+mime,"gridPaint."+extension, "Download Image",true);
		}
				
		a.setStylePrimaryName("bt");
		downloadArea.add(a);
		
		gifUrl="";//clear
	}
	
	
	
	private int[] calcurateGrid(int width,int height,int baseSplit){

		int row=0;
		int col=0;
		int gridSize=0;
		if(width>height){
			int base=height/baseSplit;
			
			int wsplit=width/base+1;
			row=baseSplit;
			if(height%baseSplit>0){
				row+=1;
			}
			col=wsplit;
			gridSize=base;
		}else{
			int base=width/baseSplit;
			int hsplit=height/base+1;
			row=hsplit;
			col=baseSplit;
			if(width%baseSplit>0){
				col+=1;
			}
			gridSize=base;
		}
		return new int[]{gridSize,row,col};
	}
	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				//LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				int baseSplit=baseSizeListBox.getValue();
				
				int[] result=calcurateGrid(element.getWidth(),element.getHeight(),baseSplit);
				
				
				final GridImageData data=new GridImageData(file.getFileName(), asStringText,result[0],result[1],result[2],element.getWidth(),element.getHeight());
				
				easyCellTableObjects.addItem(data);
				//updateList();
				
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableObjects.setSelected(data, true);
					}
				});
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

	public void clearImage(){
		//GWT.getModuleName()+"/"
	}
	
	GridImageData selection;




	private VerticalPanel downloadArea;




	private ValueListBox<Integer> baseSizeListBox;




	private ColorBox colorBox;
	public void doSelect(GridImageData selection) {
		this.selection=selection;
		target=null;
		if(selection==null){
			canvas.setVisible(false);
			if(gifImage.getUrl().isEmpty()){
				gifImage.setVisible(false);
				gifShowing=false;
			}else{
				gifImage.setVisible(true);
				gifShowing=true;
			}
			
		}else{
			gifImage.setVisible(false);
			canvas.setVisible(true);
			//ImageElement element=ImageElementUtils.create(selection.getDataUrl());
			//ImageElementUtils.copytoCanvas(element, canvas);
			updateCanvas();
			gifShowing=false;
		}
		updateStopAndPreviewButton();
	}
	
	private void updateStopAndPreviewButton(){
		if(gifShowing){
			stopAndPreview.setText("stop");
		}else{
			stopAndPreview.setText("preview");
		}
	}
	
	private boolean isStampMode(){
		return stampCheck.getValue();
	}
	
	private Point target;




	private CheckBox stampCheck;




	
	private CheckBox circleCheck;
	private ListBox modeBox;
	private Button makeBt;
	private Button stopAndPreview;
	private Image gifImage;
	private ValueListBox<String> exportImageType;
	private static class Point{
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		int x;
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		int y;
		public String toString(){
			return x+"x"+y;
		}
		public static Point from(String line){
			int xx=line.indexOf("x");
			if(xx==-1){
				return null;
			}else{
				return new Point(ValuesUtils.toInt(line.substring(0, xx),0),ValuesUtils.toInt(line.substring(xx+1),0));
			}
		}
		public boolean equals(int x,int y){
			return this.x==x &&  this.y==y;
		}
	}
	private void doClick(int cx, int cy) {
		if(mode==SELECT_GRID){
			doGrid(cx,cy);
		}else if(mode==PICK_COLOR){
			doPick(cx,cy);
		}else{
			doPick(cx,cy);
			doGrid(cx,cy);
		}
	}
	
	
	private void doPick(int cx, int cy) {
		//LogUtils.log(cx+","+cy);
		ImageData data=canvas.getContext2d().getImageData(cx, cy, 1, 1);
		int r=data.getRedAt(0, 0);
		int g=data.getGreenAt(0, 0);
		int b=data.getBlueAt(0, 0);
		//LogUtils.log(r+","+g+","+b);
		String hex=ColorUtils.toCssColor(r,g,b);
		//LogUtils.log(hex);
		colorBox.setValue(hex);
	}

	public String getValueMouseAt(int cx,int cy){
		if(selection==null){
			return null;
		}
		int atX=cx/selection.getGridSize();
		int atY=cy/selection.getGridSize();
		
		return selection.getValue(atY, atX);
	}
	public String makeGridValue(){
		return circleCheck.getValue()?"circle"+getColor():getColor();
	}
	public void doGrid(int cx, int cy)	{
		//LogUtils.log(getColor());
		//LogUtils.log("click:"+cx+","+cy);
		if(selection==null){
			return;
		}
		int atX=cx/selection.getGridSize();
		int atY=cy/selection.getGridSize();
		//LogUtils.log("at:"+atX+","+atY);
		ImageElement element=ImageElementUtils.create(selection.getDataUrl());
		if(isStampMode()){
			if(target==null){
				target=new Point(atX,atY);
				
				//return;
			}else{
				if(target.equals(atX,atY)){
					selection.pushAt(atY, atX, null);
					//updateCanvasAt(target.getX(),target.getY(),element);	
				}else{
				int size=selection.getGridSize();
				int dx=selection.getGridSize()*atX;
				int dy=selection.getGridSize()*atY;
				int sx=selection.getGridSize()*target.getX();
				int sy=selection.getGridSize()*target.getY();
				
				//LogUtils.log(sx+","+sy+","+dx+","+dy);
				canvas.getContext2d().drawImage(element, sx, sy, size, size, dx, dy, size, size);
				
				//updateCanvasAt(target.getX(),target.getY(),element);//reupdate
				selection.pushAt(atY, atX, target.toString());
				
				}
				target=null;
				//return;
			}
		}else{
		selection.pushAt(atY, atX, makeGridValue());
		}
		
		updateAround(atX,atY);
		//updateCanvasAt(atX,atY,element);
		//updateCanvas();
	}
	
	public void updateAround(int atX,int atY){
		canvas.getContext2d().save();
		
		int gridSize=selection.getGridSize();
		
		int dx=gridSize*atX-1;
		int dy=gridSize*atY-1;
		//clip
		CanvasUtils.clip(canvas,dx,dy,gridSize*3,gridSize*3);
		updateCanvas();
		canvas.getContext2d().restore();
	}
	
	/*
	public void updateCanvasAt(int x,int y,ImageElement element){
		int gridSize=selection.getGridSize();
		String color=selection.getValue(y, x);
		int dx=gridSize*x;
		int dy=gridSize*y;
		if(color!=null){
			if(GridImageData.isPointValue(color)){
				Point target=Point.from(color);
				int sx=gridSize*target.getX();
				int sy=gridSize*target.getY();
				
				canvas.getContext2d().drawImage(element, sx, sy, gridSize, gridSize, dx, dy, gridSize, gridSize);
			}else{
				if(GridImageData.isCircle(color)){
					String hex=GridImageData.parseColor(color);
					canvas.getContext2d().setFillStyle(hex);
					CanvasPaintUtils.drawCircleInRect(canvas,dx,dy,gridSize,gridSize,false,true);
				}else{
				canvas.getContext2d().setFillStyle(color);
				canvas.getContext2d().fillRect(dx, dy, selection.getGridSize(),selection.getGridSize());
				}
			}
			
		}else{
			canvas.getContext2d().save();
			canvas.getContext2d().beginPath();
			canvas.getContext2d().rect(dx, dy,selection.getGridSize(), selection.getGridSize());
			canvas.getContext2d().clip();
			
			//ImageElementUtils.copytoCanvas(element, canvas);
			canvas.getContext2d().drawImage(element, 0, 0);
			canvas.getContext2d().restore();
		}
		
	}*/

	public void updateCanvas(){
		
		GridImageData.drawGridImage(canvas,selection);
		if(target!=null){
			int atX=target.getX();
			int atY=target.getY();
			canvas.getContext2d().setStrokeStyle("#888");
			int dx=selection.getGridSize()*atX;
			int dy=selection.getGridSize()*atY;
			canvas.getContext2d().strokeRect(dx+1, dy+1, selection.getGridSize()-2,selection.getGridSize()-2);
		}
		
		
	}
	

	
	

	/*
	public void updateList(){
		imageDataCellList.setData(datas);
		//imageDataCellList.getCellTable().setFocus(true);
		//imageDataCellList.getCellTable().redraw();
		//LogUtils.log("updateList-focus-redraw");
		//dock.forceLayout();
		//imageDataCellList.getCellTable().flush();
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
	
	public static class GridImageData implements HasImageUrl{
		private int row;
		private int col;
		private int imageWidth;
		public int getImageWidth() {
			return imageWidth;
		}

		public void setImageWidth(int imageWidth) {
			this.imageWidth = imageWidth;
		}
		private int imageHeight;
		public int getImageHeight() {
			return imageHeight;
		}

		public void setImageHeight(int imageHeight) {
			this.imageHeight = imageHeight;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

		public  GridImageData(String fileName, String dataUrl,int gridSize,int row,int col,int imageWidth,int imageHeight) {
			super();
			this.fileName = fileName;
			this.dataUrl = dataUrl;
			this.gridSize=gridSize;
			this.row=row;
			this.col=col;
			this.imageHeight=imageHeight;
			this.imageWidth=imageWidth;
			
			
			Collection<Integer> rowValues=new ArrayList<Integer>();
			Collection<Integer> colValues=new ArrayList<Integer>();
			for(int i=0;i<row;i++){
				rowValues.add(i);
			}
			for(int i=0;i<col;i++){
				colValues.add(i);
			}
			gridTable=ArrayTable.create(rowValues, colValues);
			
		}
		private int gridSize;
		public int getGridSize() {
			return gridSize;
		}

		public void setGridSize(int gridSize) {
			this.gridSize = gridSize;
		}

		public void setColorAtMouse(int x,int y,String value){
			int rowKey=y/gridSize;
			int columnKey=x/gridSize;
			gridTable.put(rowKey, columnKey, value);
		}
		
		public void setColor(int rowKey,int columnKey,String value){
			gridTable.put(rowKey, columnKey, value);
		}
		public void pushAt(int rowKey,int columnKey,String value){
			//LogUtils.log(value);
			if(value!=null && !value.startsWith("#")&&!value.startsWith("circle")){
				gridTable.put(rowKey, columnKey, value);//position at
				return;
			}
			if(gridTable.get(rowKey, columnKey)==null){
				gridTable.put(rowKey, columnKey, value);
			}else{
				gridTable.put(rowKey, columnKey,null);
			}
			
		}
		
		public String getValue(int rowKey,int columnKey){
			return gridTable.get(rowKey, columnKey);
		}
		private Table<Integer, Integer, String> gridTable;
		
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
		
		private static Canvas sharedCanvas;
		public String getImageUrl() {
			if(sharedCanvas==null){
				sharedCanvas=Canvas.createIfSupported();
			}
			
			drawGridImage(sharedCanvas, this);
			
			return sharedCanvas.toDataUrl();
		}
		
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		private String dataUrl;
		
		public static boolean isPointValue(String line){
			return line.indexOf("x")!=-1;
		}
		public static boolean isCircle(String line){
			return line.startsWith("circle");
		}
		public static String parseColor(String line){
			int index=line.indexOf("#");
			if(index!=-1){
				return line.substring(index);
			}else{
				return null;
			}
		}
		public GridImageData copy(){
			GridImageData data=new GridImageData(this.fileName,this.dataUrl,this.gridSize,this.row,this.col,this.imageWidth,this.imageHeight);
			
			for(int i=0;i<this.row;i++){
				for(int j=0;j<this.col;j++){
					String v=this.gridTable.get(i, j);
					data.gridTable.put(i, j, v);
				}
			}
			
			
			return data;
		}
		
		public static void drawGridImage(Canvas targetCanvas,GridImageData gridData){
			//LogUtils.log("drawGrid");
			//draw all
					ImageElement element=ImageElementUtils.create(gridData.getDataUrl());
					ImageElementUtils.copytoCanvas(element, targetCanvas);
					
					//draw lines
					//TODO
					
					int gridSize=gridData.getGridSize();
					//TODO optimize
					for(int y=0;y<gridData.getRow();y++){
						for(int x=0;x<gridData.getCol();x++){
							String value=gridData.getValue(y, x);
							if(value!=null){
								
								int dx=gridSize*x;
								int dy=gridSize*y;
								if(isPointValue(value)){
									Point target=Point.from(value);
									int sx=gridSize*target.getX();
									int sy=gridSize*target.getY();
									targetCanvas.getContext2d().drawImage(element, sx, sy, gridSize, gridSize, dx, dy, gridSize, gridSize);
								}else{
									if(isCircle(value)){
										String hex=parseColor(value);
										targetCanvas.getContext2d().setFillStyle(hex);
										
										CanvasPaintUtils.drawCircleInRect(targetCanvas,dx,dy,gridSize,gridSize,false,true);
										targetCanvas.getContext2d().setStrokeStyle(hex);
										CanvasPaintUtils.drawCircleInRect(targetCanvas,dx,dy,gridSize,gridSize,false,false);//some hole
									}else{
										String hex=parseColor(value);
										targetCanvas.getContext2d().setFillStyle(hex);
										targetCanvas.getContext2d().fillRect(dx, dy, gridSize,gridSize);
									}
									
									//targetCanvas.getContext2d().fi
									
								}
							}
						}
					}
		}
	}
	



	@Override
	public String getAppName() {
		return "GridPaint";
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
		return "http://android.akjava.com/html5apps/index.html#gridpaint";
	}
	
}
