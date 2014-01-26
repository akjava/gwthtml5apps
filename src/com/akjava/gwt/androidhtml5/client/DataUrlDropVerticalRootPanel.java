package com.akjava.gwt.androidhtml5.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.html5.client.file.webkit.DirectoryCallback;
import com.akjava.gwt.html5.client.file.webkit.FileEntry;
import com.akjava.gwt.html5.client.file.webkit.FilePathCallback;
import com.akjava.gwt.html5.client.file.webkit.Item;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Optional;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/*
 * this is usefull when do nothing drop rootPanel
 */
public abstract class DataUrlDropVerticalRootPanel extends DropVerticalRootPanel{
	public DataUrlDropVerticalRootPanel(boolean addRootLayoutPanel) {
		super(addRootLayoutPanel);
	}

	
	@Override
	public void callback(final File file, String parent) {
		LogUtils.log(file+","+parent);
		if(file==null){
			return;
		}
		final FileReader reader = FileReader.createFileReader();
		reader.setOnLoad(new FileHandler() {
			@Override
			public void onLoad() {
				LogUtils.log("onLoad:"+file.getFileName());
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
