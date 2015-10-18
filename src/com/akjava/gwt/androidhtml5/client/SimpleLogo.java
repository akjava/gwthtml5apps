package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.androidhtml5.client.data.ImageElementData;
import com.akjava.gwt.androidhtml5.client.data.ImageUrlData;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.canvas.CanvasTextUtils;
import com.akjava.gwt.lib.client.widget.EnterKeySupportTextBox;
import com.akjava.gwt.lib.client.widget.cell.ButtonColumn;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.graphics.IntRect;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * 
 * explain of links
 * top link to anchor of app list to show other apps
 * 
 * app link is directly apps,no annoying description page
 */
public class SimpleLogo extends Html5DemoEntryPoint {

	
	
	

	private DockLayoutPanel dock;
	private HorizontalPanel topPanel;
	private EasyCellTableObjects<LayerData> EasyCellTableObjects;
	private Button makeBt;
	private ColorBox bgColorBox;
	private CheckBox keepTransparent;
	private IntegerBox marginBox;

	/* raw
	 public Panel createMainSettingPage(){
		VerticalPanel panel=new VerticalPanel();
		return panel;
	}
	public void onOpenSettingPanel(){
		
	}
	public void onCloseSettingPanel(){
		
	}
	 */

	public Panel createMainSettingPage(){
		VerticalPanel panel=new VerticalPanel();
		panel.setSpacing(4);
		Label size=new Label(textConstants.ImageSize());
		size.setStylePrimaryName("title");
		panel.add(size);
		int width=getStorageValue(KEY_IMAGE_WIDTH, 500);
		int height=getStorageValue(KEY_IMAGE_HEIGHT, 200);
		widthBox = makeIntegerBox(panel,textConstants.width(),width);
		heightBox = makeIntegerBox(panel,textConstants.height(),height);
		
		Button resetSize=new Button(textConstants.reset(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				widthBox.setValue(500);
				heightBox.setValue(200);
			}
		});
		panel.add(resetSize);
		
		Label text=new Label(textConstants.Text());
		text.setStylePrimaryName("title");
		panel.add(text);
		
		fontBox = makeTextBox(panel, textConstants.font(), getStorageValue(KEY_DRAW_FONT, "64px Audiowide"));
		panel.add(fontBox);
		
		
		
		//position
		//HorizontalPanel h=new HorizontalPanel();
		//panel.add(h);
		
		
		//moved
		
		
		marginBox=makeIntegerBox(panel, textConstants.margin(), getStorageValue(KEY_DRAW_MARGIN, 25));
		
