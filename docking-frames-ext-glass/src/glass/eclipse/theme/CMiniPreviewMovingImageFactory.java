package glass.eclipse.theme;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.dockable.*;
import bibliothek.gui.dock.title.*;
import bibliothek.gui.dock.util.*;
import glass.eclipse.theme.utils.*;


/**
 * A factory that makes a snapshot of the {@link Dockable} which will be represented
 * by its {@link MovingImage}.
 * @author Thomas Hilbert
 *
 */
public class CMiniPreviewMovingImageFactory implements DockableMovingImageFactory {
   /** the maximal size of the images created by this factory */
   private int thumbSize;

   /**
    * Creates a new factory.
    * @param max the maximal size of the images created by this factory
    */
   public CMiniPreviewMovingImageFactory(int thumbSize) {
      this.thumbSize = thumbSize;
   }

   public MovingImage create(DockController controller, DockTitle snatched) {
      return create(controller, snatched.getDockable());
   }

   /**
    * This method creates a new image that contains the contents of <code>dockable</code>.
    * @param controller the controller for which the image is made
    * @param dockable the element whose image should be taken
    * @return an image of <code>dockable</code> which is not larger than the
    * maximum {@link Dimension} that was given to this factory in the 
    * constructor.
    * @see AWTComponentCaptureStrategy
    */
   public BufferedImage createImageFrom(DockController controller, Dockable dockable) {
      Component c = dockable.getComponent();

      Dimension size = new Dimension(Math.max(1, c.getWidth()), Math.max(1, c.getHeight()));
      BufferedImage image = null;

      if ((size.width >= 10 && size.height >= 10)) {
         if (DockSwingUtilities.containsAWTComponents(c)) {
            image = controller.getProperties().get(AWTComponentCaptureStrategy.STRATEGY).createCapture(controller, c);
         } else {
            image = new BufferedImage(size.width, size.height, BufferedImage.TRANSLUCENT);
            Graphics2D g2d = image.createGraphics();
            c.paintAll(g2d);
            //            ((JComponent) c).paintComponents(g2d);
            g2d.dispose();
         }

         return (CGraphicUtils.CreateThumb(image, thumbSize));
      }
      if (image == null) {
         Icon icon = dockable.getTitleIcon();
         if (icon == null || icon.getIconHeight() < 1 || icon.getIconWidth() < 1) {
            return null;
         }

         image = new BufferedImage(icon.getIconWidth() + 2, icon.getIconHeight() + 2, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g = image.createGraphics();
         g.setColor(c.getBackground());
         g.fillRect(0, 0, image.getWidth(), image.getHeight());
         icon.paintIcon(c, g, 1, 1);
         g.dispose();
      }

      return image;
   }

   public MovingImage create(DockController controller, Dockable dockable) {
      BufferedImage image = createImageFrom(controller, dockable);

      TrueMovingImage moving = new TrueMovingImage();
      moving.setImage(image);
      return moving;
   }
}
