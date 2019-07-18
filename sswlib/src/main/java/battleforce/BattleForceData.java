package battleforce;

import java.util.ArrayList;

/*
 * This class is used to setup basic data for BattleForce calculations.  Allows 
 * for weapon information to be added and will do the work of totalling up the 
 * amounts and ammunition and determining the final value
 */
public class BattleForceData {
    public DataSet  Base = new DataSet(false);
    public DataSet  AdjBase = new DataSet(false);
    public DataSet  AC = new DataSet(true);
    public DataSet  LRM = new DataSet(true);
    public DataSet  SRM = new DataSet(true);
    public DataSet  TOR = new DataSet(true);
    public DataSet  TUR = new DataSet(true);
    public DataSet  IF = new DataSet(false);
    public DataSet  FLK = new DataSet(false);
    private int TotalHeatGenerated = 0;
    private int TotalHeatDissipation = 0;
    private ArrayList<String> Notes = new ArrayList<String>();

    public BattleForceData() {
        IF.setNormalRound(true);
        FLK.setNormalRound(true);
    }

    /**
     * Set the total heat dissipation for all special damage situations
     * 
     * @param TotalDiss 
     */
    public void SetHeat( int TotalDiss ) {
        this.TotalHeatDissipation = TotalDiss;
        Base.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        AC.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        LRM.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        SRM.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        TOR.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        TUR.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        IF.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        FLK.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        AdjBase.SetHeat(TotalHeatGenerated, TotalHeatDissipation);

        //System.out.println("Heat Set " + TotalHeatGenerated + " [" + TotalHeatDissipation + "]");
    }

    /**
     * Sets the heat levels for calculations
     * 
     * @param TotalHeat Total heat generated when firing everything and moving as fast as possible
     * @param TotalDiss  Total heat that can be removed in a single turn
     */
    public void SetHeat( int TotalHeat, int TotalDiss ) {
        this.TotalHeatGenerated = TotalHeat;
        SetHeat(TotalDiss);
    }
        
    public int BaseMaxShort() {
        return Base.BattleForceValue(Base.baseShort); //+
                //Base.BattleForceValue(AC.baseShort) +
                //Base.BattleForceValue(SRM.baseShort) +
                //Base.BattleForceValue(LRM.baseShort);
    }

    public int BaseMaxMedium() {
        //System.out.println(AdjBase.BattleForceValue(AdjBase.baseMedium) + "/" + AC.BattleForceValue(AC.baseMedium) + "/" + SRM.BattleForceValue(SRM.baseMedium) + "/" + LRM.BattleForceValue(LRM.baseMedium));
        return Base.BattleForceValue(Base.baseMedium); //+
                //Base.BattleForceValue(AC.baseMedium) +
                //Base.BattleForceValue(SRM.baseMedium) +
                //Base.BattleForceValue(LRM.baseMedium);
    }

    public int BaseMaxLong() {
        //System.out.println(AdjBase.BattleForceValue(AdjBase.baseMedium) + "/" + AC.BattleForceValue(AC.baseMedium) + "/" + SRM.BattleForceValue(SRM.baseMedium) + "/" + LRM.BattleForceValue(LRM.baseMedium));
        return Base.BattleForceValue(Base.baseLong); //+
                //Base.BattleForceValue(AC.baseMedium) +
                //Base.BattleForceValue(SRM.baseMedium) +
                //Base.BattleForceValue(LRM.baseMedium);
    }

    public int AdjustedMaxMedium() {
        return (int) AdjBase.heatMedium + (int) AC.heatMedium + (int) SRM.heatMedium + (int) LRM.heatMedium;
    }

