package com.akjava.gwt.androidhtml5.client;

import com.akjava.gwt.lib.client.ImageElementUtils;
import com.google.common.base.Function;
import com.google.gwt.dom.client.ImageElement;

public class DataToImageElement implements Function<HasImageUrl,ImageElement>{

	@Override
	public ImageElement apply(HasImageUrl input) {
		return ImageElementUtils.create(input.getImageUrl());
	}
	
}