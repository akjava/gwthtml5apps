package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasResizer;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
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
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class SimpleResize implements EntryPoint {

	private Canvas canvas;
	List<ImageData> datas=new ArrayList<SimpleResize.ImageData>();
	private SimpleCellTable<ImageData> imageDataCellList;
	
	private SingleSelectionModel<ImageData> selectionModel;
	private Image mainImage;
	
	private String appName="SimpleResize";
	private String version="1.0";
	private DockLayoutPanel dock;
	@Override
	public void onModuleLoad() {
		LogUtils.log(appName+":version "+version);
		canvas = Canvas.createIfSupported();
		//canvas.setSize("100%", "100%");
		
		
		
		dock = new DockLayoutPanel(Unit.PX);
		
		HorizontalPanel top=new HorizontalPanel();
		top.setWidth("100%");
		top.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		top.setSpacing(4);
		dock.addNorth(top,40);
		
		Label label=new Label("akjava.com "+appName+" "+version);
		label.setStylePrimaryName("title");
		top.add(label);
		
		final ValueListBox<Integer> sizesList=new ValueListBox<Integer>(new Renderer<Integer>() {
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
		top.add(controler);
		
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
		
		//dock.add(canvas);
		//canvas.removeFromParent();
		//dock.add(canvas);
		RootLayoutPanel.get().add(dock);
		
		FileUploadForm fileUp=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String asStringText) {
				try{
					//TODO create method
				ImageElement element=Document.get().createImageElement();
				element.setSrc(asStringText);
				
				//resize in here
				int width=sizesList.getValue();
				String dataUrl=CanvasResizer.on(canvas).image(element).width(width).toPngDataUrl();
				
				
				//mainImage.setUrl("resize/clear.cache.gif");
				//mainImage.setVisible(true);
				
				final ImageData data=new ImageData(file.getFileName(), dataUrl);
				data.setWidth(width);
				datas.add(data);
				updateList();
				
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						selectionModel.setSelected(data, true);
					}
				});
				
				//doSelecct(data);//only way to update on Android Chrome
				}catch (Exception e) {
					e.printStackTrace();
					LogUtils.log(e.getMessage());
				}
			}
		}, false);
		controler.add(fileUp);
		
		imageDataCellList = new SimpleCellTable<SimpleResize.ImageData>(999) {
			@Override
			public void addColumns(CellTable<ImageData> table) {
				 ButtonColumn<ImageData> removeBtColumn=new ButtonColumn<ImageData>() {
						@Override
						public void update(int index, ImageData object,
								String value) {
								datas.remove(object);
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
		imageDataCellList.setWidth("100%");
		
		
		dock.addEast(imageDataCellList, 400);
		
		selectionModel=new SingleSelectionModel<SimpleResize.ImageData>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				doSelecct(selectionModel.getSelectedObject());
			}
		});
		
		imageDataCellList.setSelectionModel(selectionModel);
		imageDataCellList.getPager().setVisible(false);
		
		mainImage = new Image();
		
		//mainImage.setVisible(false);
		//mainImage.setWidth("100%");
		
		ScrollPanel scroll=new ScrollPanel();
		scroll.setWidth("100%");
		scroll.setHeight("100%");
		dock.add(scroll);
		scroll.add(mainImage);
		
		dock.forceLayout();
	}
	
	public void clearImage(){
		//GWT.getModuleName()+"/"
	}
	
	protected void doSelecct(ImageData selection) {
		mainImage.setUrl(selection.getDataUrl());
		mainImage.setWidth(selection.getWidth()+"px");
		//mainImage.setVisible(true);
		//LogUtils.log("set-visible");
	}

	public void updateList(){
		imageDataCellList.setData(datas);
		//imageDataCellList.getCellTable().setFocus(true);
		//imageDataCellList.getCellTable().redraw();
		//LogUtils.log("updateList-focus-redraw");
		//dock.forceLayout();
		//imageDataCellList.getCellTable().flush();
		//LogUtils.log("layout");
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

	private void resizeCanvas(){
		canvas.getContext2d().setFillStyle("#080");
		canvas.getContext2d().fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		canvas.getContext2d().setFillStyle("#000");
		canvas.getContext2d().fillRect(0, 0, 50,50);
	}
	
}
