package champ2010client;

import itu.jgdiejuu.network.NeuralNetwork;

public class NEATforSPEED extends Controller{
	
	private NeuralNetwork nn;

	public NEATforSPEED(NeuralNetwork nn){
		this.nn = nn;
	}
	
	public void reset() {
		System.out.println("Restarting the race!");
		
	}

	public void shutdown() {
		System.out.println("Bye bye!");	
	}

	@Override
	public Action control(SensorModel sensors) {
		setInputs(sensors);
		return getOutput();
	}

	private Action getOutput() {
		// TODO Auto-generated method stub
		System.out.println("NEATforSPEED returning empty action :(");
		return new Action();
	}

	private void setInputs(SensorModel sensors) {
		// TODO Auto-generated method stub
		
	}
}
