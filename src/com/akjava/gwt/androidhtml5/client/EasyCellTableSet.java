package com.akjava.gwt.androidhtml5.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * single selection
 * @author aki
 *
 * @param <T>
 */
public abstract class EasyCellTableSet<T> {
	protected List<T> datas=new ArrayList<T>();
	protected SimpleCellTable<T> simpleCellTable;
	
	protected SingleSelectionModel<T> selectionModel;
	
	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public SimpleCellTable<T> getSimpleCellTable() {
		return simpleCellTable;
	}

	public void setSimpleCellTable(SimpleCellTable<T> simpleCellTable,boolean showPager) {
		this.simpleCellTable = simpleCellTable;
		
		
	}
	
	public abstract void onSelect(T selection);

	public void setSelected(T item,boolean selected){
		selectionModel.setSelected(item, selected);
	}
	public SingleSelectionModel<T> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SingleSelectionModel<T> selectionModel) {
		this.selectionModel = selectionModel;
	}

	public EasyCellTableSet(SimpleCellTable<T> simpleCellTable){
		this.simpleCellTable=simpleCellTable;
	}
	
	public EasyCellTableSet(SimpleCellTable<T> simpleCellTable, boolean showPager) {
		this.simpleCellTable=simpleCellTable;
		selectionModel=new SingleSelectionModel<T>();
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				onSelect(selectionModel.getSelectedObject());
			}
		});
		
		simpleCellTable.setSelectionModel(selectionModel);
		simpleCellTable.getPager().setVisible(showPager);
	}

	public void addItem(T data){
		datas.add(data);
		update(false);
	}
	
	public void removeItem(T data){
		selectionModel.setSelected(data, false);
		datas.remove(data);
		
		update(false);
	}
	/**
	 * 
	 * @param flush
	 */
	public void update(boolean flush){
		simpleCellTable.setData(datas,flush);
	}
}
