package com.akjava.gwt.androidhtml5.client.data;

import com.akjava.gwt.androidhtml5.client.HasImageUrl;

public class ImageUrlData implements HasImageUrl{

	

	private String url;

	public String getDataUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ImageUrlData(String fileName,String url) {
		super();
		this.fileName = fileName;
		this.url = url;
	}
	
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getImageUrl() {
		return getDataUrl();
	}

}