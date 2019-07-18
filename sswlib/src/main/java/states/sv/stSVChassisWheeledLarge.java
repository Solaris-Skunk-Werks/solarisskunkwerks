package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisWheeledLarge implements ifSVchassis {

	public double getBCV() {
		return 0.18;
	}

	public double getBEV() {
		return 0.015;
	}

	public double getMaximumTonnage() {
		return 80;
	}

	public String getMinimumTR() {
		return "A";
	}

	public double getMinimumTonnage() {
		return 5;
	}

	public String getMotiveType() {
		return "Wheeled";
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
