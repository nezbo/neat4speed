package itu.jgdiejuu.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import champ2010client.ClientProgram;
import champ2010client.NEATforSPEED;
import champ2010client.SimpleDriver;

public class NEATManager {
	
	public static final String TORCS_DIR = "C:\\Program Files (x86)\\torcs";
	
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
	
	public int evaluate(NeuralNetwork nn){
		try {
			System.out.println(">> Starting Evaluating Neural-Network.");
			// start game
	        ProcessBuilder builder = new ProcessBuilder(
	                "cmd.exe", "/c", "cd \""+TORCS_DIR+"\" && wtorcs.exe -T");
	        builder.redirectErrorStream(true);
	        Process pr = builder.start();
	        
	        System.out.println(">> Torcs Started.");
			
			// wait for output
			final BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			final ArrayList<String> output = new ArrayList<String>();
			
			System.out.println(">> InputStream fetched.");
			
			new Thread(new Runnable(){ 
				String line = null;
				@Override
				public void run() {
	            try {
					while ((line = reader.readLine()) != null) {
					    output.add(line);
					    System.out.println(line);
					}
				} catch (IOException e) {}
				}}).start();
			
			System.out.println(">> Thread started for collecting output.");
            
			// Run simulation
			ClientProgram.main(new String[]{"-"}, new SimpleDriver());
			
			System.out.println(">> Client started.");
            
            // fetch score and return
			// TODO: Write
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void main(String[] args){
		NEATManager manager = new NEATManager();
		manager.evaluate(new NeuralNetwork(5,5,5,5));
	}
	
}
