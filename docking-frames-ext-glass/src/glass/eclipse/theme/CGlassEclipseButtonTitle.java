/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
import javax.swing.*;
import javax.swing.border.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.themes.*;
import bibliothek.gui.dock.themes.basic.*;
import bibliothek.gui.dock.title.*;
import bibliothek.gui.dock.util.*;
import bibliothek.gui.dock.util.Transparency;
import kux.glass.*;
import glass.eclipse.*;
import glass.eclipse.theme.factory.*;
import glass.eclipse.theme.utils.*;


/**
 * CGlassEclipseTitleButton.
 * 
 * @author Thomas Hilbert
 */
public class CGlassEclipseButtonTitle extends BasicButtonDockTitle {
   /***/
   private static final long serialVersionUID = 1L;

   private static final Insets OUTSIDE_INSETS_HORIZONTAL = new Insets(1, 1, 1, 1);
   private static final Insets OUTSIDE_INSETS_VERTICAL = new Insets(1, 1, 1, 1);

   protected IGlassFactory glass = CGlassFactoryGenerator.Create();
   protected IGlassFactory.SGlassParameter glassStrip;
   protected boolean bSmallTitle = false;

   PropertyValue<Boolean> propValueSmall = new PropertyValue<Boolean>(CGlassExtension.SMALL_TAB_SIZE) {
      @Override
      protected void valueChanged (Boolean oldVal, Boolean newVal) {
         CGlassEclipseButtonTitle.this.setSmallTitle(newVal);
      }
   };

   PropertyValue<IGlassParameterFactory> propValueFactory = new PropertyValue<IGlassParameterFactory>(EclipseThemeExtension.GLASS_FACTORY) {
      @Override
      protected void valueChanged (IGlassParameterFactory paramA1, IGlassParameterFactory paramA2) {
         CGlassEclipseButtonTitle.this.updateLayout();
      }
   };

   public CGlassEclipseButtonTitle (Dockable dockable, DockTitleVersion origin) {
      super(dockable, origin);
      setTransparency(Transparency.DEFAULT);

      if (origin != null) {
         propValueSmall.setProperties(origin.getController());
         propValueFactory.setProperties(origin.getController());
      }
      bSmallTitle = propValueSmall.getValue();
   }

   public void setSmallTitle (boolean smallTitle) {
      bSmallTitle = smallTitle;
      updateLayout();
   }

   @Override
   public void bind () {
      super.bind();
   }

   @Override
   public void unbind () {
      super.unbind();
      propValueSmall.setProperties((DockController)null);
      propValueFactory.setProperties((DockController)null);
   }

   @Override
   public void setOrientation (Orientation orientation) {
      super.setOrientation(orientation);
      changeBorder();
   }

   @Override
   public Dimension getPreferredSize () {
      Dimension d = super.getPreferredSize();
      Insets ins = getInsets();

      if (bSmallTitle) {
         if (getOrientation().isHorizontal()) {
            d.height = 16 + 2 + ins.top + ins.bottom;
         }
         else {
            d.width = 16 + 2 + ins.left + ins.right;
         }
      }
      else {
         if (getOrientation().isHorizontal()) {
            d.height = 16 + 2 + ins.top + ins.bottom;
         }
         else {
            d.width = 16 + 2 + ins.left + ins.right;
         }
      }
      return d;
   }

   @Override
   protected void paintForeground (Graphics g, JComponent component) {
      // paint icon (if there is any)
      paintIcon(g, component);

      Insets ins = getOutsideInsets();

      int w = component.getWidth();
      int h = component.getHeight();

      // paint knob (if there is any)
      // TODO colors
      if (behavior.isShowKnob()) {
         Insets insets = getInnerInsets();

         if (getOrientation().isHorizontal()) {
            int x = ins.left + insets.left - KNOB_SIZE + 3;
            int y1 = ins.top + insets.top + 3;
            int y2 = h - insets.bottom - ins.bottom - 4;

            g.setColor(Color.WHITE);
            g.drawLine(x, y1, x, y2);
            g.drawLine(x, y1, x + 1, y1);

            g.setColor(Color.DARK_GRAY);
            g.drawLine(x, y2, x + 1, y2);
            g.drawLine(x + 1, y1 + 1, x + 1, y2);
         }
         else {
            int y = ins.top + insets.top - KNOB_SIZE + 3;
            int x1 = ins.left + insets.left + 3;
            int x2 = w - insets.right - ins.right - 4;

            g.setColor(Color.WHITE);
            g.drawLine(x1, y, x2, y);
            g.drawLine(x1, y, x1, y + 1);

            g.setColor(Color.DARK_GRAY);
            g.drawLine(x1 + 1, y + 1, x2, y + 1);
            g.drawLine(x2, y, x2, y + 1);
         }
      }
   }

