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

import Force.Force;
import Force.Unit;
import javax.swing.SortOrder;
import list.view.Column;

public class tbChatInfo extends abTable {
    public tbChatInfo() {
        Columns.add(new Column( 0, "Unit", "TypeModel", 100 ));
        Columns.add(new Column( 1, "Info", "ChatInfo", 400, false));
        Columns.add(new Column( 2, "Base BV", "BaseBV", 20, Integer.class ));
        Columns.add(new Column( 3, "Adj BV", "TotalBV", false, 20, Integer.class, true, SortOrder.ASCENDING ));
    }

    public tbChatInfo( Force f ) {
        this();
        force = f;
    }

    public void setForce( Force f ) {
        this.force = f;
    }

    public tbChatInfo Create() {
        return new tbChatInfo();
    }

    public tbChatInfo Create( Force f ) {
        return new tbChatInfo(f);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Unit u = (Unit) force.getUnits().get( rowIndex );
        switch( columnIndex ) {
            case 0:
                return u.TypeModel;
            case 1:
                return u.Info;
            case 2:
                return String.format( "%1$,.0f", u.BaseBV );
            case 3:
                return String.format( "%1$,.0f", u.TotalBV );
        }
        return null;
    }
}
