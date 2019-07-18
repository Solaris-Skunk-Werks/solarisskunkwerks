package states.sv;

import java.util.LinkedList;

import states.ifSVchassis;

public class stSVChassisNavalLarge implements ifSVchassis {

	public double getBCV() {
		return 0.17;
	}

	public double getBEV() {
		return 0.009;
	}

	public double getMaximumTonnage() {
		return 100000;
	}

	public String getMinimumTR() {
		return "B";
	}

	public double getMinimumTonnage() {
		return 300.5;
	}

	public String getMotiveType() {
		return "Naval";
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
		tempLocations.add("Turret One");
		tempLocations.add("Turret Two");
		tempLocations.add("Turret Three");
		tempLocations.add("Turret Four");
		tempLocations.add("Turret Five");
		tempLocations.add("Turret Six");
		tempLocations.add("Turret Seven");
		tempLocations.add("Turret Eight");
		return tempLocations;
	}

}
