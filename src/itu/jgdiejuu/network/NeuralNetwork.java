package itu.jgdiejuu.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NeuralNetwork {
	
    public static int RESTARTS = 10;
    public static int LIMIT = 10000; //Integer.MAX_VALUE;
    public static boolean DEBUG = false;
	
	public static void main(String[] args){
		
		// create training pairs XOR
		double[][] trainingInputs = new double[][]{new double[]{0,0},new double[]{1,0},new double[]{0,1},new double[]{1,1}};
		double[][] trainingOutputs = new double[][]{new double[]{0},new double[]{1},new double[]{1},new double[]{0}};
		
		int restarts = 0;
		NeuralNetwork nn = new NeuralNetwork("structureTest.txt");
		return;
		
		/*while(restarts <= RESTARTS){ // input,output,hidden,levels,outputValues
			NeuralNetwork nn = new NeuralNetwork("structureTest.txt");
			//NeuralNetwork nn = new NeuralNetwork(2,1,2,1);

			int i = 0;
			boolean allCorrect = nn.isFit(trainingInputs, trainingOutputs);
			
			while(!allCorrect && i < LIMIT){ // stopping condition
				System.out.println("----- Running Training "+i+" -----");
				nn.runTraining( trainingInputs,trainingOutputs);
				allCorrect = nn.isFit(trainingInputs, trainingOutputs);
				i++;
			}
			
			if(allCorrect){
				System.out.println("\nComplete after "+i+" runs and "+restarts+" restart"+(restarts == 1 ? "." : "s.")+"\n");
				System.out.println(nn.toString());
				nn.saveNetwork();
				break;
			}else{
				restarts++;
				if(restarts <= RESTARTS){
					System.out.println("Restart #"+restarts);
				}else{
					System.out.println("Max restarts exceeded.");
				}
				
			}
		}*/
	}
	
	// -------------------------------------
	
	private static double MARGIN = 0.05;
	
	private ArrayList<Node> nodes;
	private List<Node> inputNodes;
	private List<Node> outputNodes;
	private ArrayList<Connection> connections;
	
	// for backprop
	private ArrayList<ArrayList<Node>> layerz;
	private int lastNumConn = 0;
	
	public NeuralNetwork(int numInput, int numOutput, int... hiddenLayers){
		connections = new ArrayList<Connection>();
		Random rand = new Random();
		
		// create tree
		nodes = new ArrayList<Node>();
		
		for(int i = 0; i < numInput; i++) nodes.add(new Node(0.0, NodeType.INPUT));
		inputNodes = nodes.subList(0, nodes.size());
		for(int i = 0; i < numOutput; i++) nodes.add(new Node(randStart(rand), NodeType.OUTPUT));
		outputNodes = nodes.subList(numInput, nodes.size());
		int index = numInput + numOutput;
		
		for(int i = 0; i < hiddenLayers.length; i++){
			for(int j = 0; j < hiddenLayers[i]; j++){
				Node curNode = new Node(randStart(rand), NodeType.HIDDEN);
				nodes.add(curNode);
				// hook up to previous
				if(i == 0){ // first layer to input
					for(int k = 0; k < numInput; k++){
						connections.add(new Connection(nodes.get(k), curNode, randStart(rand)));
					}
				}else{ // rest to layer before
					for(int k = 0; k < hiddenLayers[i-1]; k++){
						connections.add(new Connection(nodes.get(index-k), curNode, randStart(rand)));
					}
				}
				// to output
				if(i == hiddenLayers.length-1){
					for(int k = 0; k < numOutput; k++){
						connections.add(new Connection(curNode, nodes.get(numInput+k), randStart(rand)));
					}
				}
			}
			index += hiddenLayers[i];
		}
	}
	
	public NeuralNetwork(String fileName){
		Random rand = new Random();		
		System.out.println("LOAD");
		
		try {		
			Scanner scan = new Scanner(new File("networks/"+fileName));
			String topology = scan.nextLine();
			Scanner topologyScan = new Scanner(topology);
			
			nodes = new ArrayList<Node>();
			connections = new ArrayList<Connection>();
			int curLayer = 0;
			int firstLength = -1;
			int lastLength = -1;
			while(topologyScan.hasNext()){
				String next = topologyScan.next();
				String[] biases = next.substring(1,next.length()-1).split(",");
				if(firstLength == -1) firstLength = biases.length;
				lastLength = biases.length;
				NodeType type = (curLayer == 0) ? NodeType.INPUT : NodeType.HIDDEN;
				
				for(String s : biases){
					if(isDouble(s)){
						nodes.add(new Node(Double.parseDouble(s),type));
					}else{
						nodes.add(new Node(randStart(rand), type));
					}
				}
				
				curLayer++;
			}

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
				}else{
					System.err.println("Invalid Connection Weight: "+w);
				}
				
				connections.add(new Connection(getNode(from), getNode(to), weight));
				
				conScan.close();
			}
			
			// move output nodes to after input
			ArrayList<Node> outputs = new ArrayList<Node>(lastLength);
			for(int i = 0; i < lastLength; i++) outputs.add(nodes.remove(nodes.size()-1));
			Collections.reverse(outputs);
			for(int i = 0; i < outputs.size(); i++){
				outputs.get(i).setType(NodeType.OUTPUT);
				nodes.add(firstLength+i, outputs.get(i));
			}
			
			// make sublists
			inputNodes = nodes.subList(0, firstLength);
			outputNodes = nodes.subList(firstLength, firstLength + lastLength);
			
			// close
			scan.close();
			topologyScan.close();
					
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		System.out.println(this.toString());
	}

	public void saveNetwork(){
	// get layer structure
	ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
	int numberOfFiles = new File("networks").list().length;
	System.out.println("SAVE");
	String name = "savedNetwork"+numberOfFiles;
	
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("networks/"+name+".txt"));
				StringBuilder builder = new StringBuilder();
			
				ArrayList<List<Node>> layers = new ArrayList<List<Node>>(10);
				layers.add(inputNodes);
				layers.addAll(hiddenLayers);
				layers.add(outputNodes);
				
				for(int i = 0; i < layers.size(); i++){
					builder.append("[");
					for(int j = 0; j < layers.get(i).size(); j++){
						builder.append(layers.get(i).get(j).getBias()+",");
					}
					builder.deleteCharAt(builder.length()-1);
					builder.append("] ");
				}
				builder.deleteCharAt(builder.length()-1);
				
				for(Connection c: connections){
					builder.append("\n"+getIndex(c.getFromNode())+" "+getIndex(c.getToNode())+" "+c.getWeight());
				}
				writer.write(builder.toString());
						
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
	}

	public double[] getOutput(double[] input) {
		// wrong input size?
		if(input.length != inputNodes.size()) return null;
		
		// set input
		for(int i = 0; i < input.length; i++){
			nodes.get(i).setInput(input[i]);
		}
		
		// get output
		double[] result = new double[outputNodes.size()];
		for(int i = 0; i < result.length; i++){
			result[i] = outputNodes.get(i).forwardOperation();
		}
		return result;
	}
	
	public boolean runTraining(double[] trainingInput, double[] trainingOutput){
		// get layer structure
		ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
		// set inputs
		for(int j = 0; j < inputNodes.size(); j++){
			inputNodes.get(j).setInput(trainingInput[j]);
		}
		
		if(DEBUG) System.out.println("Output=");
		boolean match = true;
		for(int j = 0; j < outputNodes.size(); j++){
			// forward operation
			double result = outputNodes.get(j).forwardOperation();
			double target = trainingOutput[j];
			
			match = match && (target + MARGIN >= result && target - MARGIN <= result);
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
	
	public boolean isFit(double[][] trainingInput, double[][] trainingOutput){
		// check all inputs and see if they comply 
		// with the current network and weights.
		// Most importantly no backpropagation.
		boolean allCorrect = true;
		for(int i = 0; i < trainingInput.length; i++){
			allCorrect = allCorrect && runTraining(trainingInput[i], trainingOutput[i]);
		}
		return allCorrect;
	}
	
	private void nodeToBuilder(Node n, StringBuilder builder){
		builder.append(getIndex(n)+": bias="+n.getBias()+"\n");
		for(Connection c : n.conn_out){
			builder.append("\t-> "+getIndex(c.getToNode())+" = "+c.getWeight()+"\n");
		}
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
	
	private Node getNode(int index){
		return nodes.get(index-1);
		
		// get layer structure
		/*ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
		index--;
		if(index < inputNodes.size()) return inputNodes.get(index);
		index -= inputNodes.size();
		
		for(ArrayList<Node> layer : hiddenLayers){
			if(index < layer.size()) return layer.get(index);
			index -= layer.size();
		}
		
		return outputNodes.get(index);*/
	}
	
	private int getIndex(Node n){
		// get layer structure
		ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
		int index = 1;
		for(int i = 0; i < inputNodes.size(); i++) if(inputNodes.get(i) == n) return index+i;
		index += inputNodes.size();
		for(int i = 0; i < hiddenLayers.size(); i++){
			for(int j = 0; j < hiddenLayers.get(i).size(); j++){
				if(hiddenLayers.get(i).get(j) == n) return index+j;
			}
			index += hiddenLayers.get(i).size();
		}
		for(int i = 0; i < outputNodes.size(); i++) if(outputNodes.get(i) == n) return index+i;
		return -1;
	}
	
	private ArrayList<ArrayList<Node>> UpdateLayers() {
		if(connections.size() != lastNumConn){
			CalculateLayers();
		}
		return layerz;
	}
	
	private void CalculateLayers() {
		ArrayList<ArrayList<Node>> layers = new ArrayList<ArrayList<Node>>();		
		List<Node> hiddenNodes = nodes.subList(inputNodes.size()+outputNodes.size(), nodes.size()); // NO EDITING!
		
		layers.add(new ArrayList<Node>());
		
		// take one node at a time and calculate dependencies for already added nodes
		for(Node n : hiddenNodes){
			// must be between highest under and lowest over
			int highestUnder = -1;
			int lowestOver = layers.size();
			
			for(Connection o : n.conn_out){
				int position = getLayer(layers, o.getToNode());
				if(position != -1 && (lowestOver == -1 || position < lowestOver)) 
					lowestOver = position;
			}
			for(Connection i : n.conn_in){
				int position = getLayer(layers, i.getFromNode());
				if(position != -1 && (highestUnder == -1 || position > highestUnder)) 
					highestUnder = position;
			}
			
			// if under == over insert new layer between
			if(highestUnder == lowestOver){
				ArrayList<Node> newLayer = new ArrayList<Node>();
				newLayer.add(n);
				layers.add(highestUnder+1, newLayer);
			}else{ // else put in under + 1
				if(highestUnder == layers.size() - 1) layers.add(new ArrayList<Node>());
				layers.get(highestUnder+1).add(n);
			}
		}
		layerz = layers;
		lastNumConn = connections.size();
	}
	
	private int getLayer(ArrayList<ArrayList<Node>> layers, Node target){
		for(int i = 0; i < layers.size(); i++){
			if(layers.get(i).contains(target)) return i;
		}
		return -1;
	}

	@Override
	public String toString(){
		// get layer structure
		ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
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
