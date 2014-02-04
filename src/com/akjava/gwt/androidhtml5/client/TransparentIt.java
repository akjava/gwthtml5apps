package com.akjava.gwt.androidhtml5.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.Blob;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.GWTUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class TransparentIt extends Html5DemoEntryPoint {

	private ImageElement imgElement;

	

	private Canvas overlayCanvas;


	private XYPoint lastPoint;
	private DataUriCommand currentCommand;
	private long lastAvatorUpdate;

	private int currentEditingId=-1;

	private int penSize=32;
	public static final int MODE_ERASE=0;
	public static final int MODE_BLACK=1;
	public static final int MODE_WHITE=2;
	public static final int MODE_COLOR=3;
	public static final int MODE_UNERASE=4;
	private int penMode=MODE_ERASE;
	private boolean mouseMoved;
	
	private Canvas canvas;
	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableSet<ImageElementData> easyCellTableSet;



	private Button saveBt;

	
	@Override
	public void initializeWidget() {
		DataUrlDropDockRootPanel root=new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				TransparentIt.this.loadFile(file, dataUrl);
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
		
		topPanel.add(new Anchor("Help", "transparent_help.html"));
	
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				loadFile(file, value);
			}
		}, true);
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		controler.add(fileUps);
		fileUps.add(upload);

		
		final CheckBox blackCheck=new CheckBox("black");
		final CheckBox trasparentCheck=new CheckBox("transparent bg");
		HorizontalPanel bgPanel=new HorizontalPanel();
		controler.add(bgPanel);
		bgPanel.add(trasparentCheck);
		trasparentCheck.setValue(true);
		trasparentCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(trasparentCheck.getValue()){
					canvas.setStylePrimaryName("transparent_bg");
				}else{
					if(blackCheck.getValue()){
						canvas.setStylePrimaryName("black_bg");
					}else{
						canvas.setStylePrimaryName("white_bg");
					}
					
				}
			}
		});
		
		blackCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(trasparentCheck.getValue()){
					return;
				}
				if(blackCheck.getValue()){
					canvas.setStylePrimaryName("black_bg");
				}else{
					canvas.setStylePrimaryName("white_bg");
				}
			}
		});
		bgPanel.add(blackCheck);
		
		
		int cbase=18;
		canvasWidth = cbase*16;
		int ch=cbase*9;
		zoomSize = 1;
		/*
		VerticalPanel bg=new VerticalPanel();
		editPanel.add(bg);
		bg.setSpacing(0);
		*/
		
		
		//size choose
		HorizontalPanel sizes=new HorizontalPanel();
		controler.add(sizes);
		
		RadioButton exs=new RadioButton("sizes");
		exs.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penSize=8;
			}
		});
		sizes.add(exs);
		sizes.add(new Label("exSmall"));

		
		
		RadioButton smallS=new RadioButton("sizes");
		smallS.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penSize=16;
			}
		});
		sizes.add(smallS);
		sizes.add(new Label("small"));

		RadioButton middleS=new RadioButton("sizes");
		middleS.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penSize=32;
			}
		});
		middleS.setValue(true);
		sizes.add(middleS);
		sizes.add(new Label("middle"));
	
		RadioButton largeS=new RadioButton("sizes");
		largeS.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penSize=48;
			}
		});
		sizes.add(largeS);
		sizes.add(new Label("large"));
		
		RadioButton exL=new RadioButton("sizes");
		exL.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penSize=96;
			}
		});
		sizes.add(exL);
		sizes.add(new Label("exLarge"));
		
		//pen choose
		HorizontalPanel pens=new HorizontalPanel();
		pens.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		controler.add(pens);
		RadioButton eraseR=new RadioButton("pens");
		pens.add(eraseR);
		eraseR.setValue(true);
		eraseR.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_ERASE;
			}
		});
		pens.add(new Label("Erase"));
		RadioButton uneraseR=new RadioButton("pens");
		uneraseR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_UNERASE;
			}
		});

		pens.add(uneraseR);
		pens.add(new Label("UnErase"));
		RadioButton blackR=new RadioButton("pens");
		pens.add(blackR);
		pens.add(new Label("Black"));
		blackR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_BLACK;
			}
		});
		
		
		RadioButton whiteR=new RadioButton("pens");
		pens.add(whiteR);
		pens.add(new Label("White"));
		whiteR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_WHITE;
			}
		});
		
		RadioButton customR=new RadioButton("pens");
		pens.add(customR);
		pens.add(new Label("Color"));
		customR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_COLOR;
			}
		});
		
		colorPicker = new ColorBox();
		colorPicker.setValue("#ff0000");
		pens.add(colorPicker);
		
		
		
		
		canvas = Canvas.createIfSupported();
		canvas.setStylePrimaryName("transparent_bg");//or bg
		
		overlayCanvas=Canvas.createIfSupported();
		
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(editBt.isVisible()){
					return;
				}
				
				if(mouseDown){
				mouseMoved=true;
				int x=event.getX()*zoomSize;
				int y=event.getY()*zoomSize;
				XYPoint newPoint=new XYPoint(x,y);
				
				switch(penMode){
				case MODE_ERASE:
					erase(lastPoint,newPoint);
					break;
				case MODE_UNERASE:
					unerase(lastPoint,newPoint);
					break;
				case MODE_BLACK:
					drawLine(lastPoint,newPoint,"#000");
					break;
				case MODE_WHITE:
					drawLine(lastPoint,newPoint,"#fff");
					break;
				case MODE_COLOR:
					drawLine(lastPoint,newPoint,colorPicker.getValue());
					break;
				}
				
				
				
				lastPoint=newPoint;
				
				long c=System.currentTimeMillis();
				
				if(lastAvatorUpdate+200<c){
					lastAvatorUpdate=c;
					
					}
				}
			}
		});
		canvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if(editBt.isVisible()){
					return;
				}
				
				mouseRight=event.getNativeButton()==NativeEvent.BUTTON_RIGHT;
				mouseDown=true;
				lastPoint=mouseToXYPoint(event.getX(),event.getY());
				
				
				currentCommand=new DataUriCommand();
				currentCommand.setBeforeUri(canvas.toDataUrl("image/png"));
				
			}
		});
		canvas.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				doMouseUp();
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				doMouseUp();
			}
		});
		
		//stop context menu;
		canvas.addDomHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.stopPropagation();
				event.preventDefault();
			}
		}, ContextMenuEvent.getType());
		

		
	
	
		
		
		HorizontalPanel exbuttons=new HorizontalPanel();
		
		HorizontalPanel buttons=new HorizontalPanel();
		controler.add(buttons);
		
		controler.add(exbuttons);
		

		
		
		
		
	
		
		editBt = new Button("Edit");
		editBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

					
					overlayBt.setVisible(true);
					editBt.setVisible(false);
					reset.setEnabled(true);
				}
			});
		editBt.setVisible(false);
		buttons.add(editBt);
		
		
		undoBt = new Button("Undo");
		undoBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentCommand.undo();
				undoBt.setEnabled(false);
				redoBt.setEnabled(true);
				
			}
		});
		buttons.add(undoBt);
		undoBt.setEnabled(false);
		
		redoBt = new Button("Redo");
		redoBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				currentCommand.redo();
				undoBt.setEnabled(true);
				redoBt.setEnabled(false);
			}
		});
		redoBt.setEnabled(false);
		buttons.add(redoBt);
		
		
		
		
		
		
		
		reset = new Button("Reset");
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doReset();
			}
		});
		buttons.add(reset);
		
		
		saveBt = new Button("Save");
		saveBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createDownloadImage();
			}
		});
		buttons.add(saveBt);
		
		
		
		//controler,fist,pre,next,auto-play + time,clear
		
		/*
		FileUploadForm uploadFiles=FileUtils.createMultiFileUploadForm(new DataURLsListener() {
			@Override
			public void uploaded(final List<File> files, final List<String> values) {
				log("uploaded:"+values.size());
				String url=values.remove(0);
				final ImageElementLoader loader=new ImageElementLoader();
				
				final ImageElementListener listener= new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						overlayCanvas.getContext2d().save();
						clearCanvas(overlayCanvas);
						overlayCanvas.getContext2d().restore();
						overlayCanvas.getContext2d().save();
						drawFitCenter(overlayCanvas, element);
						overlayCanvas.getContext2d().restore();
						String resizedImage=overlayCanvas.toDataUrl("image/png");//origin
						
						
						ImageItem item=new ImageItem(resizedImage);
						container.add(item);
						if(values.size()>0){
							String url=values.remove(0);
							loader.load(url, this);
						}
					}
				};
				
				loader.load(url,listener);
			}
		}, true);
		listPanel.add(uploadFiles);
		*/
		
	
		
		
		
		
		
		
		
		
		
		downloadArea = new VerticalPanel();
		downloadArea.setSpacing(2);

		fileUps.add(downloadArea);
		
		SimpleCellTable<ImageElementData> cellTable = new SimpleCellTable<ImageElementData>(999) {
			@Override
			public void addColumns(CellTable<ImageElementData> table) {
				 ButtonColumn<ImageElementData> removeBtColumn=new ButtonColumn<ImageElementData>() {
						@Override
						public void update(int index, ImageElementData object,
								String value) {
								easyCellTableSet.removeItem(object);
						}
						@Override
						public String getValue(ImageElementData object) {
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
				
					
					
				    TextColumn<ImageElementData> fileInfoColumn = new TextColumn<ImageElementData>() {
					      public String getValue(ImageElementData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					    
					    
			}
		};
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 200);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableSet=new EasyCellTableSet<ImageElementData>(cellTable,false) {
			@Override
			public void onSelect(ImageElementData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		ScrollPanel scroll2=new ScrollPanel();
		
		scroll2.setWidth("100%");
		scroll2.setHeight("100%");
		dock.add(scroll2);
		scroll2.add(canvas);
		
		canvas.setVisible(false);
		
	}
	


	private void doMouseUp(){

		if(editBt.isVisible()){
			return;
		}
		
		if(!mouseMoved){
			if(lastPoint!=null){
				
			
			XYPoint dummyPt=new XYPoint(lastPoint.getX()+1, lastPoint.getY()+1);
			
			switch(penMode){
			case MODE_ERASE:
				erase(lastPoint,dummyPt);
				break;
			case MODE_UNERASE:
				unerase(lastPoint,dummyPt);
				break;
			case MODE_BLACK:
				drawLine(lastPoint,dummyPt,"#000");
				break;
			case MODE_WHITE:
				drawLine(lastPoint,dummyPt,"#fff");
				break;
			case MODE_COLOR:
				drawLine(lastPoint,dummyPt,colorPicker.getValue());
				break;
			}
			}
		}
		
		mouseMoved=false;
		mouseDown=false;
		lastPoint=null;
	
		String dataUrl=canvas.toDataUrl("image/png");
		currentCommand.setAfterUri(dataUrl);
		if(selection!=null){
			selection.setDataUrl(dataUrl);
		}
		undoBt.setEnabled(true);
	}
	
	
	
	protected void doReset() {
		if(selection==null){
			canvas.setVisible(false);
			return;
		}
		currentCommand=new DataUriCommand();
		currentCommand.setBeforeUri(canvas.toDataUrl("image/png"));
		
		
		
		//canvas.getContext2d().save();
		//canvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);//seems broken in Chrome32
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		//no need to flip
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		//canvas.getContext2d().transform(-1, 0, 0, 1, 0, 0);
		canvas.getContext2d().drawImage(selection.getImageElement(), 0, 0);
		//canvas.getContext2d().restore();
		
		currentCommand.setAfterUri(canvas.toDataUrl("image/png"));
		
		
		undoBt.setEnabled(true);
		redoBt.setEnabled(false);
		
		
		LogUtils.log("after-reset:"+canvas.getContext2d().getGlobalCompositeOperation());
	}





	private void setImage(String url){
		new ImageElementLoader().load(url, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				imgElement=element;
				LogUtils.log("size:"+imgElement.getWidth()+"x"+imgElement.getHeight());
				
				
				drawImage(imgElement);
				
				
				
				editBt.setVisible(false);
				reset.setEnabled(true);
			}
			@Override
			public void onError(String url, ErrorEvent event) {
				Window.alert(event.toDebugString());
			}
		});
	}
	

	
	private void drawText(String text){
		overlayCanvas.getContext2d().save();
		overlayCanvas.getContext2d().setFont("30px Arial");
		overlayCanvas.getContext2d().setShadowColor("Black");
		overlayCanvas.getContext2d().setFillStyle("white");
		overlayCanvas.getContext2d().setShadowOffsetX(2);
		overlayCanvas.getContext2d().setShadowOffsetY(2);
		double w=overlayCanvas.getContext2d().measureText(text).getWidth();
		//originImage.getContext2d().setTextBaseline(TextBaseline.ALPHABETIC);
		int halfHeight=30;
		
		overlayCanvas.getContext2d().fillText(text, (overlayCanvas.getCoordinateSpaceWidth()-w)/2, halfHeight);
		
		//originImage.getContext2d().get
		overlayCanvas.getContext2d().restore();
	}
	

	
	/*
	private void clearCanvas(Canvas targetCanvas){
		targetCanvas.getContext2d().setFillStyle("rgba(0,0,0,0)");
		targetCanvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
		targetCanvas.getContext2d().fillRect(0, 0, overlayCanvas.getCoordinateSpaceWidth(), overlayCanvas.getCoordinateSpaceHeight());
	}*/
	


	
	
	
	
	
	private void copyToOverlayCanvas(ImageElement element,boolean flip){
		//flip horizontal
		overlayCanvas.getContext2d().save();
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
		if(flip){
		overlayCanvas.getContext2d().translate(canvas.getCoordinateSpaceWidth(), 0); //flip horizontal
		overlayCanvas.getContext2d().scale(-1, 1);
		}
		overlayCanvas.getContext2d().drawImage(element, 0, 0);
		overlayCanvas.getContext2d().restore();
	}
	private void copyToOverlayCanvas(CanvasElement element){
		//flip horizontal
		overlayCanvas.getContext2d().save();
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
		overlayCanvas.getContext2d().translate(canvas.getCoordinateSpaceWidth(), 0); //flip horizontal
		overlayCanvas.getContext2d().scale(-1, 1);
		overlayCanvas.getContext2d().drawImage(element, 0, 0);
		overlayCanvas.getContext2d().restore();
	}
	
	private int dindex;

	
	private XYPoint mouseToXYPoint(int mx,int my){
		int x=mx*zoomSize;
		int y=my*zoomSize;
		XYPoint newPoint=new XYPoint(x,y);
		return newPoint;
	}
	
	private void erase(XYPoint p1,XYPoint p2){
		canvas.getContext2d().save();
		canvas.getContext2d().setLineWidth(penSize);
		canvas.getContext2d().setLineJoin(LineJoin.ROUND);
		canvas.getContext2d().setStrokeStyle("#000");
		canvas.getContext2d().setGlobalCompositeOperation("destination-out");
		
		canvas.getContext2d().beginPath();
		
		canvas.getContext2d().moveTo(p1.getX(),p1.getY());
		canvas.getContext2d().lineTo(p2.getX(),p2.getY());
		
		canvas.getContext2d().closePath();
		canvas.getContext2d().stroke();
		canvas.getContext2d().restore();
	}
	
	private void unerase(XYPoint p1,XYPoint p2){
		if(selection==null){
			return;
		}
		overlayCanvas.getContext2d().clearRect(0, 0, overlayCanvas.getCoordinateSpaceWidth(), overlayCanvas.getCoordinateSpaceHeight());
		
		overlayCanvas.getContext2d().save();
		overlayCanvas.getContext2d().setLineWidth(penSize+2);
		overlayCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		overlayCanvas.getContext2d().setStrokeStyle("#000");
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
		overlayCanvas.getContext2d().beginPath();
		overlayCanvas.getContext2d().moveTo(p1.getX(),p1.getY());
		overlayCanvas.getContext2d().lineTo(p2.getX(),p2.getY());
		overlayCanvas.getContext2d().closePath();
		overlayCanvas.getContext2d().stroke();
		
		//TODO clip
		overlayCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_IN);
		//overlayCanvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//overlayCanvas.getContext2d().scale(-1, 1);
		overlayCanvas.getContext2d().drawImage(selection.getImageElement(), 0, 0);
		
		overlayCanvas.getContext2d().restore();
		
		canvas.getContext2d().save();
		canvas.getContext2d().drawImage(overlayCanvas.getCanvasElement(), 0, 0);
		canvas.getContext2d().restore();
	}
	
	private void drawLine(XYPoint p1,XYPoint p2,String color){
		//LogUtils.log("drawLine-before:"+canvas.getContext2d().getGlobalCompositeOperation());
		canvas.getContext2d().save();
		canvas.getContext2d().setLineWidth(penSize);
		canvas.getContext2d().setLineJoin(LineJoin.ROUND);
		canvas.getContext2d().setStrokeStyle(color);
		canvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
		
		canvas.getContext2d().beginPath();
		
		canvas.getContext2d().moveTo(p1.getX(),p1.getY());
		canvas.getContext2d().lineTo(p2.getX(),p2.getY());
		
		canvas.getContext2d().closePath();
		canvas.getContext2d().stroke();
		canvas.getContext2d().restore();
		//LogUtils.log("drawLine-after:"+canvas.getContext2d().getGlobalCompositeOperation());
	}

	
	public class XYPoint{
		public XYPoint(int x,int y){
			this.x=x;
			this.y=y;
		}
		private int x;
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
		private int y;
		
	}
	
	boolean mouseDown;
	boolean mouseRight;
	private int zoomSize;
	private Button editBt;
	private Button overlayBt;
	private Button undoBt;
	private Button redoBt;

	private int canvasWidth;
	private Button reset;
	private ColorBox colorPicker;
	protected void drawImage(ImageElement img) {
		try{
		
		//need for some transparent image
		canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		
		//canvas.getContext2d().save();
		
		//no need to flip
		//canvas.getContext2d().translate(originImage.getCoordinateSpaceWidth(), 0); //flip horizontal
		//canvas.getContext2d().scale(-1, 1);
		//canvas.getContext2d().transform(-1, 0, 0, 1, 0, 0);
		canvas.getContext2d().drawImage(selection.getImageElement(), 0, 0);
		//canvas.getContext2d().restore();
		
		}catch(Exception e){
			LogUtils.log("error:"+e.getMessage());
		}
		
		//canvas.getContext2d().setFillStyle("rgba(0,0,0,0)");
		//canvas.getContext2d().setGlobalCompositeOperation("destination-out");
		//canvas.getContext2d().fillRect(100, 100, 100, 100);
	}
	
	



	
	public class DataUriCommand implements Command{
		private String beforeUri;
		private String afterUri;
		public String getBeforeUri() {
			return beforeUri;
		}

		public void setBeforeUri(String beforeUri) {
			this.beforeUri = beforeUri;
		}

		public String getAfterUri() {
			return afterUri;
		}

		public void setAfterUri(String afterUri) {
			this.afterUri = afterUri;
		}

		
		@Override
		public void undo() {
			
			ImageElementLoader loader=new ImageElementLoader();
			loader.load(beforeUri, new ImageElementListener() {
				@Override
				public void onLoad(ImageElement element) {
					canvas.getContext2d().save();
					canvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
					canvas.getContext2d().drawImage(element,0,0);
					canvas.getContext2d().restore();
				
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					Window.alert(event.toDebugString());
				}
			});
			
		}

		@Override
		public void redo() {
			ImageElementLoader loader=new ImageElementLoader();
			loader.load(afterUri, new ImageElementListener() {
				@Override
				public void onLoad(ImageElement element) {
					canvas.getContext2d().save();
					canvas.getContext2d().setGlobalCompositeOperation(Composite.COPY);
					canvas.getContext2d().drawImage(element,0,0);
					canvas.getContext2d().restore();
					
				}
				
				@Override
				public void onError(String url, ErrorEvent event) {
					Window.alert(event.toDebugString());
				}
			});
			
			
		}
		
	}
	
	/**
	 * this faild some edge showd,i need stop antialiase.
	 * @author aki
	 *
	 */
	public class EraseCommand implements Command{
		List<XYPoint> positions=new ArrayList<XYPoint>();
		public void add(XYPoint point){
			positions.add(point);
		}
		public int size(){
			return positions.size();
		}
		@Override
		public void undo() {
			if(size()==1){//click only
				unerase(positions.get(0), positions.get(0));
			}
			for(int i=0;i<positions.size()-1;i++){
				unerase(positions.get(i), positions.get(i+1));
			}
			
		}

		@Override
		public void redo() {
			if(size()==1){//click only
				erase(positions.get(0), positions.get(0));
			}
			for(int i=0;i<positions.size()-1;i++){
				erase(positions.get(i), positions.get(i+1));
			}
			
		}
	}
	

	


	Blob blob;
	private void createDownloadImage() {
		if(selection==null){
			return;
		}
		downloadArea.clear();
		
		blob=Blob.createBase64Blob(canvas.toDataUrl(),"image/png");//for IE keep blob
		
		Anchor a=null;
		if(GWTUtils.isIE()){
			a=HTML5Download.get().generateDownloadLink(blob, "image/png","gridPaint.png", "RightClickAndSaveAs",false);
			a.setTitle("to download right mouse button to show contextmenu and select save as by yourself");
		}else{
			//TODO support ios
			a=HTML5Download.get().generateDownloadLink(blob, "image/png","gridPaint.png", "Download Image",true);
		}
				
		a.setStylePrimaryName("bt");
		downloadArea.add(a);
	}
	
	
	

	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				
				final ImageElementData data=new ImageElementData(file.getFileName(),element,asStringText);
				
				easyCellTableSet.addItem(data);
				//updateList();
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableSet.setSelected(data, true);
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
	
	ImageElementData selection;




	private VerticalPanel downloadArea;




	




	
	public void doSelect(ImageElementData selection) {
		this.selection=selection;
		if(selection==null){
			canvas.setVisible(false);
		}else{
			canvas.setVisible(true);
			//ImageElement element=ImageElementUtils.create(selection.getDataUrl());
			//ImageElementUtils.copytoCanvas(element, canvas);
			undoBt.setEnabled(false);
			redoBt.setEnabled(false);
			updateCanvas();
		}
	}

	
	private void doClick(int cx, int cy) {
		
	}
	

	public void updateCanvas(){
		ImageElementUtils.copytoCanvas(ImageElementUtils.create(selection.getDataUrl()), canvas);
		ImageElementUtils.copytoCanvas(ImageElementUtils.create(selection.getDataUrl()), overlayCanvas,false);
	}

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
	


	@Override
	public String getAppName() {
		return "TransparentIt";
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
		return "http://android.akjava.com/html5apps/index.html#transparentit";
	}
	
}
