package states.sv;

public class stSVLocation {
	private String name;
	private int internals;
	private int armor;
	
	public stSVLocation(String locationName, int locationInternals, int locationArmor) {
		this.name = locationName;
		this.internals = locationInternals;
		this.armor = locationArmor;
	}
	
	public String getName() {
		String tempName = this.name;
		return tempName;
	}
	
	public int getInternals() {
		int tempInternals = this.internals;
		return tempInternals;
	}
	
	public int getArmor() {
		int tempArmor = this.armor;
		return tempArmor;
	}
	
	public void setArmor(int newArmor) {
		this.armor = newArmor;
	}
}
