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

package common;

import components.*;
import filehandlers.BinaryReader;
import filehandlers.JsonReader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataFactory {
    // Class file to make data lookups easier and disconnected from the BFB.GUI
    Object[][] Equipment = { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } };
    EquipmentFactory Equips;
    ArrayList quirks;
    ArrayList ammo,
            weapons,
            physicals,
            equips,
            customs;

    public DataFactory() throws Exception {
        JsonReader jr = new JsonReader();
        Path basePath = Paths.get(Constants.EQUIPMENT_JSON_BASE_DIR);
        ammo = jr.ReadAllAmmo(basePath.resolve("ammunition.json"));
        weapons = jr.ReadAllRangedWeapons(basePath.resolve("weapons.json"));
        physicals = jr.ReadAllPhysicalWeapons(basePath.resolve("physicals.json"));
        equips = jr.ReadAllEquipment(basePath.resolve("equipment.json"));
        quirks = jr.ReadAllQuirks(basePath.resolve("quirks.json"));

        // Load custom equipment
        Path file = basePath.resolve("custom_ammunition.json");
        if (Files.exists(file)) {
            customs = jr.ReadAllAmmo(file);
            if (!customs.isEmpty()) {
                ammo.addAll(customs);
            }
        }
        file = basePath.resolve("custom_weapons.json");
        if (Files.exists(file)) {
            customs = jr.ReadAllRangedWeapons(file);
            if (!customs.isEmpty()) {
                weapons.addAll(customs);
            }
        }
        file = basePath.resolve("custom_physicals.json");
        if (Files.exists(file)) {
            customs = jr.ReadAllPhysicalWeapons(file);
            if (!customs.isEmpty()) {
                physicals.addAll(customs);
            }
        }
        file = basePath.resolve("custom_equipment.json");
        if (Files.exists(file)) {
            customs = jr.ReadAllEquipment(file);
            if (!customs.isEmpty()) {
                equips.addAll(customs);
            }
        }
        file = basePath.resolve("customs_quirks.json");
        if (Files.exists(file)) {
            customs = jr.ReadAllQuirks(file);
            if (!customs.isEmpty()) {
                quirks.addAll(customs);
            }
        }
    }

    // Legacy binary format
    public DataFactory(boolean binary) throws Exception
    {
        BinaryReader b = new BinaryReader();
        ammo = b.ReadAmmo( Constants.AMMOFILE );
        weapons = b.ReadWeapons( Constants.WEAPONSFILE );
        physicals = b.ReadPhysicals( Constants.PHYSICALSFILE );
        equips = b.ReadEquipment( Constants.EQUIPMENTFILE );
        quirks = b.ReadQuirks( Constants.QUIRKSFILE );

        File dataFile;
        try
        {
            dataFile = new File( Constants.CUSTOMWEAPONSFILE );
            if (dataFile.exists()) {
                customs = b.ReadWeapons( Constants.CUSTOMWEAPONSFILE );
                if ( customs.size() > 0 ) weapons.addAll(customs);
            }

            dataFile = new File( Constants.CUSTOMAMMOFILE );
            if ( dataFile.exists() ) {
                customs = b.ReadAmmo( Constants.CUSTOMAMMOFILE );
                if ( customs.size() > 0 ) ammo.addAll(customs);
            }

            dataFile = new File( Constants.CUSTOMEQUIPMENTFILE );
            if ( dataFile.exists() ) {
                customs = b.ReadEquipment( Constants.CUSTOMEQUIPMENTFILE );
                if ( customs.size() > 0 ) equips.addAll(customs);
            }

            dataFile = new File( Constants.CUSTOMPHYSICALSFILE );
            if ( dataFile.exists() ) {
                customs = b.ReadPhysicals( Constants.CUSTOMPHYSICALSFILE );
                if ( customs.size() > 0 ) physicals.addAll(customs);
            }

            dataFile = new File( Constants.CUSTOMQUIRKSFILE );
            if ( dataFile.exists() ) {
                ArrayList<Quirk> c = b.ReadQuirks( Constants.CUSTOMQUIRKSFILE );
                if ( c.size() > 0 ) quirks.addAll(c);
            }
        }
        catch (Exception e)
        {
        }
    }

    public DataFactory( Mech m ) throws Exception {
        this();
        Equips = new EquipmentFactory( weapons, physicals, equips, ammo, m );
    }

    public DataFactory( CombatVehicle v ) throws Exception {
        this();
        Equips = new EquipmentFactory( weapons, physicals, equips, ammo, v );
    }

    public EquipmentFactory GetEquipment() {
        return Equips;
    }

    public ArrayList<Quirk> GetQuirks() {
        return quirks;
    }

    public void Rebuild( Mech m ) {
        Equips.BuildPhysicals( m );
    }

    public void Rebuild( CombatVehicle v ) {
        Equips.BuildPhysicals( v );
    }

    private void sortRangedWeapons() {
        Comparator<RangedWeapon> oneShot = Comparator.comparing(RangedWeapon::IsOneShot);
        Comparator<RangedWeapon> weaponType = oneShot.thenComparing(RangedWeapon::GetWeaponType);
        Comparator<RangedWeapon> variant = weaponType.thenComparing(RangedWeapon::GetWeaponVariant);
        Comparator<RangedWeapon> faction = variant.thenComparing(RangedWeapon::GetTechBase);
        Comparator<RangedWeapon> rackSize = faction.thenComparing(RangedWeapon::GetRackSize);
        Comparator<RangedWeapon> sizeClass = rackSize.thenComparing(RangedWeapon::GetSizeClass);

        Collections.sort(weapons, sizeClass);
    }

    private void sortPhysicalWeapons() {
        Collections.sort(physicals, Comparator.comparing(PhysicalWeapon::ActualName));
    }

    private void sortEquipment() {
        Collections.sort(equips, Comparator.comparing(components.Equipment::ActualName));
    }

    private void sortQuirks() {
        Collections.sort(quirks, Comparator.comparing(Quirk::getName));
    }
}
