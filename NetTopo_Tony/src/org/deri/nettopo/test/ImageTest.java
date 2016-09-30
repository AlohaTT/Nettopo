package org.deri.nettopo.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.deri.nettopo.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ImageTest it = new ImageTest();
		Image image = it.openImage();
		//it.saveImage(image);
		byte[] imageByte =  Util.imageToArray(image);
		System.out.println("------------------:"+imageByte.length);
		Image im=it.arrayToImage(imageByte);
		it.saveAsHello(im);
	}
	
	
	public void saveAsHello(Image image)
	{
		ImageLoader il = new ImageLoader();
		il.data = new ImageData[1];
		il.data[0] = image.getImageData();
		il.save("./hello.bmp", SWT.IMAGE_BMP);
	}
	
	
//	public void saveImage(byte[] img_buffer)
//	{
//		ImageLoader il = new ImageLoader();
//		
//		il.data = new ImageData[1];
//		ByteArrayInputStream bais=new ByteArrayInputStream(img_buffer);
//		ImageData id = new ImageData(bais);
//			il.data[0] = id;
//		il.save("./hello.bmp", SWT.IMAGE_BMP);
//	}
//
//	
	public Image openImage()
	{
		ImageLoader il = new ImageLoader();
		ImageData[] data = il.load("./fox0.bmp");
		Image image = new Image(Display.getCurrent(), data[0]);
		return image;
	}
	
	
	public Image arrayToImage(byte[] imageBytes)
	{
		
		 Image image = null;  
		    try {  
		        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);  
		        image = new Image(null, bais);  
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
		    return image;  
	}
	
	
	public byte[] imageToArray(Image image)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    ImageLoader imageLoader = new ImageLoader();  
	    imageLoader.data = new ImageData[] { image.getImageData() };  
	    imageLoader.save(baos, image.type);  
	    byte[] imageByteArray = baos.toByteArray();  
	    try {  
	        baos.close();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return imageByteArray;  
		
	}
	
	
}
