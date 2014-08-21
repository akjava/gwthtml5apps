package com.akjava.gwt.androidhtml5.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FilePredicates;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataArrayListener;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.jszip.client.JSFile;
import com.akjava.gwt.jszip.client.JSZip;
import com.akjava.gwt.lib.client.Base64Utils;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.ExecuteButton;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.HtmlColumn;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.io.FileType;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OpenCVTools extends AbstractDropEastDemoEntryPoint{
private Canvas sharedCanvas=Canvas.createIfSupported();
private VerticalPanel mainPanel;
private List<Cifar10Data> datas=new ArrayList<Cifar10Data>();
	public static class Cifar10Data{
		private int classNumber;
		public int getClassNumber() {
			return classNumber;
		}
		public void setClassNumber(int classNumber) {
			this.classNumber = classNumber;
		}
		public ImageData getImageData() {
			return imageData;
		}
		public void setImageData(ImageData imageData) {
			this.imageData = imageData;
		}
		private ImageData imageData;
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	

	//TODO move common
	  private static final int UNSIGNED_MASK = 0xFF;
	  public static int toInt(byte value) {
		    return value & UNSIGNED_MASK;
		  }
	  
	
	  
	public void addData(Cifar10Data data){
		CanvasUtils.copyTo(data.getImageData(), sharedCanvas);
		if(data.getClassNumber()==3){
		Image img=new Image(sharedCanvas.toDataUrl());
		img.setWidth("128px");
		mainPanel.add(img);
		mainPanel.add(new Label(data.getName()));
		}
		
		datas.add(data);
	}

	@Override
	public Predicate<File> getDoDropFilePredicate() {		
		return FilePredicates.getImageExtensionOnly();
	}

	@Override
	public int getEastPanelWidth() {
		
		return 460;
	}

	public String createColorImageDataUrl(Canvas canvas,int w,int h,String color){
		if(canvas==null){
			canvas=Canvas.createIfSupported();
		}
		CanvasUtils.createCanvas(canvas, w, h);
		CanvasUtils.fillRect(canvas, color);
		return canvas.toDataUrl();
	}
	
	private List<PositiveData> positiveDatas;
	private String openCvResultText="";//initial empty
	//private Map<String,String> imageDataUrlMaps=new HashMap<String,String>();
	
	private void updateTestData(){
		//dummyTextBox.setText(openCvResultText);
		
		if(positiveDatas==null){
			Window.alert("set pos-image sets");
			return;
		}
		
		//TODO tableObjects.clearDataItems();
		tableObjects.getDatas().clear();
		
		List<PositiveData> resultData=textToPositiveData(openCvResultText);
		
	
		
		
		for(PositiveData positiveData:positiveDatas){
			
			PositiveData opencvResult=findByName(resultData,positiveData.getFileName());
			
			TestResultData testResult=generateTestResult(positiveData,opencvResult);
			
			
			
			tableObjects.addItem(testResult);
		}

		//total score
		scoreLabel.setText(createScoreLabel());
	}
	
	private String createScoreLabel(){
		String score="Score:";
		
		
		int maxScore=0;
		int total=0;
		int totalTarget=0;
		int missed=0;
		int completed=0;
		
		int totalC=0;
		int totalH=0;
		int totalR=0;
		int totalM=0;
		
		int totalAttemptA=0;
		int totalAttemptH=0;
		int totalAttemptM=0;
		int totalAttemptC=0;
		for(TestResultData testResult:tableObjects.getDatas()){
			totalTarget+=testResult.getPositiveData().getRects().size();
			missed+=testResult.getMissTarget();
			completed+=testResult.getCompleteTarget();
			
			total+=scoreCalcurator.getTotalScore(testResult.getScoreData());
			
			totalC+=scoreCalcurator.getCriticalScore(testResult.getScoreData());
			totalH+=scoreCalcurator.getHitScore(testResult.getScoreData());
			totalR+=scoreCalcurator.getRemainScore(testResult.getScoreData());
			totalM+=scoreCalcurator.getMissScore(testResult.getScoreData());
			
			totalAttemptA+=testResult.getScoreData().getAttempt();
			totalAttemptC+=testResult.getScoreData().getCritical();
			totalAttemptH+=testResult.getScoreData().getHit();
			totalAttemptM+=testResult.getScoreData().getMiss();
		}
		maxScore=totalTarget*10;
		
		score+=total+"/"+maxScore+"("+totalC+","+totalH+","+totalM+","+totalR+")";
		
		//scoreLabel.setText("score:"+total+" / "+maxScore+",[complete:"+completed+"][hit:"+(totalTarget-completed-missed)+"][ignore:"+missed+"] of targets:"+totalTarget+"");
		String attempt=" Attempt:";
		
		attempt+="("+totalAttemptC+","+totalAttemptH+","+totalAttemptM+")/"+totalAttemptA;
		
		
		String result=" Result:";
		result+=(totalTarget-missed)+"/"+totalTarget+"("+completed+","+(totalTarget-completed-missed)+","+missed+")";
		
		return score+attempt+result;
	}
	
	
	public static class ScoreData{
		int critical;
		public int getCritical() {
			return critical;
		}
		public void setCritical(int critical) {
			this.critical = critical;
		}
		public int getHit() {
			return hit;
		}
		public void setHit(int hit) {
			this.hit = hit;
		}
		public int getMiss() {
			return miss;
		}
		public void setMiss(int miss) {
			this.miss = miss;
		}
		public int getRemainTarget() {
			return remainTarget;
		}
		public void setRemainTarget(int remainTarget) {
			this.remainTarget = remainTarget;
		}
		public int getAttempt() {
			return attempt;
		}
		public void setAttempt(int attempt) {
			this.attempt = attempt;
		}
		int hitMatched;
		public int getHitMatched() {
			return hitMatched;
		}
		public void setHitMatched(int hitMatched) {
			this.hitMatched = hitMatched;
		}
		int hit;
		int miss;
		int remainTarget;
		int attempt;
	}
	
	private TestResultData generateTestResult(PositiveData positiveData,@Nullable PositiveData resultData){
		TestResultData data=new TestResultData(positiveData);
		//data.setImageDataUrl(imageDataUrlMaps.get(positiveData.getFileName()));
		
		LogUtils.log(positiveData.getFileName());
		//TODO calcurate
		int complete=0;
		int hit=0;
		int missed=0;
	
		
		//int score=0;
		
		//LogUtils.log(positiveData.getRects().size());
		
		
		List<Rect> missOrHit=Lists.newArrayList(positiveData.getRects());//TODO change
		
		
		if(resultData==null){
			resultData=new PositiveData();//make empty avoid if-state
		}
		
		List<Rect> rRect=Lists.newArrayList(resultData.getRects());//TODO change
		
		//LogUtils.log(resultData.getRects().size());
		
		//LogUtils.log("c-check");
		
		List<Rect> removeReserved=Lists.newArrayList();
		//complete check
		for(Rect resultRect:rRect){
			for(Rect r:positiveData.getRects()){
				if(!missOrHit.contains(r)){//already complete ignore
					continue;
				}
				if(containMatchRect(r, resultRect)){
					missOrHit.remove(r);
					removeReserved.add(resultRect);
					data.getResultRects().add(new ResultRect(resultRect, TestResultData.COMPLETE));
					complete++;
				}
			}
		}
		rRect.removeAll(removeReserved);
		removeReserved.clear();
		
		//LogUtils.log("h-check");
		//hit for remain target
		for(Rect resultRect:rRect){
			for(Rect r:positiveData.getRects()){
				if(hitRect(r,resultRect)){
					if(missOrHit.contains(r)){ //only hit exist
						missOrHit.remove(r);
						data.getResultRects().add(new ResultRect(resultRect, TestResultData.HIT));
						hit++;
					}
					removeReserved.add(resultRect);
				}
			}
		}
		rRect.removeAll(removeReserved);
		removeReserved.clear();
		//LogUtils.log("m-check");
		//finally check remain
		for(Rect resultRect:rRect){
			data.getResultRects().add(new ResultRect(resultRect, TestResultData.FAILD));
			missed++;
		}
		
		
		
		
		
		
		
		
		if(missed==0 && missOrHit.size()==0 && complete==positiveData.getRects().size()){
			data.setState(TestResultData.COMPLETE);
		}else if(complete>0 || hit>0){
			data.setState(TestResultData.HIT);
		}else{
			data.setState(TestResultData.FAILD);
		}
		
		data.setMissTarget(missOrHit.size());
		data.setCompleteTarget(complete);
		
		
		int totalHitCount=hit;//TODO change
		
		ScoreData scoreData=new ScoreData();
		scoreData.setRemainTarget(missOrHit.size());
		scoreData.setAttempt(resultData.getRects().size());
		scoreData.setCritical(complete);
		scoreData.setHit(totalHitCount);
		scoreData.setMiss(missed);
		
		data.setScoreData(scoreData);
		
		return data;
	}
	private ScoreCalcurator scoreCalcurator=new ScoreCalcurator();
	
	public class ScoreCalcurator{
		
		int completeHitScore=10;
		int hitScore=1;
		int missRemainScore=-5;
		int missHitScore=-1;
		
		public int getTotalScore(ScoreData data){
			int score=0;
			
			
			
			score+=getRemainScore(data);
			
			
			score+=getCriticalScore(data);
			
			
			score+=getHitScore(data);
			
			
			score+=getMissScore(data);
			return score;
		}
		public int getRemainScore(ScoreData data){
			return (data.getRemainTarget()*missRemainScore);
		}
		public int getCriticalScore(ScoreData data){
			return data.getCritical()*completeHitScore;
		}
		public int getMissScore(ScoreData data){
			return data.getMiss()*missHitScore;
		}
		public int getHitScore(ScoreData data){
			return data.getHit()*hitScore;
		}
	}
	
	private boolean hitRect(Rect src,Rect dest){
		if(!src.collision(dest)){
			return false;
		}
		
		Rect checkRect=dest.copy();
		checkRect.setWidth(checkRect.getWidth()/2);
		checkRect.setHeight(checkRect.getHeight()/2);
		
		//left-top
		if(src.contains(checkRect)){
			return true;
		}
		//right-top
		checkRect.setX(dest.getX()+dest.getWidth()/2);
		if(src.contains(checkRect)){
			return true;
		}
		//right-bottom
		checkRect.setY(dest.getY()+dest.getHeight()/2);
		if(src.contains(checkRect)){
			return true;
		}
		//left-bottom
		checkRect.setX(dest.getX());
		if(src.contains(checkRect)){
			return true;
		}
		//center
		checkRect.setX(dest.getX()+(dest.getWidth()/4));
		checkRect.setY(dest.getY()+(dest.getHeight()/4));
		if(src.contains(checkRect)){
			return true;
		}
		return false;
	}
	
	private boolean containMatchRect(Rect larger,Rect smaller){
		return larger.contains(smaller);
	}
	
	
	private JSZip loadedZip;
	public PositiveData findByName(List<PositiveData> list,String name){
		for(PositiveData data:list){
			if(data.getFileName().equals(name)){
				return data;
			}
		}
		return null;
	}
	
	
	private List<PositiveData> textToPositiveData(String text){
		if(text.isEmpty()){
			return Lists.newArrayList();
		}
		List<String> lines=CSVUtils.splitLinesWithGuava(text);
		return FluentIterable.from(lines).transform(new PositiveDataConverter()).toList();
	}
	
	
	
	
	
	
	public String toAttemptHtml(TestResultData data) {
		String value=String.valueOf(data.getScoreData().getAttempt());
		String color="#888";
		
		String html="<span style='bcolor:"+color+"'>"+value+"</span>";
		return html;
	}
	
	public String toCriticalHtml(TestResultData data) {
		String value=String.valueOf(data.getScoreData().getCritical());
		String color="#0f0";
		
		String html="<span style='color:"+color+"'>"+value+"</span>";
		return html;
	}
	
	public String toHitHtml(TestResultData data) {
		String value=String.valueOf(data.getScoreData().getHit());
		String color="#00f";
		
		String html="<span style='color:"+color+"'>"+value+"</span>";
		return html;
	}
	
	public String toMissHtml(TestResultData data) {
		String value=String.valueOf(data.getScoreData().getMiss());
		String color="#f00";
		
		String html="<span style='color:"+color+"'>"+value+"</span>";
		return html;
	}
	
	@Override
	public Panel getEastPanel() {
		DockLayoutPanel sidePanel=new DockLayoutPanel(Unit.PX);
		VerticalPanel panel=new VerticalPanel();
		sidePanel.addNorth(panel,200);
		
		panel.add(new Label("positive-zip(must contain info.txt or info.dat)"));
		

		
		zipUpload = FileUtils.createSingleFileUploadForm(new DataArrayListener() {
		

			@Override
			public void uploaded(File file, Uint8Array array) {
				loadedZip = JSZip.loadFromArray(array);
				
				//JsArrayString files=zip.getFiles();
				//List<String> fileNameList=JavaScriptUtils.toList(files);
				
				JSFile indexFile=loadedZip.getFile("info.txt");
				if(indexFile==null){
					indexFile=loadedZip.getFile("info.dat");//i'm windows-os user and .dat extensin used other case
				}
				
				if(indexFile==null){
					Window.alert("info.txt or info.dat name not found here.or in folder");
					return;
				}
				
				testResultDataViewer.setData(null);//clear first
				
				String text=indexFile.asText();
				LogUtils.log(text);
				List<String> lines=CSVUtils.splitLinesWithGuava(text);
				
				checkState(lines.size()>0,"info.txt or info.dat is empty");
				positiveDatas=FluentIterable.from(lines).transform(new PositiveDataConverter()).toList();
				
				LogUtils.log("p-size:"+positiveDatas.size());
				
				//imageDataUrlMaps.clear();
				
				//heavy action
				
				/* no need to generate
				for(PositiveData data:positiveDatas){
					LogUtils.log(data.getFileName());
					JSFile imgFile=loadedZip.getFile(data.getFileName());
					
					
					String dataUrl=null;
					if(imgFile==null){
						dataUrl=createColorImageDataUrl(sharedCanvas,64,64,"#800");
					}else{
						//actually consume too much memory,but image is inside zip,maybe this is only way
						
						String extension=FileNames.getExtension(data.getFileName());
						FileType type=FileType.getFileTypeByExtension(extension);
						//byte[] bt=imgFile.asUint8Array().toByteArray();
						Base64Utils.toDataUrl(type.getMimeType(),imgFile.asUint8Array().toByteArray());
						//dataUrl=Base64Utils.toDataUrl(type.getMimeType(),imgFile.asUint8Array().toByteArray());
					}
					//imageDataUrlMaps.put(data.getFileName(), dataUrl);//store image data
				}
				*/
				
				LogUtils.log("update-done");
				updateTestData();
				zipUpload.reset();//some how not reset inside,maybe there are still data?
			}
		}, false, false);
		zipUpload.setAccept(FileUploadForm.ACCEPT_ZIP);
		panel.add(zipUpload);
		
		
		
		//result file area,text support(paste area)
		
		ExecuteButton execButton=new ExecuteButton("test") {
			@Override
			public void executeOnClick() {
				
				
			}
		};
		panel.add(execButton);
		
		
		
		PasteValueReceiveArea getPaster=new PasteValueReceiveArea();
		getPaster.setText("-- Clipboard Receiver -- \nClick(Focus) & Paste Template-Text Here");
		
		getPaster.setSize("600px", "30px");
		getPaster.setFocus(true);
		panel.add(getPaster);
		getPaster.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				openCvResultText=event.getValue();
				
				updateTestData();
			}
		});
		
		//dummyTextBox = new TextArea();
		//panel.add(dummyTextBox);
		
		
		//cell list
		panel.add(new Label("Total Score"));
		scoreLabel = new Label();
		panel.add(scoreLabel);
		
		//TODO scroll?
		SimpleCellTable<TestResultData> table=new SimpleCellTable<OpenCVTools.TestResultData>() {

			@Override
			public void addColumns(CellTable<TestResultData> table) {
				
				HtmlColumn<TestResultData> stateColumn=new HtmlColumn<OpenCVTools.TestResultData>() {

					@Override
					public String toHtml(TestResultData data) {
						String color="#00f";
						if(data.getState()==TestResultData.FAILD){
							color="#f00";
						}else if(data.getState()==TestResultData.COMPLETE){
							color="#0f0";
						}
						String html="<span style='background-color:"+color+"'>&nbsp;</span>";
						return html;
					}
					
				};
				table.addColumn(stateColumn);
				

				TextColumn<TestResultData> rectNumberColumn=new TextColumn<OpenCVTools.TestResultData>() {
					
					@Override
					public String getValue(TestResultData object) {
						int total=object.getPositiveData().getRects().size();
						int remain=object.getScoreData().getRemainTarget();
						return (total-remain)+"/"+total;
						
					}
				};
				table.addColumn(rectNumberColumn,"target");
				

				TextColumn<TestResultData> scoreColumn=new TextColumn<OpenCVTools.TestResultData>() {
					
					@Override
					public String getValue(TestResultData object) {
						return String.valueOf(scoreCalcurator.getTotalScore(object.getScoreData()));
						
					}
				};
				table.addColumn(scoreColumn,"score");
				
				//try
				
				//critical
				//hit
				//miss
				
				HtmlColumn<TestResultData> attemptColumn=new HtmlColumn<OpenCVTools.TestResultData>() {
					@Override
					public String toHtml(TestResultData data) {
						
						String html=toAttemptHtml(data)+"/"+toCriticalHtml(data)+"/"+toHitHtml(data)+"/"+toMissHtml(data);
						return html;
					}
					
				};
				table.addColumn(attemptColumn,"A/C/H/M");
				
				
				
				
				TextColumn<TestResultData> nameColumn=new TextColumn<OpenCVTools.TestResultData>() {
					
					@Override
					public String getValue(TestResultData object) {
						if(object.getPositiveData().getFileName()==null){
							return "";
						}
						return Ascii.truncate(object.getPositiveData().getFileName(), 40, "...");
						
					}
				};
				table.addColumn(nameColumn,"fileName");
				
			}
		};
		
		tableObjects = new EasyCellTableObjects<TestResultData>(table){

			@Override
			public void onSelect(TestResultData selection) {
				testResultDataViewer.setData(selection);
			}
			
		};
		
		
		ScrollPanel scroll=new ScrollPanel(table);
		sidePanel.add(scroll);
		
		
		return sidePanel;
	}
	
	private TestResultDataViewer testResultDataViewer;
	private EasyCellTableObjects<TestResultData> tableObjects;
	private FileUploadForm zipUpload;
	private Label scoreLabel;
	//private TextArea dummyTextBox;

	public class TestResultDataViewer{
		private Canvas canvas;
		public Canvas getCanvas() {
			return canvas;
		}

		public TestResultDataViewer(){
			canvas=Canvas.createIfSupported();
			canvas.setVisible(false);
		}
		
		public void setData(TestResultData data){
			if(data==null){
				canvas.setVisible(false);
			}else{
				updateCanvas(data);
				canvas.setVisible(true);
			}
		}

		private void updateCanvas(TestResultData data) {
			checkNotNull(data,"data is null");
			
			JSFile imgFile=loadedZip.getFile(data.getPositiveData().getFileName());
			
			
			String dataUrl=null;
			if(imgFile==null){
				dataUrl=createColorImageDataUrl(sharedCanvas,64,64,"#800");//TODO change not found
			}else{
				//actually consume too much memory,but image is inside zip,maybe this is only way
				
				String extension=FileNames.getExtension(data.getPositiveData().getFileName());
				FileType type=FileType.getFileTypeByExtension(extension);
				//byte[] bt=imgFile.asUint8Array().toByteArray();
				dataUrl=Base64Utils.toDataUrl(type.getMimeType(),imgFile.asUint8Array().toByteArray());//should use cache 300MB etc
				//dataUrl=Base64Utils.toDataUrl(type.getMimeType(),imgFile.asUint8Array().toByteArray());
			}
			
			ImageElementUtils.copytoCanvas(dataUrl, canvas);//copy to
			
			//draw original-rect
			for(Rect r:data.getPositiveData().getRects()){
				RectCanvasUtils.stroke(r, canvas, "#888");
			}
			
			for(ResultRect result:data.getResultRects()){
				String color="#00f";//hit
				if(result.getState()==TestResultData.FAILD){
					color="#f00";
				}else if(result.getState()==TestResultData.COMPLETE){
					color="#0f0";
				}
				RectCanvasUtils.stroke(result.getRect(), canvas, color);
			}
			
			
			
		}
	}
	
	//viewer
	
	//text-line converter
	
	public static class PositiveData{
		private String fileName;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public List<Rect> getRects() {
			return rects;
		}
		public void setRects(List<Rect> rects) {
			this.rects = rects;
		}
		private List<Rect> rects=Lists.newArrayList();
	}
	
	public static class PositiveDataConverter extends Converter<String, PositiveData>{
		private static Splitter splitter=Splitter.on(CharMatcher.WHITESPACE);
		private static Joiner joiner=Joiner.on(" ");
		@Override
		protected PositiveData doForward(String a) {
			if(a.isEmpty()){
				PositiveData data=new PositiveData();
				data.setFileName("");
				return data;
			}
			List<String> values=splitter.omitEmptyStrings().splitToList(a);
			
			PositiveData data=new PositiveData();
			data.setFileName(values.get(0));
			if(values.size()>1){
			//int rectNumber=Objects.firstNonNull(Ints.tryParse(values.get(1)), 0);
			int rectNumber=ValuesUtils.toInt(values.get(1), 0);
			//set number on final
				for(int i=0;i<rectNumber;i++){
					int findex=2+i*4;
					if(values.size()>1+(i+1)*4){
						
						int x=ValuesUtils.toInt(values.get(findex), 0);
						int y=ValuesUtils.toInt(values.get(findex+1), 0);
						int w=ValuesUtils.toInt(values.get(findex+2), 0);
						int h=ValuesUtils.toInt(values.get(findex+3), 0);
						if(x>=0 && y>=0 && w>0 && h>0){
							data.getRects().add(new Rect(x,y,w,h));
						}
					}
				}
				
				//contain invalid rect,clear all
				if(rectNumber!=data.getRects().size()){
					data.getRects().clear();
				}
				
			}
			return data;
		}

		@Override
		protected String doBackward(PositiveData data) {
			List<String> vs=Lists.newArrayList();
			vs.add(data.getFileName());
			
			if(data.getRects().size()>0){
			vs.add(String.valueOf(data.getRects().size()));
			}
			for(Rect r:data.getRects()){
				//right now no check invalid rect.
				vs.add(String.valueOf(r.getX()));
				vs.add(String.valueOf(r.getY()));
				vs.add(String.valueOf(r.getWidth()));
				vs.add(String.valueOf(r.getHeight()));
			}
			return joiner.join(vs);
		}
		
	}
	
	public static class ResultRect{
		public ResultRect(Rect rect,int state){
			this.rect=rect;
			this.state=state;
		}
		private Rect rect;
		public Rect getRect() {
			return rect;
		}
		public void setRect(Rect rect) {
			this.rect = rect;
		}
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		private int state;
	}
	
	public static class TestResultData{
		public static final int COMPLETE=0;
		public static final int HIT=1;
		public static final int FAILD=2;
		//private int score;
		private PositiveData positiveData;
		private ScoreData scoreData;
		
		public ScoreData getScoreData() {
			return scoreData;
		}

		public void setScoreData(ScoreData scoreData) {
			this.scoreData = scoreData;
		}

		public TestResultData(PositiveData positiveData){
			this.positiveData=positiveData;
		}
		
		public PositiveData getPositiveData() {
			return positiveData;
		}
		public void setPositiveData(PositiveData positiveData) {
			this.positiveData = positiveData;
		}
		private List<ResultRect> resultRects=new ArrayList<ResultRect>();
		public List<ResultRect> getResultRects() {
			return resultRects;
		}
		public void setResultRects(List<ResultRect> resultRects) {
			this.resultRects = resultRects;
		}
		//private String imageDataUrl;
		
		/*
		public int getScore() {
			return score;
		}
		
		public void setScore(int score) {
			this.score = score;
		}
		*/
	
		private int completeTarget;
		public int getCompleteTarget() {
			return completeTarget;
		}

		public void setCompleteTarget(int completeTarget) {
			this.completeTarget = completeTarget;
		}
		private int missTarget;
		public int getMissTarget() {
			return missTarget;
		}

		public void setMissTarget(int missTarget) {
			this.missTarget = missTarget;
		}
		/* too big
		public String getImageDataUrl() {
			return imageDataUrl;
		}
		public void setImageDataUrl(String imageDataUrl) {
			this.imageDataUrl = imageDataUrl;
		}
		*/
		private int state;
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
	}
	

	@Override
	public Panel getCenterPanel() {
		mainPanel = new VerticalPanel();
		ScrollPanel scroll=new ScrollPanel(mainPanel);
		
		mainPanel.setSpacing(4);
		
		testResultDataViewer=new TestResultDataViewer();
		
		mainPanel.add(testResultDataViewer.getCanvas());
		
		//
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				 try {
					new RequestBuilder(RequestBuilder.GET,"test.txt").sendRequest(null, new RequestCallback() {
						
						@Override
						public void onResponseReceived(Request request, Response response) {
							openCvResultText=response.getText();
							LogUtils.log(openCvResultText);
						}
						
						@Override
						public void onError(Request request, Throwable exception) {
							// TODO Auto-generated method stub
							
						}
					});
				} catch (RequestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		return scroll;
	}

	@Override
	public String getHelpUrl() {
		return "";
	}

	@Override
	public String getAppName() {
		return "OpenCV Tools";
	}

	@Override
	public String getAppVersion() {
		return "1.0";
	}

	@Override
	public String getAppUrl() {
		return "";
	}

	@Override
	public Panel getLinkContainer() {
		// TODO Auto-generated method stub
		return new HorizontalPanel();//right now not support link
	}


	@Override
	public void doDropFile(File file, String dataUrl) {
		//basically drop image
	}

}
