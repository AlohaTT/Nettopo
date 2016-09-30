package impl;

import java.util.Comparator;

import bean.Information;

public class SlavesComparator implements Comparator<Information>{
    @Override
    public int compare(Information o1, Information o2) {	//½µÐòÅÅÁÐ
        if(o1.capacity>o2.capacity)
        	return -1;
        else if(o1.capacity<o2.capacity)
        	return 1;
        else return 0;
    }
    
}
