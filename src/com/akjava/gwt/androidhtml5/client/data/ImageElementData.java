package com.akjava.gwt.androidhtml5.client.data;

import com.akjava.gwt.androidhtml5.client.HasImageUrl;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class ImageElementData implements HasImageUrl{

		private ImageElement imageElement;
		public ImageElement getImageElement() {
			return imageElement;
		}
		public void setImageElement(ImageElement imageElement) {
			this.imageElement = imageElement;
		}

		private String dataUrl;

		public ImageElementData(String fileName,ImageElement imageElement ,String dataUrl) {
			super();
			this.fileName = fileName;
			this.imageElement=imageElement;
			this.dataUrl = dataUrl;
		}
		
		private String fileName;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getDataUrl() {
			return dataUrl;
		}
		public void setDataUrl(String dataUrl) {
			this.dataUrl = dataUrl;
		}
		public String getImageUrl() {
			return getDataUrl();
		}
		
		public ImageElementData copy(){
			ImageElement element=ImageElement.as(DOM.createImg());
			element.setSrc(imageElement.getSrc());
			ImageElementData newData=new ImageElementData(getFileName(),element,getDataUrl());
			return newData;
		}
		
	}