   @Override
   protected void paintBackground (Graphics g, JComponent component) {
      getGlassParameters();
      Graphics2D g2d = (Graphics2D)g.create();

      if (getOrientation().isHorizontal()) {
         paintBackground(g2d, 0, 0, component.getWidth(), component.getHeight(), getOrientation().isHorizontal(), component);
      }
      else {
         paintBackground(g2d, 0, 0, component.getHeight(), component.getWidth(), getOrientation().isHorizontal(), component);
      }

      g2d.dispose();
   }

   protected Insets getOutsideInsets () {
      if (getOrientation().isHorizontal()) {
         return (OUTSIDE_INSETS_HORIZONTAL);
      }
      else {
         return (OUTSIDE_INSETS_VERTICAL);
      }
   }

   protected void getGlassParameters () {
      IGlassParameterFactory f = propValueFactory.getValue();
      glassStrip = f.getStripBGGlassParameters();
   }

   protected void paintBackground (Graphics g, int x, int y, int w, int h, boolean horizontal, JComponent component) {
      Graphics2D g2d = (Graphics2D)g.create();
      CEclipseBorder ec = null;
      Insets ins = getOutsideInsets();

      x = ins.left;
      y = ins.top;
      if (getOrientation().isHorizontal()) {
         w -= ins.left + ins.right;
         h -= ins.top + ins.bottom;
      }
      else {
         w -= ins.top + ins.bottom;
         h -= ins.left + ins.right;
      }

      if (component.getBorder() instanceof CompoundBorder) {
         CompoundBorder cb = (CompoundBorder)component.getBorder();
         CompoundBorder bb = (CompoundBorder)cb.getInsideBorder();

         ec = (CEclipseBorder)bb.getOutsideBorder();

      }

      if (w > 0 && h > 0) {
         if (glassStrip != null) {
            BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D gg = im.createGraphics();
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            gg.setColor(component.getBackground());
            if (ec != null) {
               gg.fill(ec.createShape(0, 0, w, h, ec.getCornerRadius()));

            }
            else {
               gg.fillRect(0, 0, w, h);
            }

            if ( !isSelected()) {
               gg.setComposite(AlphaComposite.SrcIn);
            }
            else {
               gg.setComposite(AlphaComposite.SrcAtop);
            }

            try {
               glass.Render2Graphics(new Dimension(w, h), gg, glassStrip, true);
            }
            catch (Exception e) {
               glass.Render2Graphics(new Dimension(w, h), gg, CGlassFactory.VALUE_STEEL, true);
            }

            gg.dispose();

            if ( !getOrientation().isHorizontal()) {
               AffineTransform atTrans = AffineTransform.getTranslateInstance(x /* + h */, y + w);
               atTrans.concatenate(COutlineHelper.tRot90CCW);
               g2d.drawImage(im, atTrans, null);
            }
            else {
               g2d.drawImage(im, x, y, null);
            }
         }

         g2d.dispose();
      }
   }

   @Override
   protected void changeBorder () {
      int flags;

      Border empty = BorderFactory.createEmptyBorder();

      int iInsets = bSmallTitle ? 0 : 2;

      if (getOrientation().isHorizontal()) {
         flags = CEclipseBorder.TOP_RIGHT | CEclipseBorder.BOTTOM_RIGHT;
         empty = new EmptyBorder(iInsets, 2, iInsets, 4);
      }
      else {
         flags = CEclipseBorder.BOTTOM_LEFT | CEclipseBorder.BOTTOM_RIGHT;
         empty = new EmptyBorder(2, iInsets, 4, iInsets);
      }

      Border border = new CEclipseBorder(getOrigin().getController(), 6, flags);

      Insets ins = getOutsideInsets();
      Border b = new CompoundBorder(BorderFactory.createEmptyBorder(ins.top, ins.left, ins.bottom, ins.right), new CompoundBorder(border, empty));

      setBorder(ThemeManager.BORDER_MODIFIER + ".title.eclipse.button.flat", b);
   }
}
