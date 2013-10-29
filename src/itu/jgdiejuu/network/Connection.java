package itu.jgdiejuu.network;


public class Connection {

	private NeuralNetwork nn;
	private Node from;
	private Node to;
	private float weight;
	
	public Connection(int fromid, int toid, float weight, NeuralNetwork nn){
		
		this.nn = nn;
		for(Node n : nn.getNodes()){
			if(n.getId() == fromid){
				this.from = n;
			}
			if(n.getId() == toid){
				this.to = n;
			}
		}
		this.weight = weight;
	}
	
	public Node getFrom() {
		return from;
	}
	public void setFrom(Node from) {
		this.from = from;
	}
	public Node getTo() {
		return to;
	}
	public void setTo(Node to) {
		this.to = to;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public void activate(float nodeValue){
		float toNextValue = (nodeValue * weight) + to.getValue();
		to.setValue(toNextValue);	
	}
	
	public void adjustWeight(float learningrate){
		
		float weightAdjustment = learningrate * to.getError() * from.getValue();
	//	System.out.println("weightAdjustment = "+weightAdjustment);
		weight = weight + weightAdjustment; //learningrate not needed?
	}
}
