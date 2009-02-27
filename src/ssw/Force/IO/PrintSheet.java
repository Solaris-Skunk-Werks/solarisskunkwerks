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

package ssw.Force.IO;

import ssw.Force.*;
import ssw.Force.Common.*;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class PrintSheet implements Printable {
    private Graphics2D Graphic;
    private Force force;

    public int pageWidth = 0;
    public int pageHeight = 0;
    public int currentX = 0;
    public int currentY = 0;

    public PrintSheet(int Width, int Height, Force force){
        pageWidth = Width;
        pageHeight = Height;
        this.force = force;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if( pageIndex >= 1 ) { return Printable.NO_SUCH_PAGE; }
        Graphic = (Graphics2D) graphics;
        Graphic.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );
        PreparePrint();
        return Printable.PAGE_EXISTS;
    }

    private void PreparePrint() {
        Reset();
        currentY += 15;
        setFont(CommonTools.TitleFont);
        WriteStr("Battle Value Calculator", 0);
        NewLine();
        setFont(CommonTools.PlainFont);
        WriteLine();
        NewLine();
        NewLine();
        force.RenderPrint(this);
    }

    public void WriteStr(String line, int changeX) {
        Graphic.drawString(line, currentX, currentY);
        currentX += changeX;
    }

    public void WriteLine() {
        currentY -= 5;
        Graphic.drawLine(0, currentY, pageWidth, currentY);
        currentY += 10;
    }

    public void setFont(Font font) {
        Graphic.setFont(font);
    }

    public void NewLine() {
        currentX = 0;
        currentY += 10;
    }

    public void ResetX() {
        currentX = 0;
    }

    public void ResetY() {
        currentY = 0;
    }

    public void Reset() {
        currentX = 0;
        currentY = 0;
    }

}