		Button resetFont=new Button(textConstants.reset(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fontBox.setText("64px Audiowide");
				positionBox.setValue(POSITION_RIGHT_TOP);
				marginBox.setValue(25);
			}
		});
		panel.add(resetFont);
		
		return panel;
	}
	public void onOpenSettingPanel(){
		
	}
	public void onCloseSettingPanel(){
		int w=widthBox.getValue();
		int h=heightBox.getValue();
		
		if(w>maxSize){
			w=maxSize;
		}
		if(h>maxSize){
			h=maxSize;
		}
		
		if(w<minSize){
			w=minSize;
		}
		if(h<minSize){
			h=minSize;
		}
		
		imageWidth=w;
		imageHeight=h;
		
		widthBox.setValue(w);
		heightBox.setValue(h);
		
		
		setStorageValue(KEY_IMAGE_WIDTH, w);
		setStorageValue(KEY_IMAGE_HEIGHT, h);
		
		setStorageValue(KEY_DRAW_FONT, fontBox.getValue());
		
		int max=w>h?w:h;
		if(w!=canvasWidth || h!=canvasHeight){
			canvasWidth=max;
			canvasHeight=max;
			CanvasUtils.setSize(canvas,max,max);
			
		}
		updateImage();
	}
	
	
	private IntegerBox makeIntegerBox(Panel parent,String name,int value){
		HorizontalPanel h=new HorizontalPanel();
		parent.add(h);
		
		Label label=new Label(name);
		label.setWidth("100px");
		h.add(label);
		
		IntegerBox box=new IntegerBox();
		box.setValue(value);
		h.add(box);
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
	
	

	public   PositionData POSITION_RIGHT_TOP=new PositionData("RightTop",textConstants.RightTop());
	public class PositionData{
		private String value;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			PositionData other = (PositionData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public PositionData(String value,String label) {
			super();
			this.value = value;
			this.label=label;
		}
		private String label;
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		private SimpleLogo getOuterType() {
			return SimpleLogo.this;
		}
	}
	
	@Override
	public Panel initializeWidget() {
		
		DropDockDataUrlRootPanel root=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					
					SimpleLogo.this.loadFile(file, dataUrl);
				}
			}
			
			
		};
		root.setFilePredicate(FilePredicates.getImageExtensionOnly());
		
		
		//
		
		
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
		
		topPanel.add(new Anchor(textConstants.Help(), "simplelogo_help.html"));
	
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
		fileUps.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		controler.add(fileUps);
		fileUps.add(upload);

		downloadArea = new HorizontalPanel();
		fileUps.add(downloadArea);
		
	
		HorizontalPanel makeBtPanel=new HorizontalPanel();
		makeBtPanel.setSpacing(1);
		makeBtPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		
		controler.add(makeBtPanel);
		makeBt = new Button(textConstants.Make(),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				makeBt.setEnabled(false);
				Timer timer=new Timer(){

					@Override
					public void run() {
						generateImage();
						makeBt.setEnabled(true);
					}
					
				};
				timer.schedule(50);
				
				
			}
		});
		makeBt.setWidth("100px");
		
		makeBt.setEnabled(false);
		
		makeBtPanel.add(new Label(textConstants.image_format()+":"));
		
		typeBox = new ValueListBox<String>(new Renderer<String>() {
			@Override
			public String render(String object) {
				return object;
			}

			@Override
			public void render(String object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		typeBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				setStorageValue(KEY_EXPORT_FORMAT, event.getValue());
				
				if(!typeBox.getValue().equals("png")){
					keepTransparent.setEnabled(false);
				}else{
					keepTransparent.setEnabled(true);
				}
				
			}
		});
		typeBox.setValue(getStorageValue(KEY_EXPORT_FORMAT, "png"));
		typeBox.setAcceptableValues(Lists.newArrayList("png","jpeg","webp"));
		makeBtPanel.add(typeBox);
		
		makeBtPanel.add(makeBt);
		
	
		HorizontalPanel h1=new HorizontalPanel();
		controler.add(h1);
		final Label scaleLabel=new Label(textConstants.Scale()+":1.0");
		h1.add(scaleLabel);//TODO ondemand scale
		scaleLabel.setWidth("60px");
		
		scaleRange = HTML5InputRange.createInputRange(-99, 90, 0);
		scaleRange.setWidth("250px");
		h1.add(scaleRange);
		scaleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				
				if(selection!=null){
					//selection.setScale(getScale());
					selection.setScale(newValue);
				}
			}
		});
		scaleRange.addInputRangeListener(new InputRangeListener() {
			public String toLabel(double value){
				String v=""+value;
				return v.substring(0,Math.min(v.length(), 4));
			}
			@Override
			public void changed(int newValue) {
				scaleLabel.setText(textConstants.Scale()+":"+toLabel(getScale()));
				
			}
		});
		
		final ListBox scaleControler=new ListBox();
		scaleControler.addItem("");
		scaleControler.addItem(textConstants.reset());
		scaleControler.addItem(textConstants.fit_width());
		scaleControler.addItem(textConstants.fit_height());
		
		scaleControler.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if(selection==null){
					scaleControler.setSelectedIndex(0);//reset
					return;
				}
				int index=scaleControler.getSelectedIndex();
				if(index<=0){
					return;
				}
				if(index==1){
					scaleRange.setValue(0);
				}else if(index==2){
					ImageElement imageElement=selection.getImageElement();
					int iw=imageWidth;
					int iew=imageElement.getWidth();
					if(angleRange.getValue()==90 || angleRange.getValue()==-90){
						iew=imageElement.getHeight();
					}
					
					double r=(double)iw/iew;
					
					if(r<1){
						r=1.0-r;
						//1.0+v*0.01
						int v=(int) (r*-100);
						setScale(v);
					}else if(r==1){
						setScale(0);
					}else{
						
						//return 1.0+v*0.1;
						int v=(int)(r*10)-10;
						
						//optimize scaleup
						int newWidth=(int)getScaleValue(v)*iew;
						if(newWidth<iw){
							v++;
						}
						
						setScale(v);
					}
					
					//imageWidth;
				}else if(index==3){
					ImageElement imageElement=selection.getImageElement();
					int ih=imageHeight;
					int ieh=imageElement.getHeight();
					if(angleRange.getValue()==90 || angleRange.getValue()==-90){
						ieh=imageElement.getWidth();
					}
					double r=(double)ih/ieh;
					
					if(r<1){
						r=1.0-r;
						//1.0+v*0.01
						int v=(int) (r*-100);
						setScale(v);
					}else if(r==1){
						setScale(0);
					}else{
						
						//return 1.0+v*0.1;
						int v=(int)(r*10)-10;
						
						//optimize scaleup
						int newHeight=(int)getScaleValue(v)*ieh;
						if(newHeight<ih){
							v++;
						}
						
						setScale(v);
					}
				}
				
				scaleControler.setSelectedIndex(0);//reset
			}
		});
		
		h1.add(scaleControler);
		
		
		
		HorizontalPanel h2=new HorizontalPanel();
		controler.add(h2);
		final Label turnLabel=new Label(textConstants.angle()+":0");
		h2.add(turnLabel);//TODO ondemand scale
		turnLabel.setWidth("60px");
		
		angleRange = HTML5InputRange.createInputRange(-180, 180, 0);
		angleRange.setWidth("250px");
		h2.add(angleRange);
		angleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				updateImage();
				int angle=angleRange.getValue();
				if(selection!=null){
					selection.setAngle(angle);
				}
			}
		});
		angleRange.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				
				turnLabel.setText(textConstants.angle()+":"+(angleRange.getValue()));
				
				
			}
		});
		Button leftTurn=new Button("-90",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(-90);
			}
		});
		h2.add(leftTurn);
		Button zeroTurn=new Button("0",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(0);
			}
		});
		h2.add(zeroTurn);
		Button rightTurn=new Button("90",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				angleRange.setValue(90);
			}
		});
		h2.add(rightTurn);
	
		HorizontalPanel h3b=new HorizontalPanel();
		h3b.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		HorizontalPanel h3=new HorizontalPanel();
		h3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		Label title_logo=new Label(textConstants.title_logo()+":");
		title_logo.setWidth("65px");
		h3.add(title_logo);
		controler.add(h3);
		titleBox = new EnterKeySupportTextBox(){
			@Override
			public void onEnterKeyDown() {
				updateImage();
			}};
			titleBox.setWidth("180px");
		titleBox.setText(getStorageValue(KEY_LAST_LABEL, "TODO"));
		h3.add(titleBox);
		Label text_color=new Label(textConstants.text_color()+":");
		text_color.setWidth("65px");
		h3b.add(text_color);
		colorBox = new ColorBox();
		colorBox.setValue("#ffffff");
		h3b.add(colorBox);
		
		
		transparentTextBox = new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer value) {
				// TODO Auto-generated method stub
				return value+"%";
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				
			}
			
		});
		transparentTextBox.setValue(100);
		transparentTextBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				updateImage();
			}
		});
		
		
		
		
		
		List<Integer> values=new ArrayList<Integer>();
		for(int i=20;i>0;i--){
			values.add(i*5);
		}
		transparentTextBox.setAcceptableValues(values);
		h3b.add(transparentTextBox);
		
		
		
		positionBox = new ValueListBox<PositionData>(new Renderer<PositionData>() {
			@Override
			public String render(PositionData object) {
				return object.getLabel();
			}
			@Override
			public void render(PositionData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
	
		
		controler.add(h3b);
		
		Label label=new Label(textConstants.position()+":");
		//label.setWidth("100px");
		h3b.add(label);
		
		String posKey=getStorageValue(KEY_DRAW_POSITION, "RightTop");
		positionBox.setValue(new PositionData(posKey, ""));//is this work?
		
		positionBox.setAcceptableValues(Lists.newArrayList(POSITION_RIGHT_TOP,
				new PositionData("RightBottom", textConstants.RightBottom()),new PositionData("LeftBottom", textConstants.LeftBottom()),
				new PositionData("LeftTop", textConstants.LeftTop()),
				new PositionData("Center", textConstants.Center()),new PositionData("TopCenter", textConstants.TopCenter()),new PositionData("BottomCenter", textConstants.BottomCenter())
				));
		
		positionBox.addValueChangeHandler(new ValueChangeHandler<PositionData>() {
			@Override
			public void onValueChange(ValueChangeEvent<PositionData> event) {
				setStorageValue(KEY_DRAW_POSITION, event.getValue().getValue());
				
				updateImage();
			}
		});
		h3b.add(positionBox);
		
	
		
		Button updateBt=new Button(textConstants.update(),new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateImage();
			}
		});
		h3b.add(updateBt);
		
		HorizontalPanel h4=new HorizontalPanel();
		h4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		controler.add(h4);
		Label bg_color=new Label(textConstants.bg_color()+":");
		bg_color.setWidth("65px");
		h4.add(bg_color);
		bgColorBox = new ColorBox();
		bgColorBox.setValue("#000000");
		h4.add(bgColorBox);
		
		keepTransparent = new CheckBox(textConstants.transparent());
		keepTransparent.setTitle(textConstants.only_work_on_png());
		
		keepTransparent.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(showOutSideCheck.getValue()){
					//bgColorBox.setVisible(false);//need enabled
				}else{
					//bgColorBox.setVisible(true);//never show again...
				}
				updateImage();
			}
		});
		h4.add(keepTransparent);
		
		//only png has transparent
		if(!typeBox.getValue().equals("png")){
			keepTransparent.setEnabled(false);
		}
		
		showOutSideCheck = new CheckBox(textConstants.show_outside());
		showOutSideCheck.setValue(true);
		showOutSideCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(showOutSideCheck.getValue()){
					canvas.setStyleName("transparent_bg");
				}else{
					canvas.setStyleName("white_bg");
				}
				updateImage();
			}
		});
		h4.add(showOutSideCheck);
		
		
		
		SimpleCellTable<LayerData> cellTable = new SimpleCellTable<LayerData>(999) {
			@Override
			public void addColumns(CellTable<LayerData> table) {
				 ButtonColumn<LayerData> removeBtColumn=new ButtonColumn<LayerData>() {
						@Override
						public void update(int index, LayerData object,
								String value) {
								EasyCellTableObjects.removeItem(object);
						}
						@Override
						public String getValue(LayerData object) {
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
				
					
					
				    TextColumn<LayerData> fileInfoColumn = new TextColumn<LayerData>() {
					      public String getValue(LayerData value) {
					    	  
					    	  return value.getFileName();
					      }
					    };
					    table.addColumn(fileInfoColumn,textConstants.Name());
					    
					   
					    
			}
		};
		
		eastPanel = new DockLayoutPanel(Unit.PX);
		eastPanel.addNorth(controler, 210);
		
		ScrollPanel cellScroll=new ScrollPanel();
		cellScroll.setSize("100%", "100%");
		
		
		cellTable.setWidth("100%");
		cellScroll.add(cellTable);
		EasyCellTableObjects=new EasyCellTableObjects<LayerData>(cellTable,false) {
			@Override
			public void onSelect(LayerData selection) {
				doSelect(selection);
			}
		};
		
		eastPanel.add(cellScroll);
		
		
		dock.addEast(eastPanel, 400);
		
		
		
		
		
		
		
		
		mainScrollPanel = new ScrollPanel();
		mainScrollPanel.setWidth("100%");
		mainScrollPanel.setHeight("100%");
		dock.add(mainScrollPanel);
		
		
		imageWidth=getStorageValue(KEY_IMAGE_WIDTH, 500);
		imageHeight=getStorageValue(KEY_IMAGE_HEIGHT, 200);
		
		int max=imageWidth>imageHeight?imageWidth:imageHeight;
		
		canvasWidth = max;
		canvasHeight = max;
		
		canvas = CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		drawCanvas=CanvasUtils.createCanvas(canvasWidth, canvasHeight);
		
		mainScrollPanel.add(canvas);
		canvas.setStylePrimaryName("transparent_bg");
		
		//move
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(moveControler.isStarted()){
					moveControler.move(event.getX(), event.getY());
				}
			}
		});
		
		canvas.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				moveControler.end(event.getX(), event.getY());
			}
		});
		
		canvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				moveControler.start(event.getX(), event.getY());
			}
		});
		
		canvas.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				moveControler.end(event.getX(), event.getY());
			}
		});
		
		moveControler=new CanvasDragMoveControler(new MoveListener() {
			
			@Override
			public void dragged(int sx, int sy, int ex,int ey,int vectorX, int vectorY) {
				double scale=getScale();
				offsetX+=vectorX;
				offsetY+=vectorY;
				
				//offsetX+=(vectorX*(1.0/scale));
				//offsetY+=(vectorY*(1.0/scale));
				updateImage();
				if(selection!=null){
					selection.setOffsetX(offsetX);
					selection.setOffsetY(offsetY);
				}
			}

			@Override
			public void start(int sx, int sy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void end(int sx, int sy) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return root;
	}
	
	public static final String KEY_DRAW_POSITION="simplelogo_draw_position";
	public static final String KEY_DRAW_FONT="simplelogo_draw_font";
	public static final String KEY_DRAW_MARGIN="simplelogo_draw_margin";
	public static final String KEY_EXPORT_FORMAT="simplelogo_export_format";
	public static final String KEY_IMAGE_WIDTH="simplelogo_export_width";
	public static final String KEY_IMAGE_HEIGHT="simplelogo_export_height";
	public static final String KEY_LAST_LABEL="simplelogo_last_label";
	public void setScale(double scale){
		
		/*
		 * here is bugs and store same as range
		int v=0;
		if(scale==1){
			v=0;
		}else if(scale>1){
			v=(int) (scale/0.1);
			
		}else if(scale<1){
			v=(int) (scale/0.01);
			v*=-1;
		}
		*/
		
		scaleRange.setValue((int)scale);
		
		//LogUtils.log("setted:"+v+",value="+scaleRange.getValue()+" scale="+scale+",nv="+getScale());
	}
	
	private double getScaleValue(int v){
		
		if(v==0){
			return 1;
		}
		if(v>0){
			return 1.0+v*0.1;
		}else if(v<0){
			
			return 1.0+v*0.01;
		}
		return 1;
	}
	
	private double getScale(){
		int v=scaleRange.getValue();
		
		if(v==0){
			return 1;
		}
		if(v>0){
			return 1.0+v*0.1;
		}else if(v<0){
			
			return 1.0+v*0.01;
		}
		return 1;
	}
	
	protected void generateImage() {
		int clipSX=(canvasWidth-imageWidth)/2;
		int clipSY=(canvasHeight-imageHeight)/2;
		int clipEX=clipSX+imageWidth;
		int clipEY=clipSY+imageHeight;
		
		ImageData data=canvas.getContext2d().getImageData(clipSX, clipSY, clipEX-clipSX, clipEY-clipSY);
		CanvasUtils.createCanvas(drawCanvas, data);
		
		String type=typeBox.getValue();
		String dataUrl=drawCanvas.toDataUrl("image/"+type);
		
		String extension=type.equals("jpeg")?"jpg":type;
		String name=titleBox.getText().isEmpty()?"logo":titleBox.getText();
		
		
		Anchor anchor=HTML5Download.get().generateBase64DownloadLink(dataUrl, "image/"+type, name+"."+extension, textConstants.Download(), true);
		anchor.setStylePrimaryName("bt");
		downloadArea.clear();
		downloadArea.add(anchor);
	}

	private int offsetX,offsetY;
	
	private CanvasDragMoveControler moveControler;
	


	
	



	

	
	

	/**
	 * watch out IE maybe problem
	 * @author aki
	 *
	 */
	
	public class DataToImageElement implements Function<ImageUrlData,ImageElement>{

		@Override
		public ImageElement apply(ImageUrlData input) {
			return ImageElementUtils.create(input.getDataUrl());
		}
		
	}
	
	
	

	protected void loadFile(final File file,final String asStringText) {
		try{
			//TODO create method
		//ImageElement element=ImageElementUtils.create(asStringText);
		
		new ImageElementLoader().load(asStringText, new ImageElementListener() {
			@Override
			public void onLoad(final ImageElement element) {
				//LogUtils.log(file.getFileName()+","+element.getWidth()+"x"+element.getHeight());
				
				
				final LayerData data=new LayerData(file.getFileName(),element,asStringText);
				
				EasyCellTableObjects.addItem(data);
				//updateList();
				
				//stack on mobile,maybe because of called async method
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						EasyCellTableObjects.setSelected(data, true);
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

	
	LayerData selection;





	
	private ScrollPanel mainScrollPanel;
	private DockLayoutPanel eastPanel;

	private HorizontalPanel downloadArea;
	private Canvas canvas;
	private InputRangeWidget scaleRange;
	private InputRangeWidget angleRange;


	




	
	public void doSelect(LayerData selection) {
		this.selection=selection;
		
		
		if(selection==null){
			CanvasUtils.clear(canvas);
			
		}else{
			setScale(selection.getScale());
			angleRange.setValue((int) selection.getAngle());
			offsetX=selection.getOffsetX();
			offsetY=selection.getOffsetY();
			
			updateImage();
		}
	}

	private Canvas drawCanvas;
	
	private int maxSize=1080;
	private int minSize=16;
	
	/*
	private int clipSX=0;
	private int clipSY=150;
	private int clipEX=500;
	private int clipEY=350;
	*/
	
	private int imageWidth;
	private int imageHeight;
	
	private int canvasWidth;
	private int canvasHeight;
	private TextBox titleBox;
	private ColorBox colorBox;
	
	private double lastScale=1;
	private CheckBox showOutSideCheck;
	private ValueListBox<Integer> transparentTextBox;
	
	private IntegerBox widthBox;
	private IntegerBox heightBox;
	private TextBox fontBox;
	private ValueListBox<String> typeBox;
	private ValueListBox<PositionData> positionBox;
	private void updateImage(){
		if(selection==null){
			return;
		}
		
		if(drawCanvas.getCoordinateSpaceWidth()!=canvasWidth || drawCanvas.getCoordinateSpaceHeight()!=canvasHeight){
			drawCanvas.setCoordinateSpaceWidth(canvasWidth);
			drawCanvas.setCoordinateSpaceHeight(canvasHeight);
		}
		CanvasUtils.clear(drawCanvas);
		CanvasUtils.clear(canvas);
		canvas.getContext2d().save();
		
		ImageElement element=selection.getImageElement();
		
		double scale=getScale();
		//LogUtils.log(scale);
		
		int angle=angleRange.getValue();
		
		//int ox=(int) (offsetX*scale);
		//int oy=(int) (offsetY*scale);
		int ox=offsetX;
		int oy=offsetY;
		/*
		if(scale!=lastScale){
			int ox2=Math.abs((int) (element.getWidth()*(lastScale-scale)));
			int oy2=Math.abs((int) (element.getHeight()*(lastScale-scale)));
			ox=ox-(ox2);
			oy=oy-(oy2);
		}
		*/
		
		CanvasUtils.drawCenter(drawCanvas, element,ox,oy,scale,scale,angle,1);
				
		
		
		
		//canvas.getContext2d().setFillStyle("rgba(255, 255, 255, 0.5)");
		if(showOutSideCheck.getValue()){
		canvas.getContext2d().setGlobalAlpha(0.5);
		canvas.getContext2d().drawImage(drawCanvas.getCanvasElement(), 0,0);
		}else{
		//CanvasUtils.clear(drawCanvas);	
		}
		
		canvas.getContext2d().restore();
		
		
		int clipSX=(canvasWidth-imageWidth)/2;
		int clipSY=(canvasHeight-imageHeight)/2;
		int clipEX=clipSX+imageWidth;
		int clipEY=clipSY+imageHeight;
		
		//canvas.getContext2d().restore();
		canvas.getContext2d().save();
		canvas.getContext2d().beginPath();
		canvas.getContext2d().moveTo(clipSX,clipSY);
		canvas.getContext2d().lineTo(clipEX,clipSY);
		canvas.getContext2d().lineTo(clipEX,clipEY);
		canvas.getContext2d().lineTo(clipSX,clipEY);
		canvas.getContext2d().clip();
		canvas.getContext2d().setGlobalAlpha(1);
		
		if(!keepTransparent.getValue()){
			canvas.getContext2d().setFillStyle(bgColorBox.getValue());
			canvas.getContext2d().fillRect(clipSX, clipSY, clipEX-clipSX, clipEY-clipSY);
		}
		
		
		//CanvasUtils.drawCenter(canvas, element,ox,oy,scale,scale,angle,1);
		canvas.getContext2d().drawImage(drawCanvas.getCanvasElement(), 0,0);
		 
		
		canvas.getContext2d().setFont(fontBox.getText());
		canvas.getContext2d().setFillStyle(colorBox.getValue());
		//canvas.getContext2d().fillText("Document", 200, 200);
		
		
		String title=titleBox.getText();
		IntRect rect;
		
		
		int align=CanvasUtils.ALIGN_CENTER;
		int valign=CanvasUtils.VALIGN_MIDDLE;
		
		String positionText=positionBox.getValue().getValue().toLowerCase();
		if(positionText.indexOf("left")!=-1){
			align=CanvasUtils.ALIGN_LEFT;
		}
		if(positionText.indexOf("right")!=-1){
			align=CanvasUtils.ALIGN_RIGHT;
		}
		if(positionText.indexOf("top")!=-1){
			valign=CanvasUtils.VALIGN_TOP;
		}
		if(positionText.indexOf("bottom")!=-1){
			valign=CanvasUtils.VALIGN_BOTTOM;
		}
		
		
		
		int margin=marginBox.getValue();
		
		rect=CanvasTextUtils.getAlignRect(canvas,imageWidth-margin*2,imageHeight-margin*2,title,align,valign);
		rect.setX(rect.getX()+margin+clipSX);
		rect.setY(rect.getY()+margin+clipSY);
		//rect.setY(clipSY+25);
		
		
		
		canvas.getContext2d().save();
		double transp=(double)transparentTextBox.getValue()/100;
		canvas.getContext2d().setGlobalAlpha(transp);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		canvas.getContext2d().restore();
		
		//this is faild
		//rect=RectBuilder.from(imageWidth, imageHeight).slice(4, 2).parsePostion(positionBox.getValue()).horizontalExpand(-1).toRect();//.horizontalExpand(-1)
		
		
		
		/*
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_TOP);
		rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_MIDDLE);
		//rect.setY(clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_LEFT,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		LogUtils.log(rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_RIGHT,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		rect=CanvasTextUtils.getAlignRect(canvas,title,CanvasUtils.ALIGN_CENTER,CanvasUtils.VALIGN_BOTTOM);
		rect.setY(rect.getY()-clipSY);
		CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		*/
		rect.setY(rect.getY()+clipSY);
		//CanvasTextUtils.drawCenterInRect(canvas, title, rect);
		
		canvas.getContext2d().restore();
		lastScale=scale;
		
		setStorageValue(KEY_LAST_LABEL, titleBox.getText());
		
	}
	

	
	public class LayerData extends ImageElementData{
		public LayerData(String fileName, ImageElement imageElement, String dataUrl) {
			super(fileName, imageElement, dataUrl);
			// TODO Auto-generated constructor stub
		}
		//private double scale=1;
		private double scale=0;//now keep same as range value
		public double getScale() {
			return scale;
		}
		public void setScale(double scale) {
			this.scale = scale;
		}
		public int getOffsetX() {
			return offsetX;
		}
		public void setOffsetX(int offsetX) {
			this.offsetX = offsetX;
		}
		public int getOffsetY() {
			return offsetY;
		}
		public void setOffsetY(int offsetY) {
			this.offsetY = offsetY;
		}
		public double getAngle() {
			return angle;
		}
		public void setAngle(double angle) {
			this.angle = angle;
		}
		private int offsetX;
		private int offsetY;
		private double angle;
	}









	@Override
	public String getAppName() {
		return textConstants.SimpleLogo();
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
		return "http://android.akjava.com/html5apps/index.html#simplelogo";
	}
	
}
