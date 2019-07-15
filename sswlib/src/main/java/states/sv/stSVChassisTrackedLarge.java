package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisTrackedLarge implements ifSVchassis {

	public double getBCV() {
		return 0.25;
	}

	public double getBEV() {
		return 0.025;
	}

	public double getMaximumTonnage() {
		return 200;
	}

	public String getMinimumTR() {
		return "B";
	}

	public double getMinimumTonnage() {
		return 100.5;
	}

	public String getMotiveType() {
		return "Tracked";
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
