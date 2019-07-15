package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisRailLarge implements ifSVchassis {

	public double getBCV() {
		return 0.3;
	}

	public double getBEV() {
		return 0.005;
	}

	public double getMaximumTonnage() {
		return 600;
	}

	public String getMinimumTR() {
		return "A";
	}

	public double getMinimumTonnage() {
		return 300.5;
	}

	public String getMotiveType() {
		return "Rail";
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
