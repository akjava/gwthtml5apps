package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Optional;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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
	private EasyCellTableObjects<ImageElementData> easyCellTableObjects;


	@Override
	public Panel initializeWidget() {
		
	root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					GifPlayer.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
	
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		root.addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				if(event.getDeltaY()>0){
					doNext();
				}else{
					doPrev();
				}
			}
		});
		
		
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

		
	
		hideBt = new Button("hide control",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hideWidgets();
			}
		});
		HorizontalPanel hpanel=new HorizontalPanel();
		hpanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(hpanel);
		hpanel.add(hideBt);
		hideBt.setEnabled(false);
	
		Button stop=new Button("stop",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				easyCellTableObjects.unselect();
			}
		});
		hpanel.add(stop);
		hpanel.add(new Label("size"));
		sizeBox = new ListBox();
		sizeBox.addItem("full height");
		sizeBox.addItem("full width");
		sizeBox.addItem("normal");
		sizeBox.setSelectedIndex(0);
		hpanel.add(sizeBox);
		sizeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateImageSize();
			}
		});
		
		
		SimpleCellTable<ImageElementData> cellTable = new SimpleCellTable<ImageElementData>(999) {
			@Override
			public void addColumns(CellTable<ImageElementData> table) {
				 ButtonColumn<ImageElementData> removeBtColumn=new ButtonColumn<ImageElementData>() {
						@Override
						public void update(int index, ImageElementData object,
								String value) {
								easyCellTableObjects.removeItem(object);
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
		easyCellTableObjects=new EasyCellTableObjects<ImageElementData>(cellTable,false) {
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
		
		updateImageSize();
		
		mainPanel = new DeckLayoutPanel();
		//mainPanel.setSize("100%", "100%");
		dock.add(mainPanel);
		mainPanel.add(image);
		mainPanel.showWidget(0);
		
		
		image.setVisible(false);
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showWidgets();
			}
		});
		
		return root;
	}
	


	
	



	

	
	

	
	
	protected void doPrev() {
		int size=easyCellTableObjects.getDatas().size();
		if(size>0){
			if(selection!=null){
				int index=easyCellTableObjects.getDatas().indexOf(selection);
				if(index-1<0){
					index=size-1;
				}else{
					index=index-1;
				}
				easyCellTableObjects.setSelected(easyCellTableObjects.getDatas().get(index), true);//select next;
			}
		}
	}















	protected void doNext() {
		int size=easyCellTableObjects.getDatas().size();
		if(size>0){
			if(selection!=null){
				int index=easyCellTableObjects.getDatas().indexOf(selection);
				if(index+1>=size){
					index=0;
				}else{
					index=index+1;
				}
				easyCellTableObjects.setSelected(easyCellTableObjects.getDatas().get(index), true);//select next;
			}
		}
	}















	private void updateImageSize(){
		int selection=sizeBox.getSelectedIndex();
		if(selection==0){
			image.getElement().removeAttribute("style");//I'm not sure right way to remove width
			image.setHeight("100%");
		}else if(selection==1){
			image.getElement().removeAttribute("style");
			image.setWidth("100%");
		}else{
			image.getElement().removeAttribute("style");
			
		}
		
		LogUtils.log(image);
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

	
	ImageElementData selection;





	private Image image;
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DropDockDataUrlRootPanel root;
	private Button hideBt;
	private DeckLayoutPanel mainPanel;




	




	
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

	

	private boolean show=true;
	private ListBox sizeBox;
	
	private void showWidgets() {
		if(!show){
		image.removeFromParent();
		mainPanel.add(image);
		mainPanel.showWidget(0);
		root.add(dock);
		updateImageSize();
		show=true;
		}
	}
	
	private void hideWidgets(){
		image.removeFromParent();
		dock.removeFromParent();
		root.add(image);
		updateImageSize();
		show=false;
	}





	



	@Override
	public String getAppName() {
		return "GifPlayer";
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
		return "http://android.akjava.com/html5apps/index.html#gifplayer";
	}
	
}
