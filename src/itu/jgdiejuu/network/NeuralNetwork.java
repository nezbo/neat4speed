package itu.jgdiejuu.network;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;


public class NeuralNetwork {

	ArrayList <InputNode> inputNodes = new ArrayList <InputNode>();
	ArrayList <ArrayList<HiddenNode>> hiddenLayers = new ArrayList <ArrayList<HiddenNode>>();
	ArrayList <OutputNode> outputNodes = new ArrayList <OutputNode>();
	
	ArrayList <Connection> allConnections = new ArrayList <Connection>();
	
	ArrayList <Node> layerList = new ArrayList <Node>();
	ArrayList <Node> nodes = new ArrayList <Node>();
	Random r = new Random();
	
	public float fitnessDeviation = 0.001f;
	public int numberOfLayers = 0;
	public int numberOfHiddenLayers = 0;
	public int numberOfInputNodes = 0;
	public int numberOfOutputNodes = 0;
	
	public void buildTwoTwoOneNetwork(){
		
		numberOfHiddenLayers = 1;
		
		//2-2-1
		InputNode x1 = new InputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 1);
		InputNode x2 = new InputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 2);
		inputNodes.add(x1);
		inputNodes.add(x2);
		
		ArrayList <HiddenNode> hiddenLayer1 = new ArrayList <HiddenNode>();
		HiddenNode x3 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 3, getNewBias());
		HiddenNode x4 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 4, getNewBias());
		hiddenLayer1.add(x3);
		hiddenLayer1.add(x4);
		hiddenLayers.add(hiddenLayer1);
		
		OutputNode x5 = new OutputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 5, getNewBias());
		outputNodes.add(x5);
		
		nodes.add(x1);
		nodes.add(x2);
		nodes.add(x3);
		nodes.add(x4);
		nodes.add(x5);
		
		Connection con1 = new Connection(1, 3, getNewWeight(), this);
		Connection con2 = new Connection(1, 4, getNewWeight(), this);	
		Connection con3 = new Connection(2, 3, getNewWeight(), this);
		Connection con4 = new Connection(2, 4, getNewWeight(), this);
		x1.getOutgoing().add(con1);	x3.getIngoing().add(con1);
		x1.getOutgoing().add(con2); x4.getIngoing().add(con2);
		x2.getOutgoing().add(con3);	x3.getIngoing().add(con3);
		x2.getOutgoing().add(con4); x4.getIngoing().add(con4); 
		
		
		Connection con5 = new Connection(3, 5, getNewWeight(), this);
		Connection con6 = new Connection(4, 5, getNewWeight(), this);
		x3.getOutgoing().add(con5);	x5.getIngoing().add(con5);
		x4.getOutgoing().add(con6); x5.getIngoing().add(con6); 
		
		allConnections.add(con1);
		allConnections.add(con2);
		allConnections.add(con3);
		allConnections.add(con4);
		allConnections.add(con5);
		allConnections.add(con6);
		
		System.out.println("_________________________________Network initialized_________________________________");
		printNetwork();

	}

	
	public void buildTwoTwoTwoOneNetwork(){
		
		numberOfHiddenLayers = 2;
		
		//2-2-2-1
		InputNode x1 = new InputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 1);
		InputNode x2 = new InputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 2);
		inputNodes.add(x1);
		inputNodes.add(x2);
		
		ArrayList <HiddenNode> hiddenLayer1 = new ArrayList <HiddenNode>();
		HiddenNode x3 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 3, getNewBias());
		HiddenNode x4 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 4, getNewBias());
		hiddenLayer1.add(x3);
		hiddenLayer1.add(x4);
		hiddenLayers.add(hiddenLayer1);
		
		ArrayList <HiddenNode> hiddenLayer2 = new ArrayList <HiddenNode>();
		HiddenNode x5 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 5, getNewBias());
		HiddenNode x6 = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 6, getNewBias());
		hiddenLayer2.add(x5);
		hiddenLayer2.add(x6);
		hiddenLayers.add(hiddenLayer2);
		
		OutputNode x7 = new OutputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), 7, getNewBias());
		outputNodes.add(x7);
		
		nodes.add(x1);
		nodes.add(x2);
		nodes.add(x3);
		nodes.add(x4);
		nodes.add(x5);
		nodes.add(x6);
		nodes.add(x7);
		
		Connection con1 = new Connection(1, 3, getNewWeight(), this);
		Connection con2 = new Connection(1, 4, getNewWeight(), this);	
		Connection con3 = new Connection(2, 3, getNewWeight(), this);
		Connection con4 = new Connection(2, 4, getNewWeight(), this);
		x1.getOutgoing().add(con1);	x3.getIngoing().add(con1);
		x1.getOutgoing().add(con2); x4.getIngoing().add(con2);
		x2.getOutgoing().add(con3);	x3.getIngoing().add(con3);
		x2.getOutgoing().add(con4); x4.getIngoing().add(con4); 
		
		
		Connection con5 = new Connection(3, 5, getNewWeight(), this);
		Connection con6 = new Connection(3, 6, getNewWeight(), this);	
		Connection con7 = new Connection(4, 5, getNewWeight(), this);
		Connection con8 = new Connection(4, 6, getNewWeight(), this);
		x3.getOutgoing().add(con5);	x5.getIngoing().add(con5);
		x3.getOutgoing().add(con6); x6.getIngoing().add(con6);
		x4.getOutgoing().add(con7);	x5.getIngoing().add(con7);
		x4.getOutgoing().add(con8); x6.getIngoing().add(con8); 
		
		Connection con9 = new Connection(5, 7, getNewWeight(), this);
		Connection con10 = new Connection(6, 7, getNewWeight(), this);
		x5.getOutgoing().add(con9);	x7.getIngoing().add(con9);
		x6.getOutgoing().add(con10); x7.getIngoing().add(con10); 
		

		allConnections.add(con1);
		allConnections.add(con2);
		allConnections.add(con3);
		allConnections.add(con4);
		allConnections.add(con5);
		allConnections.add(con6);
		allConnections.add(con7);
		allConnections.add(con8);
		allConnections.add(con9);
		allConnections.add(con10);
		
		System.out.println("_________________________________Network initialized_________________________________");
		printNetwork();

	}


	
	public void run(List<Integer> inputs){

		if(inputs.size() != inputNodes.size()){
			System.out.println("number of inputNodes does not match problem-input");
			throw new IllegalArgumentException();
		}
		
		//set value for each inputNode and add them to openList
		for(int i = 0; i < inputs.size(); i++){
			inputNodes.get(i).setValue(inputs.get(i));
			layerList.add(inputNodes.get(i));
		}
		
		//activate, addBias and normalize
		while(!layerList.isEmpty()){
			activateLayer();
			normalizeLayer("sigmoid"); //tahn or sigmoid supported atm
		}
		
		//roundOutputs();
		
	//	System.out.println();
	//	printNetwork();
	}
	
	public void resetNodes(){
		for(Node n: nodes){
			n.reset();
		}
	}

	private void normalizeLayer(String method) {
		for(Node n: layerList){
			n.normalize(method);
		}
	}

	private void activateLayer() {
		
		ArrayList <Node> nextLayerList = new ArrayList<Node>();
		
		//for each node in openList
		for(Node n: layerList){
			n.activate();
			
			if (n instanceof InputNode){
				for(Connection c: ((InputNode)n).getOutgoing()){
					nextLayerList.add(c.getTo());
				}
			} else if (n instanceof HiddenNode){
				for(Connection c: ((HiddenNode)n).getOutgoing()){
					nextLayerList.add(c.getTo());
				}
			}
		}
		
		ArrayList <Node> unique = new ArrayList<Node>();
		
		for(Node n: nextLayerList){
			if(!unique.contains(n)){
				unique.add(n);
			}
		}
		
		for(Node n: unique){
			n.addBiasValue();
		}
		
		layerList = unique;
		
	}

	private boolean outputNormalized() {

		for(OutputNode out: outputNodes){
			if(!out.isNormalized())
				return false;
		}
		return true;
	}

	private float getNewWeight() {
		
		//return (float) Math.random();
		return (r.nextFloat() * 2) - 1;
	}

	public void backpropagate(List<Integer> list, float learningrate) {

		int i = 0;
		
		//compute error for outputs
		for(OutputNode out : outputNodes){
			out.computeError(list.get(i));
	//		System.out.print(outputs.get(i)+",");
			i++;
		}
		//and adjust weight for its ingoing connections
		for(OutputNode out : outputNodes){
			for(Connection c: out.getIngoing()){
				c.adjustWeight(learningrate);
			}
		}
		
		for(int j = numberOfHiddenLayers -1; j >= 0; j--){
			//compute error for hidden
			for(HiddenNode hid : hiddenLayers.get(j)){
				hid.computeError();
			}
			//and adjust weight for its ingoing connections
			for(HiddenNode hid : hiddenLayers.get(j)){
				for(Connection c: hid.getIngoing()){
					c.adjustWeight(learningrate);
				}
			}
		}
/*
		for(Connection c : allConnections){
			c.adjustWeight(learningrate);
		}
*/		
		for(Node n : nodes){
			if (n instanceof OutputNode || n instanceof HiddenNode){
				n.updateBias(learningrate);
			} 
			n.reset();
		}
		
		
	}
	
	private float getNewBias() {
		
		return (r.nextFloat() * 2) - 1; //-1 - 1
		//return r.nextFloat(); //0 - 1
	}

	public void roundOutputs(){
		for(OutputNode o : outputNodes){
			o.roundValue();
		}
	}
	
	public void printNetwork(){
		
		for(InputNode in: inputNodes){
			System.out.println("inputnode="+in.getId()+" (bias="+in.getBiasValue()+")  ");
			for(Connection c: in.getOutgoing()){
/*				System.out.println("From node "+c.getFrom().getId());
				System.out.println(" value="+c.getFrom().getValue());
				System.out.println(" bias="+c.getFrom().getBiasValue());
				System.out.println(" To node"+c.getTo().getId());
				System.out.println(" weight="+c.getWeight());
				System.out.println(" outputValue="+c.getTo().getValue());
*/				
				System.out.println("From node "+c.getFrom().getId()+" (value="+c.getFrom().getValue()+" bias="+c.getFrom().getBiasValue()+") To node"+c.getTo().getId()+" (weight="+c.getWeight()+" bias="+c.getTo().getBiasValue()+")  outputValue="+c.getTo().getValue());
			}
			
		}
		System.out.println();
		
		for(ArrayList<HiddenNode> inLayer: hiddenLayers){
			for(HiddenNode hn: inLayer){
				System.out.println("hiddennode="+hn.getId()+" (bias="+hn.getBiasValue()+")  ");
				for(Connection c: hn.getOutgoing()){
/*					System.out.println("From node "+c.getFrom().getId());
					System.out.println(" value="+c.getFrom().getValue());
					System.out.println(" bias="+c.getFrom().getBiasValue());
					System.out.println(" To node"+c.getTo().getId());
					System.out.println(" weight="+c.getWeight());
					System.out.println(" outputValue="+c.getTo().getValue());
*/					
					System.out.println("From node "+c.getFrom().getId()+" (value="+c.getFrom().getValue()+" bias="+c.getFrom().getBiasValue()+") To node"+c.getTo().getId()+" (weight="+c.getWeight()+" bias="+c.getTo().getBiasValue()+")  outputValue="+c.getTo().getValue());
				}
			}
			System.out.println();
		}
		
		for(OutputNode on: outputNodes){
			System.out.println("outputnode="+on.getId()+" (bias="+on.getBiasValue()+")  ");
		}
		System.out.println();
		
		for(Connection c : allConnections){
			System.out.println("From node "+c.getFrom().getId()+" (value="+c.getFrom().getValue()+" bias="+c.getFrom().getBiasValue()+") To node"+c.getTo().getId()+" (weight="+c.getWeight()+" bias="+c.getTo().getBiasValue()+")  outputValue="+c.getTo().getValue());
		}
		
	}

	public boolean isFit(HashMap<List<Integer>, List<Integer>> dataset) {
		
//		System.out.println("fit");
		
		boolean isFit = true;
		
		int counter = 0;
		
		for(List<Integer> inputs: dataset.keySet()){
			
			List<Integer> outputs = dataset.get(inputs);
			
			run(inputs);
		
			for(int i = 0; i < outputNodes.size(); i++){
				float out = outputNodes.get(i).getValue();
//				if(out != outputs.get(i)){
//					errorFound = true;
//				}
				System.out.println("in="+inputs);
				System.out.println("out="+out+"  outputs.get(i)="+ outputs.get(i));
				//if(Math.round(out) != outputs.get(i)){ 
				if(out > outputs.get(i) + fitnessDeviation || out < outputs.get(i) - fitnessDeviation){
					isFit = false;
					counter++;
					System.out.println("error in value"+out);
				}
			}
		}
		System.out.println("fit?="+isFit+" still "+counter+" errors left");
		
		return isFit;
	}
	
	public Float[] analyze(Float[] inputs){
		
		if(inputs.length != inputNodes.size()){
			System.out.println("number of inputNodes does not match problem-input");
			throw new IllegalArgumentException();
		}
		
		//set value for each inputNode and add them to openList
		for(int i = 0; i < inputs.length; i++){
			inputNodes.get(i).setValue(inputs[i]);
			layerList.add(inputNodes.get(i));
		}
		
		//activate, addBias and normalize
		while(!layerList.isEmpty()){
			activateLayer();
			normalizeLayer("sigmoid"); //tahn or sigmoid supported atm
		}
		
		//roundOutputs();
		
		Float[] solution = new Float[outputNodes.size()];
		for(int i = 0; i < outputNodes.size(); i++){
			solution[i] = outputNodes.get(i).getValue();
		}
		
		return solution;
		
	}
	
	public void loadNetwork(String fileName){
		
		System.out.println("LOAD");
		
		boolean biasValue = false;
		boolean input = true;
		ArrayList <Float> biasValues = new ArrayList <Float>();
		
		try {		
			Scanner scan = new Scanner(new File("networks/"+fileName));
			String topology = scan.nextLine();
			Scanner topologyScan = new Scanner(topology);
			
			//find number of layers and fill biasValues with values
			while(topologyScan.hasNext()){
				String next = topologyScan.next();
				
				if(next.equals("["))
					biasValue =true;
				else if(next.equals("]"))
					biasValue = false;
				else if(isInteger(next) && !biasValue){
					numberOfLayers++;
					if(input){
						numberOfInputNodes = Integer.parseInt(next);
						input = false;
					}
				}
				else if(isFloat(next) && biasValue)
					biasValues.add(Float.parseFloat(next));
				else if(next.equals("r"))
					biasValues.add(getNewBias());
			}
				
			
			numberOfHiddenLayers = numberOfLayers - 2;
			
/*			System.out.println("topology="+topology);
			System.out.println("numberOfLayers="+numberOfLayers);
			System.out.println("numberOfInputNodes="+numberOfInputNodes);
			System.out.println("numberOfHiddenLayers="+numberOfHiddenLayers);
			System.out.println("biasValues="+biasValues);
*/			
			Scanner newScan = new Scanner(topology);
			int id = 1;
			
			for(int i = 0; i < 3; i++){
				//for input nodes
				if(i == 0){
					int numberOfInputs = newScan.nextInt();	
					for(int j = 0; j < numberOfInputs; j++){
						InputNode in = new InputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), id);
						inputNodes.add(in);
						nodes.add(in);
						id++;
					}
				//for output nodes
				}else if(i == 2){

					numberOfOutputNodes = 0;
					
					while(true){
						String next = newScan.next();
						System.out.println("--NEXT="+next+"  i="+i);
						
						if(next.equals("["))
							biasValue =true;
						else if(next.equals("]"))
							biasValue = false;
						else if(isInteger(next) && !biasValue){
							numberOfOutputNodes = Integer.parseInt(next);
							System.out.println("FOUND!! numberOfOutputNodes="+numberOfOutputNodes+"  i="+i);
							break;
						}
					}
					
					for(int j = 0; j < numberOfOutputNodes; j++){
						OutputNode on = new OutputNode(new ArrayList <Connection>(), new ArrayList <Connection>(), id,  biasValues.get(id-(numberOfInputNodes+1)));
						outputNodes.add(on);
						nodes.add(on);
						id++;
					}
				
				//for hidden nodes in all hidden layers	
				}else{
					for(int j = 0; j < numberOfHiddenLayers; j++){
						
						int numberOfHiddenNodes = 0;
						
						while(true){
							String next = newScan.next();
							System.out.println("--NEXT="+next+"  i="+i);
							
							
							if(next.equals("["))
								biasValue =true;
							else if(next.equals("]"))
								biasValue = false;
							else if(isInteger(next) && !biasValue){
								numberOfHiddenNodes = Integer.parseInt(next);
								System.out.println("FOUND!! numberOfHiddenNodes="+numberOfHiddenNodes+"  i="+i);
								break;
							}
						}

						ArrayList <HiddenNode> curHiddenLayer = new ArrayList <HiddenNode>();
						hiddenLayers.add(curHiddenLayer);
						for(int k = 0; k < numberOfHiddenNodes; k++){
							System.out.println("id-(numberOfInputNodes+1)="+(id-(numberOfInputNodes+1)));
							HiddenNode hn = new HiddenNode(new ArrayList <Connection>(), new ArrayList <Connection>(), id, biasValues.get(id-(numberOfInputNodes+1)));
							curHiddenLayer.add(hn);
							nodes.add(hn);
							id++;
						}
					}
				}
			}

			//for all connections
			while(scan.hasNextLine()){

				String con = scan.nextLine();
				Scanner conScan = new Scanner(con);
				boolean randomWeight = false;
				
				int from = conScan.nextInt();
				int to = conScan.nextInt();
				String w = conScan.next();
				float weight = 0;
				
				if(isFloat(w)){
					weight = Float.parseFloat(w);
				}else if(w.equals("r")){
					weight = getNewWeight();
				}
				
				
				Connection conX = new Connection(from, to, weight, this);
				getNodeFromId(from).getOutgoing().add(conX);	getNodeFromId(to).getIngoing().add(conX);
				allConnections.add(conX);
			}
					
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		printNetwork();
	}

	public void saveNetwork(){
	
	int numberOfFiles = new File("networks").list().length;
	System.out.println("numberOfFiles = "+numberOfFiles);
	String name = "savedNetwork"+numberOfFiles;
	
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("networks/"+name+".txt"));
			
				String output = numberOfInputNodes+"";
				
				for(int i = 0; i < numberOfHiddenLayers; i++){
					output+=" "+hiddenLayers.get(i).size()+" [ ";
					for(int j = 0; j < hiddenLayers.get(i).size(); j++){
						output+=hiddenLayers.get(i).get(j).biasValue+" ";
					}
					output+="]";
				}
			
				output += " "+numberOfOutputNodes+" [ ";
				for(int i = 0; i < outputNodes.size(); i++){
					output+=outputNodes.get(i).getBiasValue()+" ";
				}
				output += " ]";
				
				for(Connection c: allConnections){
					output+="\n"+c.getFrom().getId()+" "+c.getTo().getId()+" "+c.getWeight();
				}
				writer.write(output);
						
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		
		
	}
	
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	public Node getNodeFromId(int id){
		for(Node n: nodes){
			if(n.getId() == id){
				return n;
			}
		}
		return null;
	}
	
	public boolean isInteger( String input ) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
	
	public boolean isFloat( String input ) {
	    try {
	        Float.parseFloat( input );
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
}
