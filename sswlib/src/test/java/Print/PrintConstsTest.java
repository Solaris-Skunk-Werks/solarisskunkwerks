package Print;

import org.junit.Test;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrintConstsTest {

    @Test
    public void fitsWideDamageTextBeforeMinRangeColumn() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setFont(new Font("Dialog", Font.PLAIN, 12));
            String variableDamage = "2/1/1";
            int minColumnX = 159;
            int padding = 3;

            int damageX = PrintConsts.fitTextStartBeforeColumn(graphics, variableDamage, 136, minColumnX, padding);
            int damageRightEdge = damageX + graphics.getFontMetrics().stringWidth(variableDamage);

            assertTrue("Wide damage text should move left when needed", damageX < 136);
            assertTrue("Damage text should stop before the min-range column", damageRightEdge <= minColumnX - padding);
        } finally {
            graphics.dispose();
        }
    }

    @Test
    public void preservesDefaultStartForNarrowDamageText() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setFont(new Font("Dialog", Font.PLAIN, 12));

            int damageX = PrintConsts.fitTextStartBeforeColumn(graphics, "6", 136, 159, 3);

            assertEquals(136, damageX);
        } finally {
            graphics.dispose();
        }
    }
}
