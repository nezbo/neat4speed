package itu.jgdiejuu.network;

import java.util.ArrayList;
import java.util.Random;

public class NEATManager {

	
	Random r = new Random();
	ArrayList<NeuralNetwork> population = new ArrayList<NeuralNetwork>();
	public static int innovationNumber = 1;
	
	//creates a fully connected network of minimal topology-structure (no hidden nodes)
	public void createInitialPopulation(int populationSize, int inputNodes, int outputNodes){
		for(int i = 0; i < populationSize; i++){
			NeuralNetwork g = new NeuralNetwork(2, 1);
			population.add(g);
		}
	}
	
	//mutates the weight of a ConnectionGene
	public void weightMutation(Connection c){
		
	}
	
	//structural mutation - add a connection between two nodes
	public void addConnection(Node parent1, Node parent2){
			
	}
	
	//RANDOM 
	//structural mutation - add a node
	public void addNode(NeuralNetwork nn){
		
	}
	
	//returns random weight between -1 and 1
	public double getRandomWeight(){
		return -1 + (r.nextDouble()+r.nextDouble());
	}
	
	//returns random bias between -1 and 1
	public double getRandomBias(){
		return -1 + (r.nextDouble()+r.nextDouble());
	}
	
}
