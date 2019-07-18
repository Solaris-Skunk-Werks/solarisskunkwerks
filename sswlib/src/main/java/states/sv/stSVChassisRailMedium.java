package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisRailMedium implements ifSVchassis {

	public double getBCV() {
		return 0.2;
	}

	public double getBEV() {
		return 0.004;
	}

	public double getMaximumTonnage() {
		return 300;
	}

	public String getMinimumTR() {
		return "A";
	}

	public double getMinimumTonnage() {
		return 5;
	}

	public String getMotiveType() {
		return "Rail";
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
