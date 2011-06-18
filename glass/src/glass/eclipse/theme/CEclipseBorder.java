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

   private BorderedComponent owner;

   protected int iCornerRadius = 5;

   /**
    * Creates a new border
    * @param controller the owner of this border
    * @param fillEdges whether to paint over the edges
    */
   public CEclipseBorder(DockController controller, int cornerRadius, BorderedComponent owner) {
      this(controller, cornerRadius, TOP_LEFT | TOP_RIGHT);
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
   public CEclipseBorder(DockController controller, int cornerRadius, int edges) {
      this.controller = controller;
      roundEdges = edges;
      iCornerRadius = cornerRadius;
   }

   /**
    * Sets which edges are painted round.
    * @param roundEdges the edges to paint round
    */
   public void setRoundEdges(int roundEdges) {
      this.roundEdges = roundEdges;
   }

   /**
    * Tells which edges are painted round.
    * @return the round edges
    */
   public int getRoundEdges() {
      return roundEdges;
   }

   public void SetCornerRadius(int radius) {
      iCornerRadius = radius;
   }

   protected Path2D createShape(int x, int y, int w, int h) {
      Path2D p = new Path2D.Float();

      if ((roundEdges & TOP_LEFT) != 0) {
         p.moveTo(x, y + iCornerRadius);
         p.quadTo(x, y, x + iCornerRadius, y);
      } else {
         p.moveTo(x, y);
      }

      if ((roundEdges & TOP_RIGHT) != 0) {
         p.lineTo(x + w - 1 - iCornerRadius, y);
         p.quadTo(x + w - 1, y, x + w - 1, y + iCornerRadius);
      } else {
         p.lineTo(x + w - 1, y);
      }

      if ((roundEdges & BOTTOM_RIGHT) != 0) {
         p.lineTo(x + w - 1, y + h - 1 - iCornerRadius);
         p.quadTo(x + w - 1, y + h - 1, x + w - 1 - iCornerRadius, y + h - 1);
      } else {
         p.lineTo(x + w - 1, y + h - 1);
      }

      if ((roundEdges & BOTTOM_LEFT) != 0) {
         p.lineTo(x + iCornerRadius, y + h - 1);
         p.quadTo(x, y + h - 1, x, y + h - 1 - iCornerRadius);
      } else {
         p.lineTo(x, y + h - 1);
      }

      p.closePath();

      return (p);
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      TabPlacement placement = owner.getDockTabPlacement();
      if (placement != null) {
         switch (placement) {
            case TOP_OF_DOCKABLE:
               setRoundEdges(TOP_LEFT | TOP_RIGHT);
               break;
            case BOTTOM_OF_DOCKABLE:
               setRoundEdges(BOTTOM_LEFT | BOTTOM_RIGHT);
               break;
            case LEFT_OF_DOCKABLE:
               setRoundEdges(BOTTOM_LEFT | TOP_LEFT);
               break;
            case RIGHT_OF_DOCKABLE:
               setRoundEdges(BOTTOM_RIGHT | TOP_RIGHT);
               break;
         }
      }

      Color color = controller.getColors().get("stack.border.glass");
      if (color == null) {
         color = RexSystemColor.getBorderColor();
      }

      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2d.setColor(color);
      Shape s = createShape(x, y, width, height);

      g2d.draw(s);

      g2d.dispose();
   }

   public Insets getBorderInsets(Component c) {
      return new Insets(1, 1, 1, 1);
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
