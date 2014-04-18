package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.androidhtml5.client.data.ImageUrlData;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;



public class UVPack extends Html5DemoEntryPoint {

	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	//EasyCellTableObjects<T>
	private EasyCellTableObjects<ImageUrlData> easyCellTableObjects;
	private Button makeBt;
	

	@Override
	public Panel initializeWidget() {
		
		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					UVPack.this.loadFile(file, dataUrl);
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
		
		topPanel.add(new Anchor("Help", "uvpack_help.html"));
	
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				if(file!=null){
					loadFile(file, value);
				}
			}
		}, true,false);//base component catch everything
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		controler.add(fileUps);
		fileUps.add(upload);

		downloadArea = new HorizontalPanel();
		downloadArea.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		fileUps.add(downloadArea);
		
	
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(h1);
		makeBt = new Button("PackImage",new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CanvasUtils.fillRect(canvas,"#888888");
				double hw=canvas.getCoordinateSpaceWidth()/2;
				double hh=canvas.getCoordinateSpaceHeight()/2;
				if(easyCellTableObjects.getDatas().size()>0){
					String url=easyCellTableObjects.getDatas().get(0).getDataUrl();
					canvas.getContext2d().drawImage(ImageElementUtils.create(url),0,0,hw,hh);
				}
				
				if(easyCellTableObjects.getDatas().size()>1){
					String url=easyCellTableObjects.getDatas().get(1).getDataUrl();
					canvas.getContext2d().drawImage(ImageElementUtils.create(url),hw,0,hw,hh);
				}
				
				if(easyCellTableObjects.getDatas().size()>2){
					String url=easyCellTableObjects.getDatas().get(2).getDataUrl();
					canvas.getContext2d().drawImage(ImageElementUtils.create(url),0,hh,hw,hh);
				}
				
				if(easyCellTableObjects.getDatas().size()>3){
					String url=easyCellTableObjects.getDatas().get(3).getDataUrl();
					canvas.getContext2d().drawImage(ImageElementUtils.create(url),hw,hh,hw,hh);
				}
				String url=canvas.toDataUrl();
				setImage(url);
				
				downloadArea.clear();
				
				//TODO support jpeg
				Anchor a=HTML5Download.get().generateBase64DownloadLink(url, "image/jpeg", "uvpack.jpg", "Download Jpeg", false);
				a.setStylePrimaryName("bt");
				downloadArea.add(a);
			}
			
		});
		h1.add(makeBt);
		makeBt.setEnabled(false);
		
	
		
		
		
		
		SimpleCellTable<ImageUrlData> cellTable = new SimpleCellTable<ImageUrlData>(999) {
			@Override
			public void addColumns(CellTable<ImageUrlData> table) {
				 ButtonColumn<ImageUrlData> removeBtColumn=new ButtonColumn<ImageUrlData>() {
						@Override
						public void update(int index, ImageUrlData object,
								String value) {
							easyCellTableObjects.removeItem(object);
						}
						@Override
						public String getValue(ImageUrlData object) {
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
				
					
			
				    
				
				    
				   
				    
					
				    TextColumn<ImageUrlData> fileInfoColumn = new TextColumn<ImageUrlData>() {
					      public String getValue(ImageUrlData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					   table.addColumn(fileInfoColumn,"Name");
							
							 table.addColumn(new ActionCellGenerator<ImageUrlData>(){
									@Override
									public void executeAt(int index,ImageUrlData object) {
										if(index==0){
										easyCellTableObjects.upItem(object);
										}else{
											easyCellTableObjects.downItem(object);
										}
									}										
									}.generateColumn(Lists.newArrayList("up","down")));
					    
					    
			}
		};
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 100);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableObjects=new EasyCellTableObjects<ImageUrlData>(cellTable,false) {
			@Override
			public void onSelect(ImageUrlData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		canvas=CanvasUtils.createCanvas(512, 512);//TODO size list
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		dock.add(mainScrollPanel);
		
		image = new Image();
		
		image.setHeight("100%");
		mainScrollPanel.add(image);
		image.setVisible(false);
	
		
		return root;
	}
	


private Canvas canvas;

	

	
	

	/**
	 * watch out IE maybe problem
	 * @author aki
	 *
	 */

	private boolean ignoreUnselectImage;
	private void setImage(final String url) {
		ignoreUnselectImage=true;
		easyCellTableObjects.unselect();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				ignoreUnselectImage=false;
				//called after once unselected
				image.setVisible(true);
				image.setUrl(url);
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
				
				
				final ImageUrlData data=new ImageUrlData(file.getFileName(),asStringText);
				
				easyCellTableObjects.addItem(data);
				//updateList();
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableObjects.setSelected(data, true);
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

	
	ImageUrlData selection;





	private Image image;
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DataUrlDropDockRootPanel root;
	private HorizontalPanel downloadArea;



	




	
	public void doSelect(ImageUrlData selection) {
		this.selection=selection;
		if(selection==null){
			if(!ignoreUnselectImage){//when set gif no need do this
			image.setUrl("");
			image.setVisible(false);
			}
			
		}else{
			
			//LogUtils.log(selection.getImageElement());
			
			image.setUrl(selection.getDataUrl());
			
			
			
			image.setVisible(true);
			//mainScrollPanel.add(image);
			
		}
	}

	

	
	








	@Override
	public String getAppName() {
		return "UVPack";
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
		return "http://android.akjava.com/html5apps/index.html#uvpack";
	}
	
}
