package com.akjava.gwt.androidhtml5.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.jsgif.client.GifAnimeBuilder;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
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
public class GifMaker extends Html5DemoEntryPoint {

	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableSet<ImageUrlData> easyCellTableSet;
	private Button makeBt;


	@Override
	public void initializeWidget() {
		root = new DataUrlDropDockRootPanel(Unit.PX,true){
			@Override
			public void loadFile(File file, String dataUrl) {
				GifMaker.this.loadFile(file, dataUrl);
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

		downloadArea = new HorizontalPanel();
		controler.add(downloadArea);
		
	
		makeBt = new Button("Make",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				
				List<ImageElement> elements=FluentIterable.from(easyCellTableSet.getDatas()).transform(new DataToImageElement()).toList();
				
				final String url=GifAnimeBuilder.from(elements).lowQuolity().loop().delay(300).toDataUrl();
				image.setVisible(true);
				image.setUrl(url);
				
				//create buttons
				downloadArea.clear();
				Anchor a=HTML5Download.get().generateBase64DownloadLink(url, "image/gif", "craeted.gif", "Download Gif", false);
				a.setStylePrimaryName("bt");
				downloadArea.add(a);
				Button preview=new Button("Preview",new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						image.setVisible(true);
						image.setUrl(url);
					}
				});
				downloadArea.add(preview);
				
			}
		});
		controler.add(makeBt);
		makeBt.setEnabled(false);
	
		
		
		
		
		SimpleCellTable<ImageUrlData> cellTable = new SimpleCellTable<GifMaker.ImageUrlData>(999) {
			@Override
			public void addColumns(CellTable<ImageUrlData> table) {
				 ButtonColumn<ImageUrlData> removeBtColumn=new ButtonColumn<ImageUrlData>() {
						@Override
						public void update(int index, ImageUrlData object,
								String value) {
								easyCellTableSet.removeItem(object);
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
					    
					    ButtonColumn<ImageUrlData> upBtColumn=new ButtonColumn<ImageUrlData>() {
							@Override
							public void update(int index, ImageUrlData object,
									String value) {
									
									easyCellTableSet.upItem(object);
							}
							@Override
							public String getValue(ImageUrlData object) {
								 return "Up";
							}
						};
						table.addColumn(upBtColumn);
						
						 ButtonColumn<ImageUrlData> downBtColumn=new ButtonColumn<ImageUrlData>() {
								@Override
								public void update(int index, ImageUrlData object,
										String value) {
										easyCellTableSet.downItem(object);
								}
								@Override
								public String getValue(ImageUrlData object) {
									 return "Down";
								}
							};
							table.addColumn(downBtColumn);
					    
					    
			}
		};
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 100);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableSet=new EasyCellTableSet<GifMaker.ImageUrlData>(cellTable,false) {
			@Override
			public void onSelect(ImageUrlData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		dock.add(mainScrollPanel);
		
		image = new Image();
		
		image.setHeight("100%");
		mainScrollPanel.add(image);
		image.setVisible(false);
	
		
		
	}
	


	
	



	

	
	

	/**
	 * watch out IE maybe problem
	 * @author aki
	 *
	 */
	public class DataToImageElement implements Function<ImageUrlData,ImageElement>{

		@Override
		public ImageElement apply(ImageUrlData input) {
			return ImageElementUtils.create(input.getUrl());
		}
		
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

	
	ImageUrlData selection;





	private Image image;
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DataUrlDropDockRootPanel root;
	private HorizontalPanel downloadArea;


	




	
	public void doSelect(ImageUrlData selection) {
		this.selection=selection;
		if(selection==null){
			image.setUrl("");
			image.setVisible(false);
			
		}else{
			
			//LogUtils.log(selection.getImageElement());
			
			image.setUrl(selection.getUrl());
			
			
			
			image.setVisible(true);
			//mainScrollPanel.add(image);
			
		}
	}

	

	
	





	public class ImageUrlData{

		

		private String url;

		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public ImageUrlData(String fileName,String url) {
			super();
			this.fileName = fileName;
			this.url = url;
		}
		
		private String fileName;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	
	}



	@Override
	public String getAppName() {
		return "GifMaker";
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
		return "http://android.akjava.com/html5apps/index.html#gifmaker";
	}
	
}
