package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisTrackedSmall implements ifSVchassis {

	public double getBCV() {
		return 0.13;
	}

	public double getBEV() {
		return 0.006;
	}

	public double getMaximumTonnage() {
		return 4.999;
	}

	public String getMinimumTR() {
		return "B";
	}

	public double getMinimumTonnage() {
		return 0.1;
	}

	public String getMotiveType() {
		return "Tracked";
	}

	public String getSize() {
		return "Small";
	}
	
	public LinkedList<String> getLocations() {
		LinkedList<String> tempLocations = new LinkedList<String>();
		tempLocations.add("Front");
		tempLocations.add("Left");
		tempLocations.add("Right");
		tempLocations.add("Rear");
		tempLocations.add("Front Turret");
		tempLocations.add("Rear Turret");
		return tempLocations;
	}

}
