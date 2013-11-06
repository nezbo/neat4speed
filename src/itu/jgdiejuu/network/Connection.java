package itu.jgdiejuu.network;

public class Connection {
	
	private Node fromNode, toNode;
	private double weight;
	private boolean active;

	public Connection(Node fromNode, Node toNode, double weight, boolean active){
		this.weight = weight;
		this.fromNode = fromNode;
		this.toNode = toNode;
		
		this.setActive(active);
	}
	
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
	
	public boolean isActive(){
		return active;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void changeWeight(double amount){
		weight += amount;
	}
	
	public Node getFromNode(){
		return fromNode;
	}
	
	public Node getToNode(){
		return toNode;
	}

	public void updateWeight() {
		double delta = Node.L*toNode.getError()*fromNode.getOutput();
		this.changeWeight(delta);
	}
}
