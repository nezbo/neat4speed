package itu.jgdiejuu.network;

import java.util.ArrayList;


public class OutputNode extends Node{

	private ArrayList <Connection> ingoing = new ArrayList <Connection>();
	
	public OutputNode(ArrayList<Connection> ingoing, ArrayList<Connection> outgoing, int id, float biasValue) {
		super(ingoing, outgoing, id, biasValue);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Connection> getIngoing() {
		return ingoing;
	}
	public void setIngoing(ArrayList<Connection> ingoing) {
		this.ingoing = ingoing;
	}

	@Override
	public void activate() {
		error = 0;
	}
	
	public void computeError(float goalValue){
		
		error = value * (1.0f - value) * (goalValue - value);
		
/*		System.out.print("compute error on node="+id+"  value="+value+"  goalValue="+goalValue+"  error="+error+"  ");
		for(Connection c: ingoing){
			System.out.print("  weight="+c.getWeight());
		}
		System.out.println();
*/	}

	public void roundValue(){
		value = Math.round(value);
	}

	@Override
	public ArrayList<Connection> getOutgoing() {
		return null;
	}
}
