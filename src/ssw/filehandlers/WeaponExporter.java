/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ssw.filehandlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import ssw.components.*;
import ssw.Constants;

/**
 *
 * @author justin
 */
public class WeaponExporter {
    // exports all the weapon info into a delimited text file
    DataFactory d;
    Mech m;
    String delim = "|";

    public WeaponExporter() {
        m = new Mech();
        m.SetEra( Constants.ALL_ERA );
        m.SetRulesLevel( Constants.EXPERIMENTAL );
        d = new DataFactory( m );
    }

    public void WriteAmmo() {
        try {
            BufferedWriter FR = new BufferedWriter( new FileWriter( "ammo.csv" ) );
            for( int i = 1; i < 200; i++ ) {
                Object[] check = d.GetAmmo().GetAmmo( i, m );
                for( int j = 0; j < check.length; j++ ) {
                    FR.write( GetAmmoString( ((abPlaceable) check[j]) ) );
                    FR.newLine();
                }
            }
            m.SetTechBase( Constants.CLAN );
            for( int i = 0; i < 200; i++ ) {
                Object[] check = d.GetAmmo().GetAmmo( i, m );
                for( int j = 0; j < check.length; j++ ) {
                    FR.write( GetAmmoString( ((abPlaceable) check[j]) ) );
                    FR.newLine();
                }
            }
            FR.close();
        } catch( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }
    }

