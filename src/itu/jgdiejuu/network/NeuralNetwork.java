package itu.jgdiejuu.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class NeuralNetwork {

	public static int RESTARTS = 0;
	public static int LIMIT = 10000; //Integer.MAX_VALUE;
	public static boolean DEBUG = false;
	
	public static void main(String[] args){
		
		// create training pairs XOR
		double[][] trainingInputs = new double[][]{new double[]{0,0},new double[]{1,0},new double[]{0,1},new double[]{1,1}};
		double[][] trainingOutputs = new double[][]{new double[]{0},new double[]{1},new double[]{1},new double[]{0}};
		
		int restarts = 0;
		
		while(restarts <= RESTARTS){ // input,output,hidden,levels,outputValues
			NeuralNetwork nn = new NeuralNetwork(2,1,2,1);

			int i = 0;
			boolean allCorrect = false;
			
			while(!allCorrect && i < LIMIT){ // stopping condition
				System.out.println("----- Running Training "+i+" -----");
				allCorrect = nn.runTraining( trainingInputs
											,trainingOutputs) == trainingInputs.length;
				i++;
			}
			
			if(allCorrect){
				System.out.println("Complete after "+i+" runs.");
				System.out.println(nn.toString());
				break;
			}else{
				restarts++;
				if(restarts <= RESTARTS){
					System.out.println("Restart #"+restarts);
				}else{
					System.out.println("Max restarts exceeded.");
				}
				
			}
		}
	}
	
	// -------------------------------------
	
	private ArrayList<Node> inputNodes, outputNodes;
	private ArrayList<ArrayList<Node>> hiddenLayers;
	private ArrayList<Connection> connections;
	
	public NeuralNetwork(int numInput, int numOutput, int numHidden, int numLayers){
		Random rand = new Random();
		
		// create tree
		inputNodes = new ArrayList<Node>();
		hiddenLayers = new ArrayList<ArrayList<Node>>(numLayers);
		outputNodes = new ArrayList<Node>();
		connections = new ArrayList<Connection>();
		
		for(int i = 0; i < numInput; i++) inputNodes.add(new Node(0.0));
		for(int i = 0; i < numOutput; i++) outputNodes.add(new Node(randStart(rand)));
		for(int i = 0; i < numLayers; i++){
			hiddenLayers.add(new ArrayList<Node>(numHidden));
			for(int j = 0; j < numHidden; j++){
				Node curNode = new Node(randStart(rand));
				hiddenLayers.get(i).add(curNode);
				// hook up to previous
				if(i == 0){ // first layer to input
					for(int k = 0; k < numInput; k++){
						connections.add(new Connection(inputNodes.get(k), curNode, randStart(rand)));
					}
				}else{ // rest to layer before
					for(int k = 0; k < numHidden; k++){
						connections.add(new Connection(hiddenLayers.get(i-1).get(k), curNode, randStart(rand)));
					}
				}
				// to output
				if(i == numLayers-1){
					for(int k = 0; k < numOutput; k++){
						connections.add(new Connection(curNode, outputNodes.get(k), randStart(rand)));
					}
				}
			}
		}
	}
	
	public NeuralNetwork(String fileName){
		Random rand = new Random();		
		System.out.println("LOAD");
		
		try {		
			Scanner scan = new Scanner(new File("networks/"+fileName));
			String topology = scan.nextLine();
			Scanner topologyScan = new Scanner(topology);
			
			hiddenLayers = new ArrayList<ArrayList<Node>>();
			while(topologyScan.hasNext()){
				String next = topologyScan.next();
				String[] biases = next.substring(1,next.length()-2).split(",");
				
				ArrayList<Node> curLayer = new ArrayList<Node>();
				
				for(String s : biases){
					if(isDouble(s)){
						curLayer.add(new Node(Double.parseDouble(s)));
					}else{
						curLayer.add(new Node(randStart(rand)));
					}
					
				}
				
				if(inputNodes == null){
					inputNodes = curLayer;
				}else{
					hiddenLayers.add(curLayer);
				}
			}
			// move last layer to output
			outputNodes = hiddenLayers.remove(hiddenLayers.size()-1);

			//for all connections
			while(scan.hasNextLine()){

				String con = scan.nextLine();
				Scanner conScan = new Scanner(con);
				
				int from = conScan.nextInt();
				int to = conScan.nextInt();
				String w = conScan.next();
				double weight = 0;
				
				if(isDouble(w)){
					weight = Double.parseDouble(w);
				}else if(w.equals("r")){
					weight = randStart(rand);
				}
				
				
				connections.add(new Connection(getNode(from), getNode(to), weight));
				
				conScan.close();
			}
			
			scan.close();
			topologyScan.close();
					
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		System.out.println(this.toString());
	}
	
	private Node getNode(int index){
		index--;
		if(index < inputNodes.size()) return inputNodes.get(index);
		index -= inputNodes.size();
		
		for(ArrayList<Node> layer : hiddenLayers){
			if(index < layer.size()) return layer.get(index);
			index -= layer.size();
		}
		
		return outputNodes.get(index);
	}
	
	private int getIndex(Node n){
		int index = 0;
		for(int i = 0; i < inputNodes.size(); i++) if(inputNodes.get(i) == n) return index+i;
		index += inputNodes.size();
		for(int i = 0; i < hiddenLayers.size(); i++){
			for(int j = 0; j < hiddenLayers.get(i).size(); j++){
				if(hiddenLayers.get(i).get(j) == n) return index+i;
			}
			index += hiddenLayers.get(i).size();
		}
		for(int i = 0; i < outputNodes.size(); i++) if(outputNodes.get(i) == n) return index+i;
		return -1;
	}
	
	public void saveNetwork(){
		
	int numberOfFiles = new File("networks").list().length;
	System.out.println("numberOfFiles = "+numberOfFiles);
	String name = "savedNetwork"+numberOfFiles;
	
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("networks/"+name+".txt"));
			
				String output = inputNodes.size()+"";
				
				for(int i = 0; i < hiddenLayers.size(); i++){
					output+=" "+hiddenLayers.get(i).size()+" [ ";
					for(int j = 0; j < hiddenLayers.get(i).size(); j++){
						output+=hiddenLayers.get(i).get(j).getBias()+" ";
					}
					output+="]";
				}
			
				output += " "+outputNodes.size()+" [ ";
				for(int i = 0; i < outputNodes.size(); i++){
					output+=outputNodes.get(i).getBias()+" ";
				}
				output += " ]";
				
				for(Connection c: connections){
					output+="\n"+getIndex(c.getFromNode())+" "+getIndex(c.getToNode())+" "+c.getWeight();
				}
				writer.write(output);
						
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		
		
	}
	
	private boolean isDouble( String input ) {
	    try {
	        Double.parseDouble( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
	
	private double randStart(Random rand){
		return (rand.nextDouble()*2.0)-1.0;
	}
	
	public double[] getOutput(double[] parseInput) {
		double[] result = new double[outputNodes.size()];
		for(int i = 0; i < result.length; i++){
			result[i] = outputNodes.get(i).forwardOperation();
		}
		return result;
	}
	
	public boolean runTraining(double[] trainingInput, double[] trainingOutput){
		// set inputs
		for(int j = 0; j < inputNodes.size(); j++){
			inputNodes.get(j).setInput(trainingInput[j]);
		}
		
		if(DEBUG) System.out.println("Output=");
		boolean match = true;
		for(int j = 0; j < outputNodes.size(); j++){
			// forward operation
			double result = outputNodes.get(j).forwardOperation();
			int round = (int)Math.round(result);
			int target = (int)Math.round(trainingOutput[j]);
			
			if(DEBUG) System.out.println("\t"+j+": "+result+" - Target= "+trainingOutput[j]);
			if(DEBUG) System.out.println("\t"+j+"(int): "+round+" - Target(int)= "+target);
			
			match = match && (target == round);
		}
	
		if(DEBUG) System.out.println("Result Match: " + match);
		
		// output error
		for(int j = 0; j < outputNodes.size(); j++){
			double T = trainingOutput[j];
			outputNodes.get(j).computeError(T);
		}
		for(int j = hiddenLayers.size()-1; j >= 0; j--){
			for(Node n : hiddenLayers.get(j)) n.computeError(0);
		}
		
		// update weights
		for(Connection c : connections) c.updateWeight();
		
		// update bias
		for(int j = 0; j < hiddenLayers.size(); j++){
			for(Node n : hiddenLayers.get(j)) n.updateBias();
		}
		for(Node n : outputNodes) n.updateBias();
		
		if(DEBUG) System.out.println(this);
		
		return match;
	}

	public int runTraining(double[][] trainingInput, double[][] trainingOutput) {
		int numCorrect = 0;
		for(int i = 0; i < trainingInput.length; i++){
			boolean match = runTraining(trainingInput[i], trainingOutput[i]);
			if(match) numCorrect++;
		}
		if(DEBUG) System.out.println(numCorrect+" out of "+trainingInput.length+" correct.");
		return numCorrect;
	}
	
	private void nodeToBuilder(Node n, StringBuilder builder){
		builder.append(n+": bias="+n.getBias()+"\n");
		for(Connection c : n.conn_out){
			builder.append("\t-> "+c.getToNode()+" = "+c.getWeight()+"\n");
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Input Nodes:\n");
		for(Node n : inputNodes) nodeToBuilder(n,builder);
		
		for(int i = 0; i < hiddenLayers.size(); i++){
			builder.append("\nLayer "+(i+1)+":\n");
			for(Node n : hiddenLayers.get(i)) nodeToBuilder(n,builder);
		}

		builder.append("\nOutput Nodes:\n");
		for(Node n : outputNodes) nodeToBuilder(n,builder);
		builder.append("\n");
		return builder.toString();
	}
}
