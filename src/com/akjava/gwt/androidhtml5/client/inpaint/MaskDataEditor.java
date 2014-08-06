package com.akjava.gwt.androidhtml5.client.inpaint;

import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MaskDataEditor extends VerticalPanel implements Editor<MaskData>{
	public interface MaskDataEditorDriver extends SimpleBeanEditorDriver<MaskData, MaskDataEditor> {}
	CheckBox transparentEditor;
	
	ColorBox colorEditor;
	InputRangeWidget expandEditor;
	InputRangeWidget fadeEditor;
	CheckBox similarColorEditor;
	CheckBox invertEditor;
	InputRangeWidget maxLengthEditor;

	//SimpleEditor<Rect> clipRectEditor=SimpleEditor.of();
	CheckBox useClipEditor;

	public MaskDataEditor(){
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(h1);
		
		transparentEditor = new CheckBox("transparent");
		h1.add(transparentEditor);
		
		colorEditor = new ColorBox();
		
		
		h1.add(colorEditor);
		
		similarColorEditor = new CheckBox("similarColor");
		h1.add(similarColorEditor);
		
		useClipEditor = new CheckBox("use clip");
		h1.add(useClipEditor);
		useClipEditor.setVisible(false);//this is for future support clip-area box
		
		invertEditor = new CheckBox("invert");
		h1.add(invertEditor);
		invertEditor.setVisible(false);//future invert support
		
		/*
		invertEditor = new CheckBox("invert");
		h1.add(useClipEditor);
		*/
		
		
		HorizontalPanel h2=new HorizontalPanel();
		h2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(h2);
		final Label maxLengthLabel=new Label("max-simiar:");
		h2.add(maxLengthLabel);
		maxLengthEditor = HTML5InputRange.createInputRange(1, 256, 1);
		h2.add(maxLengthEditor);
		maxLengthEditor.setWidth(260);
		maxLengthEditor.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				maxLengthLabel.setText("max-simiar:"+newValue);
			}
		});
		
		HorizontalPanel h3=new HorizontalPanel();
		h3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(h3);
		final Label expandLabel=new Label("expand:");
		h3.add(expandLabel);
		
		expandEditor = HTML5InputRange.createInputRange(0, 40, 0);
		h3.add(expandEditor);
		expandEditor.setWidth(260);
		expandEditor.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				expandLabel.setText("expand:"+newValue);
			}
		});
		
		HorizontalPanel h4=new HorizontalPanel();
		h4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.add(h4);
		final Label fadeLabel=new Label("fade:");
		h4.add(fadeLabel);
		fadeEditor = HTML5InputRange.createInputRange(0, 80, 0);
		h4.add(fadeEditor);
		fadeEditor.setWidth(260);
		fadeEditor.addInputRangeListener(new InputRangeListener() {
			@Override
			public void changed(int newValue) {
				fadeLabel.setText("fade:"+newValue);
			}
		});
		
	}
	
	@com.google.gwt.editor.client.Editor.Ignore
	public ColorBox getColorEditor(){
		return colorEditor;
	}

	public void setEnabled(boolean bool) {
		transparentEditor.setEnabled(bool);
		
		//colorEditor.setEnabled(bool);//bug
		expandEditor.setEnabled(bool);
		fadeEditor.setEnabled(bool);
		similarColorEditor.setEnabled(bool);
		maxLengthEditor.setEnabled(bool);
		useClipEditor.setEnabled(bool);
		
		//TODO disable range label
	}
}
