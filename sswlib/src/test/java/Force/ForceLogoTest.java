package Force;

import filehandlers.ImageTracker;
import org.junit.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ForceLogoTest {

    @Test
    public void clearingForceLogoPathClearsCachedLogoImage() {
        Image logo = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Force force = new Force();
        force.LogoPath = "first-logo.png";
        TestImageTracker imageTracker = new TestImageTracker(logo);

        assertSame(logo, force.getLogo(imageTracker));

        force.LogoPath = "";
        force.loadLogo(imageTracker);

        assertNull(force.getLogo(imageTracker));
    }

    @Test
    public void blankGroupLogoDynamicallyInheritsForceLogoPath() {
        Force force = new Force();
        force.LogoPath = "first-logo.png";
        Group group = new Group("Alpha", force.getType(), force);
        group.setLogo("");

        assertEquals("first-logo.png", group.getLogo());

        force.LogoPath = "second-logo.png";

        assertEquals("second-logo.png", group.getLogo());
    }

    @Test
    public void explicitGroupLogoOverridesForceLogoPath() {
        Force force = new Force();
        force.LogoPath = "force-logo.png";
        Group group = new Group("Alpha", force.getType(), force);
        group.setLogo("group-logo.png");

        assertEquals("group-logo.png", group.getLogo());
    }

    private static class TestImageTracker extends ImageTracker {
        private final Image image;

        private TestImageTracker(Image image) {
            this.image = image;
        }

        @Override
        public Image getImage(String filename) {
            return filename.isEmpty() ? null : image;
        }
    }
}
