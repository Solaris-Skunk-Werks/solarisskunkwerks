package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisSatelliteSmall implements ifSVchassis {

	public double getBCV() {
		return 0.08;
	}

	public double getBEV() {
		return 0.1;
	}

	public double getMaximumTonnage() {
		return 4.999;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 0.1;
	}

	public String getMotiveType() {
		return "Satellite";
	}

	public String getSize() {
		return "Small";
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
