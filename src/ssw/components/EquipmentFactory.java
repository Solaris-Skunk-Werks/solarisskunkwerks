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

package ssw.components;

import java.util.Vector;
import ssw.*;

public class EquipmentFactory {
    // big class for holding and farming out equipment
    private Vector ISEQ = new Vector(),
            CLEQ = new Vector(),
            INEQ = new Vector();

    private Mech owner;

    public EquipmentFactory(Mech m) {
        owner = m;
        BuildEquipment();
    }

    public abPlaceable GetCopy( abPlaceable p ) {
        // creates an equipment copy of p
        abPlaceable retval = null;

        if (p instanceof IndustrialEquipment){
            IndustrialEquipment e = (IndustrialEquipment) p;
            retval = new IndustrialEquipment( e.GetCritName(), e.GetType(), e.GetAvailability(), e.IsClan(), e.getValidator(), e.getValidationFalseMessage() );
            ((Equipment) retval).SetAmmo( e.HasAmmo(), e.GetAmmo(), e.GetAmmoIndex() );
            ((Equipment) retval).SetLookupName( e.GetMMName( false ) );
            ((Equipment) retval).SetHeat( e.GetHeat() );
            ((Equipment) retval).SetRange( e.GetShortRange(), e.GetMediumRange(), e.GetLongRange() );
            ((Equipment) retval).SetStats( e.NumCrits(), e.GetTonnage(), e.GetCost(), e.GetOffensiveBV(), e.GetDefensiveBV(), e.GetSpecials() );
            ((Equipment) retval).SetAllocs( e.CanAllocHD(), e.CanAllocCT(), e.CanAllocTorso(), e.CanAllocArms(), e.CanAllocLegs() );
            ((Equipment) retval).SetSplitable( e.CanSplit() );
            ((Equipment) retval).SetMountableRear( e.CanMountRear() );
            if( e.GetExclusions() != null ) {
                retval.SetExclusions( e.GetExclusions() );
            }
        }
        else if( p instanceof Equipment ) {
            Equipment e = (Equipment) p;
            retval = new Equipment( e.GetCritName(), e.GetType(), e.GetAvailability(), e.IsClan() );
            ((Equipment) retval).SetAmmo( e.HasAmmo(), e.GetAmmo(), e.GetAmmoIndex() );
            ((Equipment) retval).SetLookupName( e.GetMMName( false ) );
            ((Equipment) retval).SetHeat( e.GetHeat() );
            ((Equipment) retval).SetRange( e.GetShortRange(), e.GetMediumRange(), e.GetLongRange() );
            ((Equipment) retval).SetStats( e.NumCrits(), e.GetTonnage(), e.GetCost(), e.GetOffensiveBV(), e.GetDefensiveBV(), e.GetSpecials() );
            ((Equipment) retval).SetAllocs( e.CanAllocHD(), e.CanAllocCT(), e.CanAllocTorso(), e.CanAllocArms(), e.CanAllocLegs() );
            ((Equipment) retval).SetSplitable( e.CanSplit() );
            ((Equipment) retval).SetMountableRear( e.CanMountRear() );
            if( e.GetExclusions() != null ) {
                retval.SetExclusions( e.GetExclusions() );
            }
        } else if( p instanceof ModularArmor ) {
            retval = new ModularArmor( ((ModularArmor) p).IsClan() );
            if( p.GetExclusions() != null ) {
                retval.SetExclusions( p.GetExclusions() );
            }
            return retval;
        }

        return retval;
    }