    public void WriteFile() {
        try {
            BufferedWriter FR = new BufferedWriter( new FileWriter( "weapons.csv" ) );
            Object[] check = d.GetWeapons().GetBallisticWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.CLAN );
            check = d.GetWeapons().GetBallisticWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.INNER_SPHERE );
            check = d.GetWeapons().GetEnergyWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.CLAN );
            check = d.GetWeapons().GetEnergyWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.INNER_SPHERE );
            check = d.GetWeapons().GetMissileWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.CLAN );
            check = d.GetWeapons().GetMissileWeapons( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.INNER_SPHERE );
            check = d.GetWeapons().GetArtillery( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            m.SetTechBase( Constants.CLAN );
            check = d.GetWeapons().GetArtillery( m );
            for( int i = 0; i < check.length; i++ ) {
                FR.write( GetWeaponString( ((abPlaceable) check[i]) ) );
                FR.newLine();
            }
            FR.close();
        } catch( Exception e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
            System.err.flush();
        }
    }

    private String GetWeaponString( abPlaceable p ) {
        String retval = "";
        if( p instanceof Artillery ) {
            Artillery b = (Artillery) p;
            retval += b.GetName();
            retval += delim + b.GetMMName( false );
            retval += delim + b.GetPrintName();
            retval += delim + b.GetType();
            retval += delim + b.GetSpecials();
            retval += delim + b.GetHeat();
            retval += delim + b.GetToHitShort();
            retval += delim + b.GetToHitMedium();
            retval += delim + b.GetToHitLong();
            retval += delim + b.GetDamageShort();
            retval += delim + b.GetDamageMedium();
            retval += delim + b.GetDamageLong();
            retval += delim + b.IsCluster();
            retval += delim + b.ClusterSize();
            retval += delim + b.ClusterGrouping();
            retval += delim + b.GetRangeMin();
            retval += delim + b.GetRangeShort();
            retval += delim + b.GetRangeMedium();
            retval += delim + b.GetRangeLong();
            retval += delim + b.HasAmmo();
            retval += delim + b.GetAmmo();
            retval += delim + b.GetAmmoIndex();
            retval += delim + b.SwitchableAmmo();
            retval += delim + b.GetTonnage();
            retval += delim + b.NumCrits();
            retval += delim + b.GetCost();
            retval += delim + b.GetOffensiveBV();
            retval += delim + b.GetDefensiveBV();
            retval += delim + b.RequiresFusion();
            retval += delim + b.RequiresNuclear();
            retval += delim + b.CanSplit();
            retval += delim + b.CanMountRear();
            retval += delim + b.CanAllocHD();
            retval += delim + b.CanAllocCT();
            retval += delim + b.CanAllocTorso();
            retval += delim + b.CanAllocArms();
            retval += delim + b.CanAllocLegs();
            retval += delim + b.OmniRestrictActuators();
            retval += delim + b.IsOneShot();
            retval += delim + b.IsStreak();
            retval += delim + b.IsUltra();
            retval += delim + b.IsRotary();
            retval += delim + b.IsExplosive();
            retval += delim + b.IsFCSCapable();
            retval += delim + b.IsTCCapable();
            retval += delim + b.IsArrayCapable();
            retval += delim;
            retval += delim;
            retval += delim;
        } else if( p instanceof EnergyWeapon ) {
            EnergyWeapon b = (EnergyWeapon) p;
            retval += b.GetName();
            retval += delim + b.GetMMName( false );
            retval += delim + b.GetPrintName();
            retval += delim + b.GetType();
            retval += delim + b.GetSpecials();
            retval += delim + b.GetHeat();
            retval += delim + b.GetToHitShort();
            retval += delim + b.GetToHitMedium();
            retval += delim + b.GetToHitLong();
            retval += delim + b.GetDamageShort();
            retval += delim + b.GetDamageMedium();
            retval += delim + b.GetDamageLong();
            retval += delim + b.IsCluster();
            retval += delim + b.ClusterSize();
            retval += delim + b.ClusterGrouping();
            retval += delim + b.GetRangeMin();
            retval += delim + b.GetRangeShort();
            retval += delim + b.GetRangeMedium();
            retval += delim + b.GetRangeLong();
            retval += delim + b.HasAmmo();
            retval += delim + b.GetAmmo();
            retval += delim + b.GetAmmoIndex();
            retval += delim + b.SwitchableAmmo();
            retval += delim + b.GetTonnage();
            retval += delim + b.NumCrits();
            retval += delim + b.GetCost();
            retval += delim + b.GetOffensiveBV();
            retval += delim + b.GetDefensiveBV();
            retval += delim + b.RequiresFusion();
            retval += delim + b.RequiresNuclear();
            retval += delim + b.CanSplit();
            retval += delim + b.CanMountRear();
            retval += delim + b.CanAllocHD();
            retval += delim + b.CanAllocCT();
            retval += delim + b.CanAllocTorso();
            retval += delim + b.CanAllocArms();
            retval += delim + b.CanAllocLegs();
            retval += delim + b.OmniRestrictActuators();
            retval += delim + b.IsOneShot();
            retval += delim + b.IsStreak();
            retval += delim + b.IsUltra();
            retval += delim + b.IsRotary();
            retval += delim + b.IsExplosive();
            retval += delim + b.IsFCSCapable();
            retval += delim + b.IsTCCapable();
            retval += delim + b.IsArrayCapable();
            retval += delim + b.HasCapacitor();
            retval += delim;
            retval += delim;
        } else if( p instanceof MissileWeapon ) {
            MissileWeapon b = (MissileWeapon) p;
            retval += b.GetName();
            retval += delim + b.GetMMName( false );
            retval += delim + b.GetPrintName();
            retval += delim + b.GetType();
            retval += delim + b.GetSpecials();
            retval += delim + b.GetHeat();
            retval += delim + b.GetToHitShort();
            retval += delim + b.GetToHitMedium();
            retval += delim + b.GetToHitLong();
            retval += delim + b.GetDamageShort();
            retval += delim + b.GetDamageMedium();
            retval += delim + b.GetDamageLong();
            retval += delim + b.IsCluster();
            retval += delim + b.ClusterSize();
            retval += delim + b.ClusterGrouping();
            retval += delim + b.GetRangeMin();
            retval += delim + b.GetRangeShort();
            retval += delim + b.GetRangeMedium();
            retval += delim + b.GetRangeLong();
            retval += delim + b.HasAmmo();
            retval += delim + b.GetAmmo();
            retval += delim + b.GetAmmoIndex();
            retval += delim + b.SwitchableAmmo();
            retval += delim + b.GetTonnage();
            retval += delim + b.NumCrits();
            retval += delim + b.GetCost();
            retval += delim + b.GetOffensiveBV();
            retval += delim + b.GetDefensiveBV();
            retval += delim + b.RequiresFusion();
            retval += delim + b.RequiresNuclear();
            retval += delim + b.CanSplit();
            retval += delim + b.CanMountRear();
            retval += delim + b.CanAllocHD();
            retval += delim + b.CanAllocCT();
            retval += delim + b.CanAllocTorso();
            retval += delim + b.CanAllocArms();
            retval += delim + b.CanAllocLegs();
            retval += delim + b.OmniRestrictActuators();
            retval += delim + b.IsOneShot();
            retval += delim + b.IsStreak();
            retval += delim + b.IsUltra();
            retval += delim + b.IsRotary();
            retval += delim + b.IsExplosive();
            retval += delim + b.IsFCSCapable();
            retval += delim + b.IsTCCapable();
            retval += delim + b.IsArrayCapable();
            retval += delim;
            retval += delim + b.GetFCSType();
            retval += delim + b.IsFCSCapable();
        }

        if( ( p instanceof BallisticWeapon ) || ( p instanceof EnergyWeapon ) || ( p instanceof MissileWeapon ) || ( p instanceof Artillery ) ) {
        AvailableCode AC = p.GetAvailability();
        if( AC.IsClan() ) {
            retval += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim;
            retval += delim + AC.GetTechRating();
            retval += delim + AC.GetSLCode();
            retval += delim + AC.GetSWCode();
            retval += delim + AC.GetCICode();
            retval += delim + AC.GetIntroDate();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.WentExtinct();
            retval += delim + AC.GetExtinctDate();
            retval += delim + AC.WasReIntroduced();
            retval += delim + AC.GetReIntroDate();
            retval += delim + AC.GetReIntroFaction();
            retval += delim + AC.IsPrototype();
            retval += delim + AC.GetRandDStart();
            retval += delim + AC.GetRandDFaction();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.GetRulesLevelBM();
            retval += delim + AC.GetRulesLevelIM();
            retval += delim + delim + delim;
        } else {
            retval += delim + AC.GetTechRating();
            retval += delim + AC.GetSLCode();
            retval += delim + AC.GetSWCode();
            retval += delim + AC.GetCICode();
            retval += delim + AC.GetIntroDate();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.WentExtinct();
            retval += delim + AC.GetExtinctDate();
            retval += delim + AC.WasReIntroduced();
            retval += delim + AC.GetReIntroDate();
            retval += delim + AC.GetReIntroFaction();
            retval += delim + AC.IsPrototype();
            retval += delim + AC.GetRandDStart();
            retval += delim + AC.GetRandDFaction();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.GetRulesLevelBM();
            retval += delim + AC.GetRulesLevelIM();
            retval += delim + delim + delim;
            retval += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim;
        }
        }
        return retval;
    }

    private String GetAmmoString( abPlaceable p ) {
        String retval = "";
        if( p instanceof Ammunition ) {
            Ammunition b = (Ammunition) p;
            retval += b.GetCritName();
            retval += delim + b.GetMMName( false );
            retval += delim + b.GetBasePrintName();
            retval += delim + b.GetToHitShort();
            retval += delim + b.GetToHitMedium();
            retval += delim + b.GetToHitLong();
            retval += delim + b.GetDamage();
            retval += delim + b.ClusterSize();
            retval += delim + b.ClusterGrouping();
            retval += delim + b.GetMinRange();
            retval += delim + b.GetShortRange();
            retval += delim + b.GetMediumRange();
            retval += delim + b.GetLongRange();
            retval += delim + b.GetAmmoIndex();
            retval += delim + b.GetTonnage();
            retval += delim + b.NumCrits();
            retval += delim + b.GetCost();
            retval += delim + b.GetOffensiveBV();
            retval += delim + b.GetDefensiveBV();
            retval += delim + b.CanSplit();
            retval += delim + b.CanMountRear();
            retval += delim + b.CanAllocHD();
            retval += delim + b.CanAllocCT();
            retval += delim + b.CanAllocTorso();
            retval += delim + b.CanAllocArms();
            retval += delim + b.CanAllocLegs();
            retval += delim + b.IsExplosive();
        }
        AvailableCode AC = p.GetAvailability();
        if( AC.IsClan() ) {
            retval += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim;
            retval += delim + AC.GetTechRating();
            retval += delim + AC.GetSLCode();
            retval += delim + AC.GetSWCode();
            retval += delim + AC.GetCICode();
            retval += delim + AC.GetIntroDate();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.WentExtinct();
            retval += delim + AC.GetExtinctDate();
            retval += delim + AC.WasReIntroduced();
            retval += delim + AC.GetReIntroDate();
            retval += delim + AC.GetReIntroFaction();
            retval += delim + AC.IsPrototype();
            retval += delim + AC.GetRandDStart();
            retval += delim + AC.GetRandDFaction();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.GetRulesLevelBM();
            retval += delim + AC.GetRulesLevelIM();
            retval += delim + delim + delim;
        } else {
            retval += delim + AC.GetTechRating();
            retval += delim + AC.GetSLCode();
            retval += delim + AC.GetSWCode();
            retval += delim + AC.GetCICode();
            retval += delim + AC.GetIntroDate();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.WentExtinct();
            retval += delim + AC.GetExtinctDate();
            retval += delim + AC.WasReIntroduced();
            retval += delim + AC.GetReIntroDate();
            retval += delim + AC.GetReIntroFaction();
            retval += delim + AC.IsPrototype();
            retval += delim + AC.GetRandDStart();
            retval += delim + AC.GetRandDFaction();
            retval += delim + AC.GetIntroFaction();
            retval += delim + AC.GetRulesLevelBM();
            retval += delim + AC.GetRulesLevelIM();
            retval += delim + delim + delim;
            retval += delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim + delim;
        }
        return retval;
    }
}
