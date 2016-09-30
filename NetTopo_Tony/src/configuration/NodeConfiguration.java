package configuration;

import org.deri.nettopo.node.VNode;
import org.deri.nettopo.topology.Topology;

public class NodeConfiguration {
	private VNode node;
	private Topology nodeTopology;
	public VNode getNode() {
		return node;
	}
	public void setNode(VNode node) {
		this.node = node;
	}
	public Topology getNodeTopology() {
		return nodeTopology;
	}
	public void setNodeTopology(Topology nodeTopology) {
		this.nodeTopology = nodeTopology;
	}

}
