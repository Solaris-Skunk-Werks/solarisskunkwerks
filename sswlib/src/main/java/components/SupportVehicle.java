/*
Copyright (c) 2008~2009, Justin R. Bengtson (poopshotgun@yahoo.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
    * Neither the name of Justin R. Bengtson nor the names of contributors may
        be used to endorse or promote products derived from this software
        without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package components;

import common.Constants;
import java.util.LinkedList;
import java.util.prefs.Preferences;
import visitors.ifVisitor;
import states.ifSVchassis;
import states.ifSVengine;
import states.sv.*;

public class SupportVehicle {
	// First attempt at a support vehicle for the designer.  Note that it
	// currently does not have any BFB.GUI support.

	// PROPERTIES
	// Properties - Fluff Strings
	private String Name = "",
    			   Model = "",
    			   // TechBase = "Inner Sphere",
    			   Overview = "",
    			   Capabilities = "",
    			   History = "",
    			   Deployment = "",
    			   Variants = "",
    			   Notables = "",
    			   Additional = "",
    			   Company = "",
    			   Location = "",
    			   EngineManufacturer = "",
    			   ArmorModel = "",
    			   ChassisModel = "",
    			   CommSystem = "",
    			   TandTSystem = "",
    			   Solaris7ID = "0",
    			   Solaris7ImageID = "0",
    			   SSWImage = "";
	// Properties - Important Integers
	private int Era,
				Year,
				RulesLevel,
				baseMove;
	// Properties - SV Specific Strings
	private String motiveType,
									size,
									techRating;
								// engineType; // Not Used
	// Properties - SV Specific Doubles
	private double Tonnage,
				   baseChassisValue,
				   baseEngineValue;
	// Properties - SV Specific Integers
	private int cruiseSpeed;
	// Properties - SV Specific Lists
	// private LinkedList<stSVLocation> locations; // Not Used
	// Properties - SV Specific Booleans
	private boolean TO_Opt_twoTurrets;
	// Properties - States
	private ifSVchassis chassis;
	private stSVChassisAirshipSmall airshipSmall;
	private stSVChassisAirshipMedium airshipMedium;
	private stSVChassisAirshipLarge airshipLarge;
	private stSVChassisFixedWingSmall fixedWingSmall;
	private stSVChassisFixedWingMedium fixedWingMedium;
	private stSVChassisFixedWingLarge fixedWingLarge;
	private stSVChassisHoverSmall hoverSmall;
	private stSVChassisHoverMedium hoverMedium;
	private stSVChassisHoverLarge hoverLarge;
	private stSVChassisNavalSmall navalSmall;
	private stSVChassisNavalMedium navalMedium;
	private stSVChassisNavalLarge navalLarge;
	private stSVChassisRailSmall railSmall;
	private stSVChassisRailMedium railMedium;
	private stSVChassisRailLarge railLarge;
	private stSVChassisSatelliteSmall satelliteSmall;
	private stSVChassisSatelliteMedium satelliteMedium;
	private stSVChassisSatelliteLarge satelliteLarge;
	private stSVChassisTrackedSmall trackedSmall;
	private stSVChassisTrackedMedium trackedMedium;
	private stSVChassisTrackedLarge trackedLarge;
	private stSVChassisVTOLSmall vtolSmall;
	private stSVChassisVTOLMedium vtolMedium;
	private stSVChassisVTOLLarge vtolLarge;
	private stSVChassisWheeledSmall wheeledSmall;
	private stSVChassisWheeledMedium wheeledMedium;
	private stSVChassisWheeledLarge wheeledLarge;
	private stSVChassisWiGESmall wigeSmall;
	private stSVChassisWiGEMedium wigeMedium;
	private stSVChassisWiGELarge wigeLarge;
	private ifSVengine engine;
	private stSVEngineElectricBattery engineElectricBattery;
	private stSVEngineElectricFuelCell engineElectricFuelCell;
	private stSVEngineElectricSolar engineElectricSolar;
	private stSVEngineFission engineFission;
	private stSVEngineFusion engineFusion;
	private stSVEngineICE engineICE;
	private stSVEngineMagLev engineMagLev;
	private stSVEngineSteam engineSteam;
	// Properties - Chassis Mods
	// Note that these will be reset by methods that trigger if Type is changed.
	private String isAmphibious,
				   isArmored,
				   isBicycle,
				   isConvertible,
				   isDuneBuggy,
				   isEnvironmentallySealed,
				   isExternalPowerPickup,
				   isHydrofoil,
				   isMonocycle,
				   isOffRoad,
				   isOmni,
				   isProp,
				   isSnowmobile,
				   isSTOL,
				   isSubmersible,
				   isTractorGround,
				   isTractorNaval,
				   isTrailer,
				   isUltraLight,
				   isVSTOL;
	// Properties - Preferences
	private Preferences pref;

	// CONSTRUCTORS
	public SupportVehicle() {
		// Load preferences.
		this.pref = Preferences.userRoot().node( Constants.SSVPrefs );
		// Turn off all advanced construction options.
		this.TO_Opt_twoTurrets = false;
		// Set all chassis modifications false.
		this.isAmphibious = "false";
		this.isArmored = "false";
		this.isBicycle = "false";
		this.isConvertible = "false";
		this.isDuneBuggy = "false";
		this.isEnvironmentallySealed = "false";
		this.isExternalPowerPickup = "false";
		this.isHydrofoil = "false";
		this.isMonocycle = "false";
		this.isOffRoad = "false";
		this.isOmni = "false";
		this.isProp = "false";
		this.isSnowmobile = "false";
		this.isSTOL = "false";
		this.isSubmersible = "false";
		this.isTractorGround = "false";
		this.isTractorNaval = "false";
		this.isTrailer = "false";
		this.isUltraLight = "false";
		this.isVSTOL = "false";
		// Initalized states.
		this.airshipSmall = new stSVChassisAirshipSmall();
		this.airshipMedium = new stSVChassisAirshipMedium();
		this.airshipLarge = new stSVChassisAirshipLarge();
		this.fixedWingSmall = new stSVChassisFixedWingSmall();
		this.fixedWingMedium = new stSVChassisFixedWingMedium();
		this.fixedWingLarge = new stSVChassisFixedWingLarge();
		this.hoverSmall = new stSVChassisHoverSmall();
		this.hoverMedium = new stSVChassisHoverMedium();
		this.hoverLarge = new stSVChassisHoverLarge();
		this.navalSmall = new stSVChassisNavalSmall();
		this.navalMedium = new stSVChassisNavalMedium();
		this.navalLarge = new stSVChassisNavalLarge();
		this.railSmall = new stSVChassisRailSmall();
		this.railMedium = new stSVChassisRailMedium();
		this.railLarge = new stSVChassisRailLarge();
		this.satelliteSmall = new stSVChassisSatelliteSmall();
		this.satelliteMedium = new stSVChassisSatelliteMedium();
		this.satelliteLarge = new stSVChassisSatelliteLarge();
		this.trackedSmall = new stSVChassisTrackedSmall();
		this.trackedMedium = new stSVChassisTrackedMedium();
		this.trackedLarge = new stSVChassisTrackedLarge();
		this.wheeledSmall = new stSVChassisWheeledSmall();
		this.wheeledMedium = new stSVChassisWheeledMedium();
		this.wheeledLarge = new stSVChassisWheeledLarge();
		this.wigeSmall = new stSVChassisWiGESmall();
		this.wigeMedium = new stSVChassisWiGEMedium();
		this.wigeLarge = new stSVChassisWiGELarge();
		this.engineElectricBattery = new stSVEngineElectricBattery();
		this.engineElectricFuelCell = new stSVEngineElectricFuelCell();
		this.engineElectricSolar = new stSVEngineElectricSolar();
		this.engineFission = new stSVEngineFission();
		this.engineFusion = new stSVEngineFusion();
		this.engineICE = new stSVEngineICE();
		this.engineMagLev = new stSVEngineMagLev();
		this.engineSteam = new stSVEngineSteam();
		// Set the chassis for a TR D 20 ton medium wheeled vehicle.
		this.techRating = "D";
		this.Tonnage = 20;
		this.size = "Medium";
		this.setMotiveType("Wheeled");
	}

	// METHODS
	// Methods - General Accessors

	public String getName() {
		String nameTemp = this.Name;
		return nameTemp;
	}

	public String getModel() {
		String modelTemp = this.Model;
		return modelTemp;
	}

	public String getOverview() {
		String overviewTemp = this.Overview;
		return overviewTemp;
	}

	public String getCapabilites() {
		String capabilitiesTemp = this.Capabilities;
		return capabilitiesTemp;
	}

	public String getHistory() {
		String historyTemp = this.History;
		return historyTemp;
	}

	public String getVariants() {
		String variantsTemp = this.Variants;
		return variantsTemp;
	}

	public String getDeployment() {
		String deploymentTemp = this.Deployment;
		return deploymentTemp;
	}

	public String getCompany() {
		String companyTemp = this.Company;
		return companyTemp;
	}

	public String getAdditional() {
		String additionalTemp = this.Additional;
		return additionalTemp;
	}

	public String getNotables() {
		String notablesTemp = this.Notables;
		return notablesTemp;
	}

	public String getArmorModel() {
		String armorModelTemp = this.ArmorModel;
		return armorModelTemp;
	}

	public String getLocations() {
		String locationsTemp = this.Location;
		return locationsTemp;
	}

	public String getEngineManufacturer() {
		String engineManTemp = this.EngineManufacturer;
		return engineManTemp;
	}

	public String getCommSystem() {
		String commTemp = this.CommSystem;
		return commTemp;
	}

	public String getChassisModel() {
		String chassisModelTemp = this.ChassisModel;
		return chassisModelTemp;
	}

	public String getSolaris7ID() {
		String solarisIDTemp = this.Solaris7ID;
		return solarisIDTemp;
	}

	public String getSolaris7ImageID() {
		String solarisImageTemp = this.Solaris7ImageID;
		return solarisImageTemp;
	}

	public String getTandTSystem() {
		String targetTemp = this.TandTSystem;
		return targetTemp;
	}

	public String getSSWImage() {
		String sswImageTemp = this.SSWImage;
		return sswImageTemp;
	}

	public int getEra() {
		int eraTemp = this.Era;
		return eraTemp;
	}

	public int getYear() {
		int yearTemp = this.Year;
		return yearTemp;
	}

	public int getRulesLevel() {
		int rulesTemp = this.RulesLevel;
		return rulesTemp;
	}

	public double getTonnage() {
		double tonnageTemp = this.Tonnage;
		return tonnageTemp;
	}

	// Methods - SV Specific Accessors

	public String getMotiveType() {
		String motiveTemp = this.motiveType;
		return motiveTemp;
	}

	public String getSize() {
		String sizeTemp = this.size;
		return sizeTemp;
	}

	public String getTechRating() {
		String trTemp = this.techRating;
		return trTemp;
	}

	public double getBCV() {
		double bcvTemp = this.baseChassisValue;
		return bcvTemp;
	}

	public double getBEV() {
		double bevTemp = this.baseEngineValue;
		return bevTemp;
	}

	public double getMinimumTonnage() {
		double minTemp = this.chassis.getMinimumTonnage();
		return minTemp;
	}

	public Preferences getPref() {
		Preferences prefTemp = this.pref;
		return prefTemp;
	}

	/**
	 * Calculates the structural mass required for the support vehicle.
	 * @return A double containing the structural mass required.
	 */
	public double getStructuralTonnage() {
		double structureTonnage = this.Tonnage;
		structureTonnage = structureTonnage * this.getChassisModMultiplier();
		structureTonnage = structureTonnage * this.getTechRatingMultipler();
		structureTonnage = structureTonnage * this.chassis.getBCV();
		return structureTonnage;
	}

	/**
	 * Calculates the total structural mass multiplier for the chassis mods.
	 * @return A double containing the structural mass multipler for the chassis mods.
	 */
	private double getChassisModMultiplier() {
		double multiple = 1;
		if (this.isAmphibious.equals("true")) { multiple = multiple * 1.75; }
		if (this.isArmored.equals("true")) { multiple = multiple * 1.5; }
		if (this.isBicycle.equals("true")) { multiple = multiple * 0.75; }
		if (this.isConvertible.equals("true")) { multiple = multiple * 1.1; }
		if (this.isDuneBuggy.equals("true")) { multiple = multiple * 1.5; }
		if (this.isEnvironmentallySealed.equals("true")) { multiple = multiple * 2; }
		if (this.isExternalPowerPickup.equals("true")) { multiple = multiple * 1.1; }
		if (this.isHydrofoil.equals("true")) { multiple = multiple * 1.7; }
		if (this.isMonocycle.equals("true")) { multiple = multiple * 0.5; }
		if (this.isOffRoad.equals("true")) { multiple = multiple * 1.5; }
		if (this.isProp.equals("true")) { multiple = multiple * 1.2; }
		if (this.isSnowmobile.equals("true")) { multiple = multiple * 1.75; }
		if (this.isSTOL.equals("true")) { multiple = multiple * 1.5; }
		if (this.isSubmersible.equals("true")) { multiple = multiple * 1.8; }
		if (this.isTractorGround.equals("true")) { multiple = multiple * 1.2; }
		if (this.isTractorNaval.equals("true")) { multiple = multiple * 1.2; }
		if (this.isTrailer.equals("true")) { multiple = multiple * 0.8; }
		if (this.isUltraLight.equals("true")) { multiple = multiple * 0.5; }
		if (this.isVSTOL.equals("true")) { multiple = multiple * 2; }
		return multiple;
	}

	/**
	 * Returns the structural mass multiplier for the current tech rating.
	 * @return A double containing the structural mass multplier for the current tech rating.
	 */
	private double getTechRatingMultipler() {
		double multiple = 1;
		if (this.techRating.equals("A")) { multiple = 1.6; }
		else if (this.techRating.equals("B")) { multiple = 1.3; }
		else if (this.techRating.equals("C")) { multiple = 1.15; }
		else if (this.techRating.equals("D")) { multiple = 1; }
		else if (this.techRating.equals("E")) { multiple = 0.85; }
		else if (this.techRating.equals("F")) { multiple = 0.66; }
		return multiple;
	}

	public int getBaseMove() {
		return this.baseMove;
	}

	/**
	 * Gets the maximum move (flank MP, max accel, whatever).
	 * @return An integer representing the maximum move.
	 */
	public int getMaxMove() {
		double maxMove = this.baseMove * 1.5;
		maxMove = Math.ceil(maxMove);
		return (int) maxMove;
	}

	// Methods - SV Specific Mutators

	public void setTechRating(String newRating) {
		this.techRating = newRating;
	}

	/**
	 * Sets motive type and calls appropriate helper methods to manage chassis modifications
	 * and engine types.
	 * @param String newType: The motive type to set.
	 */
	public void setMotiveType(String newType) {
		if (newType.equals("Airship")) {
			this.cmAirship();
			this.cmSmallCheck();
		}
		else if (newType.equals("Fixed-Wing")) {
			this.cmFixedWing();
			this.cmSmallCheck();
		}
		else if (newType.equals("Hover")) {
			this.cmHover();
			this.cmSmallCheck();
		}
		else if (newType.equals("Naval")) {
			this.cmNaval();
			this.cmSmallCheck();
		}
		else if (newType.equals("Rail")) {
			this.cmRail();
			this.cmSmallCheck();
		}
		else if (newType.equals("Satellite")) {
			this.cmSatellite();
			this.cmSmallCheck();
		}
		else if (newType.equals("Tracked")) {
			this.cmTracked();
			this.cmSmallCheck();
		}
		else if (newType.equals("VTOL")) {
			this.cmVTOL();
			this.cmSmallCheck();
		}
		else if (newType.equals("Wheeled")) {
			this.cmWheeled();
			this.cmSmallCheck();
		}
		else if (newType.equals("WiGE")) {
			this.cmWiGE();
			this.cmSmallCheck();
		}
		this.motiveType = newType;
		this.setChassis();
		this.setLocations();
	}

	public void setSize(String newSize) {
		this.cmSmallCheck();
		this.size = newSize;
		this.setChassis();
		this.setLocations();
	}

	public void setTonnage(double newTonnage) {
		if (newTonnage >= this.chassis.getMinimumTonnage()) {
			if (newTonnage <= this.chassis.getMaximumTonnage()) {
				this.Tonnage = newTonnage;
			}
		}
		this.cmSmallCheck();
		this.setLocations();
	}

	/**
	 * Handles the chassis state.
	 */
	private void setChassis() {
		if (this.motiveType.equals("Airship")) {
			if (this.size.equals("Small")) { this.chassis = this.airshipSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.airshipMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.airshipLarge; }
		}
		else if (this.motiveType.equals("Fixed-Wing")) {
			if (this.size.equals("Small")) { this.chassis = this.fixedWingSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.fixedWingMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.fixedWingLarge; }
		}
		else if (this.motiveType.equals("Hover")) {
			if (this.size.equals("Small")) { this.chassis = this.hoverSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.hoverMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.hoverLarge; }
		}
		else if (this.motiveType.equals("Naval")) {
			if (this.size.equals("Small")) { this.chassis = this.navalSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.navalMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.navalLarge; }
		}
		else if (this.motiveType.equals("Rail")) {
			if (this.size.equals("Small")) { this.chassis = this.railSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.railMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.railLarge; }
		}
		else if (this.motiveType.equals("Satellite")) {
			if (this.size.equals("Small")) { this.chassis = this.satelliteSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.satelliteMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.satelliteLarge; }
		}
		else if (this.motiveType.equals("Tracked")) {
			if (this.size.equals("Small")) { this.chassis = this.trackedSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.trackedMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.trackedLarge; }
		}
		else if (this.motiveType.equals("VTOL")) {
			if (this.size.equals("Small")) { this.chassis = this.vtolSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.vtolMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.vtolLarge; }
		}
		else if (this.motiveType.equals("Wheeled")) {
			if (this.size.equals("Small")) { this.chassis = this.wheeledSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.wheeledMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.wheeledLarge; }
		}
		else if (this.motiveType.equals("WiGE")) {
			if (this.size.equals("Small")) { this.chassis = this.wigeSmall; }
			else if (this.size.equals("Medium")) { this.chassis = this.wigeMedium; }
			else if (this.size.equals("Large")) { this.chassis = this.wigeLarge; }
		}
	}

	// Methods - Chassis
	// Methods - Chassis - Chassis Mods Accessors

	public String getAmphibous() {
		String amphibiousTemp = this.isAmphibious;
		return amphibiousTemp;
	}

	public String getArmored() {
		String armoredTemp = this.isArmored;
		return armoredTemp;
	}

	public String getBicycle() {
		String bicycleTemp = this.isBicycle;
		return bicycleTemp;
	}

	public String getConvertible() {
		String convertibleTemp = this.isConvertible;
		return convertibleTemp;
	}

	public String getDuneBuggy() {
		String duneBuggyTemp = this.isDuneBuggy;
		return duneBuggyTemp;
	}

	public String getEnviroSeal() {
		String enviroSealTemp = this.isEnvironmentallySealed;
		return enviroSealTemp;
	}

	public String getExternalPower() {
		String externalPowerTemp = this.isExternalPowerPickup;
		return externalPowerTemp;
	}

	public String getHydrofoil() {
		String hydrofoilTemp = this.isHydrofoil;
		return hydrofoilTemp;
	}

	public String getMonocycle() {
		String monocycleTemp = this.isMonocycle;
		return monocycleTemp;
	}

	public String getOffRoad() {
		String offRoadTemp = this.isOffRoad;
		return offRoadTemp;
	}

	public String getOmni() {
		String omniTemp = this.isOmni;
		return omniTemp;
	}

	public String getProp() {
		String propTemp = this.isProp;
		return propTemp;
	}

	public String getSnowmobile() {
		String snowmobileTemp = this.isSnowmobile;
		return snowmobileTemp;
	}

	public String getSTOL() {
		String stolTemp = this.isSTOL;
		return stolTemp;
	}

	public String getSubmersible() {
		String subTemp = this.isSubmersible;
		return subTemp;
	}

	public String getTractorGround() {
		String tractorGTemp = this.isTractorGround;
		return tractorGTemp;
	}

	public String getTractorNaval() {
		String tractorNTemp = this.isTractorNaval;
		return tractorNTemp;
	}

	public String getTrailer() {
		String trailerTemp = this.isTrailer;
		return trailerTemp;
	}

	public String getUltraLight() {
		String ultraLightTemp = this.isUltraLight;
		return ultraLightTemp;
	}

	public String getVSTOL() {
		String vstolTemp = this.isVSTOL;
		return vstolTemp;
	}

	// Methods - Chassis - Chassis Mods Controls
	// These methods control the chassis mods when motive type and/or size are
	// changed.  Do NOT edit them without checking TM and TacOps unless you
	// want to break something.

	private void cmAirship() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		this.isArmored = "no";
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmFixedWing() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		if (this.isProp.equals("no")) { this.isProp = "false"; }
		this.isSnowmobile = "no";
		if (this.isSTOL.equals("no")) { this.isSTOL = "false"; }
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		if (this.isVSTOL.equals("no")) { this.isVSTOL = "false"; }
	}

	private void cmHover() {
		this.isAmphibious = "no";
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		if (this.isBicycle.equals("no")) { this.isBicycle = "false"; }
		if (this.isConvertible.equals("no")) { this.isConvertible = "false"; }
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		if (this.isMonocycle.equals("no")) { this.isMonocycle = "false"; }
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmNaval() {
		this.isAmphibious = "no";
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		if (this.isHydrofoil.equals("no")) { this.isHydrofoil = "false"; }
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		if (this.isSubmersible.equals("no")) { this.isSubmersible = "false"; }
		this.isTractorGround = "no";
		if (this.isTractorNaval.equals("no")) { this.isTractorNaval = "false"; }
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmRail() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		if (this.isExternalPowerPickup.equals("no")) { this.isExternalPowerPickup = "false"; }
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		if (this.isSubmersible.equals("no")) { this.isSubmersible = "false"; }
		if (this.isTractorGround.equals("no")) { this.isTractorGround = "false"; }
		this.isTractorNaval = "no";
		if (this.isTrailer.equals("no")) { this.isTrailer = "false"; }
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmSatellite() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmTracked() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		if (this.isConvertible.equals("no")) { this.isConvertible = "false"; }
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		if (this.isSnowmobile.equals("no")) { this.isSnowmobile = "false"; }
		this.isSTOL = "no";
		this.isSubmersible = "no";
		if (this.isTractorGround.equals("no")) { this.isTractorGround = "false"; }
		this.isTractorNaval = "no";
		if (this.isTrailer.equals("no")) { this.isTrailer = "false"; }
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmVTOL() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmWheeled() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		if (this.isBicycle.equals("no")) { this.isBicycle = "false"; }
		if (this.isConvertible.equals("no")) { this.isConvertible = "false"; }
		if (this.isDuneBuggy.equals("no")) { this.isDuneBuggy = "false"; }
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		if (this.isMonocycle.equals("no")) { this.isMonocycle = "false"; }
		if (this.isOffRoad.equals("no")) {this.isOffRoad = "false"; }
		this.isProp = "no";
		if (this.isSnowmobile.equals("no")) { this.isSnowmobile = "false"; }
		this.isSTOL = "no";
		this.isSubmersible = "no";
		if (this.isTractorGround.equals("no")) { this.isTractorGround = "false"; }
		this.isTractorNaval = "no";
		if (this.isTrailer.equals("no")) { this.isTrailer = "false"; }
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmWiGE() {
		if (this.isAmphibious.equals("no")) { this.isAmphibious = "false"; }
		if (this.isArmored.equals("no")) { this.isArmored = "false"; }
		this.isBicycle = "no";
		this.isConvertible = "no";
		this.isDuneBuggy = "no";
		this.isExternalPowerPickup = "no";
		this.isHydrofoil = "no";
		this.isMonocycle = "no";
		this.isOffRoad = "no";
		this.isProp = "no";
		this.isSnowmobile = "no";
		this.isSTOL = "no";
		this.isSubmersible = "no";
		this.isTractorGround = "no";
		this.isTractorNaval = "no";
		this.isTrailer = "no";
		if (this.isUltraLight.equals("no")) { this.isUltraLight = "false"; }
		this.isVSTOL = "no";
	}

	private void cmSmallCheck() {
		if (this.size.equals("Small") == false) {
			this.isUltraLight = "no";
			this.isBicycle = "no";
			this.isMonocycle = "no";
			if (this.Tonnage > 100) {
				this.isHydrofoil = "no";
			}
		}
	}

	// Methods - Chassis - Locations
	/// Note that this method needs to be updated once armor handling is added.
	/// As written, it currently discards any existing armor if called.
	/// It's also not designed to deal with large naval vessels' turrets.
	private void setLocations() {
		int vitals = (int) Math.ceil(this.Tonnage / 10);
		int armor = 0;
		LinkedList<String> newPlaces = this.chassis.getLocations();
		LinkedList<stSVLocation> newLocations = new LinkedList<stSVLocation>();
		int length = newPlaces.size();
		if (this.size.equals("Large") == false && this.motiveType.equals("Naval") == false) {
			if (this.motiveType.equals("Hover") || this.motiveType.equals("Naval") || this.motiveType.equals("Rail")
				|| this.motiveType.equals("Tracked") || this.motiveType.equals("Wheeled") || this.motiveType.equals("WiGE")) {
				if (this.TO_Opt_twoTurrets == true) {
					for (int i = 0; i < length; i++) {
						newLocations.add(new stSVLocation(newPlaces.get(i), vitals, armor));
					}
				}
				else {
					length = length - 2;
					for (int i = 0; i < length; i++) {
						newLocations.add(new stSVLocation(newPlaces.get(i), vitals, armor));
					}
					newLocations.add(new stSVLocation("Turret", vitals, armor));
				}
			}
			else {
				for (int i = 0; i < length; i++) {
					newLocations.add(new stSVLocation(newPlaces.get(i), vitals, armor));
				}
			}
		}
		else {
			for (int i = 0; i < length; i++) {
				newLocations.add(new stSVLocation(newPlaces.get(i), vitals, armor));
			}
		}
	}

	// Methods - Engine
	/**
	 * Checks to see if an engine type is valid.
         * This is used to build the displayed list of engine types in the BFB.GUI.
	 * @param type String containing the engine type to check.
	 * @throws Exception Generic exception representing an invalid engine type.
	 */
	public boolean checkEngineType(String type) throws Exception {
		if (type.equals("Electric Battery")) {
			LinkedList<String> validTypes = this.engineElectricBattery.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("Electric Fuel Cell")) {
			LinkedList<String> validTypes = this.engineElectricFuelCell.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("Electric Solar")) {
			LinkedList<String> validTypes = this.engineElectricSolar.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("Fission")) {
			LinkedList<String> validTypes = this.engineFission.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("Fusion")) {
			LinkedList<String> validTypes = this.engineFusion.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("ICE")) {
			LinkedList<String> validTypes = this.engineICE.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("MagLev")) {
			LinkedList<String> validTypes = this.engineMagLev.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else if (type.equals("Steam")) {
			LinkedList<String> validTypes = this.engineSteam.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			return valid;
		}
		else {
			throw new Exception("Unknown engine type");
		}
	}


	/**
	 * Sets the engine type, if that engine type is valid.
         * Used to actually process input about the engine type.
	 * @param type String containing the engine type to use.
	 * @throws Exception Throws an exception if the engine type is invalid.
	 */
	public void setEngine(String type) throws Exception 	{
		if (type.equals("Electric Battery")) {
			LinkedList<String> validTypes = this.engineElectricBattery.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineElectricBattery;
			}
		}
		else if (type.equals("Electric Fuel Cell")) {
			LinkedList<String> validTypes = this.engineElectricFuelCell.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineElectricFuelCell;
			}
		}
		else if (type.equals("Electric Solar")) {
			LinkedList<String> validTypes = this.engineElectricSolar.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineElectricSolar;
			}
		}
		else if (type.equals("Fission")) {
			LinkedList<String> validTypes = this.engineFission.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineFission;
			}
		}
		else if (type.equals("Fusion")) {
			LinkedList<String> validTypes = this.engineFusion.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineFusion;
			}
		}
		else if (type.equals("ICE")) {
			LinkedList<String> validTypes = this.engineICE.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineICE;
			}
		}
		else if (type.equals("MagLev")) {
			LinkedList<String> validTypes = this.engineMagLev.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineMagLev;
			}
		}
		else if (type.equals("Steam")) {
			LinkedList<String> validTypes = this.engineSteam.getValidTypes();
			boolean valid = false;
			for (int i = 0; i < validTypes.size(); i++) {
				if (this.motiveType.equals(validTypes.get(i))) {
					valid = true;
				}
			}
			if (valid == true) {
				this.engine = this.engineSteam;
			}
		}
		else {
			throw new Exception("Invalid engine type");
		}
	}

	public void setCruiseMP(int cruise) {
		this.cruiseSpeed = cruise;
	}

	public int getCruiseMP() {
		return this.cruiseSpeed;
	}

	public int getFlankMP() {
		int flank = (int) Math.ceil(this.cruiseSpeed);
		return flank;
	}

	public double getEngineTonnage() {
            this.setEngineTonnage();
		double tonnage = this.Tonnage;
		return tonnage;
	}

        private void setEngineTonnage() {
            int motiveFactor = cruiseSpeed * cruiseSpeed + 4;
            double engineMultiplier = 0;
            if (this.techRating.equals("A")) { engineMultiplier = this.engine.getMultipliers().get(0); }
            if (this.techRating.equals("B")) { engineMultiplier = this.engine.getMultipliers().get(1); }
            if (this.techRating.equals("C")) { engineMultiplier = this.engine.getMultipliers().get(2); }
            if (this.techRating.equals("D")) { engineMultiplier = this.engine.getMultipliers().get(3); }
            if (this.techRating.equals("E")) { engineMultiplier = this.engine.getMultipliers().get(4); }
            if (this.techRating.equals("F")) { engineMultiplier = this.engine.getMultipliers().get(5); }
            this.Tonnage = motiveFactor * engineMultiplier * this.baseEngineValue;
        }

	// Methods - Other
	public void Visit( ifVisitor v ) throws Exception {
        v.Visit( this );
    }
}
