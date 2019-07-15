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

package Print;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class PaperSize {
    public int PaperWidth;
    public int PaperHeight;
    public int ImageableX = 18;
    public int ImageableY = 18;
    public int ImageableWidth;
    public int ImageableHeight;

    public PaperSize(int pWidth, int pHeight, int iX, int iY, int iWidth, int iHeight) {
        PaperWidth = pWidth;
        PaperHeight = pHeight;
        ImageableX = iX;
        ImageableY = iY;
        ImageableWidth = iWidth;
        ImageableHeight = iHeight;
    }

    public PaperSize(int PixelWidth, int PixelHeight) {
        this(PixelWidth, PixelHeight, 18, 18, PixelWidth-36, PixelHeight-36);
    }

    public PaperSize(double InchWidth, double InchHeight) {
        PaperWidth = getInchesInPixels(InchWidth);
        PaperHeight = getInchesInPixels(InchHeight);
        ImageableWidth = ( PaperWidth - ( ImageableX * 2 ) );
        ImageableHeight = ( PaperHeight - ( ImageableY * 2 ) );
    }

    public static final int getInchesInPixels(double Inch) {
        return (int) Math.round((Inch / 0.0139));
    }
    
    public PageFormat toPage() {
        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(PaperWidth, PaperHeight);
        paper.setImageableArea(ImageableX, ImageableY, ImageableWidth, ImageableHeight);
        format.setPaper(paper);
        return format;
    }

}
