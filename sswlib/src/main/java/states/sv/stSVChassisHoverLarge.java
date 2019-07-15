package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisHoverLarge implements ifSVchassis {

	public double getBCV() {
		return 0.3;
	}

	public double getBEV() {
		return 0.008;
	}

	public double getMaximumTonnage() {
		return 100;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 50.5;
	}

	public String getMotiveType() {
		return "Hover";
	}

	public String getSize() {
		return "Large";
	}
	
	public LinkedList<String> getLocations() {
		LinkedList<String> tempLocations = new LinkedList<String>();
		tempLocations.add("Front");
		tempLocations.add("Front Left");
		tempLocations.add("Front Right");
		tempLocations.add("Rear Left");
		tempLocations.add("Rear Right");
		tempLocations.add("Rear");
		tempLocations.add("Front Turret");
		tempLocations.add("Rear Turret");
		return tempLocations;
	}
}
