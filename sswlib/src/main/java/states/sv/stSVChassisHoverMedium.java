package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisHoverMedium implements ifSVchassis {

	public double getBCV() {
		return 0.25;
	}

	public double getBEV() {
		return 0.004;
	}

	public double getMaximumTonnage() {
		return 50;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 5;
	}

	public String getMotiveType() {
		return "Hover";
	}

	public String getSize() {
		return "Medium";
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
