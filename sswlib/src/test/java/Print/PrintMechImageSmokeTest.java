package Print;

import components.Mech;
import filehandlers.ImageTracker;
import filehandlers.MechReader;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.imageio.ImageIO;
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

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void useHeadlessAwt() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void rendersMechImageFromSavedImagePathWhenEnabled() throws Exception {
        File imageFile = folder.newFile("mech-image.png");
        writeSolidImage(imageFile, new Color(255, 0, 255));

        Mech mech = new MechReader().ReadMech(resourceFile("mechs/Balgaire.ssw").getAbsolutePath());
        mech.SetSSWImage(imageFile.getAbsolutePath());

        ImageTracker images = new ImageTracker();
        images.preLoadMechImages();
        PrintMech printable = new PrintMech(mech, images);
        printable.setPrintMech(true);

        RenderedPage renderedPage = renderPage(printable);

        assertEquals(Printable.PAGE_EXISTS, renderedPage.printResult);
        assertTrue("Mech image should be painted onto the record sheet", renderedPage.hasMagentaImagePixels);
    }

    private void writeSolidImage(File imageFile, Color color) throws Exception {
        BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(color);
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        } finally {
            graphics.dispose();
        }
        ImageIO.write(image, "png", imageFile);
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
            return new RenderedPage(printResult, hasMagentaPixels(page));
        } finally {
            graphics.dispose();
        }
    }

    private boolean hasMagentaPixels(BufferedImage page) {
        for (int y = 0; y < page.getHeight(); y++) {
            for (int x = 0; x < page.getWidth(); x++) {
                Color color = new Color(page.getRGB(x, y), true);
                if (color.getRed() > 220 && color.getBlue() > 220 && color.getGreen() < 40) {
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
        private final boolean hasMagentaImagePixels;

        private RenderedPage(int printResult, boolean hasMagentaImagePixels) {
            this.printResult = printResult;
            this.hasMagentaImagePixels = hasMagentaImagePixels;
        }
    }
}
