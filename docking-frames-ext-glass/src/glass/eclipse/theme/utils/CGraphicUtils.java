package glass.eclipse.theme.utils;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;


/**
 * CGraphicUtils.java
 *
 * Thomas Hilbert
 * 20.09.2009
 */
public class CGraphicUtils {

   /**
    * Gets a scaled instance of the source image.
    * Uses Graphics2D for scaling.
    * @param img
    * @param destw Target width, -1 if width should be in correct aspect ratio to target height.
    * @param desth Target height, -1 if height should be in correct aspect ratio to target width.
    * @param hint See RenderingHints.VALUE_INTERPOLATION_XXX
    * @return
    */
   public static BufferedImage getScaledInstance(BufferedImage img, int destw, int desth, Object hint) {
      Dimension d = getScaledSize(img.getWidth(), img.getHeight(), destw, desth);

      BufferedImage bimg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = bimg.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
      g2d.drawImage(img, 0, 0, d.width, d.height, null);
      g2d.dispose();

      return (bimg);
   }

   /**
    * Calculates the scaled size.
    * @param ow
    * @param oh
    * @param tw
    * @param th
    * @return
    */
   private static Dimension getScaledSize(int ow, int oh, int tw, int th) {
      if ((tw > -1 && th > -1)) {
         return new Dimension(tw, th);
      } else if (tw < 0 && th < 0) {
         return new Dimension(ow, oh);
      }

      float fAspect = (float) ow / (float) oh;

      if (tw == -1) {
         // change width
         tw = (int) (th * fAspect);
      } else if (th == -1) {
         // change height
         th = (int) (tw / fAspect);
      }

      return new Dimension(tw, th);
   }

   /**
    * Adds a two colored border to the given image.
    * @param src
    * @param outer
    * @param inner
    * @param colOuter
    * @param colInner
    * @return
    */
   public static BufferedImage addPhotoBorder(BufferedImage src, int outer, int inner, Color colOuter, Color colInner) {
      BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = img.createGraphics();

      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setColor(colOuter);
      g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
      g2d.setColor(colInner);
      g2d.fillRect(outer, outer, img.getWidth() - 2 * outer, img.getHeight() - 2 * outer);
      g2d.drawImage(src, outer + inner, outer + inner, img.getWidth() - 2 * (outer + inner), img.getHeight() - 2 * (outer + inner), null);

      g2d.dispose();

      return img;
   }

   /**
    * Creates a thumb of the specified image with the given thumb size.
    * @param img
    * @param thumbSize
    * @return
    */
   public static BufferedImage CreateThumb(BufferedImage img, int thumbSize) {
      try {
         if (img.getWidth() < thumbSize && img.getHeight() < thumbSize) {
            return (addPhotoBorder(img, 2, 1, Color.DARK_GRAY, Color.LIGHT_GRAY));
         }

         BufferedImage ret;

         if (img.getWidth() < img.getHeight()) {
            ret = getScaledInstance(img, -1, thumbSize, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         } else {
            ret = getScaledInstance(img, thumbSize, -1, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         }

         return (addPhotoBorder(ret, 2, 1, Color.DARK_GRAY, Color.LIGHT_GRAY));

      } catch (Exception e) {
         e.printStackTrace();
      }

      return (null);
   }

   /**
    * Merges the specified shapes.
    * In other words, returns the intersection of <code>destination</code> and <code>currentClip</code>
    * @param destination
    * @param currentClip
    * @return
    */
   public static Shape MergeClipShapes(Shape destination, Shape currentClip) {
      Area a = new Area(destination);
      a.intersect(new Area(currentClip));

      return (a);
   }
}
