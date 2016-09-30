/**
 * 
 */
package org.deri.nettopo.node.sdn;

/**
 * @author Tony
 *
 */
public class PacketHeader {

	/**
	 * 
	 */
	private int source; // 源节点
	private int destination; // 目标节点
	private int type;// type为0表示这是control message,为1表示这是update
						// message,为2表示这是data message
	private int flag;// flag为0表示把message送到1-hop
						// neighbor,flag为1表示把message送到controller
	private int state;// state为0则为睡眠,为1则为醒
	private int behavior;// behavior为0则为request,为1则为action

	/**
	 * 
	 */
	public PacketHeader() {
		source = 0;
		destination = 0;
		type = 0;
		flag = 0;
		state = 0;
		behavior = 0;
	}

	/**
	 * @return the behavior
	 */
	public int getBehavior() {
		return behavior;
	}

	/**
	 * @param behavior
	 *            the behavior to set
	 */
	public void setBehavior(int behavior) {
		this.behavior = behavior;
	}

	private int nextHopId;

	/**
	 * @return the source
	 */
	public int getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(int source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public int getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(int destination) {
		this.destination = destination;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the flag
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return behavior;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.behavior = priority;
	}

	/**
	 * @return the nextHopId
	 */
	public int getNextHopId() {
		return nextHopId;
	}

	/**
	 * @param nextHopId
	 *            the nextHopId to set
	 */
	public void setNextHopId(int nextHopId) {
		this.nextHopId = nextHopId;
	}

}
