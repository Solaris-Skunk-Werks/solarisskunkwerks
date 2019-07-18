/*
Copyright (c) 2008, George Blouin Jr. (skyhigh@solaris7.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.
    * Neither the name of George Blouin Jr nor the names of contributors may be
used to endorse or promote products derived from this software without specific prior
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package Force.View;

import Force.*;
import common.*;
import javax.swing.SortOrder;
import list.view.Column;

public class tbFactors extends abTable {
    public tbFactors() {
        Columns.add(new Column( 0, "Unit", "TypeModel", 150 ));
        Columns.add(new Column( 1, "Type", "UnitType", 50 ));
        Columns.add(new Column( 2, "Prb", "Prb", 10, Integer.class ));
        Columns.add(new Column( 3, "ECM", "ECM", 10, Integer.class ));
        Columns.add(new Column( 4, "Spd", "Spd", 10, Integer.class ));
        Columns.add(new Column( 5, "Jmp", "Jmp", 10, Integer.class ));
        Columns.add(new Column( 6, "TSM", "TSM", 10, Integer.class ));
        Columns.add(new Column( 7, "Phys", "Phys", 10, Integer.class ));
        Columns.add(new Column( 8, "Armr", "Arm", 10, Integer.class ));
        Columns.add(new Column( 9, "TC", "TC", 10, Integer.class ));
        Columns.add(new Column( 10, "8+", "8+", 10, Integer.class ));
        Columns.add(new Column( 11, "10+", "10+", 10, Integer.class ));
        Columns.add(new Column( 12, "Hd Cap", "Head", 10, Integer.class, true, SortOrder.DESCENDING ));
        Columns.add(new Column( 13, "Tot Dmg", "Tot Dmg", 10, Integer.class, true, SortOrder.DESCENDING ));
        Columns.add(new Column( 14, "Base BV", "BaseBV", 30, Integer.class ));
        Columns.add(new Column( 15, "Adj PV", "TotalBV", 30, Integer.class ));

        SortFields.add(Columns.get(12));
        SortFields.add(Columns.get(11));
    }

    public tbFactors( Force f ) {
        this();
        force = f;
    }

    public void setForce( Force f ) {
        this.force = f;
    }

    public tbFactors Create() {
        return new tbFactors();
    }

    public tbFactors Create( Force f ) {
        return new tbFactors(f);
    }

    public Object getValueAt( int row, int col ) {
        Unit u = force.getUnits().get( row );
        switch( col ) {
            case 0:
                return u.getFullName();
            case 1:
                return CommonTools.UnitTypes[u.UnitType];
            case 2:
                return u.Probe;
            case 3:
                return u.ECM;
            case 4:
                return u.Speed;
            case 5:
                return u.Jump;
            case 6:
                return u.TSM;
            case 7:
                return u.Physical;
            case 8:
                return u.Armor;
            case 9:
                return u.TC;
            case 10:
                return u.Eight;
            case 11:
                return u.Ten;
            case 12:
                return u.HeadCap;
            case 13:
                return u.Damage;
            case 14:
                return (int) u.BaseBV;
            case 15:
                return (int) u.TotalBV;
        }
        return null;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
        return;
    }
}
