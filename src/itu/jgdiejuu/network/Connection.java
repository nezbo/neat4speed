package itu.jgdiejuu.network;

/**
 * A Connection between two Nodes in the Neural-Network. It automatically connects
 * itself to the two Nodes when it is active. The Connection contains a weight telling
 * how string it is when used in the NN.
 * @author Emil
 *
 */
public class Connection {
	
	private Node fromNode, toNode;
	private double weight;
	private boolean active;
	private int innovationNumber;

	/**
	 * Creates a Connection with the complete information given.
	 * @param fromNode The Node this Connection originates from.
	 * @param toNode The Node this Connection targets.
	 * @param weight The weight of this Connection.
	 * @param active True if this Connection is active, and thus connecting the two Nodes.
	 * @param innovationNumber A unique identifier for use in the NEAT algorithm.
	 */
	public Connection(Node fromNode, Node toNode, double weight, boolean active, int innovationNumber){
		this.weight = weight;
		this.fromNode = fromNode;
		this.toNode = toNode;
		
		this.setActive(active);
		this.innovationNumber = innovationNumber;
	}
	
	/**
	 * Connects or disconnects this connection from its two end Nodes.
	 * @param activate True if this connection should be activated, else false.
	 */
	public void setActive(boolean activate){
		active = activate;
		if(activate){
			// adding to nodes
			fromNode.conn_out.add(this);
			toNode.conn_in.add(this);
		}else{
			fromNode.conn_out.remove(this);
			toNode.conn_in.remove(this);
		}
	}
	
	/**
	 * Tells if this connection is currently connecting its two Nodes.
	 * @return
	 */
	public boolean isActive(){
		return active;
	}
	
	/**
	 * Gets the current weight of this Connection.
	 * @return
	 */
	public double getWeight(){
		return weight;
	}
	
	/**
	 * Sets the current weight of this connection.
	 * @param newWeight
	 */
	public void setWeight(double newWeight){
		weight = newWeight;
	}
	
	/**
	 * Changes the current weight of this connection by the specified amount.
	 * @param amount
	 */
	public void changeWeight(double amount){
		weight += amount;
	}
	
	/**
	 * Gets the Node this Connection starts at.
	 * @return
	 */
	public Node getFromNode(){
		return fromNode;
	}
	
	/**
	 * Gets the Node this Connection ends at.
	 * @return
	 */
	public Node getToNode(){
		return toNode;
	}

	/**
	 * Updates the weights according to the error. For use in backpropagation.
	 */
	public void updateWeight() {
		double delta = Node.L*toNode.getError()*fromNode.getOutput();
		this.changeWeight(delta);
	}
	
	/**
	 * Gets the innovation number of this Connection.
	 * This value never changes.
	 * @return
	 */
	public int getInnovationNumber(){
		return innovationNumber;
	}
}
