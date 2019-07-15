package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisAirshipLarge implements ifSVchassis {

	public double getBCV() {
		return 0.3;
	}

	public double getBEV() {
		return 0.012;
	}

	public double getMaximumTonnage() {
		return 1000;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 300.5;
	}

	public String getMotiveType() {
		return "Airship";
	}

	public String getSize() {
		return "Large";
	}
	
	public LinkedList<String> getLocations() {
		LinkedList<String> tempLocations = new LinkedList<String>();
		tempLocations.add("Nose");
		tempLocations.add("Left Wing");
		tempLocations.add("Right Wing");
		tempLocations.add("Aft");
		return tempLocations;
	}

}
