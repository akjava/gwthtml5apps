package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.androidhtml5.client.data.ImageUrlData;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.canvas.CanvasTextUtils;
import com.akjava.gwt.lib.client.canvas.Rect;
import com.akjava.gwt.lib.client.canvas.RectBuilder;
import com.akjava.gwt.lib.client.widget.EnterKeySupportTextBox;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Function;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class SimpleLogo extends Html5DemoEntryPoint {

	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableSet<LayerData> easyCellTableSet;
	private Button makeBt;
	private ColorBox bgColorBox;
	private CheckBox keepTransparent;


	@Override
	public void initializeWidget() {
		root = new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				SimpleLogo.this.loadFile(file, dataUrl);
			}
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		//
		
		
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
		
		topPanel.add(new Anchor("Help", "simplelogo_help.html"));
	
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				loadFile(file, value);
			}
		}, true,false);//base component catch everything
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		fileUps.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		controler.add(fileUps);
		fileUps.add(upload);

		downloadArea = new HorizontalPanel();
		fileUps.add(downloadArea);
		
	
		HorizontalPanel makeBtPanel=new HorizontalPanel();
		controler.add(makeBtPanel);
		makeBt = new Button("Make",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				generateImage();
			}
		});
		makeBtPanel.add(makeBt);
		makeBt.setEnabled(false);
		
		showOutSideCheck = new CheckBox("show out");
		showOutSideCheck.setValue(true);
		showOutSideCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(showOutSideCheck.getValue()){
					canvas.setStyleName("transparent_bg");
				}else{
					canvas.setStyleName("white_bg");
				}
				updateImage();
			}
		});
		makeBtPanel.add(showOutSideCheck);
		
		keepTransparent = new CheckBox("transparent");
		
		keepTransparent.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(showOutSideCheck.getValue()){
					//bgColorBox.setVisible(false);//need enabled
				}else{
					//bgColorBox.setVisible(true);//never show again...
				}
				updateImage();
			}
		});
		makeBtPanel.add(keepTransparent);
		
	
		HorizontalPanel h1=new HorizontalPanel();
		controler.add(h1);
		final Label scaleLabel=new Label("Scale:1.0");
		h1.add(scaleLabel);//TODO ondemand scale
		scaleLabel.setWidth("70px");
		
		scaleRange = HTML5InputRange.createInputRange(1, 40, 10);
		scaleRange.setWidth("200px");
		h1.add(scaleRange);
		scaleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				int scale=scaleRange.getValue();
				if(selection!=null){
					selection.setScale(0.1*scale);
				}
			}
		});
		scaleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				scaleLabel.setText("Scale:"+((double)scaleRange.getValue()/10));
				
			}
		});
		
		HorizontalPanel h2=new HorizontalPanel();
		controler.add(h2);
		final Label turnLabel=new Label("Angle:0");
		h2.add(turnLabel);//TODO ondemand scale
		turnLabel.setWidth("70px");
		
		rotateRange = HTML5InputRange.createInputRange(-180, 180, 0);
		rotateRange.setWidth("200px");
		h2.add(rotateRange);
		rotateRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				int angle=rotateRange.getValue();
				if(selection!=null){
					selection.setAngle(angle);
				}
			}
		});
		rotateRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				
				turnLabel.setText("angle:"+(rotateRange.getValue()));
				
				
			}
		});
	
		HorizontalPanel h3=new HorizontalPanel();
		controler.add(h3);
		titleBox = new EnterKeySupportTextBox(){
			@Override
			public void onEnterKeyDown() {
				updateImage();
			}};
		titleBox.setText("TODO");
		h3.add(titleBox);
		colorBox = new ColorBox();
		colorBox.setValue("#ffffff");
		h3.add(colorBox);
		
		
		h3.add(new Label("BG"));
		
		bgColorBox = new ColorBox();/*
		bgColorBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LogUtils.log(bgColorBox.getValue());
			}
		});*/
		bgColorBox.setValue("#000000");
		h3.add(bgColorBox);
		
		Button updateBt=new Button("Update",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateImage();
			}
		});
		h3.add(updateBt);
		
		
		SimpleCellTable<LayerData> cellTable = new SimpleCellTable<LayerData>(999) {
			@Override
			public void addColumns(CellTable<LayerData> table) {
				 ButtonColumn<LayerData> removeBtColumn=new ButtonColumn<LayerData>() {
						@Override
						public void update(int index, LayerData object,
								String value) {
								easyCellTableSet.removeItem(object);
						}
						@Override
						public String getValue(LayerData object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					
					/*
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
				
					
					
				    TextColumn<LayerData> fileInfoColumn = new TextColumn<LayerData>() {
					      public String getValue(LayerData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					    
					   
					    
			}
		};
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 150);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableSet=new EasyCellTableSet<LayerData>(cellTable,false) {
			@Override
			public void onSelect(LayerData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		dock.add(mainScrollPanel);
		
		
		canvasWidth = 500;
		canvasHeight = 500;
		
		canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		
		drawCanvas=CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		mainScrollPanel.add(canvas);
		canvas.setStylePrimaryName("transparent_bg");
		
		//move
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(moveControler.isStarted()){
					moveControler.move(event.getX(), event.getY());
				}
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				moveControler.end(event.getX(), event.getY());
			}
		});
		
		canvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				moveControler.start(event.getX(), event.getY());
			}
		});
		
		canvas.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				moveControler.end(event.getX(), event.getY());
			}
		});
		
		moveControler=new MoveControler(new MoveListener() {
			
			@Override
			public void moved(int sx, int sy, int vectorX, int vectorY) {
				offsetX+=vectorX;
				offsetY+=vectorY;
				updateImage();
				if(selection!=null){
					selection.setOffsetX(offsetX);
					selection.setOffsetY(offsetY);
				}
			}
		});
		
	}
	
	protected void generateImage() {
		ImageData data=canvas.getContext2d().getImageData(clipSX, clipSY, clipEX-clipSX, clipEY-clipSY);
		CanvasUtils.createCanvas(drawCanvas, data);
		String dataUrl=drawCanvas.toDataUrl();
		Anchor anchor=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/png", "logo.png", "Download", true);
		anchor.setStylePrimaryName("bt");
		downloadArea.clear();
		downloadArea.add(anchor);
	}

	private int offsetX,offsetY;
	
	private MoveControler moveControler;
	
	public static class MoveControler{
		
		
		public MoveControler(MoveListener moveListener) {
			super();
			this.moveListener = moveListener;
		}
		private int startX;
		private boolean started;
		public boolean isStarted() {
			return started;
		}

		public void setStarted(boolean started) {
			this.started = started;
		}

		public void start(int x,int y){
			startX=x;
			startY=y;
			started=true;
		}
		
		public void move(int x,int y){
			if(!started){
				return;
			}
			int dx=x-startX;
			int dy=y-startY;
			if(moveListener!=null){
				moveListener.moved(startX, startY, dx, dy);
			}
			startX=x;
			startY=y;
		}
		public void end(int x,int y){
			move(x,y);
			started=false;
		}
		public int getStartX() {
			return startX;
		}
		public void setStartX(int startX) {
			this.startX = startX;
		}
		public int getStartY() {
			return startY;
		}
		public void setStartY(int startY) {
			this.startY = startY;
		}
		public int getMovedX() {
			return movedX;
		}
		public void setMovedX(int movedX) {
			this.movedX = movedX;
		}
		public int getMovedY() {
			return movedY;
		}
		public void setMovedY(int movedY) {
			this.movedY = movedY;
		}
		private int startY;
		
		
		
		private int movedX;
		private int movedY;
		
		private MoveListener moveListener;
		
	}

	public static interface MoveListener{
		public void moved(int sx,int sy,int vectorX,int vectorY);
	}
	
	



	

	
	

	/**
	 * watch out IE maybe problem
	 * @author aki
	 *
	 */
	
	public class DataToImageElement implements Function<ImageUrlData,ImageElement>{

		@Override
		public ImageElement apply(ImageUrlData input) {
			return ImageElementUtils.create(input.getDataUrl());
		}
		
	}
	
	
	

	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(final ImageElement element) {
				//LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				
				final LayerData data=new LayerData(file.getFileName(),element,asStringText);
				
				easyCellTableSet.addItem(data);
				//updateList();
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableSet.setSelected(data, true);
						makeBt.setEnabled(true);
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

	
	LayerData selection;





	
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DataUrlDropDockRootPanel root;
	private HorizontalPanel downloadArea;
	private Canvas canvas;
	private InputRangeWidget scaleRange;
	private InputRangeWidget rotateRange;


	




	
	public void doSelect(LayerData selection) {
		this.selection=selection;
		
		
		if(selection==null){
			CanvasUtils.clear(canvas);
			
		}else{
			scaleRange.setValue((int) (selection.getScale()*10));
			rotateRange.setValue((int) selection.getAngle());
			offsetX=selection.getOffsetX();
			offsetY=selection.getOffsetY();
			
			updateImage();
		}
	}

	private Canvas drawCanvas;
	
	private int clipSX=0;
	private int clipSY=150;
	private int clipEX=500;
	private int clipEY=350;
	private int canvasWidth;
	private int canvasHeight;
	private TextBox titleBox;
	private ColorBox colorBox;
	
	private double lastScale=1;
	private CheckBox showOutSideCheck;
	private void updateImage(){
		
		
		if(drawCanvas.getCoordinateSpaceWidth()!=canvasWidth || drawCanvas.getCoordinateSpaceHeight()!=canvasHeight){
			drawCanvas.setCoordinateSpaceWidth(canvasWidth);
			drawCanvas.setCoordinateSpaceHeight(canvasHeight);
		}
		CanvasUtils.clear(drawCanvas);
		CanvasUtils.clear(canvas);
		canvas.getContext2d().save();
		ImageElement element=selection.getImageElement();
		
		double scale=(double)scaleRange.getValue()/10;
		
		int angle=rotateRange.getValue();
		
		int ox=(int) (offsetX*scale);
		int oy=(int) (offsetY*scale);
		
		if(scale!=lastScale){
			int ox2=(int) (element.getWidth()*(lastScale-scale));
			int oy2=(int) (element.getWidth()*(lastScale-scale));
			ox=ox-(ox-ox2);
			oy=oy-(oy-oy2);
		}
		
		CanvasUtils.drawCenter(drawCanvas, element,ox,oy,scale,scale,angle,1);
				
		
		
		
		//canvas.getContext2d().setFillStyle("rgba(255, 255, 255, 0.5)");
		if(showOutSideCheck.getValue()){
		canvas.getContext2d().setGlobalAlpha(0.5);
		canvas.getContext2d().drawImage(drawCanvas.getCanvasElement(), 0,0);
		}else{
		//CanvasUtils.clear(drawCanvas);	
		}
		
		canvas.getContext2d().restore();
		
		
		//canvas.getContext2d().restore();
		canvas.getContext2d().save();
		canvas.getContext2d().beginPath();
		canvas.getContext2d().moveTo(clipSX,clipSY);
		canvas.getContext2d().lineTo(clipEX,clipSY);
		canvas.getContext2d().lineTo(clipEX,clipEY);
		canvas.getContext2d().lineTo(clipSX,clipEY);
		canvas.getContext2d().clip();
		canvas.getContext2d().setGlobalAlpha(1);
		
		if(!keepTransparent.getValue()){
			canvas.getContext2d().setFillStyle(bgColorBox.getValue());
			canvas.getContext2d().fillRect(clipSX, clipSY, clipEX-clipSX, clipEY-clipSY);
		}
		
		
		//CanvasUtils.drawCenter(canvas, element,ox,oy,scale,scale,angle,1);
		canvas.getContext2d().drawImage(drawCanvas.getCanvasElement(), 0,0);
		 
		
		canvas.getContext2d().setFont("64px Audiowide");
		canvas.getContext2d().setFillStyle(colorBox.getValue());
		//canvas.getContext2d().fillText("Document", 200, 200);
		
		
		String title=titleBox.getText();
		Rect rect;
		
		
		rect=CanvasTextUtils.getAlignRect(canvas,canvas.getCoordinateSpaceWidth()-50,canvas.getCoordinateSpaceHeight()-50,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_TOP);
		rect.setX(rect.getX()+25);
		rect.setY(clipSY+25);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		
		rect=RectBuilder.from(500, 200).slice(4, 2).topRight().horizontalExpand(-1).toRect();//.horizontalExpand(-1)
		
		
		
		/*
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		LogUtils.log(rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		*/
		rect.setY(rect.getY()+clipSY);
		//CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		canvas.getContext2d().restore();
		lastScale=scale;
		
	}
	

	
	public class LayerData extends ImageElementData{
		public LayerData(String fileName, ImageElement imageElement, String dataUrl) {
			super(fileName, imageElement, dataUrl);
			// TODO Auto-generated constructor stub
		}
		private double scale=1;
		public double getScale() {
			return scale;
		}
		public void setScale(double scale) {
			this.scale = scale;
		}
		public int getOffsetX() {
			return offsetX;
		}
		public void setOffsetX(int offsetX) {
			this.offsetX = offsetX;
		}
		public int getOffsetY() {
			return offsetY;
		}
		public void setOffsetY(int offsetY) {
			this.offsetY = offsetY;
		}
		public double getAngle() {
			return angle;
		}
		public void setAngle(double angle) {
			this.angle = angle;
		}
		private int offsetX;
		private int offsetY;
		private double angle;
	}









	@Override
	public String getAppName() {
		return "SimpleLogo";
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
		return "http://android.akjava.com/html5apps/index.html#simplelogo";
	}
	
}
