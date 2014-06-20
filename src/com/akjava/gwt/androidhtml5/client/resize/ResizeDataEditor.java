package com.akjava.gwt.androidhtml5.client.resize;

import com.akjava.gwt.androidhtml5.client.SimpleResize;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResizeDataEditor extends VerticalPanel implements Editor<ResizeData>{
	public static Driver driver=GWT.create(Driver.class);
	 interface Driver extends SimpleBeanEditorDriver<ResizeData, ResizeDataEditor> {};
	 
	 IntegerBox sizeEditor;
	 
	 public ResizeDataEditor(){
			super();
			
			HorizontalPanel sizes=new HorizontalPanel();
			sizes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			this.add(sizes);
			Label f1Label=new Label(SimpleResize.textConstants.size());
			f1Label.setWidth("100px");
			sizes.add(f1Label);
			
			sizeEditor=new IntegerBox();
			sizeEditor.setWidth("100px");
			sizes.add(sizeEditor);
	 }
}
