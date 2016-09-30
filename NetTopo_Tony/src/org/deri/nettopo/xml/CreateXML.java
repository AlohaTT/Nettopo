package org.deri.nettopo.xml;


import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * dom4j框架学习 使用dom4j框架创建xml文档并输出保存
 * 
 */
public class CreateXML
{

    public static void main(String[] args) throws Exception
    {
        // 第一种方式：创建文档，并创建根元素
        // 创建文档:使用了一个Helper类
       // Document document = DocumentHelper.createDocument();

        // 创建根节点并添加进文档
       // Element root = DocumentHelper.createElement("Configuration");
       // document.setRootElement(root);

        // 第二种方式:创建文档并设置文档的根元素节点
        Element root = DocumentHelper.createElement("Configuration");
        Document document2 = DocumentHelper.createDocument(root);

        // 添加属性
       // root2.addAttribute("WSN", "zhangsan");
        // 添加子节点:add之后就返回这个元素
        Element wsn = root.addElement("WirelessSensorNetwork");
        	Element width=wsn.addElement("Length");
        	width.setText("500");
        	Element length=wsn.addElement("Width");
        	length.setText("400");
        	Element hight=wsn.addElement("Hight");
        	hight.setText("0");
        Element node = root.addElement("Node");
        node.addAttribute("name", "SensorNode");
        node.addAttribute("class", "org.deri.nettopo.node.tpgf.SensorNode_TPGF");
        	Element p1=node.addElement("Property");
        		Element maxTR=p1.addElement("Max_TR");
        		maxTR.setText("50");
        		Element livingTime=p1.addElement("Expected_Life_Time");
        		livingTime.setText("1");
        		Element Minimum_Rate=p1.addElement("Minimum_Rate");
        		Minimum_Rate.setText("1");
        		Element energy=p1.addElement("Energy");
        		energy.setText("1");
        		Element bandwidth=p1.addElement("Bandwidth");
        		bandwidth.setText("1");
        	Element sensorTopology=node.addElement("Topology");
        	sensorTopology.addAttribute("name", "random time seed");
        	sensorTopology.addAttribute("class", "org.deri.nettopo.topology.simpletopo.Topo_Random_Time");
        		Element nodeNumber = sensorTopology.addElement("Node_numbers");
        		nodeNumber.setText("500");
         		
        		
        		 Element sinkNode = root.addElement("Node");
        		 sinkNode.addAttribute("name", "SinkNode");
        		 sinkNode.addAttribute("class", "org.deri.nettopo.node.SinkNode");
        	        	Element p12=sinkNode.addElement("Property");
        	        		Element maxTR2=p12.addElement("Max_TR");
        	        		maxTR2.setText("50");
        	        		Element sinkBandwidth=p12.addElement("Bandwidth");
        	        		sinkBandwidth.setText("1");
        	        	Element sinkTopology=sinkNode.addElement("Topology");
        	        	sinkTopology.addAttribute("name", "random time seed");
        	        	sinkTopology.addAttribute("class", "org.deri.nettopo.topology.simpletopo.Topo_Random_Time");
        	        		Element nodeNumber2 = sinkTopology.addElement("Node_numbers");
        	        		nodeNumber2.setText("1");
         		
        	        		 Element sourceNode = root.addElement("Node");
        	        		 sourceNode.addAttribute("name", "SourceNode");
        	        		 sourceNode.addAttribute("class", "org.deri.nettopo.node.tpgf.SourceNode_TPGF");
        	        	        	
        	        		 Element ps=sourceNode.addElement("Property");
        	        	        	Element maxTRSource=ps.addElement("Max_TR");
        	        	        	maxTRSource.setText("50");
        	                		Element livingTimeSource=ps.addElement("Expected_Life_Time");
        	                		livingTimeSource.setText("1");
        	                		Element Minimum_RateSource=ps.addElement("Minimum_Rate");
        	                		Minimum_RateSource.setText("1");
        	                		Element energySource=ps.addElement("Energy");
        	                		energySource.setText("1");
        	                		Element bandwidthSource=ps.addElement("Bandwidth");
        	                		bandwidthSource.setText("1");
        	                		
        	        	        	Element sourceTopology=sourceNode.addElement("Topology");
        	        	        	sourceTopology.addAttribute("name", "random time seed");
        	        	        	sourceTopology.addAttribute("class", "org.deri.nettopo.topology.simpletopo.Topo_Random_Time");
        	        	        		Element nodeNumbers = sourceTopology.addElement("Node_numbers");
        	        	        		nodeNumbers.setText("1");
        	        		
       
        	    Element algorithm = root.addElement("Algorithm");
        	    algorithm.addAttribute("name", "find a path");
        	    algorithm.addAttribute("class", "org.deri.nettopo.algorithm.tpgf.function.TPGF_FindOnePath");
        	     System.out.println("XML has been created!");   		
        	        		// 输出
        // 输出到控制台
//        XMLWriter xmlWriter = new XMLWriter();
//        xmlWriter.write(document);

        // 输出到文件
        // 格式
        OutputFormat format = new OutputFormat("    ", true);// 设置缩进为4个空格，并且另起一行为true
//        XMLWriter xmlWriter2 = new XMLWriter(
//                new FileOutputStream("student.xml"), format);
//        xmlWriter2.write(document2);

        // 另一种输出方式，记得要调用flush()方法,否则输出的文件中显示空白
        XMLWriter xmlWriter = new XMLWriter(new FileWriter("./xml/task.xml"),
                format);
        xmlWriter.write(document2);
        xmlWriter.flush();
        // close()方法也可以

    }
}