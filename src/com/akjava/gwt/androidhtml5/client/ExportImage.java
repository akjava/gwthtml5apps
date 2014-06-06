package com.akjava.gwt.androidhtml5.client;


import javax.swing.border.StrokeBorder;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.HtmlColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.io.FileType;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.SafeHtmlCell;
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
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ExportImage extends Html5DemoEntryPoint {

	public final String KEY_BASE_NAME="exportimage_key_basename";
	public final String KEY_BASE_PATH="exportimage_key_basepath";
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableObjects<ImageElementCaptionData> easyCellTableObjects;
	private TextBox pathBox;

	private StorageControler storageControler=new StorageControler();

	@Override
	public Panel initializeWidget() {
		
	root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					ExportImage.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		dock = new DockLayoutPanel(Unit.PX);
		root.add(dock);
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setStylePrimaryName("bg1");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(1);
		dock.addNorth(topPanel,30);
		
		
		topPanel.add(createTitleWidget());
		
		topPanel.add(new Anchor("Help", "exportimage_help.html"));
	
		
		
		VerticalPanel controler=new VerticalPanel();
		controler.setSpacing(1);
		
		
		HorizontalPanel namePanel=new HorizontalPanel();
		namePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		namePanel.add(new Label("BaseName"));
		nameBox = new TextBox();
		namePanel.add(nameBox);
		nameBox.setValue(storageControler.getValue(KEY_BASE_NAME, "image"));
		Button updateBt=new Button("Update",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateList();
				try {
					storageControler.setValue(KEY_BASE_NAME, nameBox.getValue());
				} catch (StorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Window.alert(e.getMessage());
				}
			}
		});
		namePanel.add(updateBt);
		controler.add(namePanel);
		
		HorizontalPanel dirPanel=new HorizontalPanel();
		dirPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		dirPanel.add(new Label("ImagePath"));
		pathBox = new TextBox();
		dirPanel.add(pathBox);
		pathBox.setValue(storageControler.getValue(KEY_BASE_PATH, "/img/"));
		Button update3Bt=new Button("Update",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateList();
				try {
					storageControler.setValue(KEY_BASE_PATH, pathBox.getValue());
				} catch (StorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Window.alert(e.getMessage());
				}
			}
		});
		dirPanel.add(update3Bt);
		controler.add(dirPanel);
		
		
		HorizontalPanel captionPanel=new HorizontalPanel();
		captionPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		captionPanel.add(new Label("Image Caption"));
		
		
		markedTextArea = new TextArea();
		markedTextArea.setSize("350px", "200px");
		controler.add(markedTextArea);
		
		
		FileUploadForm upload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				loadFile(file, value);
			}
		}, true,false);//base component catch everything
		
		
		HorizontalPanel fileUps=new HorizontalPanel();
		controler.add(fileUps);
		fileUps.add(upload);

		
		
		SimpleCellTable<ImageElementCaptionData> cellTable = new SimpleCellTable<ImageElementCaptionData>(999) {
			@Override
			public void addColumns(CellTable<ImageElementCaptionData> table) {
				 ButtonColumn<ImageElementCaptionData> removeBtColumn=new ButtonColumn<ImageElementCaptionData>() {
						@Override
						public void update(int index, ImageElementCaptionData object,
								String value) {
								easyCellTableObjects.removeItem(object);
						}
						@Override
						public String getValue(ImageElementCaptionData object) {
							 return "X";
						}
					};
					table.addColumn(removeBtColumn);
					
				    TextColumn<ImageElementCaptionData> fileInfoColumn = new TextColumn<ImageElementCaptionData>() {
					      public String getValue(ImageElementCaptionData value) {
					    	  return Ascii.truncate(value.getCaption()+" "+value.getFileName(), 8, "..");
					    	  
					      }
					    };
					    table.addColumn(fileInfoColumn,"Name");
					   
					    
					    
					    
					    table.addColumn(new ActionCellGenerator<ImageElementCaptionData>(){
							@Override
							public void executeAt(int index,ImageElementCaptionData object) {
								if(index==0){
								easyCellTableObjects.upItem(object);
								}else{
									easyCellTableObjects.downItem(object);
								}
							}										
							}.generateColumn(Lists.newArrayList("Up","Down")));
					    
					    
					    
					    HtmlColumn<ImageElementCaptionData> downloadColumn=new HtmlColumn<ImageElementCaptionData> (new SafeHtmlCell()){
							@Override
							public String toHtml(ImageElementCaptionData data) {
								String extension=FileUtils.getExtension(data.getFileName());
								FileType fileType=FileType.getFileTypeByExtension(extension);
								if(fileType==null){
									return "";
								}
								int index=easyCellTableObjects.getDatas().indexOf(data)+1;//start 0
								
								Anchor anchor=HTML5Download.get().generateBase64DownloadLink(data.getDataUrl(), fileType.getMimeType(), nameBox.getValue()+index+"."+extension, "Download"	, false);
								anchor.setStylePrimaryName("bt");
								return anchor.toString();
								//return data.getDownloadLink().toString();
							}
							
						};
						table.addColumn(downloadColumn);
					    
			}
		};
		
		imageCaptionBox = new TextBox();
		imageCaptionBox.setEnabled(false);
		captionPanel.add(imageCaptionBox);
		imageCaptionBox.setWidth("200px");
		Button update2Bt=new Button("Update",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateCaption();
			}
		});
		captionPanel.add(update2Bt);
		controler.add(captionPanel);
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 350);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		easyCellTableObjects=new EasyCellTableObjects<ImageElementCaptionData>(cellTable,false) {
			@Override
			public void onSelect(ImageElementCaptionData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		
		
		image = new Image();
		
		mainPanel = new DeckLayoutPanel();
		//mainPanel.setSize("100%", "100%");
		dock.add(mainPanel);
		mainPanel.add(image);
		mainPanel.showWidget(0);
		
		
		image.setVisible(false);
		
		
		return root;
	}
	

	private String toImageFileName(ImageElementCaptionData data){
		String extension=FileUtils.getExtension(data.getFileName());
		FileType fileType=FileType.getFileTypeByExtension(extension);
		if(fileType==null){
			return "";
		}
		int index=easyCellTableObjects.getDatas().indexOf(data)+1;//start 0
		return nameBox.getText()+index+"."+extension;
	}

	
	



	

	protected void updateCaption() {
		if(selection!=null){
			selection.setCaption(imageCaptionBox.getText());
			int index=easyCellTableObjects.getDatas().indexOf(selection);
			easyCellTableObjects.getSimpleCellTable().getCellTable().redrawRow(index);
		}
		
		updateMarkedText();
	}










	private void updateMarkedText() {
		LogUtils.log("update");
		String result="";
		
		for(ImageElementCaptionData data:easyCellTableObjects.getDatas()){
			String image=toImageFileName(data);
			result+=data.getCaption()+"\n";
			result+="\n";
			result+="[]("+pathBox.getValue()+image+")\n";
			result+="\n";
			result+="\n";
		}
		
		markedTextArea.setText(result);
		
	}










	public class ImageElementCaptionData extends ImageElementData{
		private String caption;
		public String getCaption() {
			if(caption==null){
				return "";
			}
			return caption;
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		public ImageElementCaptionData(String fileName, ImageElement imageElement, String dataUrl) {
			super(fileName, imageElement, dataUrl);
		}
		
	}
	












	protected void updateList() {
		easyCellTableObjects.update();
		updateMarkedText();
	}
























	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(ImageElement element) {
				LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				
				final ImageElementCaptionData data=new ImageElementCaptionData(file.getFileName(),element,asStringText);
				
				easyCellTableObjects.addItem(data);
				//updateList();
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						easyCellTableObjects.setSelected(data, true);
						
						updateMarkedText();
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

	
	ImageElementCaptionData selection;





	private Image image;
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DropDockDataUrlRootPanel root;
	
	private DeckLayoutPanel mainPanel;




	




	
	public void doSelect(ImageElementCaptionData selection) {
		this.selection=selection;
		if(selection==null){
			image.setUrl("");
			image.setVisible(false);
			
			imageCaptionBox.setEnabled(false);
			imageCaptionBox.setValue("");
		}else{
			image.setUrl(selection.getDataUrl());
			image.setVisible(true);
			
			imageCaptionBox.setEnabled(true);
			imageCaptionBox.setValue(selection.getCaption());
		}

	}

	

	private ListBox sizeBox;
	private TextBox nameBox;
	private TextBox imageCaptionBox;
	private TextArea markedTextArea;
	
	



	@Override
	public String getAppName() {
		return "ExportImage";
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
		return "http://android.akjava.com/html5apps/index.html#exportimage";
	}
	
}
