package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.webkit.FileEntry;
import com.akjava.gwt.lib.client.CanvasResizer;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
	private EasyCellTableSet<ImageData> easyCellTableSet;

	private ValueListBox<Integer> sizesList;
	@Override
	public void initializeWidget() {
		DataUrlDropDockRootPanel root=new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				
				SimpleResize.this.loadFile(file, dataUrl);
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
		
		sizesList = new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer value) {
				return ""+value;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
			}
		});
		
		canvas.setCoordinateSpaceHeight(360);
		canvas.setCoordinateSpaceWidth(360);
		
		HorizontalPanel controler=new HorizontalPanel();
		controler.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		controler.setSpacing(2);
		//topPanel.add(controler);
		
		controler.add(new Label("Width:"));
		sizesList.setValue(360);
		sizesList.setAcceptableValues(Lists.newArrayList(180,360,480,640,720,800,920,1280,1600));
		controler.add(sizesList);
		sizesList.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				try{
				canvas.setCoordinateSpaceHeight(event.getValue());
				canvas.setCoordinateSpaceWidth(event.getValue());
				
				}catch(Exception e){
					e.printStackTrace();
					LogUtils.log(e.getMessage());
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
		controler.add(fileUp);

		SimpleCellTable<ImageData> cellTable = new SimpleCellTable<SimpleResize.ImageData>(999) {
			@Override
			public void addColumns(CellTable<ImageData> table) {
				 ButtonColumn<ImageData> removeBtColumn=new ButtonColumn<ImageData>() {
						@Override
						public void update(int index, ImageData object,
								String value) {
								easyCellTableSet.removeItem(object);
						}
						@Override
						public String getValue(ImageData object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					
					HtmlColumn<ImageData> downloadColumn=new HtmlColumn<ImageData> (new SafeHtmlCell()){
						@Override
						public String toHtml(ImageData data) {
							Anchor a=HTML5Download.get().generateBase64DownloadLink(data.getDataUrl(), "image/png", data.getFileName(), "Download", false);
							a.setStylePrimaryName("bt");
							return a.toString();
						}
						
					};
					table.addColumn(downloadColumn);
					
				    TextColumn<ImageData> fileInfoColumn = new TextColumn<ImageData>() {
					      public String getValue(ImageData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					    
					    TextColumn<ImageData> widthColumn = new TextColumn<ImageData>() {
						      public String getValue(ImageData value) {
						    	 
						    	  return ""+value.getWidth();
						      }
						    };
						    table.addColumn(widthColumn,"Width");
					    
			}
		};
		
		DockLayoutPanel eastPanel=new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 30);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableSet=new EasyCellTableSet<SimpleResize.ImageData>(cellTable,false) {
			@Override
			public void onSelect(ImageData selection) {
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
		
	}
	
	protected void loadFile(File file, String asStringText) {
		try{
			//TODO create method
		ImageElement element=ImageElementUtils.create(asStringText);
		
		//resize in here
		int width=sizesList.getValue();
		String dataUrl=CanvasResizer.on(canvas).image(element).width(width).toPngDataUrl();
		
		
		//mainImage.setUrl("resize/clear.cache.gif");
		//mainImage.setVisible(true);
		
		final ImageData data=new ImageData(file.getFileName(), dataUrl);
		data.setWidth(width);
		easyCellTableSet.addItem(data);
		//updateList();
		
		
		//stack on mobile,maybe because of called async method
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				easyCellTableSet.setSelected(data, true);
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
	
	public void doSelect(ImageData selection) {
		if(selection==null){
			mainImage.setVisible(false);
			mainImage.setUrl("");
		}else{
		mainImage.setUrl(selection.getDataUrl());
		mainImage.setWidth(selection.getWidth()+"px");
		mainImage.setVisible(true);
		}
		//LogUtils.log("set-visible");
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



	@Override
	public String getAppName() {
		return "SimpleResize";
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
		return "http://android.akjava.com/html5apps/index.html#simpleresize";
	}
	
}
