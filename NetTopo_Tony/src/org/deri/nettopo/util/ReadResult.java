package org.deri.nettopo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ReadResult {
	
	final private String imagePath="./image/"; 
	final private String resultPath="./result.txt"; 
	
	public List<Integer> getOnePath(String name) throws IOException
	{
		File resultFile=new File(resultPath);
		BufferedReader br=new BufferedReader(new FileReader(resultFile));
		String line=null;
		List<Integer> path=new ArrayList<Integer>();
		boolean isFind=false;
		while((line=br.readLine())!=null)
		{
			if(line.startsWith("name"))
			{
				if(line.substring(line.indexOf(":")+7,line.length()-4).equals(name))
				{
					isFind=true;
				}
				else if(isFind)
					break;
			}
			if(isFind&&line.contains("[")&&line.contains("]"))
			{
				line=line.substring(line.indexOf("[")+1,line.indexOf("]"));
				String[] array=line.split(",");
				if(array.length!=0)
				{
					for(String arr:array)
					path.add(Integer.parseInt(arr.trim()));
				}
			}
		}
		br.close();
		return path;
	}
	
	public ArrayList<ArrayList<Integer>> getAllPaths(String name,String functionName) throws IOException
	{
		File resultFile=new File(resultPath);
		BufferedReader br=new BufferedReader(new FileReader(resultFile));
		String line=null;
		ArrayList<ArrayList<Integer>> paths=new ArrayList<ArrayList<Integer>>();
		
		boolean isFind=false;
		while((line=br.readLine())!=null)
		{
			ArrayList<Integer> path=new ArrayList<Integer>();
			if(line.startsWith("name"))
			{
				if(line.endsWith(name))
				{
					isFind=true;
				}
				else if(isFind)
					break;
			}
			if(isFind&&line.contains("[")&&line.contains("]"))
			{
				line=line.substring(line.indexOf("[")+1,line.indexOf("]"));
				String[] array=line.split(",");
				if(array.length!=0)
				{
					for(String arr:array)
					path.add(Integer.parseInt(arr.trim()));
					paths.add(path);
				}
			}
		}
		br.close();
		return paths;
	}
	
	public Image getBufferImage(String name,Display display)
	{
		File imageFile=new File(imagePath);
		File[] allImage=imageFile.listFiles();
		for(File f:allImage)
		{
			String filename=f.getName();
			if(filename.endsWith(".bmp")&&filename.startsWith(name))
			{
				
					ImageLoader il = new ImageLoader();
					ImageData[] data = il.load(f.getPath());
					if (data != null & data.length > 0)
						return new Image(display, data[0]);
			}
		}
		
		return null;
	}

}
