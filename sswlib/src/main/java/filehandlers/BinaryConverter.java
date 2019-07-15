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

package filehandlers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class BinaryConverter {
// Provides conversion tools for binary files, either into Java classes or from
// CSV to binary (and back again if needed).
    String Messages = "";

/**
 * Converts a CSV text file of RangedWeapon data into a binary file to conserve
 * space and prevent unwanted user changes.
 * 
 * @param input The canonical input CSV filename.
 * @param output The canonical output binary filename.
 * @param delim The text delimiter used in the CSV file.  One character only.
 */
    public boolean ConvertRangedWeaponsCSVtoBin( String input, String output, String delim ) {
        // take two filenames as input.
        Messages = "";
        int NumConverted = 0;
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else {
                        ProcessWeaponString( FW, read, delim );
                        NumConverted++;
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            Messages += e.getMessage();
            Messages += e.toString();
            return false;
        }
        Messages += "Wrote " + NumConverted + " weapons to " + output + "\n";
        return true;
    }

/**
 * Converts a CSV text file of Physical Weapons data into a binary file to conserve
 * space and prevent unwanted user changes.
 *
 * @param input The canonical input CSV filename.
 * @param output The canonical output binary filename.
 * @param delim The text delimiter used in the CSV file.  One character only.
 */
    public boolean ConvertPhysicalWeaponCSVtoBin( String input, String output, String delim ) {
        Messages = "";
        int NumConverted = 0;
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else {
                        ProcessPhysicalWeaponString( FW, read, delim );
                        NumConverted++;
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            Messages += e.getMessage();
            Messages += e.toString();
            return false;
        }
        Messages += "Wrote " + NumConverted + " physical weapons to " + output + "\n";
        return true;
    }

/**
 * Converts a CSV text file of Equipment data into a binary file to conserve
 * space and prevent unwanted user changes.
 *
 * @param input The canonical input CSV filename.
 * @param output The canonical output binary filename.
 * @param delim The text delimiter used in the CSV file.  One character only.
 */
    public boolean ConvertEquipmentCSVtoBin( String input, String output, String delim ) {
        Messages = "";
        int NumConverted = 0;
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else {
                        ProcessEquipmentString( FW, read, delim );
                        NumConverted++;
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            Messages += e.getMessage();
            Messages += e.toString();
            return false;
        }
        Messages += "Wrote " + NumConverted + " equipments to " + output + "\n";
        return true;
    }

/**
 * Converts a CSV text file of Ammunition data into a binary file to conserve
 * space and prevent unwanted user changes.
 * 
 * @param input The canonical input CSV filename.
 * @param output The canonical output binary filename.
 * @param delim The text delimiter used in the CSV file.  One character only.
 */
    public boolean ConvertAmmunitionCSVtoBin( String input, String output, String delim ) {
        // take two filenames as input.
        Messages = "";
        int NumConverted = 0;
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else {
                        ProcessAmmoString( FW, read, delim );
                        NumConverted++;
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            Messages += e.getMessage();
            Messages += e.toString();
            return false;
        }
        Messages += "Wrote " + NumConverted + " ammunitions to " + output + "\n";
        return true;
    }


/**
 * Converts a CSV text file of Quirks data into a binary file to conserve
 * space and prevent unwanted user changes.
 *
 * @param input The canonical input CSV filename.
 * @param output The canonical output binary filename.
 * @param delim The text delimiter used in the CSV file.  One character only.
 */
    public boolean ConvertQuirksCSVtoBin( String input, String output, String delim ) {
        // take two filenames as input.
        Messages = "";
        int NumConverted = 0;
        BufferedReader FR;
        DataOutputStream FW;
        try {
            FR = new BufferedReader( new FileReader( input ) );
            FW = new DataOutputStream( new FileOutputStream( output ) );
            boolean EOF = false;
            String read = "";
            //Skip the first two lines
            FR.readLine();
            FR.readLine();
            while( EOF == false ) {
                read = FR.readLine();
                if( read == null ) {
                    // We've hit the end of the file.
                    EOF = true;
                } else {
                    if( read.equals( "EOF" ) ) {
                        // end of file.
                        EOF = true;
                    } else if ( read.trim().length() == 0 ) {
                        //Skip!
                    } else {
                        ProcessQuirksString( FW, read, delim );
                        NumConverted++;
                    }
                }
            }
            FR.close();
            FW.close();
        } catch( Exception e ) {
            Messages += e.getMessage();
            Messages += e.toString();
            return false;
        }
        Messages += "Wrote " + NumConverted + " quirks to " + output + "\n";
        return true;
    }

    public String GetMessages() {
        return Messages;
    }

/**
 * Processes the given weapons string from a CSV file into binary data.  Output
 * is fed to the given DataOutputStream.
 * 
 * @param FW The DataOutputStream to write to.
 * @param read The delimited string to read from.  One full line of data.
 * @param delim The text delimiter used in the CSV file.  One character only.
 * @throws java.lang.Exception
 */
    private void ProcessWeaponString( DataOutputStream FW, String read, String delim ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming semi-colon delimited.
        String[] data = read.split( delim );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.

        // basic weapon info
        FW.writeUTF( data[0] ); // Name
        FW.writeUTF( data[1] ); // Name
        FW.writeUTF( data[2] ); // Lookup Name
        FW.writeUTF( data[3] ); // MM Name
        FW.writeUTF( data[4] ); // Type
        FW.writeUTF( data[5] ); // Specials
        FW.writeInt( Integer.parseInt( data[6] ) ); // Class
        ProcessAvailableCodeString( FW, data, 7 );
        // Meat of the weapon stats start here
        FW.writeDouble( Double.parseDouble( data[47] ) ); // Tons
        FW.writeInt( Integer.parseInt( data[48] ) ); // Mspc
        FW.writeInt( Integer.parseInt( data[49] ) ); // Vspc
        FW.writeDouble( Double.parseDouble( data[50] ) ); // Cost
        FW.writeDouble( Double.parseDouble( data[51] ) ); // OBV
        FW.writeDouble( Double.parseDouble( data[52] ) ); // DBV
        FW.writeInt( Integer.parseInt( data[53] ) ); // heat
        FW.writeInt( Integer.parseInt( data[54] ) ); // To-hit S
        FW.writeInt( Integer.parseInt( data[55] ) ); // To-hit M
        FW.writeInt( Integer.parseInt( data[56] ) ); // To-hit L
        FW.writeInt( Integer.parseInt( data[57] ) ); // Dam S
        FW.writeInt( Integer.parseInt( data[58] ) ); // M
        FW.writeInt( Integer.parseInt( data[59] ) ); // L
        FW.writeBoolean( Boolean.parseBoolean( data[60] ) ); // Cluster
        FW.writeInt( Integer.parseInt( data[61] ) ); // Size
        FW.writeInt( Integer.parseInt( data[62] ) ); // Group
        FW.writeInt( Integer.parseInt( data[63] ) ); // Cluster Mod Short
        FW.writeInt( Integer.parseInt( data[64] ) ); // Cluster Mod Medium
        FW.writeInt( Integer.parseInt( data[65] ) ); // Cluster Mod Long
        FW.writeInt( Integer.parseInt( data[66] ) ); // Range Min
        FW.writeInt( Integer.parseInt( data[67] ) ); // S
        FW.writeInt( Integer.parseInt( data[68] ) ); // M
        FW.writeInt( Integer.parseInt( data[69] ) ); // L
        FW.writeBoolean( Boolean.parseBoolean( data[70] ) ); // Has Ammo
        FW.writeInt( Integer.parseInt( data[71] ) ); // Lot
        FW.writeInt( Integer.parseInt( data[72] ) ); // Idx
        FW.writeBoolean( Boolean.parseBoolean( data[73] ) ); // Switch
        FW.writeBoolean( Boolean.parseBoolean( data[74] ) ); // HD
        FW.writeBoolean( Boolean.parseBoolean( data[75] ) ); // CT
        FW.writeBoolean( Boolean.parseBoolean( data[76] ) ); // Torso
        FW.writeBoolean( Boolean.parseBoolean( data[77] ) ); // Arms
        FW.writeBoolean( Boolean.parseBoolean( data[78] ) ); // Legs
        FW.writeBoolean( Boolean.parseBoolean( data[79] ) ); // Split
        FW.writeBoolean( Boolean.parseBoolean( data[80] ) ); // OmniArm
        FW.writeBoolean( Boolean.parseBoolean( data[81] ) ); // CV Front
        FW.writeBoolean( Boolean.parseBoolean( data[82] ) ); // CV Sides
        FW.writeBoolean( Boolean.parseBoolean( data[83] ) ); // CV Rear
        FW.writeBoolean( Boolean.parseBoolean( data[84] ) ); // CV Turret
        FW.writeBoolean( Boolean.parseBoolean( data[85] ) ); // CV Body
        FW.writeBoolean( Boolean.parseBoolean( data[86] ) ); // Fusion
        FW.writeBoolean( Boolean.parseBoolean( data[87] ) ); // Nuclear
        FW.writeBoolean( Boolean.parseBoolean( data[88] ) ); // Power Amps
        FW.writeBoolean( Boolean.parseBoolean( data[89] ) ); // OS
        FW.writeBoolean( Boolean.parseBoolean( data[90] ) ); // Streak
        FW.writeBoolean( Boolean.parseBoolean( data[91] ) ); // Ultra
        FW.writeBoolean( Boolean.parseBoolean( data[92] ) ); // Rotary
        FW.writeBoolean( Boolean.parseBoolean( data[93] ) ); // Explode
        FW.writeBoolean( Boolean.parseBoolean( data[94] ) ); // TC
        FW.writeBoolean( Boolean.parseBoolean( data[95] ) ); // Array
        FW.writeBoolean( Boolean.parseBoolean( data[96] ) ); // Capacitor
        FW.writeBoolean( Boolean.parseBoolean( data[97] ) ); // Insulator
        FW.writeBoolean( Boolean.parseBoolean( data[98] ) );
        FW.writeInt( Integer.parseInt( data[99] ) );
        FW.writeBoolean( Boolean.parseBoolean( data[100] ) ); // A-IV
        FW.writeInt( Integer.parseInt( data[101] ) ); // A-IV Type
        FW.writeUTF( data[102] ); // ChatName
        FW.writeUTF( data[103] ); // BookReference
        FW.writeUTF( data[104] ); // Battleforce Abilities
    }

/**
 * Processes the given physical weapons string from a CSV file into binary data.
 * Output is fed to the given DataOutputStream.
 *
 * @param FW The DataOutputStream to write to.
 * @param read The delimited string to read from.  One full line of data.
 * @param delim The text delimiter used in the CSV file.  One character only.
 * @throws java.lang.Exception
 */
    private void ProcessPhysicalWeaponString( DataOutputStream FW, String read, String delim ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming semi-colon delimited.
        String[] data = read.split( delim );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.
        FW.writeUTF( data[0] ); // Lookup Name
        FW.writeUTF( data[1] ); // Actual Name
        FW.writeUTF( data[2] ); // Crit Name
        FW.writeUTF( data[3] ); // Chat Name
        FW.writeUTF( data[4] ); // MM Name
        FW.writeUTF( data[5] ); // Type
        FW.writeUTF( data[6] ); // Specials
        ProcessAvailableCodeString( FW, data, 7 );
        // weapon stats start here
        FW.writeDouble( Double.parseDouble( data[47] ) ); // Tonnage Multiplier
        FW.writeDouble( Double.parseDouble( data[48] ) ); // Tonnage Adder
        FW.writeBoolean( Boolean.parseBoolean( data[49] ) ); // Round to Half Ton
        FW.writeDouble( Double.parseDouble( data[50] ) ); // Cost Multiplier
        FW.writeDouble( Double.parseDouble( data[51] ) ); // Cost Adder
        FW.writeDouble( Double.parseDouble( data[52] ) ); // BV Multiplier
        FW.writeDouble( Double.parseDouble( data[53] ) ); // BV Adder
        FW.writeDouble( Double.parseDouble( data[54] ) ); // Def BV
        FW.writeDouble( Double.parseDouble( data[55] ) ); // Crit Multiplier
        FW.writeInt( Integer.parseInt( data[56] ) ); // Crit Adder
        FW.writeInt( Integer.parseInt( data[57] ) ); // Heat
        FW.writeInt( Integer.parseInt( data[58] ) ); // To-Hit Short
        FW.writeInt( Integer.parseInt( data[59] ) ); // To-Hit Medium
        FW.writeInt( Integer.parseInt( data[60] ) ); // To-Hit Long
        FW.writeDouble( Double.parseDouble( data[61] ) ); // Damage Multiplier
        FW.writeInt( Integer.parseInt( data[62] ) ); // Damage Adder
        FW.writeBoolean( Boolean.parseBoolean( data[63] ) ); // Can Alloc HD
        FW.writeBoolean( Boolean.parseBoolean( data[64] ) ); // Can Alloc CT
        FW.writeBoolean( Boolean.parseBoolean( data[65] ) ); // Can Alloc Torso
        FW.writeBoolean( Boolean.parseBoolean( data[66] ) ); // Can Alloc Arms
        FW.writeBoolean( Boolean.parseBoolean( data[67] ) ); // Can Alloc Legs
        FW.writeBoolean( Boolean.parseBoolean( data[68] ) ); // Can Split
        FW.writeBoolean( Boolean.parseBoolean( data[69] ) ); // Requires Lower Arm
        FW.writeBoolean( Boolean.parseBoolean( data[70] ) ); // Requires Hand
        FW.writeBoolean( Boolean.parseBoolean( data[71] ) ); // Replaces Lower Arm
        FW.writeBoolean( Boolean.parseBoolean( data[72] ) ); // Replaces Hand
        FW.writeBoolean( Boolean.parseBoolean( data[73] ) ); // Requires Fusion
        FW.writeBoolean( Boolean.parseBoolean( data[74] ) ); // Requires Nuclear
        FW.writeBoolean( Boolean.parseBoolean( data[75] ) ); // Requires Power Amps
        FW.writeInt( Integer.parseInt( data[76] ) ); // Physical Weapon Class
        FW.writeUTF( data[77] ); // Book Reference
        FW.writeUTF( data[78] ); // Battleforce Abilities
        if( Boolean.parseBoolean( data[79] ) ) {
            FW.writeBoolean( true ); // has a mech modifier
            FW.writeInt( Integer.parseInt( data[80] ) ); // Walking Adder
            FW.writeInt( Integer.parseInt( data[81] ) ); // Running Adder
            FW.writeInt( Integer.parseInt( data[82] ) ); // Jumping Adder
            FW.writeDouble( Double.parseDouble( data[83] ) ); // Running Multiplier
            FW.writeInt( Integer.parseInt( data[84] ) ); // PSR Modifier
            FW.writeInt( Integer.parseInt( data[85] ) ); // GSR Modifier
            FW.writeInt( Integer.parseInt( data[86] ) ); // Heat Adder
            FW.writeDouble( Double.parseDouble( data[87] ) ); // Defensive BV Bonus
            FW.writeDouble( Double.parseDouble( data[88] ) ); // Minimum Defensive BV Bonus
            FW.writeDouble( Double.parseDouble( data[89] ) ); // Armor Multiplier
            FW.writeDouble( Double.parseDouble( data[90] ) ); // Internal Multiplier
            FW.writeBoolean( Boolean.parseBoolean( data[91] ) ); // Use for BV Movement
            FW.writeBoolean( Boolean.parseBoolean( data[92] ) ); // Use for BV Heat
        } else {
            FW.writeBoolean( false ); // doesn't have a mech modifier
        }
    }

/**
 * Processes the given equipment string from a CSV file into binary data.
 * Output is fed to the given DataOutputStream.
 *
 * @param FW The DataOutputStream to write to.
 * @param read The delimited string to read from.  One full line of data.
 * @param delim The text delimiter used in the CSV file.  One character only.
 * @throws java.lang.Exception
 */
    private void ProcessEquipmentString( DataOutputStream FW, String read, String delim ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming semi-colon delimited.
        String[] data = read.split( delim );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.
        FW.writeUTF( data[0] ); // Lookup Name
        FW.writeUTF( data[1] ); // Actual Name
        FW.writeUTF( data[2] ); // Crit Name
        FW.writeUTF( data[3] ); // Chat Name
        FW.writeUTF( data[4] ); // MM Name
        FW.writeUTF( data[5] ); // Type
        FW.writeUTF( data[6] ); // Specials
        ProcessAvailableCodeString( FW, data, 7 );
        
        FW.writeDouble( Double.parseDouble( data[47] ) ); // Tonnage
        FW.writeBoolean( Boolean.parseBoolean( data[48] ) ); // Is Variable Tonnage
        FW.writeDouble( Double.parseDouble( data[49] ) ); // Variable Increment
        FW.writeDouble( Double.parseDouble( data[50] ) ); // Minimum Tonnage
        FW.writeDouble( Double.parseDouble( data[51] ) ); // Maximum Tonnage
        FW.writeDouble( Double.parseDouble( data[52] ) ); // Cost
        FW.writeDouble( Double.parseDouble( data[53] ) ); // Cost Per Ton
        FW.writeDouble( Double.parseDouble( data[54] ) ); // OBV
        FW.writeDouble( Double.parseDouble( data[55] ) ); // DBV
        FW.writeInt( Integer.parseInt( data[56] ) ); // NumCrits
        FW.writeDouble( Double.parseDouble( data[57] ) ); // Tons Per Crit
        FW.writeInt( Integer.parseInt( data[58] ) ); // Vspc
        FW.writeInt( Integer.parseInt( data[59] ) ); // Heat
        FW.writeInt( Integer.parseInt( data[60] ) ); // Short Range
        FW.writeInt( Integer.parseInt( data[61] ) ); // Medium Range
        FW.writeInt( Integer.parseInt( data[62] ) ); // Long Range
        FW.writeBoolean( Boolean.parseBoolean( data[63] ) ); // Has Ammo
        FW.writeInt( Integer.parseInt( data[64] ) ); // Lot Size
        FW.writeInt( Integer.parseInt( data[65] ) ); // Ammo Index
        FW.writeBoolean( Boolean.parseBoolean( data[66] ) ); // Can Alloc HD
        FW.writeBoolean( Boolean.parseBoolean( data[67] ) ); // Can Alloc CT
        FW.writeBoolean( Boolean.parseBoolean( data[68] ) ); // Can Alloc Torso
        FW.writeBoolean( Boolean.parseBoolean( data[69] ) ); // Can Alloc Arms
        FW.writeBoolean( Boolean.parseBoolean( data[70] ) ); // Can Alloc Legs
        FW.writeBoolean( Boolean.parseBoolean( data[71] ) ); // Can Split
        FW.writeBoolean( Boolean.parseBoolean( data[72] ) ); // Requires Quad
        FW.writeInt( Integer.parseInt( data[73] ) ); // Number Allowed Per Mech
        FW.writeBoolean( Boolean.parseBoolean( data[74] ) ); // Can alloc front
        FW.writeBoolean( Boolean.parseBoolean( data[75] ) ); // Can alloc sides
        FW.writeBoolean( Boolean.parseBoolean( data[76] ) ); // Can alloc rear
        FW.writeBoolean( Boolean.parseBoolean( data[77] ) ); // Can alloc turret
        FW.writeBoolean( Boolean.parseBoolean( data[78] ) ); // Can alloc body
        FW.writeBoolean( Boolean.parseBoolean( data[79] ) ); // Can Mount Rear
        FW.writeBoolean( Boolean.parseBoolean( data[80] ) ); // Explosive
        FW.writeUTF( data[81] ); // Book Reference
        FW.writeUTF( data[82] ); // Battleforce Abilities
        int numexceptions = Integer.parseInt( data[83] );
        FW.writeInt( numexceptions ); // number of exceptions to read
        for( int i = 0; i < numexceptions; i++ ) {
            FW.writeUTF( data[84+i] ); // each exception
        }
    }

/**
 * Processes the given ammo string from a CSV file into binary data.  Output
 * is fed to the given DataOutputStream.
 * 
 * @param FW The DataOutputStream to write to.
 * @param read The delimited string to read from.  One full line of data.
 * @param delim The text delimiter used in the CSV file.  One character only.
 * @throws java.lang.Exception
 */
    private void ProcessAmmoString( DataOutputStream FW, String read, String delim ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming semi-colon delimited.
        String[] data = read.split( delim );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.

        // basic ammo info
        FW.writeUTF( data[0] ); // Actual Name
        FW.writeUTF( data[1] ); // Crit Name
        FW.writeUTF( data[2] ); // Lookup Name
        FW.writeUTF( data[3] ); // MM Name
        FW.writeInt( Integer.parseInt( data[4] ) ); // IDX
        // Availability Code Starts Here
        ProcessAvailableCodeString( FW, data, 5 );
        // Meat of the ammo stats start here
        FW.writeDouble( Double.parseDouble( data[45] ) ); // Tonnage
        FW.writeDouble( Double.parseDouble( data[46] ) ); // Cost
        FW.writeDouble( Double.parseDouble( data[47] ) ); // OBV
        FW.writeDouble( Double.parseDouble( data[48] ) ); // DBV
        FW.writeInt( Integer.parseInt( data[49] ) ); // To Hit S
        FW.writeInt( Integer.parseInt( data[50] ) ); // M
        FW.writeInt( Integer.parseInt( data[51] ) ); // L
        FW.writeInt( Integer.parseInt( data[52] ) ); // Damage S
        FW.writeInt( Integer.parseInt( data[53] ) ); // M
        FW.writeInt( Integer.parseInt( data[54] ) ); // L
        FW.writeBoolean( Boolean.parseBoolean( data[55] ) ); // Clustered
        FW.writeInt( Integer.parseInt( data[56] ) ); // Cluster
        FW.writeInt( Integer.parseInt( data[57] ) ); // Group
        FW.writeInt( Integer.parseInt( data[58] ) ); // Range Min
        FW.writeInt( Integer.parseInt( data[59] ) ); // S
        FW.writeInt( Integer.parseInt( data[60] ) ); // M
        FW.writeInt( Integer.parseInt( data[61] ) ); // L
        FW.writeInt( Integer.parseInt( data[62] ) ); // Lot Size
        FW.writeBoolean( Boolean.parseBoolean( data[63] ) ); // Explosive
        FW.writeInt( Integer.parseInt( data[64] ) ); // Weapon Class
        FW.writeInt( Integer.parseInt( data[65] ) ); // FCS Type
        FW.writeUTF( data[66] ); // Book Reference
    }


/**
 * Processes the given ammo string from a CSV file into binary data.  Output
 * is fed to the given DataOutputStream.
 *
 * @param FW The DataOutputStream to write to.
 * @param read The delimited string to read from.  One full line of data.
 * @param delim The text delimiter used in the CSV file.  One character only.
 * @throws java.lang.Exception
 */
    private void ProcessQuirksString( DataOutputStream FW, String read, String delim ) throws Exception {
        // here we're going to read the data string and output it to binary form
        // Assuming semi-colon delimited.
        String[] data = read.split( delim );
        // this is very unsafe, but we're going to assume that all the information
        // is correct and in the proper order.

        // basic ammo info
        FW.writeUTF( data[0] ); // Actual Name
        FW.writeBoolean( Boolean.parseBoolean( data[1] ) );  // Positive or Negative
        FW.writeInt( Integer.parseInt( data[2]) ); // Cost
        FW.writeBoolean( Boolean.parseBoolean( data[3] ) ); // BM
        FW.writeBoolean( Boolean.parseBoolean( data[4] ) ); // IM
        FW.writeBoolean( Boolean.parseBoolean( data[5] ) ); // CV
        FW.writeBoolean( Boolean.parseBoolean( data[6] ) ); // BA
        FW.writeBoolean( Boolean.parseBoolean( data[7] ) ); // AF
        FW.writeBoolean( Boolean.parseBoolean( data[8] ) ); // CF
        FW.writeBoolean( Boolean.parseBoolean( data[9] ) ); // DS
        FW.writeBoolean( Boolean.parseBoolean( data[10] ) ); // JS
        FW.writeBoolean( Boolean.parseBoolean( data[11] ) ); // WS
        FW.writeBoolean( Boolean.parseBoolean( data[12] ) ); // SS
        FW.writeBoolean( Boolean.parseBoolean( data[13] ) ); // PM
        FW.writeBoolean( Boolean.parseBoolean( data[14] ) ); //Is Variable
        if (data.length == 16)
            FW.writeUTF( data[15].trim() ); // Description
        else
            FW.writeUTF("");
    }

    private void ProcessAvailableCodeString( DataOutputStream FW, String[] data, int sindex ) throws Exception {
        FW.writeInt( Integer.parseInt( data[sindex] ) ); // Tbase
        FW.writeInt( Integer.parseInt( data[sindex+1] ) ); // BM
        FW.writeInt( Integer.parseInt( data[sindex+2] ) ); // IM
        FW.writeInt( Integer.parseInt( data[sindex+3] ) ); // CV
        FW.writeInt( Integer.parseInt( data[sindex+4] ) ); // AF
        FW.writeInt( Integer.parseInt( data[sindex+5] ) ); // CF
        // Inner Sphere Availability
        FW.writeChar( data[sindex+6].charAt( 0 ) ); // Tech
        FW.writeChar( data[sindex+7].charAt( 0 ) ); // SL
        FW.writeChar( data[sindex+8].charAt( 0 ) ); // SW
        FW.writeChar( data[sindex+9].charAt( 0 ) ); // CI
        FW.writeChar( data[sindex+10].charAt( 0 ) ); // DA
        //Need to increment from here....
        
        FW.writeInt( Integer.parseInt( data[sindex+11] ) ); // Intro
        FW.writeUTF( data[sindex+12] ); // Ifac
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+13] ) ); // Extinct
        FW.writeInt( Integer.parseInt( data[sindex+14] ) ); // Eyear
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+15] ) ); // ReIntro
        FW.writeInt( Integer.parseInt( data[sindex+16] ) ); // RIYear
        FW.writeUTF( data[sindex+17] ); // RIFac
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+18] ) ); // Prototype
        FW.writeInt( Integer.parseInt( data[sindex+19] ) ); // R&DYear
        FW.writeUTF( data[sindex+20] ); // R&DFac
        FW.writeInt( Integer.parseInt( data[sindex+21] ) ); // Pyear
        FW.writeUTF( data[sindex+22] ); // Pfac
        // Clan Availability
        FW.writeChar( data[sindex+23].charAt( 0 ) ); // Tech
        FW.writeChar( data[sindex+24].charAt( 0 ) ); // SL
        FW.writeChar( data[sindex+25].charAt( 0 ) ); // SW
        FW.writeChar( data[sindex+26].charAt( 0 ) ); // CI
        FW.writeChar( data[sindex+27].charAt( 0 ) ); // DA
        
        FW.writeInt( Integer.parseInt( data[sindex+28] ) ); // Intro
        FW.writeUTF( data[sindex+29] ); // Ifac
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+30] ) ); // Extinct
        FW.writeInt( Integer.parseInt( data[sindex+31] ) ); // Eyear
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+32] ) ); // ReIntro
        FW.writeInt( Integer.parseInt( data[sindex+33] ) ); // RIYear
        FW.writeUTF( data[sindex+34] ); // RIFac
        FW.writeBoolean( Boolean.parseBoolean( data[sindex+35] ) ); // Prototype
        FW.writeInt( Integer.parseInt( data[sindex+36] ) ); // R&DYear
        FW.writeUTF( data[sindex+37] ); // R&DFac
        FW.writeInt( Integer.parseInt( data[sindex+38] ) ); // Pyear
        FW.writeUTF( data[sindex+39] ); // Pfac
    }
}