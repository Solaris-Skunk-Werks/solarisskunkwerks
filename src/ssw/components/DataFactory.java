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

import java.util.Hashtable;
import ssw.Options;
import ssw.visitors.*;

public class DataFactory {
    // Class file to make data lookups easier and disconnected from the GUI
    public Hashtable Lookup = new Hashtable();
    Options GlobalOptions = new Options();
    Object[][] Equipment = { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } };
    WeaponFactory Weapons = null;
    EquipmentFactory Equips;
    AmmoFactory Ammo = new AmmoFactory();

    public DataFactory(Mech m){
        Weapons = new WeaponFactory( m, GlobalOptions );
        BuildLookupTable();
        Weapons.RebuildPhysicals( m );
        Equips = new EquipmentFactory (m);
    }

    public WeaponFactory GetWeapons() {
        return Weapons;
    }

    public EquipmentFactory GetEquipment() {
        return Equips;
    }

    public AmmoFactory GetAmmo() {
        return Ammo;
    }

    public ifVisitor Lookup( String s ) {
        // returns a visitor from the lookup table based on the lookup string
        return (ifVisitor) Lookup.get( s );
    }

    private void BuildLookupTable() {
        // sets up the lookup hashtable with String keys and ifVisitor values
        Lookup.put( "Standard Armor", new VArmorSetStandard() );
        Lookup.put( "Ferro-Fibrous", new VArmorSetFF() );
        Lookup.put( "Stealth Armor", new VArmorSetStealth() );
        Lookup.put( "Light Ferro-Fibrous", new VArmorSetLightFF() );
        Lookup.put( "Heavy Ferro-Fibrous", new VArmorSetHeavyFF() );
        Lookup.put( "Ferro-Lamellor", new VArmorSetFL() );
        Lookup.put( "Hardened Armor", new VArmorSetHA() );
        Lookup.put( "Laser-Reflective", new VArmorSetLR() );
        Lookup.put( "Reactive Armor", new VArmorSetRE() );
        Lookup.put( "Industrial Armor", new VArmorSetIndustrial() );
        Lookup.put( "Commercial Armor", new VArmorSetCommercial() );
        Lookup.put( "Standard Structure", new VChassisSetStandard() );
        Lookup.put( "Composite Structure", new VChassisSetComposite() );
        Lookup.put( "Endo-Steel", new VChassisSetEndoSteel() );
        Lookup.put( "Endo-Composite", new VChassisSetEndoComposite() );
        Lookup.put( "Reinforced Structure", new VChassisSetReinforced() );
        Lookup.put( "Industrial Structure", new VChassisSetIndustrial() );
        Lookup.put( "Standard Cockpit", new VCockpitSetStandard() );
        Lookup.put( "Industrial Cockpit", new VCockpitSetIndustrial() );
        Lookup.put( "Industrial w/ Adv. FC", new VCockpitSetIndustrialAFC() );
        Lookup.put( "Small Cockpit", new VCockpitSetSmall() );
        Lookup.put( "Fuel-Cell Engine", new VEngineSetFuelCell() );
        Lookup.put( "Fission Engine", new VEngineSetFission() );
        Lookup.put( "Fusion Engine", new VEngineSetFusion() );
        Lookup.put( "XL Engine", new VEngineSetFusionXL() );
        Lookup.put( "XXL Engine", new VEngineSetFusionXXL() );
        Lookup.put( "I.C.E. Engine", new VEngineSetICE() );
        Lookup.put( "Compact Fusion Engine", new VEngineSetCompactFusion() );
        Lookup.put( "Light Fusion Engine", new VEngineSetLightFusion() );
        Lookup.put( "Standard Gyro", new VGyroSetStandard() );
        Lookup.put( "Heavy-Duty Gyro", new VGyroSetHD() );
        Lookup.put( "Extra-Light Gyro", new VGyroSetXL() );
        Lookup.put( "Compact Gyro", new VGyroSetCompact() );
        Lookup.put( "No Enhancement", new VEnhanceSetNone() );
        Lookup.put( "MASC", new VEnhanceSetMASC() );
        Lookup.put( "TSM", new VEnhanceSetTSM() );
        Lookup.put( "Industrial TSM", new VEnhanceSetITSM() );
        Lookup.put( "Single Heat Sink", new VHeatSinkSetSingle() );
        Lookup.put( "Double Heat Sink", new VHeatSinkSetDouble() );
        Lookup.put( "Standard Jump Jet", new VJumpJetSetStandard() );
        Lookup.put( "Improved Jump Jet", new VJumpJetSetImproved() );
        Lookup.put( "Mech UMU", new VJumpJetSetUMU() );
    }
}
