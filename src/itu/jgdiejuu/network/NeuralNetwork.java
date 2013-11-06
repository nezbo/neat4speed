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

/**
 * The class encapsulates the content of an Artificial Neural-Network and
 * provides methods necessary for training it with supervised learning by
 * backpropagation, aswell as methods for application within a NEAT evolution
 * context. The class is capable of loading from a file and saving a
 * (possibly changed version) aswell.
 * 
 * @author Emil
 *
 */
public class NeuralNetwork {
	
    public static int RESTARTS = 10; // The maximum number of restarts in the demo training.
    public static int LIMIT = 10; // The maximum number of iterations of the training set in the demo training.
    public static boolean DEBUG = false; // Toggle debug output to console.
	   
	public final float randomWeightMutationRate = 0.1f;//chance of connection recieving a new random weight
	public final float uniformlyWeightMutationRate = 0.9f;//chance of weight being uniformly pertubed
	
	//Random object
	Random r = new Random();
	
	public static void main(String[] args){
		
		// create training pairs XOR
		double[][] trainingInputs = new double[][]{new double[]{0,0},new double[]{1,0},new double[]{0,1},new double[]{1,1}};
		double[][] trainingOutputs = new double[][]{new double[]{0},new double[]{1},new double[]{1},new double[]{0}};
		
		int restarts = 0;
		
		while(restarts <= RESTARTS){ // input,output,hidden,levels,outputValues
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
					nn.saveNetwork();
				}
			}
		}
	}
	
	// -------------------------------------
	
	private static double MARGIN = 0.05;// The maximum deviation from the wanted value in supervised learning.
	
	private ArrayList<Node> nodes; // All nodes in the network; first input, output and then hidden nodes.
	private List<Node> inputNodes; // A sublist of the input nodes.
	private List<Node> outputNodes; // A sublist of the output nodes.
	private ArrayList<Connection> connections; // All connections in the network.
	
	private ArrayList<ArrayList<Node>> layerz; // An ordering of the nodes in minimum number of layers for easly visualization and saving.
	private int lastNumConn = 0; // The number of connections at last update of layers, to know when to update.
	
	/**
	 * Creates a Neural-Network with the specified number of nodes in input, output and
	 * an unlimited number of layers. The network will be created fully connected, with
	 * each node one one layer connected to all nodes in the next and initially random
	 * biases.
	 * 
	 * @param numInput The number of input nodes.
	 * @param numOutput The number of output nodes.
	 * @param hiddenLayers One number for each hidden layer specifying how many hidden nodes that layer should have.
	 */
	public NeuralNetwork(int numInput, int numOutput, int... hiddenLayers){
		connections = new ArrayList<Connection>();
		
		// create tree
		nodes = new ArrayList<Node>();
		
		for(int i = 0; i < numInput; i++) nodes.add(new Node(0.0, NodeType.INPUT));
		inputNodes = nodes.subList(0, nodes.size());
		for(int i = 0; i < numOutput; i++) nodes.add(new Node(randStart(), NodeType.OUTPUT));
		outputNodes = nodes.subList(numInput, nodes.size());
		int index = numInput + numOutput;
		
		int tempInnovationNumber = 0;
		for(int i = 0; i < hiddenLayers.length; i++){
			for(int j = 0; j < hiddenLayers[i]; j++){
				Node curNode = new Node(randStart(), NodeType.HIDDEN);
				nodes.add(curNode);
				// hook up to previous
				if(i == 0){ // first layer to input
					for(int k = 0; k < numInput; k++){
						connections.add(new Connection(nodes.get(k), curNode, randStart(),true, tempInnovationNumber));
						tempInnovationNumber++;
					}
				}else{ // rest to layer before
					for(int k = 0; k < hiddenLayers[i-1]; k++){
						connections.add(new Connection(nodes.get(index-k), curNode, randStart(),true, tempInnovationNumber));
						tempInnovationNumber++;
					}
				}
				// to output
				if(i == hiddenLayers.length-1){
					for(int k = 0; k < numOutput; k++){
						connections.add(new Connection(curNode, nodes.get(numInput+k), randStart(),true, tempInnovationNumber));
						tempInnovationNumber++;
					}
				}
			}
			index += hiddenLayers[i];
		}
	}
	
	/**
	 * Loads the Neural-Network from the specified file.
	 * 
	 * @param fileName The file with all information of the network 
	 * to build. Located in the networks\ folder.
	 */
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
						nodes.add(new Node(randStart(), type));
					}
				}
				
				curLayer++;
			}

			//for all connections
			int tempInnovationNumber = 0;
			while(scan.hasNextLine()){

				String con = scan.nextLine();
				Scanner conScan = new Scanner(con);
				
				int from = conScan.nextInt();
				int to = conScan.nextInt();
				String w = conScan.next();
				int a = conScan.nextInt();
				double weight = 0;
				
				if(isDouble(w)){
					weight = Double.parseDouble(w);
				}else if(w.equals("r")){
					weight = randStart();
				}else{
					System.err.println("Invalid Connection Weight: "+w);
				}
				
				connections.add(new Connection(getNode(from), getNode(to), weight,a == 1 ? true : false, tempInnovationNumber));
				
				conScan.close();
				tempInnovationNumber++;
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
			
			//nodes.add(4, nodes.remove(nodes.size()-1)); // DEBUG DEBUG DEBUG
			
			// close
			scan.close();
			topologyScan.close();
					
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		System.out.println(this.toString());
	}

	/**
	 * Creates a new Neural-Network around a list of Nodes and Connections using
	 * these Nodes. The Nodes must be ordered in INPUT -> OUTPUT -> HIDDEN
	 * @param nodes An ArrayList containing all Nodes in the Network.
	 * @param connections An ArrayList containing all Connections to those nodes.
	 */
	private NeuralNetwork(ArrayList<Node> nodes, ArrayList<Connection> connections){
		this.nodes = nodes;
		this.connections = connections;
		
		// find input and output subLists
		int indexEnd = -1;
		for(int i = 0; i < nodes.size(); i++) 
			if(nodes.get(i).getType() != NodeType.INPUT){
				this.inputNodes = nodes.subList(0, i);
				indexEnd = i;
			}
		for(int i = indexEnd; i < nodes.size(); i++) 
			if(nodes.get(i).getType() == NodeType.HIDDEN ) 
				this.outputNodes = nodes.subList(indexEnd, i);
	}
	
	/**
	 * Saves the network in a file in the \networks\ folder and a running
	 * number.
	 */
	public void saveNetwork(){
	// get layer structure
	ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
	int numberOfFiles = new File("networks").list().length;
	
	String name = "savedNetwork"+numberOfFiles;
	System.out.println("SAVE ("+name+".txt)");
	
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
					builder.append("\n"+getIndex(c.getFromNode())+" "+getIndex(c.getToNode())+" "+c.getWeight()+" "+(c.isActive() ? "1" : "0"));
				}
				writer.write(builder.toString());
						
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
	}

	/**
	 * Calculates the output of the network with the given input
	 * applied to the input nodes. No modification to the network
	 * is performed.
	 * @param input An array containing the input values for each input node.
	 * @return An array of output values, one for each output node.
	 * Null if wrong number of input values.
	 */
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
	
	/**
	 * mutates the weight of most connections based on mutation rates.
	 * the mutation will either add or subtract a constant or set weight 
	 * to a new random weight between -1 and 1.
	 */
	public void weightMutation(int innovationNumber){
		
		double weightChange = 0.02;
		
		for(Connection c: connections){
			float randomFloat = r.nextFloat();
			
			if(randomFloat < uniformlyWeightMutationRate){
				c.setWeight(randStart());
			}

			if(randomFloat < randomWeightMutationRate && randomFloat > 0.5){
				c.setWeight(c.getWeight()+weightChange);
			}else if(randomFloat < randomWeightMutationRate && randomFloat < 0.5){
				c.setWeight(c.getWeight()-weightChange);
			}
		}
	}
	
	/**
	 * Adds a legal connection between two random nodes in the network
	 * Nothing happens if no legal connection can be added
	 * @param innovationNumber to be assigned to the newly created connection
	 */
	public void addConnectionMutation(int innovationNumber){
		
		//clone nodes. these nodes represent "unchecked" nodes
		ArrayList<Node> nodesToBeChecked = new ArrayList<Node>(nodes);
		ArrayList<Node> nodesToBeRemoved = new ArrayList<Node>();
		
		//find the first node and remove it from nodesToBeChecked
		//if firstNode is output - pick again 

		Node fromNode = nodesToBeChecked.get(r.nextInt(nodes.size()));
		while(fromNode.getType() == NodeType.OUTPUT){
			fromNode = nodesToBeChecked.get(r.nextInt(nodes.size()));
		}
		nodesToBeChecked.remove(fromNode);
		
		//get all nodes already connected to firstNode
		//and remove them from nodesToBeChecked
		for(Connection c: fromNode.conn_out){
				nodesToBeChecked.remove(c.getToNode());
		}
		for(Connection c: fromNode.conn_in){
			nodesToBeChecked.remove(c.getFromNode());
		}
		
		//remove all inputNodes (connection can't go backwards from hidden to input)
		for(Node n: nodesToBeChecked){
			if(n.getType() == NodeType.INPUT){
				nodesToBeRemoved.add(n);
			}
		}
		for(Node n: nodesToBeRemoved){
			nodesToBeChecked.remove(n);
		}
		//pick random node - all of them shound be legal
		Node toNode = nodesToBeChecked.get(r.nextInt(nodesToBeChecked.size()));
		
		//create the new connection and add it to connections and Node.in/out
		Connection newCon = new Connection(fromNode, toNode, randStart(), true, innovationNumber);
		connections.add(newCon);
		fromNode.conn_out.add(newCon);
		toNode.conn_in.add(newCon);
	}
	
	/**
	 * Adds a node in the network at a random connection
	 * The old connection is disabled (inactive) and two new connections are created
	 * connection the new node to the former input and output.
	 * @param innovationNumber to be assigned to the newly created connection
	 */
	public void addNodeMutation(int innovationNumber){
		
		Connection toBeReplaced = connections.get(r.nextInt(connections.size()));
		Node oldFrom = toBeReplaced.getFromNode();
		Node oldTo = toBeReplaced.getToNode();
		toBeReplaced.setActive(false);
		Node newNode = new Node(randStart(), NodeType.HIDDEN);
		Connection newIn = new Connection(oldFrom, newNode, randStart(), true, innovationNumber);
		Connection newout = new Connection(newNode, oldTo, randStart(), true, innovationNumber+1);
		
	}
	
	/**
	 * Performs supervised training using backpropagation on the network with the
	 * given input and expected output pair for error calculation.
	 * @param trainingInput An array containing an input value for each input node.
	 * @param trainingOutput An array containing an expected output value for each output node.
	 * @return True if the networks gives the correct output for all output nodes within MARGIN of the target.
	 */
	public boolean runTraining(double[] trainingInput, double[] trainingOutput){
		// get layer structure
		ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
		// set inputs
		for(int j = 0; j < inputNodes.size(); j++){
			inputNodes.get(j).setInput(trainingInput[j]);
		}

		boolean match = true;
		for(int j = 0; j < outputNodes.size(); j++){
			// forward operation
			double result = outputNodes.get(j).forwardOperation();
			double target = trainingOutput[j];
			
			match = match && (target + MARGIN >= result && target - MARGIN <= result);
		}
		
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
		
		return match;
	}

	/**
	 * Performs supervised training using backpropagation on the network the the 
	 * given training set.
	 * @param trainingInput An array of training input values.
	 * @param trainingOutput An array of training output values.
	 * @return The number of correctly calculated training pairs.
	 */
	public int runTraining(double[][] trainingInput, double[][] trainingOutput) {
		int numCorrect = 0;
		for(int i = 0; i < trainingInput.length; i++){
			boolean match = runTraining(trainingInput[i], trainingOutput[i]);
			if(match) numCorrect++;
		}
		if(DEBUG) System.out.println(numCorrect+" out of "+trainingInput.length+" correct.");
		return numCorrect;
	}
	
	/**
	 * Runs all the traning pairs through the network WITHOUT any modification
	 * to the network, to see if all are calculated correctly with the current
	 * structure.
	 * @param trainingInput An array of training input values.
	 * @param trainingOutput An array of training output values.
	 * @return True if all training pairs are calculated correctly, else false.
	 */
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
	
	/**
	 * Compares two neural networks to get their compatibility distance.
	 * The compatibility distance is used to determine how "similar" the 
	 * topology of the two networks are.
	 * @param The "other" network to test against.
	 * @return a double denoting how much "genetic history" the two networks share.
	 */
	public double getCompatibilityDistance(NeuralNetwork other){
		
		//constants and avgW
		float C1 = 1;
		float C2 = 1;
		float C3 = 1;
		double avgWD = 0;
		int N = 1;
		int disjoints = 0;
		int excess = 0;
		
		//get connections
		ArrayList<Connection> othersGenes = other.getConnections();
		
		//avgW for this
		int avgWthis = 0;
		for(Connection c: connections){
			avgWthis += c.getWeight();
		}
		avgWthis /= connections.size();
		
		//avgW for other
		int avgWother = 0;
		for(Connection c: othersGenes){
			avgWother += c.getWeight();
		}
		avgWother /= othersGenes.size();
		
		//average weight distance
		avgWD = Math.abs(avgWthis-avgWother);
				
		int max = Math.max(connections.size(), othersGenes.size());
		
		//if both genomes are smaller than 20 N=1 else N=max
		if(max > 20){
			N = max;
		}
		
		//get number of excess
		int maxIN = Math.max(connections.get(connections.size()-1).getInnovationNumber(), othersGenes.get(othersGenes.size()-1).getInnovationNumber());
		int minIN = Math.min(connections.get(connections.size()-1).getInnovationNumber(), othersGenes.get(othersGenes.size()-1).getInnovationNumber());
		
		excess = maxIN - minIN;
		
		//get number of disjoints
		int[] cArray1 = this.getCompatibilityArray();
		int[] cArray2 = other.getCompatibilityArray();
		
		for(int i = minIN-1; i <= 0; i++){
			if(cArray1[i] != cArray2[i]){
				disjoints++;
			}
		}
		
		return (C1*excess)+(C2*disjoints)+(C2*avgWD);
	}
	
	/**
	 * @return An integer array that is sorted so that each connection is placed at the index corresponding to it's innovationNumber. "Empty" indexes will ahve a value of -1.
	 */
	public int[] getCompatibilityArray(){
		
		int iNumber = connections.get(connections.size()-1).getInnovationNumber();
		int[] compatibilityArray = new int[iNumber];
		for(int i = 0; i < iNumber; i++){
			compatibilityArray[i] = -1;
		}
		for(Connection c: connections){
			compatibilityArray[c.getInnovationNumber()] = c.getInnovationNumber();
		}
		return compatibilityArray;
	}

	// Adds the information of the current node to the StringBuilder.
	private void nodeToBuilder(Node n, StringBuilder builder){
		builder.append(getIndex(n)+": bias="+n.getBias()+"\n");
		for(Connection c : n.conn_out){
			builder.append("\t-> "+getIndex(c.getToNode())+" = "+c.getWeight()+"\n");
		}
	}
	
	// True if the content of the String matches a double.
	private boolean isDouble( String input ) {
	    try {
	        Double.parseDouble( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
	
	// Creates a random value between -1.0 and 1.0 using the given Random object.
	private double randStart(){
		return (r.nextDouble() + r.nextDouble())-1.0;
	}
	
	// Gets the node at the given index. BEFORE output nodes 
	private Node getNode(int index){
		return nodes.get(index-1);
	}
	
	// Gets the connections-ArrayList of this network 
	public ArrayList<Connection> getConnections(){
		return connections;
	}
	
	// Gets the (1-based) index of a given node in the "nodes" array according
	// to the LAYERED LAYOUT
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
	
	// Updates the layers model if necessary.
	private ArrayList<ArrayList<Node>> UpdateLayers() {
		if(connections.size() != lastNumConn){
			CalculateLayers();
		}
		return layerz;
	}
	
	// Groups the nodes in legal layers and updates the saved structure.
	private void CalculateLayers() {
		if(DEBUG) System.out.println("Calculating Layer Structure");
		ArrayList<ArrayList<Node>> layers = new ArrayList<ArrayList<Node>>();		
		List<Node> hiddenNodes = nodes.subList(inputNodes.size()+outputNodes.size(), nodes.size()); // NO EDITING!
		
		layers.add(new ArrayList<Node>());
		
		// take one node at a time and calculate dependencies for already added nodes
		for(Node n : hiddenNodes){
			if(DEBUG) System.out.println("Adding node with bias: "+n.getBias());
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
			
			if(DEBUG) System.out.println("\thighest under: "+highestUnder+" and lowest over: "+lowestOver);
			// if under == over insert new layer between
			if(highestUnder + 1 >= lowestOver){
				
				if(lowestOver < layers.size()){
					// get output in layer
					ArrayList<Node> outInLayer = getOutInLayer(n,layers.get(lowestOver));
					for(Node o : outInLayer){
						layers.get(lowestOver).remove(o);
						addAboveOrNewLayer(o,layers,lowestOver);
					}
				}
				if(DEBUG) System.out.println("\tAdding layer between");
				
				ArrayList<Node> newLayer = new ArrayList<Node>();
				newLayer.add(n);
				layers.add(highestUnder+1, newLayer);
			}else{ // else put in under + 1
				if(DEBUG) System.out.println("\tPutting between");
				if(highestUnder == layers.size() - 1) layers.add(new ArrayList<Node>());
				layers.get(highestUnder+1).add(n);
			}
			if(DEBUG) System.out.println(printArray(layers));
		}
		layerz = layers;
		lastNumConn = connections.size();
	}
	
	// For debugging the structuring of nodes into layers
	private String printArray(ArrayList<ArrayList<Node>> layers) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(ArrayList<Node> l : layers){
			builder.append("[");
			for(Node n : l){
				builder.append(n.getBias()+",");
			}
			builder.append("] ");
		}
		builder.append("]");
		return builder.toString();
	}

	// Adds the node above the given layer or (if a connected node is there, create a new layer between
	private void addAboveOrNewLayer(Node n, ArrayList<ArrayList<Node>> layers,
			int above) {
		
		// add the given node above the specified layer or create a new layer between.
		if(layers.size() > above+1 && !(getOutInLayer(n,layers.get(above+1)).size() > 0)){
			// it is not top layer and it doesnt have an outbound connection in that layer
			if(DEBUG) System.out.println("\tMoving: "+n.getBias()+" to layer "+(above+1));
			layers.get(above+1).add(n);
		}else{ // create new layer
			if(DEBUG) System.out.println("\tMoving: "+n.getBias()+". Creating new layer");
			ArrayList<Node> newLayer = new ArrayList<Node>();
			newLayer.add(n);
			layers.add(above+1, newLayer);
		}
	}

	// Gets a nodes outbound connected neighbors in a given layer
	private ArrayList<Node> getOutInLayer(Node n, ArrayList<Node> layer){
		ArrayList<Node> result = new ArrayList<Node>();
		for(Node o : layer){
			for(Connection c : n.conn_out){
				if(c.getToNode() == o){
					result.add(o);
				}
			}				
		}
		return result;
	}
	
	// Finds what layer a given node is located in. -1 if not found.
	// Gets the layer index that a given node is contained in, or -1 of not found.
	private int getLayer(ArrayList<ArrayList<Node>> layers, Node target){
		for(int i = 0; i < layers.size(); i++){
			if(layers.get(i).contains(target)) return i;
		}
		return -1;
	}

	// Creates a String from a specific layering of hidden nodes.
	private String toString(ArrayList<ArrayList<Node>> hiddenLayers){
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
	
	@Override
	public String toString(){
		// get layer structure
		ArrayList<ArrayList<Node>> hiddenLayers = UpdateLayers();
		
		return toString(hiddenLayers);
	}

	
	@Override
	protected Object clone() {
		// clone nodes (no connections)
		ArrayList<Node> newNodes = new ArrayList<Node>(nodes.size());
		for(Node n : nodes) newNodes.add((Node)n.clone());
		
		// connect with new connections similar to existing
		ArrayList<Connection> newConnections = new ArrayList<Connection>(connections.size());
		for(Connection c : connections){
			int start = nodes.indexOf(c.getFromNode());
			int end = nodes.indexOf(c.getToNode());
			newConnections.add(new Connection(newNodes.get(start), 
					newNodes.get(end), 
					c.getWeight(), 
					c.isActive(), 
					c.getInnovationNumber()));
		}
		
		// make new NN with this
		return new NeuralNetwork(newNodes,newConnections);
	}
}
