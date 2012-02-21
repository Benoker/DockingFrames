/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package glass.eclipse.theme.icon;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import bibliothek.gui.dock.themes.icon.*;
import bibliothek.gui.dock.util.font.*;


/**
 * This icon shows an arrow and a number indicating the size of some menu. 
 * @author Thomas Hilbert
 */
public class CTabMenuOverflowIcon extends TabMenuOverflowIcon {
   int iSize;

   /**
    * Creates a new icon.
    * @param size the number to show
    */
   public CTabMenuOverflowIcon (int size) {
      super(size);
      iSize = size;
   }

   @Override
   public int getIconWidth () {
      if (iSize < 10) {
         return (12);
      }
      else if (iSize > 99) {
         return (22);
      }
      else {
         return (16);
      }
   }

   @Override
   public int getIconHeight () {
      return 12;
   }

   @Override
   public void paintIcon (Component c, Graphics g, int x, int y) {
      Graphics2D g2d = (Graphics2D)g;
      //      gg.setColor(Color.RED);
      //      gg.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);

      g2d.setColor(c.getForeground());

      drawArrow(g2d, x, y);
      drawArrow(g2d, x + 4, y);

      String text;
      if (iSize > 99) {
         text = "99+";
      }
      else {
         text = String.valueOf(iSize);
      }

      Font font = g2d.getFont();
      GenericFontModifier modifier = new GenericFontModifier();
      modifier.setSizeDelta(false);
      modifier.setSize(GetDPICorrectedFontSize(7));
      g2d.setFont(modifier.modify(font));

      TextLayout layout = new TextLayout(text, g2d.getFont(), g2d.getFontRenderContext());
      Rectangle2D bounds = layout.getBounds();

      layout.draw(g2d, (float)(x + getIconWidth() - bounds.getWidth() - bounds.getX()), (float)(y + getIconHeight() - bounds.getHeight() - bounds.getY()));

      g2d.setFont(font);
   }

   private void drawArrow (Graphics g, int x, int y) {
      g.drawLine(x, y, x + 1, y);
      g.drawLine(x + 1, y + 1, x + 2, y + 1);
      g.drawLine(x + 2, y + 2, x + 3, y + 2);
      g.drawLine(x + 1, y + 3, x + 2, y + 3);
      g.drawLine(x, y + 4, x + 1, y + 4);
   }

   public static int GetDPICorrectedFontSize (int fontSize) {
      int iOSdpi = Toolkit.getDefaultToolkit().getScreenResolution();

      int iAdjustedFontSize = (int)(fontSize * iOSdpi / 72.0D + 0.5D);
      return iAdjustedFontSize;
   }
}
