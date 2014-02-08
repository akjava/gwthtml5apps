package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Predicate;
import com.google.gwt.dom.client.Style.Unit;

/*
 * 
 * this is usefull when do nothing drop rootPanel
 */

/**
 * @deprecated use common
 * @author aki
 *
 */
public abstract class DataUrlDropDockRootPanel extends DropDockRootPanel{
	public DataUrlDropDockRootPanel(Unit unit,boolean addRootLayoutPanel) {
		super(unit,addRootLayoutPanel);
	}

	private Predicate<File> filePredicate;
	
	public Predicate<File> getFilePredicate() {
		return filePredicate;
	}

	public void setFilePredicate(Predicate<File> filePredicate) {
		this.filePredicate = filePredicate;
	}

	@Override
	public void callback(final File file, String parent) {
		if(file==null){
			return;
		}
		if(filePredicate!=null && !filePredicate.apply(file)){
			return;
		}
		
		//LogUtils.log(file+","+parent);
		
		final FileReader reader = FileReader.createFileReader();
		reader.setOnLoad(new FileHandler() {
			@Override
			public void onLoad() {
				
				String dataUrl=reader.getResultAsString();
				loadFile(file, dataUrl);
			}
		});
		
		if(file!=null){
			reader.readAsDataURL(file);
		}
	}
	
	public abstract void loadFile(final File file,final String dataUrl);
}
