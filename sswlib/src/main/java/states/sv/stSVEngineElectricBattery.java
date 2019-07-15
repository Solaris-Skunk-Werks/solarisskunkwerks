package states.sv;

import java.util.LinkedList;
import states.ifSVengine;

public class stSVEngineElectricBattery implements ifSVengine {

	public double getFuelMultiplier() {
		return 5.0;
	}

	public String getMinTR() {
		return "C";
	}

	public LinkedList<Double> getMultipliers() {
		LinkedList<Double> multipliers = new LinkedList<Double>();
		multipliers.add(0.0);
		multipliers.add(0.0);
		multipliers.add(1.5);
		multipliers.add(1.2);
		multipliers.add(1.0);
		multipliers.add(0.8);
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
