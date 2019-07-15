package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisWiGELarge implements ifSVchassis {

	public double getBCV() {
		return 0.17;
	}

	public double getBEV() {
		return 0.006;
	}

	public double getMaximumTonnage() {
		return 160;
	}

	public String getMinimumTR() {
		return "C";
	}

	public double getMinimumTonnage() {
		return 80.5;
	}

	public String getMotiveType() {
		return "WiGE";
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
