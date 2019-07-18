package states.sv;

import java.util.LinkedList;

import states.ifSVengine;

public class stSVEngineMagLev implements ifSVengine {

	public double getFuelMultiplier() {
		return 0;
	}

	public String getMinTR() {
		return "C";
	}

	public LinkedList<Double> getMultipliers() {
		LinkedList<Double> multipliers = new LinkedList<Double>();
		multipliers.add(0.0);
		multipliers.add(0.0);
		multipliers.add(0.8);
		multipliers.add(0.7);
		multipliers.add(0.5);
		multipliers.add(0.5);
		return multipliers;
	}

	public LinkedList<String> getValidTypes() {
		LinkedList<String> valid = new LinkedList<String>();
		valid.add("Rail");
		return valid;
	}

}
