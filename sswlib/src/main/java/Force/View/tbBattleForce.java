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

public class tbBattleForce extends abTable {
    public tbBattleForce() {
        Columns.add(new Column( 0, "Unit", "TypeModel", 150 ));
        Columns.add(new Column( 1, "Type", "UnitType", 50 ));
        Columns.add(new Column( 2, "Lance/Star", "Group", true, 0, String.class, true, SortOrder.ASCENDING));
        Columns.add(new Column( 3, "MV", "MV", 10, Integer.class ));
        Columns.add(new Column( 4, "S", "S", 10, Integer.class ));
        Columns.add(new Column( 5, "M", "M", 10, Integer.class ));
        Columns.add(new Column( 6, "L", "L", 10, Integer.class ));
        Columns.add(new Column( 7, "E", "E", 10, Integer.class ));
        Columns.add(new Column( 8, "Wt", "Wt", 10, Integer.class ));
        Columns.add(new Column( 9, "OV", "OV", 10, Integer.class ));
        Columns.add(new Column( 10, "A/S", "Armor", 20, Integer.class ));
        Columns.add(new Column( 11, "SA", "SA", 150, String.class ));
        Columns.add(new Column( 12, "Skill", "Skill", true, 20, Integer.class, false, SortOrder.ASCENDING ));
        Columns.add(new Column( 13, "Base PV", "BaseBV", 30, Integer.class ));
        Columns.add(new Column( 14, "Adj PV", "TotalBV", false, 40, Integer.class, true, SortOrder.ASCENDING ));
    }

    public tbBattleForce( Force f ) {
        this();
        force = f;
    }

    public void setForce( Force f ) {
        this.force = f;
    }

    public tbBattleForce Create() {
        return new tbBattleForce();
    }

    public tbBattleForce Create( Force f ) {
        return new tbBattleForce(f);
    }

    public Object getValueAt( int row, int col ) {
        Unit u = force.getUnits().get( row );
        switch( col ) {
            case 0:
                return u.getBFStats().getElement();
            case 1:
                return CommonTools.UnitTypes[u.UnitType];
            case 2:
                return u.getGroup();
            case 3:
                return u.getBFStats().getMovement();
            case 4:
                return u.getBFStats().getCombinedShort();
            case 5:
                return u.getBFStats().getCombinedMedium();
            case 6:
                return u.getBFStats().getCombinedLong();
            case 7:
                return u.getBFStats().getCombinedExtreme();
            case 8:
                return u.getBFStats().getWeight();
            case 9:
                return u.getBFStats().getOverheat();
            case 10:
                return u.getBFStats().getArmor() + " (" + u.getBFStats().getInternal() + ")";
            case 11:
                return u.getBFStats().getAbilitiesString();
            case 12:
                return u.getBFStats().getSkill();
            case 13:
                return u.getBFStats().getBasePV();
            case 14:
                return u.getBFStats().getPointValue();
        }
        return null;
    }
    @Override
    public void setValueAt( Object value, int row, int col ) {
        Unit u = force.getUnits().get( row );
        switch( col ) {
            case 2:
                u.setGroup(value.toString());
                force.GroupUnit(u);
                break;
            case 12:
                String Skills = u.getBFStats().determineGP(Integer.parseInt(value.toString()));
                u.setGP(Integer.parseInt(Skills.split("/")[0]), Integer.parseInt(Skills.split("/")[1]));
                break;
        }
        force.isDirty = true;
        u.Refresh();
        force.RefreshBV();
    }
}