    /**
     * Runs adjustments on the various potential special damage situations
     */
    public void CheckSpecials() {
        AdjBase.baseShort = Base.baseShort;
        AdjBase.baseMedium = Base.baseMedium;
        AdjBase.baseLong = Base.baseLong;
        AdjBase.baseExtreme = Base.baseExtreme;

//        Commented out 7/17/2013 because errata changes make it so that base damage
//        does not get changed
//        if ( AC.CheckSpecial() ) Adjust(AC);
//        if ( SRM.CheckSpecial() ) Adjust(SRM);
//        if ( LRM.CheckSpecial() ) Adjust(LRM);
//        if ( TOR.CheckSpecial() ) Adjust(TOR);
        
        AdjBase.SetHeat(TotalHeatGenerated, TotalHeatDissipation);
        AdjBase.BattleForceValues();
    }

    public void Adjust( DataSet special ) {
        AdjBase.baseShort -= special.baseShort;
        AdjBase.baseMedium -= special.baseMedium;
        AdjBase.baseLong -= special.baseLong;
        AdjBase.baseExtreme -= special.baseExtreme;
    }

    /**
     * Add weapon data to the base calculations
     * 
     * @param vals double[] with calculated values for each range
     */
    public void AddBase( double[] vals ) {
        Base.AddBase(vals);
        TotalHeatGenerated += (int)vals[BFConstants.BF_OV];
    }

    /**
     * Add special heat (like Stealth Armor)
     * 
     * @param Heat Amount of heat to add
     */
    public void AddHeat( int Heat ) {
        this.TotalHeatGenerated += Heat;
    }

    /**
     * Collection of notes generated during the calculation process
     * 
     * @param note Note to add to the list
     */
    public void AddNote( String note ) {
        Notes.add(note);
    }

    @Override
    public String toString() {
        String data = "";
        for ( String note : Notes ) {
            data += note + "\n";
        }
        data += "\n";
        data += "Base\n" + Base.toString();
        data += "AC\n" + AC.toString();
        data += "LRM\n" + LRM.toString();
        data += "SRM\n" + SRM.toString();
        data += "TOR\n" + TOR.toString();
        data += "TUR\n" + TUR.toString();
        data += "IF\n" + IF.toString();
        data += "FLK\n" + FLK.toString();
        data += "Heat: Dissipation (" + TotalHeatDissipation + ") < Max (" + TotalHeatGenerated + ") [Max-4]";
        data += "\nAdjusted\n" + AdjBase.toString();
        return data;
    }

    /**
     * @return the TotalHeatGenerated
     */
    public int getTotalHeatGenerated() {
        return TotalHeatGenerated;
    }

    /**
     * @return the TotalHeatDissipation
     */
    public int getTotalHeatDissipation() {
        return TotalHeatDissipation;
    }
    
    public class DataSet {
        private double baseShort = 0.0;
        private double baseMedium = 0.0;
        private double baseLong = 0.0;
        private double baseExtreme = 0.0;
        private double heatShort = 0.0;
        private double heatMedium = 0.0;
        private double heatLong = 0.0;
        private double heatExtreme = 0.0;
        private int BFBaseShort = 0;
        private int BFBaseMedium = 0;
        private int BFBaseLong = 0;
        private int BFBaseExtreme = 0;
        private int BFShort = 0;
        private int BFMedium = 0;
        private int BFLong = 0;
        private int BFExtreme = 0;
        private int TotalHeatGenerated = 0;
        private int TotalHeatDissipation = 0;
        private int LauncherCount = 0;
        private int AmmoCount = 0;
        private boolean hasOverheat = false,
                        isSpecial = false,
                        SpecialDamage = false,
                        useNormalRound = false,
                        NotEnoughAmmo = false;

        public DataSet() {
            this(false);
        }

        /**
         * DateSet holds all the information about the weapons for this specific
         * item
         * 
         * @param isSpecial Is this dataset used for a Special Ability
         */
        public DataSet( boolean isSpecial ) {
            this.isSpecial = isSpecial;
        }

