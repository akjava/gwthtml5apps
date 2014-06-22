package com.akjava.gwt.androidhtml5.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.io.GWTLineReader;
import com.akjava.lib.common.csv.CSVProcessor;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Html5DemoEntryPoint implements EntryPoint {
	public static TextConstants textConstants=GWT.create(TextConstants.class);
	
	public abstract String getAppName();
	public abstract String getAppVersion();
	public abstract String getAppUrl();
	
	public abstract Panel initializeWidget();
	public abstract Panel getLinkContainer();
	
	protected DeckLayoutPanel rootDeck;
	protected StorageControler storageControler=new StorageControler();
	
	public int getStorageValue(String key,int defaultValue){
		return storageControler.getValue(key, defaultValue);
	}
	
	public String getStorageValue(String key,String defaultValue){
		return storageControler.getValue(key, defaultValue);
	}
	public void setStorageValue(String key,int value){
		 try {
			storageControler.setValue(key, value);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
	}
	public void setStorageValue(String key,String value){
		 try {
			storageControler.setValue(key, value);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
	}
	
	@Override
	public void onModuleLoad() {
		LogUtils.log(getAppName()+":version "+getAppVersion());
		
		rootDeck = new DeckLayoutPanel();
		RootLayoutPanel.get().add(rootDeck);
		
				rootDeck.add(initializeWidget());
				
				rootDeck.showWidget(0);
				
				//create setting
				DockLayoutPanel settingPanel=new DockLayoutPanel(Unit.PX);
				HorizontalPanel settingTitles=new HorizontalPanel();
				settingTitles.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
				settingTitles.setSpacing(2);
				settingTitles.add(new Label(getAppName()+" >> "+textConstants.Settings()));
				
				
				VerticalPanel settingMain=new VerticalPanel();
				settingMain.setSize("100%", "100%");
				
				HorizontalPanel firstControlPanel=new HorizontalPanel();
				settingMain.add(firstControlPanel);
				Button closeBt=new Button(textConstants.close(),new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						onCloseSettingPanel();
						rootDeck.showWidget(0);
					}
				});
				closeBt.setWidth("200px");
				firstControlPanel.add(closeBt);
				
				
				settingPanel.addNorth(settingTitles,30);
				settingPanel.add(settingMain);
				settingMain.add(createMainSettingPage());
				
				rootDeck.add(settingPanel);//index 1 is setting
		
		parseLinkCsv(getLinkContainer());
	}
	
	public Widget createTitleWidget(){
		Anchor label=new Anchor("akjava.com "+getAppName()+" "+getAppVersion(),getAppUrl());
		label.setStylePrimaryName("title");
		
		return label;
	}
	
	
	private class ValidCsv implements Predicate<List<String>>{
		public boolean apply(List<String> input) {
			if(input==null || input.size()<2){//must have [title],[link]
				return false;
			}
			
			//no need same app
			if(input.get(0).equals(getAppName())){
				return false;
			}
			return true;
		}
	}
	
	private class CsvToAlink implements Function<List<String>,Anchor>{

		@Override
		public Anchor apply(List<String> csv) {
			Anchor a=null;
			if(csv.size()>1){
				a=new Anchor(""+csv.get(0)+"",csv.get(1));
				a.setStylePrimaryName("title");
			}
			if(csv.size()>2){//optional
				a.setTitle(csv.get(2));
			}
			
			return a;
		}
		
	}
	

	private void parseLinkCsv(final Panel linkContainer){
		try {
			new RequestBuilder(RequestBuilder.GET, "apps.csv").sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					
					List<List<String>> csvs;
					try {
						
						csvs = GWTLineReader.wrap(response.getText()).readLines(new CSVProcessor(','));
						HorizontalPanel p1=new HorizontalPanel();
						
						p1.setSpacing(2);
						
						String links=Joiner.on(" | ").join(
						FluentIterable.from(csvs).filter(new ValidCsv()).transform(new CsvToAlink())
						);
						
						HTML html=new HTML(links);
						
						
						
						linkContainer.add(html);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					LogUtils.log("csv not found");
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public Panel createMainSettingPage(){
		VerticalPanel panel=new VerticalPanel();
		return panel;
	}
	public void onOpenSettingPanel(){
		
	}
	public void onCloseSettingPanel(){
		
	}
	//TODO move up
	public Anchor createSettingAnchor(){
		Anchor setting=new Anchor("Settings");
		//topPanel.add(setting);
		setting.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onOpenSettingPanel();
				rootDeck.showWidget(1);//setting
			}
		});
		return setting;
	}
}
