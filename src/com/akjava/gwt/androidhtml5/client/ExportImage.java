package com.akjava.gwt.androidhtml5.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasPaintUtils;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.canvas.Rect;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.io.FileType;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class ExportImage extends Html5DemoEntryPoint {

	public final String KEY_BASE_NAME="exportimage_key_basename";
	public final String KEY_BASE_PATH="exportimage_key_basepath";
	public final String KEY_USE_MARKED="exportimage_key_usemarked";
	
	public final String KEY_FONT="exportimage_key_font";
	public final String KEY_TEXT_COLOR="exportimage_key_text_color";
	public final String KEY_BG_COLOR="exportimage_key_bg_color";
	public final String KEY_BG_DIAMETER="exportimage_key_bg_diameter";
	public final String KEY_BG_TYPE="exportimage_key_bg_type";
	
	public final int BG_TYPE_SQUARE=0;
	public final int BG_TYPE_CIRCLE=1;
	public final int BG_TYPE_NONE=2;
	//public final int BG_TYPE_TRANSPARENT_SQUARE=3;
	
	
	public static final int MODE_ERASE=0;
	public static final int MODE_COLOR=3;
	public static final int MODE_NUMBER=4;
	private int penMode=MODE_COLOR;

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableObjects<ImageElementCaptionData> easyCellTableObjects;
	private TextBox pathBox;

	private StorageControler storageControler=new StorageControler();

	private int penSize=4;
	private ColorBox colorPicker;
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
		
		topPanel.add(new Anchor(textConstants.Help(), "exportimage_help.html"));
	
		topPanel.add(createSettingAnchor());
		
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
		
		HorizontalPanel clearAllPanel=new HorizontalPanel();
		controler.add(clearAllPanel);
		
		//size choose
		HorizontalPanel sizes=new HorizontalPanel();
		sizes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(sizes);
		
		Label penSizeLabel=new Label(textConstants.Pen_Size());
		sizes.add(penSizeLabel);
		final ValueListBox<Integer> sizeListBox=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
				// TODO Auto-generated method stub
				return ""+object;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		List<Integer> sizeList=Lists.newArrayList(1,2,3,4,5,6,8,12,16,24,32,48,64);
		sizeListBox.setValue(penSize);
		sizeListBox.setAcceptableValues(sizeList);
		sizeListBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				penSize=event.getValue();
			}
			
		});
		sizes.add(sizeListBox);
		
		HorizontalPanel pens=new HorizontalPanel();
		pens.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		sizes.add(pens);
		
		colorPicker = new ColorBox();
		colorPicker.setValue("#ff0000");
		pens.add(colorPicker);
		
		final RadioButton eraseR=new RadioButton("pens");
		eraseR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_ERASE;
			}
		});
		pens.add(eraseR);
		pens.add(new Label(textConstants.Erase()));
		
		RadioButton pickR=new RadioButton("pens");
		pens.add(pickR);
		pens.add(new Label(textConstants.Draw()));
		pickR.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_COLOR;
			}
		});
		pickR.setValue(true);
		
		
		RadioButton pickNumber=new RadioButton("pens");
		pens.add(pickNumber);
		pens.add(new Label(textConstants.Number()));
		pickNumber.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				penMode=MODE_NUMBER;
			}
		});
		
		
		numberBox = new ListBox();
		for(int i=0;i<=20;i++){
			numberBox.addItem(""+i);
		}
		sizes.add(numberBox);
		numberBox.setSelectedIndex(1);
		
		Button clearNumber=new Button("x",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clearNumber();
			}
		});
		sizes.add(clearNumber);
		
		
		Button clearBt=new Button(textConstants.Clear_All(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(selection==null){
					return;
				}
				CanvasUtils.clear(selection.getImageCanvas());
				updateCanvas();
			}
		});
		clearAllPanel.add(clearBt);
		
		
		
		HorizontalPanel namePanel=new HorizontalPanel();
		namePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		namePanel.add(new Label(textConstants.BaseName()));
		nameBox = new TextBox();
		namePanel.add(nameBox);
		nameBox.setValue(storageControler.getValue(KEY_BASE_NAME, "image"));
		Button updateBt=new Button(textConstants.update(),new ClickHandler() {
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
		dirPanel.add(new Label(textConstants.ImagePath()));
		pathBox = new TextBox();
		dirPanel.add(pathBox);
		pathBox.setValue(storageControler.getValue(KEY_BASE_PATH, "/img/"));
		Button update3Bt=new Button(textConstants.update(),new ClickHandler() {
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
		
		markedCheck = new CheckBox(textConstants.use_marked());
		markedCheck.setValue(storageControler.getValue(KEY_USE_MARKED, false));
		markedCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				try {
					storageControler.setValue(KEY_USE_MARKED, event.getValue());
				} catch (StorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateMarkedText();
			}
			
		});
		dirPanel.add(markedCheck);
		controler.add(dirPanel);
		
		
		HorizontalPanel captionPanel=new HorizontalPanel();
		captionPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		captionPanel.add(new Label(textConstants.Image_Caption()));
		
		
		markedTextArea = new TextArea();
		markedTextArea.setSize("350px", "180px");
		controler.add(markedTextArea);
		
		downloadLinks = new HorizontalPanel();
		controler.add(downloadLinks);
		
		

		
		
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
					    table.addColumn(fileInfoColumn,textConstants.Name());
					   
					    
					    
					    
					    table.addColumn(new ActionCellGenerator<ImageElementCaptionData>(){
							@Override
							public void executeAt(int index,ImageElementCaptionData object) {
								if(index==0){
								easyCellTableObjects.upItem(object);
								}else{
									easyCellTableObjects.downItem(object);
								}
								updateMarkedText();
							}										
							}.generateColumn(Lists.newArrayList(textConstants.UP(),textConstants.DOWN())));
					    
					    
					    //if use downloa here need update every mouse-up event,it's really slow
					    /*
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
						*/
						
						table.addColumn(new ButtonColumn<ExportImage.ImageElementCaptionData>() {

							@Override
							public void update(int index, ImageElementCaptionData data, String value) {
								downloadLinks.clear();
								String extension=FileUtils.getExtension(data.getFileName());
								FileType fileType=FileType.getFileTypeByExtension(extension);
								if(fileType==null){
									//return "";
								}
								int imgIndex=easyCellTableObjects.getDatas().indexOf(data)+1;//start 0
								
								drawCanvas(data);//need redraw here.because when pushed button's row is not selected,doSelect called after update and canvas was not drawed.
								String name=nameBox.getValue()+imgIndex+"."+extension;
								Anchor anchor=HTML5Download.get().generateBase64DownloadLink(canvas.toDataUrl(), fileType.getMimeType(),name, name	, true);
								//anchor.setStylePrimaryName("bt");
								downloadLinks.add(anchor);
							}

							@Override
							public String getValue(ImageElementCaptionData object) {
								// TODO Auto-generated method stub
								return textConstants.updateimage();
							}
							
						});
						
						
					    
			}
		};
		
		imageCaptionBox = new TextBox();
		imageCaptionBox.setEnabled(false);
		captionPanel.add(imageCaptionBox);
		imageCaptionBox.setWidth("200px");
		Button update2Bt=new Button(textConstants.update(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateCaption();
			}
		});
		captionPanel.add(update2Bt);
		controler.add(captionPanel);
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 380);
		
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
		
		
		canvas = Canvas.createIfSupported();
		
		
		mainPanel = new DeckLayoutPanel();
		//mainPanel.setSize("100%", "100%");
		dock.add(mainPanel);
		mainPanel.add(canvas);
		mainPanel.showWidget(0);
		
		
		
	
		moveControler = new CanvasDragMoveControler(canvas, new MoveListener() {
			
			@Override
			public void start(int sx, int sy) {
				// TODO Auto-generated method stub
				if(penMode==MODE_NUMBER){
					if(moveControler.isShiftKeyDown()){
						int selection=numberBox.getSelectedIndex();
						if(selection<numberBox.getItemCount()-1){
							numberBox.setSelectedIndex(selection+1);	
						}	
					}
					NumberData number=new NumberData(numberBox.getSelectedIndex());
					number.setX(sx);
					number.setY(sy);
					addNumberToCurrent(number);
					updateCanvas();
				}
			}
			
			@Override
			public void dragged(int startX, int startY, int endX, int endY, int vectorX, int vectorY) {
				if(selection==null){
					return;
				}
				
				
				
				
				if(penMode==MODE_COLOR){
					drawLine(startX,startY,endX,endY,colorPicker.getValue());
				}else if(penMode==MODE_ERASE){
					
					erase(startX,startY,endX,endY);
				}else{
					return;
				}
				updateCanvas();
			}
			
			@Override
			public void end(int sx, int sy) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return root;
	}
	
	protected void clearNumber() {
		removeNumberToCurrent(new NumberData(numberBox.getSelectedIndex()));
		updateCanvas();
	}

	private IntegerBox makeIntegerBox(Panel parent,String name,int value){
		HorizontalPanel h1=new HorizontalPanel();
		//h.setWidth("100%");
		
		parent.add(h1);
		
		Label label=new Label(name);
		label.setWidth("100px");
		h1.add(label);
		
		IntegerBox box=new IntegerBox();
		box.setWidth("40px");
		box.setValue(value);
		h1.add(box);
		return box;
	}
	
	private TextBox makeTextBox(Panel parent,String name,String value){
		HorizontalPanel h=new HorizontalPanel();
		parent.add(h);
		
		Label label=new Label(name);
		label.setWidth("100px");
		h.add(label);
		
		TextBox box=new TextBox();
		box.setValue(value);
		h.add(box);
		return box;
	}
	
	private ColorBox makeColorBox(Panel parent,String name,String value){
		HorizontalPanel h=new HorizontalPanel();
		parent.add(h);
		
		Label label=new Label(name);
		label.setWidth("100px");
		h.add(label);
		
		ColorBox box=new ColorBox();
		box.setValue(value);
		h.add(box);
		return box;
	}
	
	private class BGType{
		private int type;
		public BGType(int type, String label) {
			super();
			this.type = type;
			this.label = label;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		private String label;
	}
	
	@Override
	 public Panel createMainSettingPage(){
			VerticalPanel panel=new VerticalPanel();
			HTML html=new HTML("<h3>"+textConstants.Number()+"</h3>");
			panel.add(html);
			
			fontBox = makeTextBox(panel,textConstants.font(),storageControler.getValue(KEY_FONT,"16px Audiowide"));
			
			
			textColorBox = makeColorBox(panel,textConstants.textcolor(),storageControler.getValue(KEY_TEXT_COLOR,  "#000000"));
			
			
			bgColorBox = makeColorBox(panel,textConstants.bgcolor(),storageControler.getValue(KEY_BG_COLOR, "#ff0000"));
			
			
			digimeterBox = makeIntegerBox(panel, textConstants.digimeter(), storageControler.getValue(KEY_BG_DIAMETER, 24));
			
			
			final List<BGType> types=Lists.newArrayList(new BGType(BG_TYPE_SQUARE, textConstants.square()),
					new BGType(BG_TYPE_CIRCLE, textConstants.circle()),
					new BGType(BG_TYPE_NONE, textConstants.none())
			);
			int selection=storageControler.getValue(KEY_BG_TYPE, 0);
			BGType bgtype=null;
			for(BGType type:types){
				if(type.getType()==selection){
					bgtype=type;
					break;
				}
			}
			if(bgtype==null){
				bgtype=types.get(0);
			}
			
			bgTypeBox = new ValueListBox<ExportImage.BGType>(new Renderer<BGType>() {

				@Override
				public String render(BGType object) {
					return object.getLabel();
				}

				@Override
				public void render(BGType object, Appendable appendable) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
			bgTypeBox.setValue(bgtype);
			bgTypeBox.setAcceptableValues(types);
			
			
			HorizontalPanel h1=new HorizontalPanel();
			panel.add(h1);
			
			Label label=new Label(textConstants.bgtype());
			label.setWidth("100px");
			h1.add(label);
			
			
			
			h1.add(bgTypeBox);
			
			Button resetBt=new Button(textConstants.reset(),new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					fontBox.setValue("16px Audiowide");
					textColorBox.setValue("#000000");
					bgColorBox.setValue("#ff0000");
					digimeterBox.setValue(24);
					bgTypeBox.setValue(types.get(0));
				}
			});
			panel.add(resetBt);
			
			//TODO create list box
			//valuelistbox
			
			/*
			 * フォント
文字色
背景色
背景形
背景直径
			 */
			
			
			return panel;
		}
	
	
	
	private void addNumberToCurrent(NumberData data){
		if(selection==null){
			return;
		}
		List<NumberData> numbers=selection.getNumbers();
		
		int index=numbers.indexOf(data);
		if(index!=-1){
			numbers.get(index).setX(data.getX());
			numbers.get(index).setY(data.getY());
		}else{
			numbers.add(data);
		}
	}
	
	private void removeNumberToCurrent(NumberData data){
		if(selection==null){
			return;
		}
		List<NumberData> numbers=selection.getNumbers();
		numbers.remove(data);
	}
	
	private List<NumberData> getCurrentNumbers(){
		if(selection==null){
			return null;
		}
		List<NumberData> numbers=selection.getNumbers();
		return numbers;
	}
	
	private class NumberData{
		int x;
		int y;
		int number;
		public NumberData(int number) {
			super();
			this.number = number;
		}
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
		public int getNumber() {
			return number;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + number;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NumberData other = (NumberData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (number != other.number)
				return false;
			return true;
		}
		private ExportImage getOuterType() {
			return ExportImage.this;
		}
		
		
	}
	
	private void erase(int x1,int y1,int x2,int y2){
		Canvas imageCanvas=selection.getImageCanvas();
		imageCanvas.getContext2d().save();
		imageCanvas.getContext2d().setLineWidth(penSize*2);
		imageCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		imageCanvas.getContext2d().setStrokeStyle("#000");
		imageCanvas.getContext2d().setGlobalCompositeOperation("destination-out");
		
		imageCanvas.getContext2d().beginPath();
		
		imageCanvas.getContext2d().moveTo(x1,y1);
		imageCanvas.getContext2d().lineTo(x2,y2);
		
		imageCanvas.getContext2d().closePath();
		imageCanvas.getContext2d().stroke();
		imageCanvas.getContext2d().restore();
	}
	
	private void drawLine(int x1,int y1,int x2,int y2,String color){
		//LogUtils.log("drawLine-before:"+canvas.getContext2d().getGlobalCompositeOperation());
		Canvas ImageCanvas=selection.getImageCanvas();
		ImageCanvas.getContext2d().save();
		ImageCanvas.getContext2d().setLineWidth(penSize);
		ImageCanvas.getContext2d().setLineJoin(LineJoin.ROUND);
		ImageCanvas.getContext2d().setStrokeStyle(color);
		ImageCanvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
		
		ImageCanvas.getContext2d().beginPath();
		
		ImageCanvas.getContext2d().moveTo(x1,y1);
		ImageCanvas.getContext2d().lineTo(x2,y2);
		
		ImageCanvas.getContext2d().closePath();
		ImageCanvas.getContext2d().stroke();
		ImageCanvas.getContext2d().restore();
		//LogUtils.log("drawLine-after:"+canvas.getContext2d().getGlobalCompositeOperation());
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
		
		String result="";
		
		for(ImageElementCaptionData data:easyCellTableObjects.getDatas()){
			String image=toImageFileName(data);
			result+=data.getCaption()+"\n";
			result+="\n";
			if(markedCheck.getValue()){
				result+="![]("+pathBox.getValue()+image+")\n";
			}else{
				result+=""+pathBox.getValue()+image+"\n";
			}
			
			result+="\n";
			result+="\n";
		}
		
		markedTextArea.setText(result);
		
	}










	public class ImageElementCaptionData extends ImageElementData{
		private String caption;
		private Canvas imageCanvas;
		private List<NumberData> numbers=new ArrayList<ExportImage.NumberData>();
		public List<NumberData> getNumbers() {
			return numbers;
		}
		public Canvas getImageCanvas() {
			return imageCanvas;
		}
		public void setImageCanvas(Canvas imageCanvas) {
			this.imageCanvas = imageCanvas;
		}
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
			imageCanvas=CanvasUtils.createCanvas(imageElement.getWidth(), imageElement.getHeight());
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





	
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;
	private DropDockDataUrlRootPanel root;
	
	private DeckLayoutPanel mainPanel;




	




	
	public void doSelect(ImageElementCaptionData selection) {
		this.selection=selection;
		if(selection==null){
			
			
			imageCaptionBox.setEnabled(false);
			imageCaptionBox.setValue("");
		}else{
			
			imageCaptionBox.setEnabled(true);
			imageCaptionBox.setValue(selection.getCaption());
		}

		updateCanvas();
		updateMarkedText();
	}

	private void updateCanvas(){
		if(selection==null){
			CanvasUtils.clear(canvas);
		}else{
			drawCanvas(selection);
		}
	}
	
	
	
	private void drawCanvas(ImageElementCaptionData data){
		ImageElementUtils.copytoCanvas(data.getImageElement(), canvas);
		canvas.getContext2d().drawImage(data.getImageCanvas().getCanvasElement(), 0, 0);
		
		if(getCurrentNumbers()!=null){
		for(NumberData number:getCurrentNumbers()){
			double r=(double)digimeterBox.getValue()/2;
			double dx=(double)number.getX()-r;
			double dy=(double)number.getY()-r;
			double w=r*2;
			double h=r*2;
			
			Rect rect=new Rect((int)dx, (int)dy, (int)w, (int)h);
			if(bgTypeBox.getValue().getType()==BG_TYPE_SQUARE){
				canvas.getContext2d().setFillStyle(bgColorBox.getValue());
				canvas.getContext2d().fillRect(dx, dy, w, h);
			}else if(bgTypeBox.getValue().getType()==BG_TYPE_CIRCLE){
				canvas.getContext2d().setFillStyle(bgColorBox.getValue());
				
				CanvasPaintUtils.drawCircleInRect(canvas, (int)dx, (int)dy,(int) w,(int) h, true, true);
				
				
			}else{//none
				
			}
			
			//set font?
			
			canvas.getContext2d().setFont(fontBox.getText());
			canvas.getContext2d().setFillStyle(textColorBox.getValue());//TODO text-picker
			
			
			drawCenterInRect(canvas, ""+number.getNumber(), rect);
			
		}
		}
	}
	
	//TODO replace common
	public static void drawCenterInRect(Canvas canvas,String text,Rect rect){
		canvas.getContext2d().save();
		TextMetrics metrix=canvas.getContext2d().measureText(text);
		
		int dx=(int) ((rect.getWidth()-metrix.getWidth())/2);
		int dy=rect.getHeight()/2;
		
		//int size=parseFontSize(canvas.getContext2d().getFont());
		//double descentOffset=(double)size/4; offset no need anymore
		
		canvas.getContext2d().setTextBaseline("middle");
		
		canvas.getContext2d().fillText(text, rect.getX()+dx, rect.getY()+dy);
		canvas.getContext2d().restore();
	}
	
	@Override
	public void onCloseSettingPanel(){
		try {
			//number settings
			storageControler.setValue(KEY_FONT, fontBox.getValue());
			storageControler.setValue(KEY_TEXT_COLOR, textColorBox.getValue());
			storageControler.setValue(KEY_BG_COLOR, bgColorBox.getValue());
			
			storageControler.setValue(KEY_BG_DIAMETER, digimeterBox.getValue());
			storageControler.setValue(KEY_BG_TYPE, bgTypeBox.getValue().getType());
		} catch (StorageException e) {
			LogUtils.log(e.getMessage());
			//TODO 
		}
		updateCanvas();//possible number-settings changed.
	}


	private ListBox sizeBox;
	private TextBox nameBox;
	private TextBox imageCaptionBox;
	private TextArea markedTextArea;
	private Canvas canvas;
	private HorizontalPanel downloadLinks;
	private CheckBox markedCheck;
	private CanvasDragMoveControler moveControler;
	private ListBox numberBox;
	private ValueListBox<BGType> bgTypeBox;
	private IntegerBox digimeterBox;
	private ColorBox bgColorBox;
	private ColorBox textColorBox;
	private TextBox fontBox;
	
	



	@Override
	public String getAppName() {
		return textConstants.ExportImage();
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
