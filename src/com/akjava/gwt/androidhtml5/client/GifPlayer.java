package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
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


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class GifPlayer extends Html5DemoEntryPoint {

	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableSet<ImageElementData> easyCellTableSet;


	@Override
	public void initializeWidget() {
		root = new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				GifPlayer.this.loadFile(file, dataUrl);
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
		
		topPanel.add(new Anchor("Help", "gifplayer_help.html"));
	
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				loadFile(file, value);
			}
		}, true,false);//base component catch everything
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		controler.add(fileUps);
		fileUps.add(upload);

		
	
		hideBt = new Button("hide",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hideWidgets();
			}
		});
		controler.add(hideBt);
		hideBt.setEnabled(false);
	
		
		
		
		
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
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 100);
		
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
		
		
		
		
		
		
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		//dock.add(mainScrollPanel);
		
		image = new Image();
		
		image.setHeight("100%");
		dock.add(image);
		image.setVisible(false);
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showWidgets();
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

	
	ImageElementData selection;





	private Image image;
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DataUrlDropDockRootPanel root;
	private Button hideBt;




	




	
	public void doSelect(ImageElementData selection) {
		this.selection=selection;
		if(selection==null){
			image.setUrl("");
			image.setVisible(false);
			hideBt.setEnabled(false);
		}else{
			hideBt.setEnabled(true);
			//LogUtils.log(selection.getImageElement());
			
			image.setUrl(selection.getDataUrl());
			
			
			
			image.setVisible(true);
			//mainScrollPanel.add(image);
			
		}
	}

	

	
	private void showWidgets() {
		image.removeFromParent();
		dock.add(image);
		root.add(dock);
		image.setHeight("100%");
	}
	
	private void hideWidgets(){
		image.removeFromParent();
		dock.removeFromParent();
		root.add(image);
		image.setHeight("100%");
	}





	



	@Override
	public String getAppName() {
		return "GifPlayer";
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
		return "http://android.akjava.com/html5apps/index.html#gifplayer";
	}
	
}
