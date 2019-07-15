package states;

import java.util.LinkedList;

// Interface for the SV engine states.
public interface ifSVengine {
	
	public String getMinTR();
	public LinkedList<String> getValidTypes();
	public LinkedList<Double> getMultipliers();
	public double getFuelMultiplier();
}
