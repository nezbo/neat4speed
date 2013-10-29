package champ2010client;

public class NEATforSPEED extends Controller{

	
	public void reset() {
		System.out.println("Restarting the race!");
		
	}

	public void shutdown() {
		System.out.println("Bye bye!");	
	}

	@Override
	public Action control(SensorModel sensors) {


		// build a CarControl variable and return it
        Action action = new Action ();

        action.accelerate = 1;

        
        return action;

	}
}
