package com.akjava.gwt.androidhtml5.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

/**
 * @deprecated moved to common
 * @author aki
 *
 * @param <T>
 */
public abstract class HtmlColumn<T> extends Column<T,SafeHtml>{

	public HtmlColumn(Cell<SafeHtml> cell) {
		super(cell);
	}

	public abstract String toHtml(T object);
	
	@Override
	public SafeHtml getValue(T object) {
		SafeHtmlBuilder sb = new SafeHtmlBuilder();
    	 sb.appendHtmlConstant(toHtml(object));
    	 return sb.toSafeHtml();
	}
	
}