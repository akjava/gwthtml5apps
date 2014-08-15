package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.Uint8Array;
import com.akjava.gwt.html5.client.file.ui.DropDockDataArrayRootPanel;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

public abstract class AbstractDropArrayEastDemoEntryPoint extends Html5DemoEntryPoint {

	
	protected DropDockDataArrayRootPanel mainRoot;
	protected HorizontalPanel topPanel;
	
	public abstract void doDropFile(File file, Uint8Array array);
	public abstract Predicate<File>  getDoDropFilePredicate();
	
	public abstract int  getEastPanelWidth();
	public abstract Panel  getEastPanel();
	public abstract Panel  getCenterPanel();
	public abstract String getHelpUrl();
	
	@Override
	public Panel initializeWidget() {
		//create root & ondrop
		mainRoot=new DropDockDataArrayRootPanel(Unit.PX,false){
			
			@Override
			public void loadFile(String pareht, Optional<File> optional, Uint8Array array) {
				for(File file:optional.asSet()){
					doDropFile(file, array);
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
