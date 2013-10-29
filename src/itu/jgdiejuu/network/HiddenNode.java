package itu.jgdiejuu.network;

import java.util.ArrayList;


public class HiddenNode extends Node{

	protected ArrayList <Connection> ingoing = new ArrayList <Connection>();
	protected ArrayList <Connection> outgoing = new ArrayList <Connection>();
	
	public HiddenNode(ArrayList<Connection> ingoing, ArrayList<Connection> outgoing, int id, float biasValue) {
		super(ingoing, outgoing, id, biasValue);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Connection> getIngoing() {
		return ingoing;
	}
	public void setIngoing(ArrayList<Connection> ingoing) {
		this.ingoing = ingoing;
	}
	public ArrayList<Connection> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(ArrayList<Connection> outgoing) {
		this.outgoing = outgoing;
	}
	
	@Override
	public void activate() {
		error = 0;
		for(Connection c: outgoing){
			c.activate(value);
		}
	}

	public void computeError() {
		error = 0;
		float sum = 0f;
	//	System.out.println();
		for(Connection c : outgoing){
		//	System.out.print("  c.weight="+c.getWeight()+"   To.errror="+c.getTo().getError());
			sum += (c.getWeight() * c.getTo().getError());
		}
		error = value * (1 - value) * sum;
		
		//System.out.println("  compute error on node="+id+"  value="+value+"  error="+error+"  sum="+sum);
		//System.out.println();
	}
}
