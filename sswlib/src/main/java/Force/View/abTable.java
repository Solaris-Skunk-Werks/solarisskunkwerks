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
import java.util.List;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import list.view.Column;

public abstract class abTable extends AbstractTableModel {
    public ArrayList<Column> Columns = new ArrayList<Column>();
    public ArrayList<Column> SortFields = new ArrayList<Column>();
    public Force force;
    private TableRowSorter sorter;

    public abstract void setForce( Force f );
    public abstract abTable Create();
    public abstract abTable Create(Force f);
    public Force getForce() {
        return force;
    };

    public void setupTable( JTable tbl ) {
        //Create a sorting class and apply it to the list
        sorter = new TableRowSorter<abTable>(this);
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();

        if ( SortFields.size() > 0 ) {
            for ( Column col : SortFields ) {
                if ( col.isSortable ) {
                    sortKeys.add(new RowSorter.SortKey(col.Index, col.sortOrder));
                }
            }
        } else {
            for ( Column col : Columns ) {
                if ( col.isSortable ) {
                    sortKeys.add(new RowSorter.SortKey(col.Index, col.sortOrder));
                }
            }
        }
        sorter.setSortKeys(sortKeys);
        tbl.setRowSorter(sorter);

        for ( Column col : Columns ) {
            if ( col.preferredWidth > 0 ) {
                tbl.getColumnModel().getColumn(col.Index).setPreferredWidth(col.preferredWidth);
            }
        }
    }

    @Override
    public String getColumnName( int col ) { return Columns.get(col).Title; }
    public int getColumnCount() { return Columns.size(); }
    public int getRowCount() { return force.getUnits().size(); }
    @Override
    public Class getColumnClass(int c) { return Columns.get(c).classType; }
    public Object getClassOf( int row, int col ) { return Columns.get(col).classType; }
    @Override
    public boolean isCellEditable( int row, int col ) { return Columns.get(col).isEditable; }

    public TableRowSorter getSorter() {
        return sorter;
    }

    public void setSorter(TableRowSorter sorter) {
        this.sorter = sorter;
    }

}