    public Object[] GetEquipment( Mech m ) {
        // returns an array based on the given specifications of era and year
        Vector RetVal = new Vector(),
               test;
        abPlaceable p;
        AvailableCode AC;

        if( m.IsClan() ) {
            test = CLEQ;
        } else {
            test = ISEQ;
        }

        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
            }
        }

        test = INEQ;
        for( int i = 0; i < test.size(); i++ ) {
            p = (abPlaceable) test.get( i );
            AC = p.GetAvailability();
            if( CommonTools.IsAllowed( AC, m ) ) {
                RetVal.add( p );
            }
        }

        if( RetVal.size() < 1 ) {
            return null;
        } else {
            return RetVal.toArray();
        }
    }

    public Equipment GetEquipmentByName( String name, boolean clan ) {
        Vector Test = new Vector();
        if( clan ) {
            Test = CLEQ;
        } else {
            Test = ISEQ;
        }

        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (Equipment) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }

        Test = INEQ;
        for( int i = 0; i < Test.size(); i++ ) {
            if( ((abPlaceable) Test.get( i )).GetCritName().equals( name ) ) {
                return (Equipment) GetCopy( (abPlaceable) Test.get( i ) );
            }
        }
        return null;
    }

    private void BuildEquipment() {
        AvailableCode a;
        Equipment addEQ;

/*******************************************************************************
 *      START INNER SPHERE EQUIPMENT
 ******************************************************************************/

        a = new AvailableCode( false, 'B', 'X', 'X', 'D', 3055, 0, 0, "CS", "", false, false );
        addEQ = new Equipment( "A-Pod", "PD", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISAntiPersonnelPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 0.5f, 1500.0f, 0.0f, 1.0f, "OS/AI" );
        addEQ.SetAllocs( false, false, false, false, true );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3069, 0, 0, "LA", "", false, false );
        addEQ = new Equipment( "B-Pod", "PD", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISBPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 2500.0f, 0.0f, 2.0f, "OS/AI" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'C', 'X', 'X', 'E', 3064, 0, 0, "LA", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEQ = new Equipment( "M-Pod", "PD", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISMPod" );
        addEQ.SetRange( 1, 2, 3 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 6000.0f, 5.0f, 0.0f, "C/V/X/OS" );
        addEQ.SetMountableRear( true );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'F', 'X', 'X', 'F', 3057, 0, 0, "DC", "", false, false, 3053, true, "DC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Angel ECM", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISAngelECM" );
        addEQ.SetRange( 0, 0, 6 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 2.0f, 750000.0f, 0.0f, 100.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2617, 2796, 3040, "TH", "FC", true, true );
        addEQ = new Equipment( "Anti-Missile System", "PD", a, false );
        addEQ.SetAmmo( true, 12, 82 );
        addEQ.SetLookupName( "ISAntiMissileSystem" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 1 );
        addEQ.SetStats( 1, 0.5f, 100000.0f, 0.0f, 32.0f, "-" );
        addEQ.SetMountableRear( true );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3059, 0, 0, "FS", "", false, false, 3054, true, "FC", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Laser Anti-Missile System", "PD", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISLaserAntiMissileSystem" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 7 );
        addEQ.SetStats( 2, 1.5f, 225000.0f, 0.0f, 45.0f, "-" );
        addEQ.SetMountableRear( true );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2576, 2835, 3045, "TH", "CC", true, true );
        addEQ = new Equipment( "Beagle Active Probe", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISBeagleActiveProbe" );
        addEQ.SetRange( 0, 0, 4 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 1.5f, 200000.0f, 0.0f, 10.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'F', 3058, 0, 0, "CS", "", false, false, 3056, true, "CS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Bloodhound Active Probe", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISBloodhoundActiveProbe" );
        addEQ.SetRange( 0, 0, 8 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 3, 2.0f, 500000.0f, 0.0f, 25.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3050, 0, 0, "DC", "", false, false );
        addEQ = new Equipment( "C3 Computer (Master)", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISC3MasterComputer" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 5, 5.0f, 1500000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetExclusions( new Exclusion( new String[] { "Improved C3 Computer" }, "C3 Computer (Master)" ) );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3050, 0, 0, "DC", "", false, false );
        addEQ = new Equipment( "C3 Computer (Slave)", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISC3SlaveUnit" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 250000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetExclusions( new Exclusion( new String[] { "Improved C3 Computer" }, "C3 Computer (Slave)" ) );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'X', 'X', 'E', 3062, 0, 0, "CS", "", false, false );
        addEQ = new Equipment( "Improved C3 Computer", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISImprovedC3CPU" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 2.5f, 750000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetExclusions( new Exclusion( new String[] { "C3 Computer (Master)", "C3 Computer (Slave)" }, "Improved C3 Computer" ) );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'F', 'F', 'X', 'F', 2751, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEQ = new Equipment( "Ground Mobile HPG", "PE", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISGroundMobileHPG" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 20 );
        addEQ.SetAllocs( false, true, true, false, false );
        addEQ.SetSplitable( true );
        addEQ.SetStats( 12, 12.0f, 4000000000.0f, 0.0f, 0.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2597, 2845, 3045, "TH", "CC", true, true );
        addEQ = new Equipment( "Guardian ECM Suite", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISGuardianECM" );
        addEQ.SetRange( 0, 0, 6 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 1.5f, 200000.0f, 0.0f, 61.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( false, 'E', 'E', 'F', 'D', 2600, 2835, 3033, "TH", "FS", true, true );
        addEQ = new Equipment( "TAG", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ISTAG" );
        addEQ.SetRange( 5, 9, 15 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 50000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetMountableRear( true );
        ISEQ.add( addEQ );

        ISEQ.add( new ModularArmor( false ) );

/*******************************************************************************
 *      START CLAN EQUIPMENT
 ******************************************************************************/

        a = new AvailableCode( true, 'B', 'X', 'D', 'C', 2850, 0, 0, "CGB", "", false, false );
        addEQ = new Equipment( "A-Pod", "PD", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLAntiPersonnelPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 0.5f, 1500.0f, 0.0f, 1.0f, "OS/AI" );
        addEQ.SetAllocs( false, false, false, false, true );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 3068, 0, 0, "CWX", "", false, false );
        addEQ = new Equipment( "B-Pod", "PD", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "ClanBPod" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 2500.0f, 0.0f, 2.0f, "OS/AI" );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3058, 0, 0, "CGS", "", false, false, 3056, true, "CGS", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Angel ECM", "E", a, false );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLAngelECM" );
        addEQ.SetRange( 0, 0, 6 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 2.0f, 750000.0f, 0.0f, 100.0f, "-" );
        ISEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2617, 0, 0, "TH", "", false, false );
        addEQ = new Equipment( "Anti-Missile System", "PD", a, true );
        addEQ.SetAmmo( true, 24, 83 );
        addEQ.SetLookupName( "CLAntiMissileSystem" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 1 );
        addEQ.SetStats( 1, 0.5f, 100000.0f, 0.0f, 32.0f, "-" );
        addEQ.SetMountableRear( true );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3048, 0, 0, "CWF", "", false, false, 3045, true, "CWF", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Laser Anti-Missile System", "PD", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLLaserAntiMissileSystem" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 5 );
        addEQ.SetStats( 1, 1.0f, 225000.0f, 0.0f, 45.0f, "-" );
        addEQ.SetMountableRear( true );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2576, 0, 0, "TH", "", false, false );
        addEQ = new Equipment( "Active Probe", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLActiveProbe" );
        addEQ.SetRange( 0, 0, 5 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 200000.0f, 0.0f, 12.0f, "-" );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'X', 'D', 3059, 0, 0, "CSJ", "", false, false );
        addEQ = new Equipment( "Light Active Probe", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "Light Active Probe" );
        addEQ.SetRange( 0, 0, 3 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 0.5f, 50000.0f, 0.0f, 7.0f, "-" );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'D', 'C', 2597, 0, 0, "TH", "", false, false );
        addEQ = new Equipment( "ECM Suite", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLECMSuite" );
        addEQ.SetRange( 0, 0, 6 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 200000.0f, 0.0f, 61.0f, "-" );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'F', 'F', 2751, 0, 0, "TH", "", false, false, 0, false, "", Constants.ADVANCED, Constants.ADVANCED );
        addEQ = new Equipment( "Ground Mobile HPG", "PE", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLGroundMobileHPG" );
        addEQ.SetRange( 0, 0, 0 );
        addEQ.SetHeat( 20 );
        addEQ.SetAllocs( false, true, true, false, false );
        addEQ.SetSplitable( true );
        addEQ.SetStats( 12, 12.0f, 4000000000.0f, 0.0f, 0.0f, "-" );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'E', 'X', 'D', 'C', 2600, 0, 0, "TH", "", false, false );
        addEQ = new Equipment( "TAG", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLTAG" );
        addEQ.SetRange( 5, 9, 15 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 1.0f, 50000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetMountableRear( true );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'X', 'E', 3054, 0, 0, "CWF", "", false, false );
        addEQ = new Equipment( "Light TAG", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLLightTAG" );
        addEQ.SetRange( 3, 6, 9 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 1, 0.5f, 40000.0f, 0.0f, 0.0f, "-" );
        addEQ.SetMountableRear( true );
        CLEQ.add( addEQ );

        a = new AvailableCode( true, 'F', 'X', 'X', 'F', 3059, 0, 0, "CSJ", "", false, false, 3057, true, "CSJ", Constants.EXPERIMENTAL, Constants.EXPERIMENTAL );
        addEQ = new Equipment( "Watchdog CEWS", "E", a, true );
        addEQ.SetAmmo( false, 0, 0 );
        addEQ.SetLookupName( "CLWatchdogECM" );
        addEQ.SetRange( 0, 0, 4 );
        addEQ.SetHeat( 0 );
        addEQ.SetStats( 2, 1.5f, 600000.0f, 7.0f, 61.0f, "-" );
        CLEQ.add( addEQ );

        CLEQ.add( new ModularArmor( true ) );

        rebuildIndustrialEquipment(owner);
    }

    public void rebuildIndustrialEquipment(Mech m){
        INEQ.clear();
        AvailableCode a;
        Equipment addEQ;
        boolean isClan = m.IsClan();
        String techBase = "IS";
        if (isClan )
            techBase = "CL";

        // Light Bridgelayer
        a = new AvailableCode(isClan, 'B', 'D', 'E', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Bridgelayer, Light", "IE", a, isClan, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers." );
        addEQ.SetLookupName(techBase + "LightBridgelayer" );
        addEQ.SetStats( 2, 1, 40000, 0, 5, "-" );
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        INEQ.add( addEQ );

        // Medium Bridgelayer
        a = new AvailableCode(isClan, 'C', 'D', 'E', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Bridgelayer, Medium", "IE", a, isClan, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers." );
        addEQ.SetLookupName(techBase + "MediumBridgelayer" );
        addEQ.SetStats( 4, 2, 75000, 0, 10, "-" );
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        INEQ.add( addEQ );

        // Heavy Bridgelayer
        a = new AvailableCode(isClan, 'D', 'E', 'E', 'E', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Bridgelayer, Heavy", "IE", a, isClan, new BridgelayerValidator(), "Bipedal Mechs cannot carry bridgelayers." );
        addEQ.SetLookupName(techBase + "HeavyBridgelayer" );
        addEQ.SetStats( 12, 6, 100000, 0, 20, "-" );
        addEQ.SetAllocs(false, false, true, false, false);
        addEQ.SetMountableRear(true);
        INEQ.add( addEQ );

        // Fluid Suction System, Standard
        a = new AvailableCode (isClan, 'C', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Fluid Suction System, Standard", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "StandardFluidSuctionSystem");
        addEQ.SetStats(1, 1, 25000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);

        // Fluid Suction System, Light
        a = new AvailableCode (isClan, 'B', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Fluid Suction System, Light", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "LightFluidSuctionSystem");
        addEQ.SetStats(1, 0.5f, 1000, 0, 0, "-");
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);

        // Lift Hoist
        a = new AvailableCode (isClan, 'A', 'A', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Lift Hoist", "IE", a, isClan, new LiftHoistValidator(), "Mech cannot have more than 2 lift hoists.");
        addEQ.SetLookupName(techBase + "LiftHoist");
        addEQ.SetStats(3, 3, 50000, 0, 0, "-");
        addEQ.SetAllocs(false, false, true, true, false);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);

        // Nail/Rivet Gun
        a = new AvailableCode (isClan, 'C', 'C', 'C', 'C', 2310, 0, 0, "FWL", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Nail//Rivet Gun", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "Nail//RivetGun");
        addEQ.SetStats(1, 0.5f, 7000, 1, 0, "-");
        addEQ.SetAmmo(true, 300, 0);
        addEQ.SetMountableRear(true);
        addEQ.SetRange(1, 0, 0);
        INEQ.add(addEQ);

        // Remote Sensor Dispenser
        a = new AvailableCode (isClan, 'C', 'F', 'F', 'D', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.TOURNAMENT, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Remote Sensor Dispenser", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "RemoteSensorDispenser");
        addEQ.SetStats(1, 0.5f, 30000, 0, 0, "-");
        addEQ.SetAmmo(true, 60, 0);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);
        
        // Searchlight
        a = new AvailableCode (isClan, 'A', 'A', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.TOURNAMENT, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Searchlight", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "Searchlight");
        addEQ.SetStats(1, 0.5f, 2000, 0, 0, "-");
        addEQ.SetRange(0,0,170);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);

        // Sprayer
        a = new AvailableCode (isClan, 'B', 'B', 'B', 'B', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Sprayer", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "Sprayer");
        addEQ.SetStats(1, 0.5f, 1000, 0, 0, "-");
        addEQ.SetRange(0,0,1);
        addEQ.SetAmmo(true, 10, 0);
        addEQ.SetMountableRear(true);
        INEQ.add(addEQ);

        // Cargo Container
        a = new AvailableCode (isClan, 'A', 'A', 'A', 'A', 1950, 0, 0, "PS", "", false, false, 0, false, "", Constants.EXPERIMENTAL, Constants.TOURNAMENT );
        addEQ = new IndustrialEquipment("Cargo Container", "IE", a, isClan, new SimpleValidator(), "");
        addEQ.SetLookupName(techBase + "CargoContainer");
        addEQ.SetStats(1, 10, 0, 0, 0, "-");
        INEQ.add(addEQ);
    }

    // Classes to validate equipment
    // This is why java needs closures so I could do this an inline function

    // Alawys allocatable equipment
    private class SimpleValidator implements EquipmentValidationInterface{
        public boolean validate(Mech m) {
            return true;
        }
    }

    // Bridgelayers
    private class BridgelayerValidator implements EquipmentValidationInterface{
        public boolean validate(Mech m) {
            return m.IsQuad();
        }
    }

    // Lift Hoists
    private class LiftHoistValidator implements EquipmentValidationInterface{
        public boolean validate(Mech m){
            Vector currentEquipment = m.GetLoadout().GetEquipment();
            for (int i = 0, c = 0; i < currentEquipment.size(); ++i){
                abPlaceable currentItem = (abPlaceable) currentEquipment.get(i);
                if (currentItem.GetCritName().equals("Lift Hoist")){
                    ++c;
                    if (c == 2)
                        return false;
                }
            }
            return true;
        }
    }

}
