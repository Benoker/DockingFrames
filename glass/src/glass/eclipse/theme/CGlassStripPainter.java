/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package glass.eclipse.theme;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import bibliothek.extension.gui.dock.theme.eclipse.stack.*;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.util.color.*;
import kux.glass.*;
import glass.eclipse.theme.utils.*;


/**
 * Paints the background of the tab by painting a glass background.
 * @author Thomas Hilbert
 */
@ColorCodes("stack.border.glass")
public class CGlassStripPainter implements TabPanePainter {
   private final AbstractDockColor color = new AbstractDockColor("stack.border.glass", DockColor.KIND_DOCK_COLOR, Color.BLACK) {
      @Override
      protected void changed (Color oldColor, Color newColor) {
         pane.repaint();
      }
   };

   private final EclipseTabPane pane;

   IGlassFactory glass = CGlassFactoryGenerator.Create();

   /**
    * Creates a new painter.
    * @param tabbedComponent the component for which this painter will work
    */
   public CGlassStripPainter (EclipseTabPane pane) {
      this.pane = pane;
   }

   public void paint (Graphics g) {
      paintBackground(g);
   }

   private void paintHorizontal (Graphics g, Rectangle available, Rectangle bounds, int y) {
      paintBackground(g, available.x, bounds.y, available.width, bounds.height, true);

      if (available.x < bounds.x - 1) {
         // left side
         g.drawLine(available.x, y, bounds.x - 1, y);
      }

      if (available.x + available.width > bounds.x + bounds.width) {
         // right side
         g.drawLine(available.x + available.width, y, bounds.x + bounds.width, y);
      }
   }

   private void paintVertical (Graphics g, Rectangle available, Rectangle bounds, int x) {
      //      paintBackground(g, bounds.x, available.y, bounds.width, available.height, false);
      paintBackground(g, bounds.x, available.y, available.height, bounds.width, false);

      if (available.y < bounds.y - 1) {
         g.drawLine(x, available.y, x, bounds.y - 1);
      }
      if (available.y + available.height > bounds.y + bounds.height) {
         g.drawLine(x, available.y + available.height, x, bounds.y + bounds.height);
      }
   }

   protected void paintBackground (Graphics g, int x, int y, int w, int h, boolean horizontal) {
      if (w != 0 && h != 0) {
         Graphics2D g2d = (Graphics2D)g.create();

         BufferedImage img = null;
         try {
            img = glass.RenderBufferedImage(CGlassEclipseTabPainter.VALUE_PLAIN, new Dimension(w, h), true);
         }
         catch (Exception e) {
            img = glass.RenderBufferedImage(CGlassFactory.VALUE_STEEL, new Dimension(w, h), true);
         }

         if ( !horizontal) {
            AffineTransform atTrans = AffineTransform.getTranslateInstance(x /*+ h*/, y + w);
            atTrans.concatenate(COutlineHelper.tRot90CCW);

            g2d.drawImage(img, atTrans, null);
         }
         else {
            g2d.drawImage(img, x, y, null);
         }

         g2d.dispose();
      }
   }

   public void setController (DockController controller) {
      ColorManager colors = controller == null ? null : controller.getColors();
      color.setManager(colors);
   }

   public void paintBackground (Graphics g) {
      Dockable selection = pane.getSelectedDockable();
      if (selection == null) {
         return;
      }

      EclipseTab tab = pane.getTab(selection);
      if (tab == null || !tab.isPaneVisible()) {
         return;
      }

      Rectangle bounds = tab.getBounds();
      Rectangle available = pane.getAvailableArea();

      g.setColor(color.value());

      switch (pane.getTabPlacement()) {
         case TOP_OF_DOCKABLE:
            paintHorizontal(g, available, bounds, bounds.y + bounds.height - 1);
            break;
         case BOTTOM_OF_DOCKABLE:
            paintHorizontal(g, available, bounds, bounds.y);
            break;
         case LEFT_OF_DOCKABLE:
            paintVertical(g, available, bounds, bounds.x + bounds.width - 1);
            break;
         case RIGHT_OF_DOCKABLE:
            paintVertical(g, available, bounds, bounds.x);
            break;
      }
   }

   public void paintForeground (Graphics g) {
   // TODO Auto-generated method stub

   }
}