        /**
         * Determine if there is enough ammo for the launchers
         * 
         * @return True if enough, false if not.
         */
        public boolean EnoughAmmo() {
            if ( LauncherCount > 0 ) {
                if ( AmmoCount - (LauncherCount * 10) < 0 ) {
                    NotEnoughAmmo = true;
                    return false;
                }
            }
            NotEnoughAmmo = false;
            return true;
        }
        
        /**
         * Determines if the unit has special damage or not
         * @return True if using special damage
         */
        public boolean CheckSpecial() {
            boolean retval = false;
            if ( TotalHeatDissipation < TotalHeatGenerated ) {
                hasOverheat = true;
                if ( heatMedium > 9.0 ) retval = true;
            } else {
                if ( baseMedium > 9.0 ) retval = true;
            }
            //if ( !EnoughAmmo() )
            //    retval = false;
            
            SpecialDamage = retval;
            BattleForceValues();
            return retval;
        }

        /**
         * Clear the base values
         */
        public void Clear() {
            baseShort = 0;
            baseMedium = 0;
            baseLong = 0;
        }

        /**
         * Returns a / delimited string of values for the ranges
         * 
         * @return ie. 0/1/1
         */
        public String GetAbility() {
            return BFShort + "/" + BFMedium + "/" + BFLong; //+ "/" + BFExtreme;
        }

        public void AddBase( double[] vals ) {
            this.baseShort += vals[BFConstants.BF_SHORT];
            this.baseMedium += vals[BFConstants.BF_MEDIUM];
            this.baseLong += vals[BFConstants.BF_LONG];
            this.baseExtreme += vals[BFConstants.BF_EXTREME];
            this.TotalHeatGenerated += (int)vals[BFConstants.BF_OV];
        }

//        /**
//         * Add amount of ammo to this specific type
//         * 
//         * @param lotsize Amount of ammo to add
//         */
//        public void AddAmmo( int lotsize ) {
//            this.AmmoCount += lotsize;
//        }
//
//        /*
//         * Used to increase the count of launchers that need ammunition
//         */
//        public void AddLauncher() {
//            LauncherCount++;
//        }
        
        public void SetHeat( int TotalHeatGenerated, int TotalHeatDissipation ) {
            this.TotalHeatGenerated = TotalHeatGenerated;
            this.TotalHeatDissipation = TotalHeatDissipation;
            if ( TotalHeatDissipation < TotalHeatGenerated ) hasOverheat = true;
            HeatAdjustments();
            BattleForceValues();
        }

        public double HeatAdjustment( double base ) {
            //if (!EnoughAmmo()) { base *= .75; }
            if ( TotalHeatGenerated > 0 && TotalHeatDissipation > 0 && base > 0 ) {
                return Math.ceil((base * TotalHeatDissipation) / TotalHeatGenerated);
            }
            return 0.0;
        }

        private void HeatAdjustments() {
            this.heatShort = HeatAdjustment( baseShort );
            this.heatMedium = HeatAdjustment( baseMedium );
            this.heatLong = HeatAdjustment( baseLong );
            this.heatExtreme = HeatAdjustment( baseExtreme );
        }

        public int BattleForceValue( double base ) {
            //if (!EnoughAmmo()) { base *= .75; }  This is being done in the per weapon damage check
            if ( base > 9.0 || SpecialDamage || !isSpecial )
                if ( isSpecial || useNormalRound)
                    return (int) Math.round(base / 10);
                else
                    return (int) Math.ceil(base / 10);
            return 0;
        }

        private void BattleForceValues() {
            if ( hasOverheat ) {
                this.BFShort = BattleForceValue( heatShort );
                this.BFMedium = BattleForceValue( heatMedium );
                this.BFLong = BattleForceValue( heatLong );
                this.BFExtreme = BattleForceValue( heatExtreme );
            } else {
                this.BFShort = BattleForceValue( baseShort );
                this.BFMedium = BattleForceValue( baseMedium );
                this.BFLong = BattleForceValue( baseLong );
                this.BFExtreme = BattleForceValue( baseExtreme );
            }
        }

