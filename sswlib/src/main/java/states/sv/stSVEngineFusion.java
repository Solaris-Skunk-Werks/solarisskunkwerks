package states.sv;

import java.util.LinkedList;

import states.ifSVengine;

public class stSVEngineFusion implements ifSVengine {

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
		multipliers.add(1.5);
		multipliers.add(1.0);
		multipliers.add(0.75);
		multipliers.add(0.5);
		return multipliers;
	}

	public LinkedList<String> getValidTypes() {
		LinkedList<String> valid = new LinkedList<String>();
		valid.add("Airship");
		valid.add("Fixed-Wing");
		valid.add("Hover");
		valid.add("Naval");
		valid.add("Rail");
		valid.add("Satellite");
		valid.add("Tracked");
		valid.add("VTOL");
		valid.add("Wheeled");
		valid.add("WiGE");
		return valid;
	}

}
