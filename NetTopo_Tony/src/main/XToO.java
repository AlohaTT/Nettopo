package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.deri.nettopo.node.SinkNode;
import org.deri.nettopo.node.tpgf.SensorNode_TPGF;
import org.deri.nettopo.topology.simpletopo.Topo_Random_Time;

import configuration.AlgorithmConfiguration;
import configuration.NetWorkConfiguration;
import configuration.TPGFConfiguration;

public class XToO {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TPGFConfiguration TPGFConf = new TPGFConfiguration(); 
		
		
		NetWorkConfiguration nc=new NetWorkConfiguration();
		nc.setX(100);
		nc.setY(100);
		nc.setZ(0);
		
		System.out.println("nc");
		
		HashMap<String, SensorNode_TPGF> nodeList=new HashMap<String, SensorNode_TPGF>();
		
		/***************传感器节点**************************/
		SensorNode_TPGF sensor=new SensorNode_TPGF();
		sensor.setAttrValue("Energy", 1+"");
		sensor.setAttrValue("Max TR", 50+"");
		sensor.setAttrValue("Bandwidth", 1+"");
		sensor.setAttrValue("Expected Life Time", 1+"");
		sensor.setAttrValue("Minimum Rate", 1+"");
		Topo_Random_Time nodeTopology=new Topo_Random_Time();
		nodeTopology.setArgValue("Node numbers", 100+"");
		/*****************sinkNode**********************/
		SinkNode sink=new SinkNode();
		sink.setAttrValue("Max TR", 50+"");
		sink.setAttrValue("Bandwidth", 50+"");
		Topo_Random_Time sinkTopology=new Topo_Random_Time();
		sinkTopology.setArgValue("Node numbers", 1+"");
		
		
		System.out.println("nodeList");
		
		AlgorithmConfiguration ac=new AlgorithmConfiguration();
		ac.setAlgorithmName("org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath");
		
		TPGFConf.setaConf(ac);
		TPGFConf.setNetConf(nc);
		TPGFConf.setNodes(nodeList);
		
		
		System.out.println("TPGFConf");
		
		
        		File file=new File("./TPGFConf.xml");
        try {  
            JAXBContext context = JAXBContext.newInstance(TPGFConfiguration.class);  
            Marshaller marshaller = context.createMarshaller(); 
            
            marshaller.marshal(TPGFConf,new FileOutputStream(file));
            
        } catch (JAXBException e) {  
            e.printStackTrace();  
            
        }  
        System.out.println("");
        System.out.println("OK");
	}

}
