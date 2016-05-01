package glass.eclipse.theme;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.*;
import bibliothek.extension.gui.dock.theme.eclipse.rex.*;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.station.stack.tab.layouting.*;
import bibliothek.gui.dock.util.color.*;


/**
 * A border that has round edges.
 * @author Thomas Hilbert
 */
@ColorCodes("stack.border.glass")
public class CEclipseBorder implements Border {
   /** constant indicating the top left edge has to be painted round */
   public static final int TOP_LEFT = 1;
   /** constant indicating the top right edge has to be painted round */
   public static final int TOP_RIGHT = 2;
   /** constant indicating the bottom left edge has to be painted round */
   public static final int BOTTOM_LEFT = 4;
   /** constant indicating the bottom right edge has to be painted round */
   public static final int BOTTOM_RIGHT = 8;

   /** which edges to paint round */
   private int roundEdges;

   private DockController controller;

   protected BorderedComponent owner;

   protected int iCornerRadius = 5;

   /**
    * Creates a new border
    * @param controller the owner of this border
    * @param cornerRadius The radius of the round edges.
    * @param owner The owner component.
    * @param roundEdges Edge mask.
    */
   public CEclipseBorder (DockController controller, int cornerRadius, BorderedComponent owner, int roundEdges) {
      this(controller, cornerRadius, roundEdges);
      if (owner == null) {
         throw new IllegalArgumentException("owner must not be null");
      }
      this.owner = owner;
   }

   /**
    * Creates a new border
    * @param controller the owner of this border
    * @param fillEdges whether to paint over the edges
    * @param edges the edges that are painted round, or-ed from {@link #TOP_LEFT},
    * {@link #TOP_RIGHT}, {@link #BOTTOM_LEFT} and {@link #BOTTOM_RIGHT}
    */
   public CEclipseBorder (DockController controller, int cornerRadius, int edges) {
      this.controller = controller;

      roundEdges = edges;
      iCornerRadius = cornerRadius;
   }

   /**
    * Updates the edge mask for the specified placement.
    * @param tabPlacement
    */
   public void update4Placement (TabPlacement tabPlacement) {
      switch (tabPlacement) {
         case TOP_OF_DOCKABLE:
            roundEdges = (CEclipseBorder.TOP_LEFT | CEclipseBorder.TOP_RIGHT);
            break;
         case BOTTOM_OF_DOCKABLE:
            roundEdges = (CEclipseBorder.BOTTOM_LEFT | CEclipseBorder.BOTTOM_RIGHT);
            break;
         case LEFT_OF_DOCKABLE:
            roundEdges = (CEclipseBorder.BOTTOM_LEFT | CEclipseBorder.TOP_LEFT);
            break;
         case RIGHT_OF_DOCKABLE:
            roundEdges = (CEclipseBorder.BOTTOM_RIGHT | CEclipseBorder.TOP_RIGHT);
            break;
      }
   }

   /**
    * Sets which edges are painted round.
    * @param roundEdges the edges to paint round
    */
   public void setRoundEdges (int roundEdges) {
      this.roundEdges = roundEdges;
   }

   /**
    * Tells which edges are painted round.
    * @return the round edges
    */
   public int getRoundEdges () {
      return roundEdges;
   }

   public void setCornerRadius (int radius) {
      iCornerRadius = radius;
   }

   public int getCornerRadius () {
      return (iCornerRadius);
   }

   public static Path2D CreateBorderShape (int x, int y, int w, int h, int roundEdges, int cornerRadius) {
      Path2D p = new Path2D.Float();

      if ((roundEdges & TOP_LEFT) != 0) {
         p.moveTo(x, y + cornerRadius);
         p.quadTo(x, y, x + cornerRadius, y);
      }
      else {
         p.moveTo(x, y);
      }

      if ((roundEdges & TOP_RIGHT) != 0) {
         p.lineTo(x + w - 1 - cornerRadius, y);
         p.quadTo(x + w - 1, y, x + w - 1, y + cornerRadius);
      }
      else {
         p.lineTo(x + w - 1, y);
      }

      if ((roundEdges & BOTTOM_RIGHT) != 0) {
         p.lineTo(x + w - 1, y + h - 1 - cornerRadius);
         p.quadTo(x + w - 1, y + h - 1, x + w - 1 - cornerRadius, y + h - 1);
      }
      else {
         p.lineTo(x + w - 1, y + h - 1);
      }

      if ((roundEdges & BOTTOM_LEFT) != 0) {
         p.lineTo(x + cornerRadius, y + h - 1);
         p.quadTo(x, y + h - 1, x, y + h - 1 - cornerRadius);
      }
      else {
         p.lineTo(x, y + h - 1);
      }

      p.closePath();

      return (p);
   }

   protected static boolean isSet (int mask, int bit) {
      return ((mask & bit) == bit);
   }

   public static int ShiftEdgeMask (int srcMask, boolean CCW) {
      int iMask = 0;
      if (CCW) {
         iMask |= isSet(srcMask, TOP_RIGHT) ? TOP_LEFT : 0;
         iMask |= isSet(srcMask, TOP_LEFT) ? BOTTOM_LEFT : 0;
         iMask |= isSet(srcMask, BOTTOM_LEFT) ? BOTTOM_RIGHT : 0;
         iMask |= isSet(srcMask, BOTTOM_RIGHT) ? TOP_RIGHT : 0;
      }
      else {
         iMask |= isSet(srcMask, TOP_RIGHT) ? BOTTOM_RIGHT : 0;
         iMask |= isSet(srcMask, BOTTOM_RIGHT) ? BOTTOM_LEFT : 0;
         iMask |= isSet(srcMask, BOTTOM_LEFT) ? TOP_LEFT : 0;
         iMask |= isSet(srcMask, TOP_LEFT) ? TOP_RIGHT : 0;
      }

      return (iMask);
   }

   protected Path2D createShape (int x, int y, int w, int h, int cornerRadius) {
      return (CreateBorderShape(x, y, w, h, roundEdges, cornerRadius));
   }

   public void paintBorder (Component c, Graphics g, int x, int y, int width, int height) {
      Color color = controller.getColors().get("stack.border.glass");
      if (color == null) {
         color = RexSystemColor.getBorderColor();
      }

      Graphics2D g2d = (Graphics2D)g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2d.setColor(color);
      Shape s = createShape(x, y, width, height, getCornerRadius());

      g2d.draw(s);

      g2d.dispose();
   }

   public Insets getBorderInsets (Component c) {
      return new Insets(1, 1, 1, 1);
   }

   public boolean isBorderOpaque () {
      return false;
   }
}
