package states;

import java.util.LinkedList;


// Interface for the SV chassis states.
public interface ifSVchassis {
	
	public String getSize();
	public String getMotiveType();
	public double getMinimumTonnage();
	public double getMaximumTonnage();
	public String getMinimumTR();
	public double getBCV();
	public double getBEV();
	public LinkedList<String> getLocations();
}
