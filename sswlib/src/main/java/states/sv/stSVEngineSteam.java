package states.sv;

import java.util.LinkedList;
import states.ifSVengine;

public class stSVEngineSteam implements ifSVengine {

	public double getFuelMultiplier() {
		return 3.0;
	}

	public String getMinTR() {
		return "A";
	}

	public LinkedList<Double> getMultipliers() {
		LinkedList<Double> multipliers = new LinkedList<Double>();
		multipliers.add(4.0);
		multipliers.add(3.5);
		multipliers.add(3.0);
		multipliers.add(2.8);
		multipliers.add(2.8);
		multipliers.add(2.5);
		return multipliers;
	}

	public LinkedList<String> getValidTypes() {
		LinkedList<String> valid = new LinkedList<String>();
		valid.add("Airship");
		valid.add("Naval");
		valid.add("Rail");
		valid.add("Tracked");
		valid.add("Wheeled");
		return valid;
	}

}
