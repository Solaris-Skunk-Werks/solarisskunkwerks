package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisSatelliteLarge implements ifSVchassis {

	public double getBCV() {
		return 0.16;
	}

	public double getBEV() {
		return 0.1;
	}

	public double getMaximumTonnage() {
		return 200;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 100.5;
	}

	public String getMotiveType() {
		return "Satellite";
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
