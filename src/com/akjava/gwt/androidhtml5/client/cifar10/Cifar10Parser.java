package com.akjava.gwt.androidhtml5.client.cifar10;
import static com.google.common.base.Preconditions.checkState;
import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.androidhtml5.client.ImageDataUtils;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.dom.client.ImageData;

public class Cifar10Parser {

	public List<Cifar10Data> parse(byte[] bytes,@Nullable String name){
		List<Cifar10Data> datas=Lists.newArrayList();
		checkState(bytes.length==30730000," invalid byte-data:length="+bytes.length);
		for(int i=0;i<1000;i++){
			//LogUtils.log("making:"+i);
			Cifar10Data data=new Cifar10Data();
			data.setName(name);
			int index=3073*i;
			
			data.setClassNumber(bytes[index]);
			ImageData imageData=ImageDataUtils.create(32,32);
			//red
			for(int ry=0;ry<32;ry++){
				for(int rx=0;rx<32;rx++){
					int redIndex=(index+1)+ry*32+rx;
					int red=toInt(bytes[redIndex]);
					
					imageData.setRedAt(red, rx, ry);
					imageData.setAlphaAt(255, rx, ry);
				}
			}
			
			//green
			for(int gy=0;gy<32;gy++){
				for(int gx=0;gx<32;gx++){
					int greenIndex=(index+1)+1024+gy*32+gx;
					imageData.setGreenAt(toInt(bytes[greenIndex]), gx, gy);
				}
			}
			//blue
			for(int by=0;by<32;by++){
				for(int bx=0;bx<32;bx++){
					int blueIndex=(index+1)+1024+1024+by*32+bx;
					imageData.setBlueAt(toInt(bytes[blueIndex]), bx, by);
				}
			}
			data.setImageData(imageData);
			
			
			
			//cat only test
			
			datas.add(data);
			
		}
		return datas;
	}
	  private static final int UNSIGNED_MASK = 0xFF;
	  public static int toInt(byte value) {
		    return value & UNSIGNED_MASK;
		  }
}
