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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;
import common.*;
import battleforce.BattleForceStats;
import components.*;

public class CombatVehicleWriter {
    private CombatVehicle CurVee;
    private String tab = "    ";
    private String NL = System.getProperty( "line.separator" );

    public CombatVehicleWriter()
    {
    }

    public CombatVehicleWriter( CombatVehicle v ) {
        CurVee = v;
    }

    public void WriteXML( String filename ) throws IOException {
        //BufferedWriter FR = new BufferedWriter( new FileWriter( filename ) );
        BufferedWriter FR = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( filename ), "UTF-8" ) );

        // beginning of an XML file:
        FR.write( "<?xml version=\"1.0\" encoding =\"UTF-8\"?>" );
        FR.newLine();

        WriteXML(FR);

        FR.close();
    }


    public void WriteXML( BufferedWriter FR ) throws IOException {
        // start parsing the mech
        FR.write( "<combatvehicle name=\"" + FileCommon.EncodeFluff( CurVee.GetName() ) + "\" model=\"" + FileCommon.EncodeFluff( CurVee.GetModel() ) + "\" tons=\"" + CurVee.GetTonnage() + "\" omnivehicle=\"" + FileCommon.GetBoolean( CurVee.IsOmni() ) + "\" solaris7id=\"" + CurVee.GetSolaris7ID() + "\" solaris7imageid=\"" + CurVee.GetSolaris7ImageID() + "\" sswimage=\"" + CurVee.GetSSWImage() + "\">" );
        FR.newLine();

        // version number for new files
        FR.write( tab + "<ssw_savefile_version>1</ssw_savefile_version>" );
        FR.newLine();

        // add the battle value if this is not an omnimech.  otherwise, we'll
        // add the battle value for each omni loadout.  NOTE: This value is never
        // used by SSW since the BV is dynamically calculated.  This is purely for
        // other programs that may want to use the program.
        if( ! CurVee.IsOmni() ) {
            FR.write( tab + "<battle_value>" + CurVee.GetCurrentBV() + "</battle_value>" );
            FR.newLine();
        }

        FR.write( tab + "<cost>" + CurVee.GetTotalCost() + "</cost>" );
        FR.newLine();

        FR.write( tab + "<rules_level>" + CurVee.GetBaseRulesLevel() + "</rules_level>" );
        FR.newLine();

        if( CurVee.UsingFractionalAccounting() ) {
            FR.write( tab + "<fractional />" );
            FR.newLine();
        }

        FR.write( tab + "<era>" + CurVee.GetBaseEra() + "</era>" );
        FR.newLine();
        FR.write( tab + "<productionera>" + CurVee.GetBaseProductionEra() + "</productionera>" );
        FR.newLine();

        FR.write( tab + "<techbase manufacturer=\"" + FileCommon.EncodeFluff( CurVee.GetCompany() ) + "\" location=\"" + FileCommon.EncodeFluff( CurVee.GetLocation() ) + "\">" + GetBaseTechbase() + "</techbase>" );
        FR.newLine();

        FR.write( tab + "<year restricted=\"" + FileCommon.GetBoolean( CurVee.IsYearRestricted() ) + "\">" + CurVee.GetBaseYear() + "</year>" );
        FR.newLine();
    }



    private String GetBaseTechbase() {
        return AvailableCode.TechBaseSTR[CurVee.GetBaseTechbase()];
    }

    private String GetTechbase() {
        return AvailableCode.TechBaseSTR[CurVee.GetTechbase()];
    }
}