        @Override
        public String toString() {
            String data = "";
            data += " Base: " + String.format( "%1$,.2f", baseShort ) + "/" + String.format( "%1$,.2f", baseMedium ) + "/" + String.format( "%1$,.2f", baseLong ) + "/" + String.format( "%1$,.2f", baseExtreme ) + "\n";
            if ( hasOverheat) data += " Heat: " + String.format( "%1$,.2f", heatShort ) + "/" + String.format( "%1$,.2f", heatMedium ) + "/" + String.format( "%1$,.2f", heatLong ) + "/" + String.format( "%1$,.2f", heatExtreme ) + "\n";
            data += "   BF: " + BFShort + "/" + BFMedium + "/" + BFLong + "/" + BFExtreme + "\n";
            data += " Separate Damage: " + SpecialDamage + "\n";
            if ( NotEnoughAmmo ) { data += "Not enough ammo for all of the launchers (" + LauncherCount + " launchers with " + AmmoCount + " total ammo; needs " + (LauncherCount * 10) + ")\n"; }
            return data;
        }

        /**
         * @return the baseShort
         */
        public double getBaseShort() {
            return baseShort;
        }

        /**
         * @param baseShort the baseShort to set
         */
        public void setBaseShort(double baseShort) {
            this.baseShort = baseShort;
        }

        /**
         * @return the baseMedium
         */
        public double getBaseMedium() {
            return baseMedium;
        }

        /**
         * @param baseMedium the baseMedium to set
         */
        public void setBaseMedium(double baseMedium) {
            this.baseMedium = baseMedium;
        }

        /**
         * @return the baseLong
         */
        public double getBaseLong() {
            return baseLong;
        }

        /**
         * @param baseLong the baseLong to set
         */
        public void setBaseLong(double baseLong) {
            this.baseLong = baseLong;
        }

        /**
         * @return the baseExtreme
         */
        public double getBaseExtreme() {
            return baseExtreme;
        }

        /**
         * @param baseExtreme the baseExtreme to set
         */
        public void setBaseExtreme(double baseExtreme) {
            this.baseExtreme = baseExtreme;
        }

        /**
         * @return the heatShort
         */
        public double getHeatShort() {
            return heatShort;
        }

        /**
         * @return the heatMedium
         */
        public double getHeatMedium() {
            return heatMedium;
        }

        /**
         * @return the heatLong
         */
        public double getHeatLong() {
            return heatLong;
        }

        /**
         * @return the heatExtreme
         */
        public double getHeatExtreme() {
            return heatExtreme;
        }

        /**
         * @return the BFShort
         */
        public int getBFShort() {
            return BFShort;
        }

        /**
         * @return the BFMedium
         */
        public int getBFMedium() {
            return BFMedium;
        }

        /**
         * @return the BFLong
         */
        public int getBFLong() {
            return BFLong;
        }

        /**
         * @return the BFExtreme
         */
        public int getBFExtreme() {
            return BFExtreme;
        }

        /**
         * @return the TotalHeatGenerated
         */
        public int getTotalHeatGenerated() {
            return TotalHeatGenerated;
        }

        /**
         * @param TotalHeatGenerated the TotalHeatGenerated to set
         */
        public void setTotalHeatGenerated(int TotalHeatGenerated) {
            this.TotalHeatGenerated = TotalHeatGenerated;
            HeatAdjustments();
        }

        /**
         * @return the TotalHeatDissipation
         */
        public int getTotalHeatDissipation() {
            return TotalHeatDissipation;
        }

        /**
         * @param TotalHeatDissipation the TotalHeatDissipation to set
         */
        public void setTotalHeatDissipation(int TotalHeatDissipation) {
            this.TotalHeatDissipation = TotalHeatDissipation;
            HeatAdjustments();
        }

        public void setNormalRound( boolean useNormal ) {
            this.useNormalRound = useNormal;
        }
    }
}
