package com.akjava.gwt.androidhtml5.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TopBarPanel extends DockLayoutPanel implements HasTop{

	private HorizontalPanel topPanel;

	public TopBarPanel() {
		super(Unit.PX);
	
		topPanel = new HorizontalPanel();
		topPanel.setSpacing(2);
		topPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		addNorth(topPanel,40);
	}

	@Override
	public Panel getTopPanel() {
		return topPanel;
	}

}
