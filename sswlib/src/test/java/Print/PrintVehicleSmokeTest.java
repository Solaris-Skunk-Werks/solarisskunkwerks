package Print;

import components.CombatVehicle;
import filehandlers.CVReader;
import filehandlers.ImageTracker;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrintVehicleSmokeTest {

    @BeforeClass
    public static void useHeadlessAwt() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void rendersMusketeerRecordSheetWithoutPrintSpooler() throws Exception {
        CombatVehicle vehicle = new CVReader().ReadUnit(resourceFile("vehicles/Musketeer Hover Tank.saw").getAbsolutePath());
        ImageTracker images = new ImageTracker();
        images.preLoadVehicleImages();
        PrintVehicle printable = new PrintVehicle(vehicle, images);

        RenderedPage renderedPage = renderPage(printable);

        assertEquals(Printable.PAGE_EXISTS, renderedPage.printResult);
        assertTrue("Vehicle record sheet should contain rendered content", renderedPage.hasInk);
    }

    private RenderedPage renderPage(Printable printable) throws Exception {
        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(612, 792);
        paper.setImageableArea(0, 0, 612, 792);
        format.setPaper(paper);

        BufferedImage page = new BufferedImage(
                (int) Math.ceil(format.getWidth()),
                (int) Math.ceil(format.getHeight()),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = page.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, page.getWidth(), page.getHeight());
            int printResult = printable.print(graphics, format, 0);
            return new RenderedPage(printResult, hasInk(page));
        } finally {
            graphics.dispose();
        }
    }

    private boolean hasInk(BufferedImage page) {
        for (int y = 0; y < page.getHeight(); y++) {
            for (int x = 0; x < page.getWidth(); x++) {
                if ((page.getRGB(x, y) & 0x00ffffff) != 0x00ffffff) {
                    return true;
                }
            }
        }
        return false;
    }

    private File resourceFile(String path) throws Exception {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Missing test resource: " + path);
        }
        return new File(resource.toURI());
    }

    private static class RenderedPage {
        private final int printResult;
        private final boolean hasInk;

        private RenderedPage(int printResult, boolean hasInk) {
            this.printResult = printResult;
            this.hasInk = hasInk;
        }
    }
}
