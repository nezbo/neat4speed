package itu.jgdiejuu.network;

import java.util.ArrayList;
import java.util.Random;

public class NEATManager {

	
	Random r = new Random();
	ArrayList<NeuralNetwork> population = new ArrayList<NeuralNetwork>();
	
	//initial population parameters
	private static final int numberOfInputNodes = 3;
	private static final int numberOfOutputNodes = 2;
	private static final int populationSize = 100;
	private static int innovationNumber = numberOfInputNodes*numberOfOutputNodes;
	private static final int TOP_POPULATION = 5;
	
    //mutate rates
	public final float interspeciesMatingRate = 0.001f;
	public final float weightMutationRate = 0.8f;
	public final float newNodeMutationRate = 0.03f;
	public final float newConnectionMutationRate = 0.05f;
	
	//creates a fully connected network of minimal topology-structure (no hidden nodes)
	public void createInitialPopulation(int populationSize, int inputNodes, int outputNodes){
		for(int i = 0; i < populationSize; i++){
			NeuralNetwork g = new NeuralNetwork(2, 1);
			population.add(g);
		}
	}
	
	//mutates the weight of a ConnectionGene
	public void weightMutation(NeuralNetwork nn){
		nn.weightMutation(innovationNumber);
		innovationNumber++;
	}
	
	//structural mutation - add a connection between two nodes
	public void addConnectionMutation(NeuralNetwork nn){
		nn.addConnectionMutation(innovationNumber);
		innovationNumber++;
	}
	

	//structural mutation - add a node
	public void addNodeMutation(NeuralNetwork nn){
		nn.addNodeMutation(innovationNumber);
		innovationNumber+=2;
	}
	
	//returns random weight between -1 and 1
	public double getRandomWeight(){
		return -1 + (r.nextDouble()+r.nextDouble());
	}
	
	//returns random bias between -1 and 1
	public double getRandomBias(){
		return -1 + (r.nextDouble()+r.nextDouble());
	}
	
	public void evaluateGeneration(){
		
	}
	
	public void produceNextGeneration(){
		
	}
	
	//launchpoint
	public static void main(String[] args){
		NEATManager neat = new NEATManager();
		neat.createInitialPopulation(populationSize, numberOfInputNodes, numberOfOutputNodes);
	}
}
