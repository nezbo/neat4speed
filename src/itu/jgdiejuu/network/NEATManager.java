package itu.jgdiejuu.network;

import java.util.ArrayList;
import java.util.Iterator;
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
	

	//structural mutation - add a node
	public void addNode(NeuralNetwork nn){
		
	}
	
	public double getCompatibilityDistance(NeuralNetwork nn1, NeuralNetwork nn2){
		
		//constants and avgW
		float C1 = 1;
		float C2 = 1;
		float C3 = 1;
		double avgWD = 0;
		int N = 1;
		int disjoints = 0;
		int excess = 0;
		
		//get connections
		ArrayList<Connection> nn1ConGenes = nn1.getConnections();
		ArrayList<Connection> nn2ConGenes = nn2.getConnections();
		
		//avgW for nn1
		int avgW1 = 0;
		for(Connection c: nn1ConGenes){
			avgW1 += c.getWeight();
		}
		avgW1 /= nn1ConGenes.size();
		
		//avgW for nn2
		int avgW2 = 0;
		for(Connection c: nn2ConGenes){
			avgW2 += c.getWeight();
		}
		avgW2 /= nn2ConGenes.size();
		
		//average weight distance
		avgWD = Math.abs(avgW1-avgW2);
				
		int max = Math.max(nn1ConGenes.size(), nn2ConGenes.size());
		
		//if both genomes are smaller than 20 N=1 else N=max
		if(max > 20){
			N = max;
		}
		
		//get number of excess
		int maxIN = Math.max(nn1ConGenes.indexOf(nn1ConGenes.size()-1).getInnovationNumber(), 
								nn2ConGenes.indexOf(nn2ConGenes.size()-1).getInnovationNumber);
		int minIN = Math.min(nn1ConGenes.indexOf(nn1ConGenes.size()-1).getInnovationNumber(), 
				nn2ConGenes.indexOf(nn2ConGenes.size()-1).getInnovationNumber);
		
		excess = maxIN - minIN;
		
		//get number of disjoints
		int[] cArray1 = getCompatibilityArray(nn1ConGenes);
		int[] cArray2 = getCompatibilityArray(nn2ConGenes);
		
		for(int i = minIN-1; i <= 0; i++){
			if(cArray1[i] != cArray2[i]){
				disjoints++;
			}
		}
		
		double CompatibilityDistance = (C1*excess)+(C2*disjoints)+(C2*avgWD);
	}
	
	public int[] getCompatibilityArray(ArrayList<Connection> nn){
		
		int iNumber = nn.get(nn.size()-1);
		int[] compatibilityArray = new int[iNumber];
		for(int i = 0; i < iNumber; i++){
			compatibilityArray[i] = -1;
		}
		for(Connection c: nn){
			compatibilityArray[c.getInnovationNumber;] = c.getInnovationNumber;;
		}
		return compatibilityArray;
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
