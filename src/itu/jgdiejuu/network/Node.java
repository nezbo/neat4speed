package itu.jgdiejuu.network;

import java.util.ArrayList;


public abstract class Node {

	
	protected float value = 0;
	protected float biasValue;
	
	public float getBiasValue() {
		return biasValue;
	}

	public void setBiasValue(float biasValue) {
		this.biasValue = biasValue;
	}

	protected int id;
	protected float error;

	protected boolean normalized = false;
			
	public Node(ArrayList<Connection> ingoing, ArrayList<Connection> outgoing, int id, float biasValue) {
		super();
		this.value = value;
		this.id = id;
		this.biasValue = biasValue;
	}
	
	public abstract void activate();
	public abstract ArrayList<Connection> getIngoing();
	public abstract ArrayList<Connection> getOutgoing();
	
	public void addBiasValue(){
		value += biasValue;
	}
	
	public boolean isNormalized() {
		return normalized;
	}

	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * Uses Sigmoid function to normalize.
	 */
	public void normalize(String method){
//		System.out.println(method+"  answer="+1f / ( 1f +  Math.pow(Math.E, (-value))));
		switch(method){
		case "sigmoid": value = (float) (1f / ( 1f +  Math.pow(Math.E, (-value)))); break;
		case "tanh": value = (float) Math.tanh(value); break;
		default: throw new RuntimeException("Legal normalize method not given");
		}
		
	}
	
	public void updateBias(float learningRate){
//		System.out.println("update bias on node="+id+"  error="+error+"  learningrate="+learningRate);
		biasValue = biasValue + (error * learningRate);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public float getError() {
		return error;
	}

	public void setError(float error) {
		this.error = error;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void reset() {
		value = 0;
		error = 0;
		
	}
}
