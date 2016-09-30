package org.deri.nettopo.util;

import java.util.*;
import java.io.Serializable;
import java.math.*;


public class FormatVerifier implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static boolean isPositive(String value){
		try {
			double doubleValue = Double.parseDouble(value);
			if (doubleValue>0)
				return true;
			else
				return false;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isNotNegative(String value){
		return isAtLeast(value,0);
	}
	
	public static boolean isStringEmpty(String value){
		String str = value; 
		if (str.length() > 0){
			return true;
		}else {
			return false;	
		}
	}
	
	public static boolean isIpAddress(String ipAddress){
		/* get the occurence time of the dot character */
		int counter = 0;
		int fromIndex = 0;
		int index = 0;
		while((index=ipAddress.indexOf('.',fromIndex))!=-1){
			counter++;
			fromIndex = index + 1;
		}
		
		if(counter!=3)
			return false;
		StringTokenizer st = new StringTokenizer(ipAddress,".");
		if(st.countTokens()!=4)
			return false;
		while(st.hasMoreTokens()){
			String token = st.nextToken().trim();
			if(!isInRange(token,0,256))
				return false;
		}
		return true;
	}
	
	public static boolean isInteger(String value){
		try{
			Integer.parseInt(value);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isLong(String value){
		try{
			Long.parseLong(value);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isFloat(String value){
		try{
			Float.parseFloat(value);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isDouble(String value){
		try{
			Double.parseDouble(value);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isPositiveDouble(String value){
		try{
			if(isPositive(value) && isDouble(value)){
				return true;
			}else{
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isPositiveFloat(String value){
		try{
			if(isPositive(value) && isFloat(value)){
				return true;
			}else{
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isPositiveLong(String value){
		try{
			if(isPositive(value) && isLong(value)){
				return true;
			}else{
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isPositiveInteger(String value){
		try{
			if(isPositive(value) && isInteger(value)){
				return true;
			}else{
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
	}

	public static boolean isAtLeast(String value, double least){
		try {
			double doubleValue = Double.parseDouble(value);
			if (doubleValue>=least)
				return true;
			else
				return false;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean isInRange(String value, double start, double end){ 
		try {
			double doubleValue = Double.parseDouble(value);
			if (doubleValue>=start&&doubleValue<end) // start inclusive, end exclusive
				return true;
			else
				return false;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static boolean is2DCoordinate(String value){
		/* test if the value contain 1 and only 1 comma*/
		if(value.contains(",") && value.indexOf(",")==value.lastIndexOf(",")){
			StringTokenizer st = new StringTokenizer(value,",");
			if(st.countTokens()!=2)
				return false;
			while(st.hasMoreTokens()){
				String token = st.nextToken().trim();
				if(!isNotNegative(token)){ // if it's negative
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * get the int value round to the double value
	 * @param dSource
	 * @return
	 */
	public static int getRound(double dSource){
		int iRound;
		BigDecimal deSource = new BigDecimal(dSource);
		iRound= deSource.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
		return iRound;
	}
	
}
