package Print;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public final class RasterizedPrintable implements Printable {
    private static final int RASTER_SCALE = 2;

    private final Printable source;

    public RasterizedPrintable(Printable source) {
        this.source = source;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        BufferedImage page = renderPage(pageFormat);
        if (page == null) {
            return NO_SUCH_PAGE;
        }
        graphics.drawImage(page, 0, 0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight(), null);
        return PAGE_EXISTS;
    }

    private BufferedImage renderPage(PageFormat pageFormat) throws PrinterException {
        int width = (int) Math.ceil(pageFormat.getWidth() * RASTER_SCALE);
        int height = (int) Math.ceil(pageFormat.getHeight() * RASTER_SCALE);

        BufferedImage page = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D pageGraphics = page.createGraphics();
        try {
            pageGraphics.setColor(Color.WHITE);
            pageGraphics.fillRect(0, 0, width, height);
            pageGraphics.scale(RASTER_SCALE, RASTER_SCALE);
            pageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            pageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int result = source.print(pageGraphics, pageFormat, 0);
            if (result == NO_SUCH_PAGE) {
                return null;
            }
        } finally {
            pageGraphics.dispose();
        }

        return page;
    }
}
