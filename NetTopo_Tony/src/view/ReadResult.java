package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadResult {
	
	private String filePath="./result.txt";
	//private Map<Integer,Integer> dataset=null;//<nodeNumber,PathNumber>
	
	
	public Map<Integer,Integer> readPathNumber(String key,String val) throws IOException
	{
		Map<Integer,Integer> data=new HashMap<Integer,Integer>();
		File file=new File(filePath);
		BufferedReader br=new BufferedReader(new FileReader(file));
		String line=null;
		boolean isOK=false;
		int sensorNumber=-1,pathNum=-1;
		while((line=br.readLine())!=null)
		{
			int index=line.indexOf(key);
			if(index!=-1)
			{
				index=line.indexOf(":");
				sensorNumber=Integer.parseInt(line.substring(index+1));
				if(isOK)
				{
					data.put(sensorNumber, pathNum);
					isOK=false;
				}
			}
			else {
				index = line.indexOf(val);
				if (index != -1) {
					index = line.indexOf(":");
					pathNum = Integer.parseInt(line.substring(index + 1));
					isOK=true;
				}
			}
		}
		br.close();
		return data;
	}

}
