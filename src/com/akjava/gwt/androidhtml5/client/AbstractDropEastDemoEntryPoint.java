package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.ui.DropDockDataUrlRootPanel;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

public abstract class AbstractDropEastDemoEntryPoint extends Html5DemoEntryPoint {

	
	protected DropDockDataUrlRootPanel mainRoot;
	protected HorizontalPanel topPanel;
	
	public abstract void doDropFile(File file,String dataUrl);
	public abstract Predicate<File>  getDoDropFilePredicate();
	
	public abstract int  getEastPanelWidth();
	public abstract Panel  getEastPanel();
	public abstract Panel  getCenterPanel();
	public abstract String getHelpUrl();
	
	@Override
	public Panel initializeWidget() {
		//create root & ondrop
		mainRoot=new DropDockDataUrlRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, String dataUrl) {
				for(File file:optional.asSet()){
					doDropFile(file, dataUrl);
				}
			}
		};
		mainRoot.setFilePredicate(getDoDropFilePredicate());
		
		
		topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		topPanel.setStylePrimaryName("bg1");
		topPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		topPanel.setSpacing(1);
		mainRoot.addNorth(topPanel,30);
		
		
		topPanel.add(createTitleWidget());
		
		topPanel.add(new Anchor(textConstants.Help(), getHelpUrl()));
		
		mainRoot.addEast(getEastPanel(),getEastPanelWidth());
		
		mainRoot.add(getCenterPanel());
		return mainRoot;
	}
	

	@Override
	public Panel getLinkContainer() {
		// TODO Auto-generated method stub
		return topPanel;
	}

}
