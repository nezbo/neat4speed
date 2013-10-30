package itu.jgdiejuu.network;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class NetworkCoach{
	
	private boolean errorFound = true;
	private Float learningrate = 0.05f;
	private int errors = 0;
	public float fitnessDeviation = 0.5f;
	static InputManager im;
	Font font = new Font("Verdana", Font.PLAIN, 20);
	JFrame frame;
	JLabel textLabel = new JLabel("learninrate="+learningrate.toString());
	NeuralNetwork nn;
	
	public static void main(String[] args){
		
		NetworkCoach nCoach = new NetworkCoach();	
		
	}

	public NetworkCoach(){
		
		im = new InputManager(this);
		frame = new JFrame();
		frame.addKeyListener(im);
		frame.setFocusable(true);
		frame.setVisible(true);
		frame.setSize(250, 100);
		frame.setBackground(Color.BLACK);
		frame.add(textLabel);
		textLabel.setFont(font);
		
		nn = new NeuralNetwork("testwork.txt");
		
		
		//nn.buildTwoOneNetwork();
		//nn.buildTwoTwoOneNetwork();
//		nn.buildTwoTwoTwoOneNetwork();
		//nn.buildTwoTwoTwoNetwork();
		//nn.buildFiveFiveFourNetwork();
		//nn.buildFiveFourFourNetwork();
		//nn.buildFiveSevenFourNetwork();
		//nn.buildFiveFourteenFourNetwork();
		//nn.buildFiveFiftyFourNetwork();
		
		HashMap<List<Integer>, List<Integer>> dataset = makePacManProblem();
		
		train(nn, dataset);
		
		for(List<Integer> inputs : dataset.keySet()){
			nn.run(inputs);			
		}
		
		System.out.println("------------------WINZ-----------------");
		nn.saveNetwork();
		System.out.println(nn);
		
	}
	
	//train the network
		public void train(NeuralNetwork network, HashMap <List<Integer>, List<Integer>> dataset){
		float t = 0;	
		
	//	for(int i = 0; i < 10; i++){
		while(true){
			
			errorFound = false;
			errors=0;
			t++;
			
	//		System.out.println();
	//		System.out.println("learning = " + learningrate);
				
			for(List<Integer> inputs : dataset.keySet()){
				runAndBackpropagate(network, inputs, dataset.get(inputs), learningrate);
					
			}
			
			System.out.println(errors+" errors found");
			
			if (!errorFound && network.isFit(dataset))		
				break;	
			System.out.println(t + " iterations");
		
		}

			System.out.println(t + " iterations");
		}
		
		private void runAndBackpropagate(NeuralNetwork network, List<Integer> inputs, List<Integer> list, float learningrate){		

			network.run(inputs);
			
			for(int i = 0; i < network.outputNodes.size(); i++){
				float value = network.outputNodes.get(i).getValue();
				//if(Math.round(value) != outputs.get(i)){											
				if(value < list.get(i)-fitnessDeviation || value > list.get(i)+fitnessDeviation){
					errorFound = true;
					errors++;
				}
			}		
			
			network.backpropagate(list, learningrate);
			
			network.resetNodes();
		}
		
		private HashMap<List<Integer>, List<Integer>> makePacManProblem(){
			
			HashMap<List<Integer>, List<Integer>> dataset = new HashMap<List<Integer>, List<Integer>>();
			
			try {
				
			//replace . with ,
			 
			 Scanner scanIn = new Scanner(new File("pacInputs.txt"));
			 Scanner scanOut = new Scanner(new File("pacOutputs.txt"));
			 String content = "";
			 String content2 = "";
			 
			 while(scanIn.hasNextLine()) {
				 content += scanIn.nextLine()+"\n";
			  }
			 while(scanOut.hasNextLine()) {
				 content2 += scanOut.nextLine()+"\n";
			  }

			 content = content.replaceAll("\\." , ",");
			 content2 = content2.replaceAll("\\." , ",");

			 
			 FileWriter fw = new FileWriter("pacInputs.txt");
			 FileWriter fw2 = new FileWriter("pacOutputs.txt");
			 fw.write(content);
			 fw2.write(content2);
			 fw.close();
			 fw2.close();

			} catch (IOException e1) {e1.printStackTrace();}
			
			
			 
			 
			 Scanner is;
			Scanner os;
			try {
				
				is = new Scanner(new File("pacInputs.txt"));	
				os = new Scanner(new File("pacOutputs.txt"));
			
			//for every input
			int counter = 0;
			while(is.hasNextInt()){

				dataset.put(Arrays.asList(is.nextInt(),is.nextInt()), 
							Arrays.asList(os.nextInt()));
				
				
			}
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return dataset;
		}
		
		public void increaseLearningrate(){
			learningrate *= 2;
			System.out.println("learningrate increased = "+learningrate);
		}
		
		public void decreaseLearningrate(){
			learningrate /= 2;
			System.out.println("learningrate decreased = "+learningrate);
		}
		
		public void setText(){
			System.out.println("setText");
			textLabel.setText("learningrate="+learningrate.toString());
		}

		public void saveNetwork(){
			nn.saveNetwork();
		}

}
