package junit;

import junit.framework.TestCase;

import com.akjava.gwt.androidhtml5.client.OpenCVTools.PositiveData;
import com.akjava.gwt.androidhtml5.client.OpenCVTools.PositiveDataConverter;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class PositiveDataConverterTest extends TestCase{

	public void testEmpty(){
		String line="hello";
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertTrue(pdata.getFileName().equals(line) && pdata.getRects().size()==0);
	}
	public void testEmpty2(){
		String line="hello";
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertEquals(line, new PositiveDataConverter().reverse().convert(pdata));
	}
	
	public void testRect1(){
		String line="hello 1 0 0 100 100";
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertEquals(line, new PositiveDataConverter().reverse().convert(pdata));
	}
	

	
	public void testRect2(){
		String line="hello   \t1 0   0   100   100  ";
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		line=Joiner.on(" ").join(Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().split(line));
		assertEquals(line, new PositiveDataConverter().reverse().convert(pdata));
	}
	
	public void testRect3(){
		String line="hello 2 0 0 100 100 20 20 40 40";
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertEquals(line, new PositiveDataConverter().reverse().convert(pdata));
	}
	
	public void testRectInvalid1(){
		String line="hello 2 0 0 100 100";//invalid rect size
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertTrue(pdata.getRects().size()==0);
	}
	
	public void testRectInvalid2(){
		String line="hello 2 0 0 100 ";//lack of value
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertTrue(pdata.getRects().size()==0);
	}
	
	public void testRectInvalid3(){
		String line="hello 2 0 0 0 0";//empty
		
		PositiveData pdata=new PositiveDataConverter().convert(line);
		
		assertTrue(pdata.getRects().size()==0);
	}
}
