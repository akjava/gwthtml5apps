package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
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

	private Canvas canvas;
	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableSet<GridImageData> easyCellTableSet;

	
	@Override
	public void initializeWidget() {
		DataUrlDropDockRootPanel root=new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				GridPaint.this.loadFile(file, dataUrl);
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
		
		bh.add(downloadArea);
		
		
		
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
		
		Button regrid=new Button("ReGrid",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				regrid();
			}
		});
		topControler.add(regrid);
		
Button saveBt=new Button("Save",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createDownloadImage();
			}
		});
		topControler.add(saveBt);
		saveBt.setWidth("80px");
		
		

		SimpleCellTable<GridImageData> cellTable = new SimpleCellTable<GridPaint.GridImageData>(999) {
			@Override
			public void addColumns(CellTable<GridImageData> table) {
				 ButtonColumn<GridImageData> removeBtColumn=new ButtonColumn<GridImageData>() {
						@Override
						public void update(int index, GridImageData object,
								String value) {
								easyCellTableSet.removeItem(object);
						}
						@Override
						public String getValue(GridImageData object) {
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
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 65);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableSet=new EasyCellTableSet<GridPaint.GridImageData>(cellTable,false) {
			@Override
			public void onSelect(GridImageData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		canvas.setVisible(false);
		
		canvas.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				doClick(event.getX(),event.getY());
			}

			
		});
		
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setWidth("100%");
		scroll.setHeight("100%");
		dock.add(scroll);
		scroll.add(canvas);
		
	}
	
	private String getColor(){
		String color=colorBox.getValue();
		if(color.isEmpty()){
			return "#000";
		}else{
			return color;
		}
	}
	
	private void fillColor(){
		for(int y=0;y<selection.getRow();y++){
			for(int x=0;x<selection.getCol();x++){
				String color=selection.getColor(y, x);
				if(color!=null){
					if(isPointValue(color)){
						//ignore
					}else{
						selection.setColor(y,x,getColor());
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
			easyCellTableSet.addItem(newData);
			easyCellTableSet.setSelected(newData, true);
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
				LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				int baseSplit=baseSizeListBox.getValue();
				
				int[] result=calcurateGrid(element.getWidth(),element.getHeight(),baseSplit);
				
				
				final GridImageData data=new GridImageData(file.getFileName(), asStringText,result[0],result[1],result[2],element.getWidth(),element.getHeight());
				
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
	
	GridImageData selection;




	private VerticalPanel downloadArea;




	private ValueListBox<Integer> baseSizeListBox;




	private ColorBox colorBox;
	public void doSelect(GridImageData selection) {
		this.selection=selection;
		if(selection==null){
			canvas.setVisible(false);
		}else{
			canvas.setVisible(true);
			//ImageElement element=ImageElementUtils.create(selection.getDataUrl());
			//ImageElementUtils.copytoCanvas(element, canvas);
			updateCanvas();
		}
	}
	
	private boolean isStampMode(){
		return stampCheck.getValue();
	}
	
	private Point target;




	private CheckBox stampCheck;
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
		LogUtils.log(getColor());
		LogUtils.log("click:"+cx+","+cy);
		if(selection==null){
			return;
		}
		int atX=cx/selection.getGridSize();
		int atY=cy/selection.getGridSize();
		LogUtils.log("at:"+atX+","+atY);
		ImageElement element=ImageElementUtils.create(selection.getDataUrl());
		if(isStampMode()){
			if(target==null){
				target=new Point(atX,atY);
				canvas.getContext2d().setStrokeStyle("#888");
				int dx=selection.getGridSize()*atX;
				int dy=selection.getGridSize()*atY;
				canvas.getContext2d().strokeRect(dx+1, dy+1, selection.getGridSize()-2,selection.getGridSize()-2);
				return;
			}else{
				if(target.equals(atX,atY)){
					selection.pushAt(atY, atX, null);
					updateCanvasAt(target.getX(),target.getY(),element);	
				}else{
				int size=selection.getGridSize();
				int dx=selection.getGridSize()*atX;
				int dy=selection.getGridSize()*atY;
				int sx=selection.getGridSize()*target.getX();
				int sy=selection.getGridSize()*target.getY();
				
				LogUtils.log(sx+","+sy+","+dx+","+dy);
				canvas.getContext2d().drawImage(element, sx, sy, size, size, dx, dy, size, size);
				
				updateCanvasAt(target.getX(),target.getY(),element);//reupdate
				selection.pushAt(atY, atX, target.toString());
				
				}
				target=null;
				return;
			}
		}else{
		selection.pushAt(atY, atX, getColor());
		}
		
		updateCanvasAt(atX,atY,element);
		//updateCanvas();
	}
	
	public void updateCanvasAt(int x,int y,ImageElement element){
		int gridSize=selection.getGridSize();
		String color=selection.getColor(y, x);
		int dx=gridSize*x;
		int dy=gridSize*y;
		if(color!=null){
			if(isPointValue(color)){
				Point target=Point.from(color);
				int sx=gridSize*target.getX();
				int sy=gridSize*target.getY();
				
				canvas.getContext2d().drawImage(element, sx, sy, gridSize, gridSize, dx, dy, gridSize, gridSize);
			}else{
				canvas.getContext2d().setFillStyle(color);
				canvas.getContext2d().fillRect(dx, dy, selection.getGridSize(),selection.getGridSize());
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
		
	}
	private boolean isPointValue(String line){
		return line.indexOf("x")!=-1;
	}
	public void updateCanvas(){
		
		//draw all
		ImageElement element=ImageElementUtils.create(selection.getDataUrl());
		ImageElementUtils.copytoCanvas(element, canvas);
		
		//draw lines
		//TODO
		
		int gridSize=selection.getGridSize();
		//TODO optimize
		for(int y=0;y<selection.getRow();y++){
			for(int x=0;x<selection.getCol();x++){
				String color=selection.getColor(y, x);
				if(color!=null){
					
					int dx=gridSize*x;
					int dy=gridSize*y;
					if(isPointValue(color)){
						Point target=Point.from(color);
						int sx=gridSize*target.getX();
						int sy=gridSize*target.getY();
						canvas.getContext2d().drawImage(element, sx, sy, gridSize, gridSize, dx, dy, gridSize, gridSize);
					}else{
						canvas.getContext2d().setFillStyle(color);
						canvas.getContext2d().fillRect(dx, dy, gridSize,gridSize);
					}
				}
			}
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
	
	public class GridImageData{
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

		public GridImageData(String fileName, String dataUrl,int gridSize,int row,int col,int imageWidth,int imageHeight) {
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

		public void setColor(int rowKey,int columnKey,String value){
			gridTable.put(rowKey, columnKey, value);
		}
		public void pushAt(int rowKey,int columnKey,String value){
			if(value!=null && !value.startsWith("#")){
				gridTable.put(rowKey, columnKey, value);//position at
				return;
			}
			if(gridTable.get(rowKey, columnKey)==null){
				gridTable.put(rowKey, columnKey, value);
			}else{
				gridTable.put(rowKey, columnKey,null);
			}
			
		}
		
		public String getColor(int rowKey,int columnKey){
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
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		private String dataUrl;
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
