package configuration;

import java.util.HashMap;



import javax.xml.bind.annotation.XmlRootElement;

import org.deri.nettopo.node.tpgf.SensorNode_TPGF;



@XmlRootElement  
public class TPGFConfiguration {
	
	
	
	private NetWorkConfiguration netConf;
	private HashMap<String, SensorNode_TPGF> nodes;
	private AlgorithmConfiguration aConf;
	
	
	
	
	public HashMap<String, SensorNode_TPGF> getNodes() {
		return nodes;
	}
	public void setNodes(HashMap<String, SensorNode_TPGF> nodes) {
		this.nodes = nodes;
	}
	public NetWorkConfiguration getNetConf() {
		return netConf;
	}
	public void setNetConf(NetWorkConfiguration netConf) {
		this.netConf = netConf;
	}

	public AlgorithmConfiguration getaConf() {
		return aConf;
	}
	public void setaConf(AlgorithmConfiguration aConf) {
		this.aConf = aConf;
	}
}
