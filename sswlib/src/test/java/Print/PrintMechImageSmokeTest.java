package Print;

import components.Mech;
import filehandlers.ImageTracker;
import filehandlers.MechReader;
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

public class PrintMechImageSmokeTest {

    @BeforeClass
    public static void useHeadlessAwt() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void rendersMechImageFromSavedImagePathWhenEnabled() throws Exception {
        Mech mech = new MechReader().ReadMech(resourceFile("mechs/Balgaire.ssw").getAbsolutePath());
        mech.SetSSWImage(resourceFile("images/Balgaire.png").getAbsolutePath());

        ImageTracker images = new ImageTracker();
        images.preLoadMechImages();
        PrintMech withoutImage = new PrintMech(mech, images);
        PrintMech withImage = new PrintMech(mech, images);
        withImage.setPrintMech(true);

        RenderedPage baselinePage = renderPage(withoutImage);
        RenderedPage imagePage = renderPage(withImage);

        assertEquals(Printable.PAGE_EXISTS, baselinePage.printResult);
        assertEquals(Printable.PAGE_EXISTS, imagePage.printResult);
        assertTrue("Balgaire image should change the rendered record sheet",
                countPixelDifferences(baselinePage.page, imagePage.page) > 1000);
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
            return new RenderedPage(printResult, page);
        } finally {
            graphics.dispose();
        }
    }

    private int countPixelDifferences(BufferedImage baseline, BufferedImage candidate) {
        int differences = 0;
        for (int y = 0; y < baseline.getHeight(); y++) {
            for (int x = 0; x < baseline.getWidth(); x++) {
                Color baselineColor = new Color(baseline.getRGB(x, y), true);
                Color candidateColor = new Color(candidate.getRGB(x, y), true);
                if (Math.abs(baselineColor.getRed() - candidateColor.getRed()) > 20
                        || Math.abs(baselineColor.getGreen() - candidateColor.getGreen()) > 20
                        || Math.abs(baselineColor.getBlue() - candidateColor.getBlue()) > 20) {
                    differences++;
                }
            }
        }
        return differences;
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
        private final BufferedImage page;

        private RenderedPage(int printResult, BufferedImage page) {
            this.printResult = printResult;
            this.page = page;
        }
    }
}
