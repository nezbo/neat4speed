package itu.jgdiejuu.network;

import java.util.ArrayList;


public class InputNode extends Node{

	protected ArrayList <Connection> outgoing = new ArrayList <Connection>();
	
	public InputNode(ArrayList<Connection> ingoing, ArrayList<Connection> outgoing, int id) {
		super(ingoing, outgoing, id, 0);
		// TODO Auto-generated constructor stub
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

	@Override
	public ArrayList<Connection> getIngoing() {
		return null;
	}
}
