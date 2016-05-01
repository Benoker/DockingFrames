package glass.eclipse.theme.utils;

import java.awt.*;
import java.awt.geom.*;


/**
 * COutlineHelper.java
 *
 * Creates the basic shapes for the tab painter.
 *
 * Thomas Hilbert
 * 02.03.2010
 */
public class COutlineHelper {
   public static AffineTransform tRot90CW;
   public static AffineTransform tRot90CCW;
   public static AffineTransform atRot90SclX;
   public static AffineTransform tSclY;
   public static AffineTransform tSclX;

   static {
      tRot90CW = AffineTransform.getRotateInstance(Math.PI / 2);
      tRot90CCW = AffineTransform.getRotateInstance(-Math.PI / 2);
      tSclY = AffineTransform.getScaleInstance(1, -1);
      tSclX = AffineTransform.getScaleInstance(-1, 1);
      atRot90SclX = AffineTransform.getScaleInstance(-1, 1);
      atRot90SclX.concatenate(tRot90CW);
   }

   /**
    * <pre>
    * Creates the selected tab shape with round edge at left and curve at right side.
    *
    *     _____________________________
    *   /                               -_
    *  |                                  \_
    *  |                                     -_
    *  |                                       -_
    *  |                                          --__
    * </pre>
    * @param cornerRadius
    * @param w
    * @param h
    * @param firstTab Shape without the left edge and rounded corner (if true)
    * @return
    */
   public static Shape CreateSelectedTabShape(int cornerRadius, int w, int h, boolean firstTab) {
      GeneralPath p = new GeneralPath();

      int iCurveWidth = h * 40 / 24;

      if (firstTab) {
         p.moveTo(cornerRadius, 0);
      } else {
         p.moveTo(0, h - 1);
         p.lineTo(0, cornerRadius);
         p.quadTo(0, 0, 0 + cornerRadius, 0);
         p.lineTo(cornerRadius, 0);
      }

      // curve at right side
      p.lineTo(w - 1 - iCurveWidth, 0);
      p.curveTo(w - 1 - iCurveWidth / 2, 0, w - 1 - iCurveWidth / 2, h - 1, w - 1, h - 1);

      if (firstTab) {
         p.moveTo(cornerRadius, h - 1);
      }

      return (p);
   }

   /**
    * <pre>
    * Creates the shape of an unselected tab for clipping and for border painting.
    * If the tab is left of the selected, it will have a rounded edge left. Otherwise
    * right.
    * 
    * If the shape is not for clipping, only the needed border path is created.
    * 
    * before selected:
    *    _________________________________________
    *  /
    * |
    * |
    * |
    * |
    * |
    * 
    * after selected:
    *  _________________________________________
    *                                            \
    *                                             |
    *                                             |
    *                                             |
    *                                             |
    *                                             |
    * </pre>
    * @param w
    * @param h
    * @param firstTab
    * @param forClip
    * @param beforeSelected
    * @return
    */
   public static Shape CreateUnselectedTabShape(int cornerRadius, int w, int h, boolean firstTab, boolean forClip, boolean beforeSelected) {
      GeneralPath p = new GeneralPath();

      if (beforeSelected) {
         // unselected tab before selected one (round edge at left side)
         if (firstTab) {
            p.moveTo(cornerRadius, 0);
         } else {
            p.moveTo(0, h - 1);

            p.lineTo(0, cornerRadius);
            p.quadTo(0, 0, cornerRadius, 0);
         }

         p.lineTo(0 + w - 1, 0);

         if (forClip) {
            p.lineTo(w - 1, h - 1);

            if (firstTab) {
               p.lineTo(cornerRadius, h - 1);
            }
         }
      } else {
         // unselected tab after selected one (round edge at right side)
         if (forClip) {
            p.moveTo(0, h - 1);
            p.lineTo(0, 0);
         } else {
            p.moveTo(0, 0);
         }

         p.lineTo(w - 1 - cornerRadius, 0);
         p.quadTo(w - 1, 0, w - 1, cornerRadius);
         p.lineTo(w - 1, h - 1);
      }

      return (p);
   }

   /**
    * Translates the specified shape.
    * @param x Distance on x-axis to translate.
    * @param y Distance on y-axis to translate.
    * @param s
    * @return
    */
   public static Shape TranslateShapeTo(int x, int y, Shape s) {
      AffineTransform atTrans = AffineTransform.getTranslateInstance(x, y);

      return (atTrans.createTransformedShape(s));
   }

   /**
    * Modifies the shape for left side.
    * @param src The top side shape.
    * @return
    */
   public static Shape Modify4LeftSide(Shape src) {
      Shape s = atRot90SclX.createTransformedShape(src);
      return (s);
   }

   /**
    * Modifies the shape for right side.
    * @param src The top side shape.
    * @return
    */
   public static Shape Modify4RightSide(Shape src) {
      Shape s = tRot90CW.createTransformedShape(src);
      return (TranslateShapeTo(s.getBounds().width, 0, s));
   }

   /**
    * Modifies the shape for bottom side.
    * @param src The top side shape.
    * @return
    */
   public static Shape Modify4BottomSide(Shape src) {
      Shape s = tSclY.createTransformedShape(src);
      return (TranslateShapeTo(0, s.getBounds().height, s));
   }
}
