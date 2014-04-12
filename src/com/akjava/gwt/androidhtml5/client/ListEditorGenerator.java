package com.akjava.gwt.androidhtml5.client;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Converter;
import com.google.common.base.Objects;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class ListEditorGenerator<T> {

	private EasyCellTableObjects<T> easyCells;
	public EasyCellTableObjects<T> getEasyCells() {
		return easyCells;
	}


	private Button updateButton;
	private Button newButton;

	private Button removeButton;
	private Button addButton;
	public VerticalPanel generatePanel(final SimpleCellTable<T> cellTable,final Converter<List<T>,String> converter,final Editor<T> editor,final  SimpleBeanEditorDriver driver,Map<String,String> labelMaps,final ValueControler valueControler){
		//check nulls
		final SimpleBeanEditorDriver<T,Editor<? super T>> castdriver=driver;
		
		VerticalPanel panel=new VerticalPanel();
		panel.add(cellTable);
		
		easyCells = new EasyCellTableObjects<T>(cellTable
				) {
			@Override
			public void onSelect(T selection) {
				if(selection!=null){
					castdriver.edit(selection);
					//baseFormantEditor.setVisible(true);
					updateButton.setEnabled(true);
					removeButton.setEnabled(true);
					newButton.setEnabled(true);
					addButton.setEnabled(false);
				}else{
					//baseFormantEditor.setVisible(false);
					updateButton.setEnabled(false);
					removeButton.setEnabled(false);
					newButton.setEnabled(false);
					addButton.setEnabled(true);
				}		
			}
			
		};
		
		castdriver.initialize(editor);
		castdriver.edit(createNewData());
		
		String baseText=valueControler.getValue();
		
		easyCells.setDatas(converter.reverse().convert(baseText));
		easyCells.update(true);
		

		HorizontalPanel buttons=new HorizontalPanel();
		panel.add(buttons);
		
		newButton = new Button(Objects.firstNonNull(labelMaps.get("new"),"New"),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				easyCells.unselect();
				castdriver.edit(createNewData());
			}
		});
		buttons.add(newButton);
		newButton.setEnabled(false);
		
		addButton = new Button(Objects.firstNonNull(labelMaps.get("add"),"Add"),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				T data=castdriver.flush();
				easyCells.addItem(data);
				castdriver.edit(createNewData());
				
				valueControler.setValue(converter.convert(easyCells.getDatas()));
			}
		});
		buttons.add(addButton);
		addButton.setEnabled(true);
		

		
		updateButton = new Button(Objects.firstNonNull(labelMaps.get("update"),"Update"),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				driver.flush();
				easyCells.update(true);
				
				valueControler.setValue(converter.convert(easyCells.getDatas()));
				
			}
		});
		updateButton.setEnabled(false);
		buttons.add(updateButton);
		
		removeButton = new Button(Objects.firstNonNull(labelMaps.get("remove"),"Remove"),new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(easyCells.getSelection()!=null){
					easyCells.removeItem(easyCells.getSelection());
					
					valueControler.setValue(converter.convert(easyCells.getDatas()));
				}
			}
		});
		buttons.add(removeButton);
		removeButton.setEnabled(false);
		
		//panel.add(editor);
		
		//add editor by yourself
		
		
		return panel;
	}
	
	//should i move to ?
	public abstract T createNewData();
	
	
	/**
	 * need when data load or store
	 * @author aki
	 *
	 */
	public static interface  ValueControler{
		public void setValue(String value);
		public String getValue();
		
	}
}
