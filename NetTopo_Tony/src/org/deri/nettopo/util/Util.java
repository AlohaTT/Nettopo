package org.deri.nettopo.util;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.regex.Pattern;

import org.deri.nettopo.network.WirelessSensorNetwork;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class Util {
	
	/**
	 * @param arg  such as: 3-56
	 * @return true if it is a string range
	 */
	
	
	public static boolean isStringRange(String arg){
		boolean result = false;
		String regex = "\\d+-\\d+";
		if(Pattern.matches(regex, arg)){
			int index= arg.indexOf('-');
			int first = Integer.parseInt(arg.substring(0, index));
			int last  = Integer.parseInt(arg.substring(index+1));
			if(last >= first){
				result = true;
			}else{
				result = false;
			}
		}else{
			result = false;
		}
		
		return result;
	}
	
	/**
	 * convert string range to int array
	 * @param arg
	 * @return
	 */
	public static int[] stringRange2IntArray(String arg){
		int[] result = null;
		if(isStringRange(arg)){
			int index = arg.indexOf('-');
			int first = Integer.parseInt(arg.substring(0, index));
			int last =  Integer.parseInt(arg.substring(index+1));
			int size = last - first + 1;
			result = new int[size];
			for(int i=0;i<size;i++){
				result[i] = first + i;
			}
		}else{
			result = null;
		}
		return result;
	}
	
	/**
	 * to check if the arg can be convert to Integer array
	 * if arg=="", return false
	 * @param arg
	 * @return
	 */
	public static boolean string2IntArrayBoolean(String arg){
		if(arg.trim().equals("")){
			return false;
		}
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			try {
				if(!FormatVerifier.isNotNegative(value)){
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * to check if the arg can be convert to Integer array
	 * if arg=="", return false
	 * @param arg
	 * @return
	 */
	public static boolean string2PositiveIntArrayBoolean(String arg){
		if(arg.trim().equals("")){
			return false;
		}
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			try {
				if(!FormatVerifier.isPositiveInteger(value)){
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * The token of the StringTokenizer is:",: ",that is , or : or blank space
	 * Converts a String to an int array.
	 * @return new int array
	 */
	public static int[] string2IntArray(String arg) {
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		Vector<Integer> rs = new Vector<Integer>();
		while (st.hasMoreTokens()) {
			rs.addElement(new Integer(Integer.parseInt(st.nextToken())));
		}
		int[] result = new int[rs.size()];
		for (int j = 0; j < result.length; j++)
			result[j] = ((Integer)rs.elementAt(j)).intValue();
		return result;
	}

	/**
	 * to check if the arg can be convert to Double array
	 * if arg=="", return false
	 * @param arg
	 * @return
	 */
	public static boolean string2DoubleArrayBoolean(String arg){
		if(arg.trim().equals("")){
			return false;
		}
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			try {
				if(!FormatVerifier.isDouble(value)){
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * to check if the arg can be convert to Double array
	 * if arg=="", return false
	 * @param arg
	 * @return
	 */
	public static boolean string2PositiveDoubleArrayBoolean(String arg){
		if(arg.trim().equals("")){
			return false;
		}
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			try {
				if(!FormatVerifier.isPositiveDouble(value)){
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * The token of the StringTokenizer is:",: ". that is , or : or blank space
	 * Converts a String to a double array.
	 * @return new double array
	 */
	public static double[] string2DoubleArray(String arg) {
		StringTokenizer st = new StringTokenizer(arg, ",: ");
		Vector<Double> rs = new Vector<Double>();
		while (st.hasMoreTokens()) {
			String value = st.nextToken();
			try {
				if(FormatVerifier.isDouble(value)){
					rs.addElement(new Double(Double.parseDouble(value)));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
		double[] result = new double[rs.size()];
		for (int j = 0; j < result.length; j++)
			result[j] = rs.elementAt(j).doubleValue();
		return result;
	}
	
	/**
	 * Concatenates two string arrays
	 * @param a first array
	 * @param b second array
	 * @return new and concated string array
	 */
	public static String[] stringArrayConcat(String[] a, String[] b) {
		String[] c = new String[((a != null) ? a.length : 0) + ((b != null) ? b.length : 0)];
		if (a != null)
			System.arraycopy(a, 0, c, 0, a.length);
		if (b != null)
			System.arraycopy(b, 0, c, (a != null) ? a.length : 0, b.length);
		return c;
	}

	/**
	 * 
	 * @param array the String[] to be processed
	 * @param n how many elements will be removed from array
	 * @return new String[]
	 */
	public static String[] removeFirstElements(String[] array, int n) {
		String[] r = new String[array.length - n];
		System.arraycopy(array, n, r, 0, r.length);
		return r;
	}
	
	/**
	 * to check if the nodeID is in the nodeIDs array
	 * @param nodeID given integer
	 * @param nodeIDs given integer array
	 * @return 
	 */
	public static boolean isIntegerInIntegerArray(int element, int[] array){
		boolean in = false;
		for(int i=0;i<array.length;i++){
			if(array[i] == element){
				in = true;
				break;
			}
		}
		return in;
	}
	
	/**
	 * to check if the array1's elements are all in the array2
	 * @param nodeIDs1 given integer array1
	 * @param nodeIDs2 given integer array2
	 * @return 
	 */
	public static boolean isIntegerArrayInIntegerArray(int[] array1, int[] array2){
		if(array1.length > array2.length){
			return false;
		}
		for(int i=0;i<array1.length;i++){
			boolean isIn = Util.isIntegerInIntegerArray(array1[i], array2);
			if(isIn == false){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This function return the 0-based element index of the attrName in the attrNames array\
	 * and -1 if the attrName is not found in the array 
	 * @param attrName
	 * @return 
	 */
	public static int indexOf(String[] attrNames, String attrName){
		for(int i=0;i<attrNames.length;i++){
			if(attrNames[i].equals(attrName))
				return i;
		}
		return -1;
	}
	
	/**
	 * check if the given boolean array does not contain false
	 * @param attrValid a boolean array
	 * @return
	 */
	public static boolean checkAllArgValid(boolean[] attrValid){
		for(int i=0;i<attrValid.length;i++){
			if(!attrValid[i])
				return false;
		}
		return true;
	}

	/**
	 * check if there is more than one num in the array
	 * @param num
	 * @param array
	 * @return
	 */
	public static boolean isDuplicatedInIntegerArray(int num, int[] array){
		int count = 0;
		for(int i=0;i<array.length;i++){
			if(array[i] == num){
				++count;
				if(count >= 2){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * check if there is only one element in the array of all the elements.
	 * @param array
	 * @return
	 */
	public static boolean isDuplicatedIntegerArray(int[] array){
		boolean duplicated = false;
		for(int i=0;i<array.length;i++){
			if(Util.isDuplicatedInIntegerArray(array[i], array)){
				duplicated = true;
				break;
			}
		}
		return duplicated;
	}
	
	/**
	 * test if Class c1 is derived from Class with name c2
	 * @param c1 
	 * @param c2
	 * @return
	 */
	public static boolean isDerivedClass(Class<?> c1, String c2){
		Class<?> superC = c1.getSuperclass();
		if(superC!=null){
			String superName = superC.getName();
			if(superName.equals(c2)){
				return true;
			}else{
				return isDerivedClass(superC, c2);
			}
		}
		return false;
	}

	/**
	 * give a possibility, then you can use this function for many times to 
	 * validate the possibility. It is meaningless if you just use it once.
	 * @param possibility
	 * @return
	 */
	public static boolean isDoneWithThePossibility(double possibility){
		boolean result = false;
		if(possibility > 1 || possibility < 0){
			System.err.println("The possibility should between 0 and 1");
			System.exit(0);
		}else{
			double temp = Math.random();
			if(temp <= possibility)
				result = true;
			else
				result = false;
		}
		
		return result;
	}
	
	/**
	 * to generate a no duplicated integer array with 1-based elements.
	 * @param size
	 * @return
	 */
	public static int[] generateNotDuplicatedIntArray(int size){
		int[] array = new int[size];
		for(int i=0;i<size;){
			int nextInt = 1 + new Random().nextInt(size);
			if(isIntegerInIntegerArray(nextInt,array)){
				continue;
			}else{
				array[i] = nextInt;
			}
			i++;
		}
		return array;
	}
	
	/**
	 * generate Disordered Integer Array With Existing Array.
	 * @param array
	 * @return
	 */
	public static int[] generateDisorderedIntArrayWithExistingArray(int[] array){
		int[] indexArray = generateNotDuplicatedIntArray(array.length);
		int[] resultArray = new int[array.length];
		for(int i=0;i<resultArray.length;i++){
			resultArray[i] = array[indexArray[i] - 1];
		}
		return resultArray;
	}
	
	/**
	 * @param first
	 * @param in
	 * @return the the elements in first array that alse in the in array
	 */
	public static int[] IntegerArrayInIntegerArray(int[] first, int[] in){
		LinkedList<Integer> array = new LinkedList<Integer>();
		for(int i=0;i<first.length;i++){
			if(isIntegerInIntegerArray(first[i],in)){
				array.add(first[i]);
			}
		}
		return IntegerArray2IntArray(array.toArray(new Integer[array.size()]));
	}
	
	public static int[] IntegerArray2IntArray(Integer[] array){
		int[] result = new int[array.length];
		for(int i=0;i<array.length;i++){
			result[i] = array[i].intValue();
		}
		return result;
	}
	
//	public static void main(String[] args){
//		int[] array1 = {1,2};
//		int[] array2 = {3,4,5};
//		Integer[] result = IntegerArrayInIntegerArray(array1,array2);
//		System.out.println(Arrays.toString(result));
//	}
	/***********************************************修改************************************************/
	
	public static boolean isNumeric(String str) {
//		for (int i = str.length(); --i >= 0;) {
//			if (!Character.isDigit(str.charAt(i))) {
//				return false;
//			}
//		}
//		return true;
		return FormatVerifier.isPositive(str);
	}
	
	
	
	public static boolean isInArea(Coordinate a,int tr_a,Coordinate b, int tr_b)
	{
		
//		if((a.getY()+tr_a)<((b.getY()-tr_b))&&(a.getY()-tr_a)>(b.getY()+tr_b))
//			return false;
//		if((a.getX()+tr_a)<((b.getX()-tr_b))&&(a.getX()-tr_a)>(b.getX()+tr_b))
//			return false;
		if(Math.abs(a.getX()-b.getX())<(tr_a+tr_b)&&Math.abs(a.getY()-b.getY())<(tr_a+tr_b)&&Math.abs(a.getZ()-b.getZ())<(tr_a+tr_b))
		return true;
		return false;
	}
	
	
	/*****************************************add***************************************/
	/**
	 * calculate the area
	 * @param <E>
	 * @param Point List
	 * @return the area 
	 * formulation: s=0.5*((x0*y1-x1*y0)+(x1*y2-x2*y1)+...+(xn*y0-x0*yn))
	 */
	
	public static  double getArea(List<Object> list){
		double area = 0.00;
		for(int i=0; i <list.size(); i++){
			if(i<list.size()-1){
				Point p1 = (Point) list.get(i);
				Point p2 = (Point)list.get(i+1);
				area += p1.getX()*p2.getY()-p1.getY()*p2.getX();
			}else{
				Point pn = (Point)list.get(i);
				Point p0 = (Point)list.get(0);
				area += pn.getX()*p0.getY()-p0.getX()*pn.getY();
			}
		}
		area = area/2.00;
		return area;
	}
	
	
	//计算叉乘，判断方向，若叉乘结果小于0，大于180度，大于0，则小于180度，也就是sin的值正负的问题
	public static double getCrossMultiple(Coordinate a, Coordinate b,Coordinate c){// a:center   b:first   c:next

		int ab_x = b.x-a.x;
		int ab_y = b.y-a.y;
		int ac_x = c.x-a.x;
		int ac_y = c.y-a.y;
		double result = ab_x*ac_y-ab_y*ac_x;
		return result;
	}
	
	//计算点乘法 ，角度为0-180，角度越小，点乘越大
	public static double getDotMultiple(Coordinate a,Coordinate b,Coordinate c){
		double cosBAC;
		int ab_x = b.x-a.x;
		int ab_y = b.y-a.y;
		int ac_x = c.x-a.x;
		int ac_y = c.y-a.y;
		int result = ab_x*ac_x + ab_y*ac_y;
		cosBAC = result/(Math.sqrt(ab_x*ab_x+ab_y*ab_y)*Math.sqrt(ac_x*ac_x+ac_y*ac_y));
		return cosBAC;
	}
//	public static void main(String[] args){
//		int[] array1 = {1,2};
//		int[] array2 = {3,4,5};
//		Integer[] result = IntegerArrayInIntegerArray(array1,array2);
//		System.out.println(Arrays.toString(result));
//	}
	
	public static double angel(Coordinate cen,Coordinate first,Coordinate second)
	{
		double ma_x = first.x - cen.x;  
	    double ma_y = first.y - cen.y;  
	    double mb_x = second.x - cen.x;  
	    double mb_y = second.y - cen.y;  
	    double v1 = (ma_x * mb_x) + (ma_y * mb_y);  
	    double ma_val = Math.sqrt(ma_x * ma_x + ma_y * ma_y);  
	    double mb_val = Math.sqrt(mb_x * mb_x + mb_y * mb_y);  
	    double cosM = v1 / (ma_val * mb_val);  
	    return cosM;
	}
	
	
	
	public static List<String> getXMLFileName(String fileDir)//“。xml” is not included.
	{
		ArrayList<String> tasksName=new ArrayList<String>();
		File file=new File(fileDir);
		File[] tasksFile=file.listFiles();
		for(File f:tasksFile)
		{
			String filename=f.getName();
			if(f.isFile()&&f.getName().endsWith(".xml"))
			{
				filename=filename.substring(0,filename.indexOf("."));
				tasksName.add(filename);
			}
		}
		return tasksName;
	}
	
	public static List<String> getXMLFilePath(String fileDir)//
	{
		ArrayList<String> tasksName=new ArrayList<String>();
		File file=new File(fileDir);
		File[] tasksFile=file.listFiles();
		for(File f:tasksFile)
		{
			String filename=f.getName();
			if(f.isFile()&&f.getName().endsWith(".xml"))
			{
				tasksName.add(filename);
			}
		}
		return tasksName;
	}
	
	
	static public byte[] objectToByteArray(WirelessSensorNetwork wsn0)
			throws Exception // 序列化
	{
		byte[] bytes = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		// ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(
		// new File("E:/Person.txt")));
		oo.writeObject(wsn0);
		bytes = bo.toByteArray();
		System.out.println("wsn length:" + bytes.length);
		oo.close();
		bo.close();
		return bytes;
	}

	static public WirelessSensorNetwork byteArrayToObject(byte[] bytesBackup)
			throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bytesBackup));
		WirelessSensorNetwork wsn0 = (WirelessSensorNetwork) ois.readObject();
		ois.close();
		return wsn0;
	}
	
	public static byte[] imageToArray(Image image) throws IOException{
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
	
	public static Image arrayToImage(byte[] imageBytes) throws IOException{
		 Image image = null;  
		    try {  
		        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);  
		        image = new Image(null, bais);  
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
		    return image;  
		
	}
	
	
	
//    /**
//     * 递归删除目录下的所有文件及子目录下所有文件
//     * @param dir 将要删除的文件目录
//     * @return boolean Returns "true" if all deletions were successful.
//     *                 If a deletion fails, the method stops attempting to
//     *                 delete and returns "false".
//     */
    public static boolean deleteDir(File dir) {
    	File[] files = dir.listFiles();
    	for(File fi:files)
    	{
    		if(!fi.delete())
    			return false;
    	}
       return true;
    }
	
    
	public static boolean roulette(double rate)		//true  需要减小半径;   false    不用减少半径
	{
		double temp=Math.random()*100;
		if(rate<temp)
			return true;
		return false;
	}
    
	
	public static Integer[] copyIntegerArray(Integer[] source)
	{
		Integer[] dis=new Integer[source.length];
		for(int i=0;i<source.length;++i)
		{
			dis[i]=new Integer(source[i]);
		}
		return dis;
	}
	
	
	
	
	public static void main(String[] args) {
		System.out.println(Util.getCrossMultiple(new Coordinate(0,0,0), new Coordinate(-2,-2,0), new Coordinate(-1,2,0)));
	}
	
}
