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
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import bibliothek.extension.gui.dock.theme.eclipse.stack.*;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.station.stack.tab.layouting.*;
import bibliothek.gui.dock.themes.color.*;
import bibliothek.gui.dock.util.*;
import bibliothek.gui.dock.util.color.*;
import kux.glass.*;
import kux.utils.*;
import glass.eclipse.*;
import glass.eclipse.theme.factory.*;
import glass.eclipse.theme.utils.*;


/**
 * Eclipse tab painter with glass look.
 * 
 * Based on ArchPainter of Janni Kovacs.
 * 
 * @author Thomas Hilbert
 */
@ColorCodes({
	"glass.selected.light", 
	"glass.selected.boundary", 
	"glass.selected.center", 
	"glass.unselected.light", 
	"glass.unselected.boundary", 
	"glass.unselected.center", 
	"glass.focused.light", 
	"glass.focused.boundary", 
	"glass.focused.center",
	"glass.disabled.light", 
	"glass.disabled.boundary", 
	"glass.disabled.center", 
	
	"stack.tab.border.glass", 
	"stack.tab.border.selected.glass", 
	"stack.tab.border.selected.focused.glass", 
	"stack.tab.border.selected.focuslost.glass",
	"stack.tab.border.disabled.glass", 
	"stack.tab.top.glass", 
	"stack.tab.top.selected.glass", 
	"stack.tab.top.selected.focused.glass", 
	"stack.tab.top.selected.focuslost.glass",
	"stack.tab.top.disabled.glass", 
	"stack.tab.bottom.glass", 
	"stack.tab.bottom.selected.glass", 
	"stack.tab.bottom.selected.focused.glass", 
	"stack.tab.bottom.selected.focuslost.glass",
	"stack.tab.bottom.disabled.glass", 
	"stack.tab.text.glass", 
	"stack.tab.text.selected.glass", 
	"stack.tab.text.selected.focused.glass",
	"stack.tab.text.selected.focuslost.glass",
	"stack.tab.text.disabled.glass", 
	"stack.border.glass"})
public class CGlassEclipseTabPainter extends BaseTabComponent {
   /***/
   private static final long serialVersionUID = -3944491545940520488L;

   /**
    * Glass parameters for inactive tabs and tab strip background.
    */
   private IGlassFactory.SGlassParameter glassUnSelected;
   /**
    * Glass parameter for selected tab background.
    */
   private IGlassFactory.SGlassParameter glassSelected;
   /**
    * Glass parameter for focused tab background.
    */
   private IGlassFactory.SGlassParameter glassFocused;
   
   /**
    * Glass parameter for disabled tab background.
    */
   private IGlassFactory.SGlassParameter glassDisabled;

   private boolean wasPreviousSelected = false;
   private final IGlassFactory glass = CGlassFactoryGenerator.Create();

   public static final int CORNER_RADIUS = 6;

   protected TabColor colGlassCenterFocused, colGlassBoundaryFocused, colGlassLightFocused;
   protected TabColor colGlassCenterSelected, colGlassBoundarySelected, colGlassLightSelected;
   protected TabColor colGlassCenterUnSelected, colGlassBoundaryUnSelected, colGlassLightUnSelected;
   protected TabColor colGlassCenterDisabled, colGlassBoundaryDisabled, colGlassLightDisabled;

   /** number of pixels at the left side that are empty and under the selected predecessor of this tab */
   private final int TAB_OVERLAP = 24;

   PropertyValue<Boolean> propValueSmall = new PropertyValue<Boolean>(CGlassExtension.SMALL_TAB_SIZE) {
      @Override
      protected void valueChanged (Boolean oldVal, Boolean newVal) {
         CGlassEclipseTabPainter.this.setSmallTabs(newVal);
      }
   };

   PropertyValue<IGlassParameterFactory> propValueFactory = new PropertyValue<IGlassParameterFactory>(EclipseThemeExtension.GLASS_FACTORY) {
      @Override
      protected void valueChanged (IGlassParameterFactory paramA1, IGlassParameterFactory paramA2) {
         CGlassEclipseTabPainter.this.update();
      }
   };

   private boolean bSmallerTabs = false;

   /**
    * Creates a new painter.
    * @param pane the owner of this painter
    * @param dockable the dockable which this painter represents
    */
   public CGlassEclipseTabPainter (EclipseTabPane pane, Dockable dockable) {
      super(pane, dockable, ".glass");

      setOpaque(false);

      initAdditionalColors();

      update();
      updateFont();
      updateBorder();

      if (getController() != null) {
         propValueSmall.setProperties(getController());
         propValueFactory.setProperties(getController());
      }

      bSmallerTabs = propValueSmall.getValue();
   }

   public void setSmallTabs (boolean smallTabs) {
      bSmallerTabs = smallTabs;
      update();
   }

   @Override
   public void unbind () {
      super.unbind();
      // unregister listener
      propValueSmall.setProperties((DockController)null);
      propValueFactory.setProperties((DockController)null);
   }

   protected IGlassParameterFactory getGlassParameterFactory () {
      return (propValueFactory.getValue());
   }

   /**
    * Initializes additional colors for painting the glass effect.
    */
   protected void initAdditionalColors () {
      colGlassCenterSelected = new CGlassColor("glass.selected.center", getStation(), getDockable(), new Color(222, 222, 222));
      colGlassBoundarySelected = new CGlassColor("glass.selected.boundary", getStation(), getDockable(), new Color(0, 40, 255));
      colGlassLightSelected = new CGlassColor("glass.selected.light", getStation(), getDockable(), new Color(222, 222, 222));

      colGlassCenterUnSelected = new CGlassColor("glass.unselected.center", getStation(), getDockable(), new Color(222, 222, 222));
      colGlassBoundaryUnSelected = new CGlassColor("glass.unselected.boundary", getStation(), getDockable(), new Color(0, 40, 255));
      colGlassLightUnSelected = new CGlassColor("glass.unselected.light", getStation(), getDockable(), new Color(222, 222, 222));

      colGlassCenterFocused = new CGlassColor("glass.focused.center", getStation(), getDockable(), new Color(0, 0, 150));
      colGlassBoundaryFocused = new CGlassColor("glass.focused.boundary", getStation(), getDockable(), new Color(0, 40, 80));
      colGlassLightFocused = new CGlassColor("glass.focused.light", getStation(), getDockable(), new Color(100, 200, 255));
      
      colGlassCenterDisabled = new CGlassColor("glass.disabled.center", getStation(), getDockable(), new Color(150, 150, 150));
      colGlassBoundaryDisabled = new CGlassColor("glass.disabled.boundary", getStation(), getDockable(), new Color(0, 40, 80));
      colGlassLightDisabled = new CGlassColor("glass.disabled.light", getStation(), getDockable(), new Color(150, 150, 150));

      addAdditionalColors(colGlassBoundaryFocused, colGlassBoundarySelected, colGlassCenterFocused, colGlassCenterSelected, colGlassLightFocused, colGlassLightSelected, colGlassLightUnSelected, colGlassCenterUnSelected, colGlassBoundaryUnSelected, colGlassCenterDisabled, colGlassBoundaryDisabled, colGlassLightDisabled );
   }

   @Override
   public void updateBorder () {
      EclipseTabPane pane = getPane();
      int index = getDockableIndex();

      if (isBound() && pane != null && index >= 0) {
         Color color2;

         Window window = SwingUtilities.getWindowAncestor(getComponent());
         boolean focusTemporarilyLost = false;

         if (window != null) {
            focusTemporarilyLost = !window.isActive();
         }

         if( !isEnabled() ){
        	 color2 = colorStackTabBorderDisabled.value();
         }
         else if (isSelected()) {
            if (isFocused()) {
               if (focusTemporarilyLost) {
                  color2 = colorStackTabBorderSelectedFocusLost.value();
               }
               else {
                  color2 = colorStackTabBorderSelectedFocused.value();
               }
            }
            else {
               color2 = colorStackTabBorderSelected.value();
            }
         }
         else {
            color2 = colorStackTabBorder.value();
         }

         // set border around tab content
         pane.setContentBorderAt(index, BorderFactory.createMatteBorder(1, 1, 1, 1, color2));
      }
   }

   public Insets getOverlap (TabComponent other) {
      if (other instanceof CGlassEclipseTabPainter) {
         CGlassEclipseTabPainter painter = (CGlassEclipseTabPainter)other;
         if (painter.isSelected()) {
            if (getOrientation().isHorizontal()) {
               return new Insets(0, 10 + TAB_OVERLAP, 0, 0);
            }
            else {
               return new Insets(10 + TAB_OVERLAP, 0, 0, 0);
            }
         }
      }

      return new Insets(0, 0, 0, 0);
   }

   @Override
   public Dimension getPreferredSize () {
      boolean previousSelected = isPreviousTabSelected();
      if (wasPreviousSelected != previousSelected) {
         update();
      }

      return super.getPreferredSize();
   }

   @Override
   public Dimension getMinimumSize () {
      boolean previousSelected = isPreviousTabSelected();
      if (wasPreviousSelected != previousSelected) {
         update();
      }

      return super.getMinimumSize();
   }

   @Override
   public void updateFocus () {
      update();
      updateBorder();
      updateFont();
   }

   @Override
   protected void updateOrientation () {
      update();
   }

   @Override
   protected void updateSelected () {
      update();
      updateBorder();
      updateFont();
   }

   @Override
   protected void updateColors () {
      update();
   }
   
   @Override
	protected void updateEnabled(){
	   updateBorder();
	   update();
	}

   /**
    * Updates the layout information of this painter.
    */
   protected void update () {
      wasPreviousSelected = isPreviousTabSelected();

      Insets labelInsets = null;
      Insets buttonInsets = null;

      int iInsets = bSmallerTabs ? 1 : 3;

      switch (getOrientation()) {
         case TOP_OF_DOCKABLE:
         case BOTTOM_OF_DOCKABLE:
            labelInsets = new Insets(iInsets, 5, iInsets, 2);
            buttonInsets = new Insets(1, 0, 1, 5);
            break;
         case LEFT_OF_DOCKABLE:
         case RIGHT_OF_DOCKABLE:
            labelInsets = new Insets(5, iInsets, 2, iInsets);
            buttonInsets = new Insets(0, 1, 5, 1);
            break;
      }

      boolean horizontal = getOrientation().isHorizontal();

      if (isSelected()) {
         if (horizontal) {
            buttonInsets.right += 24;
         }
         else {
            buttonInsets.bottom += 24;
         }
      }

      if (wasPreviousSelected) {
         if (horizontal) {
            labelInsets.left += TAB_OVERLAP;
         }
         else {
            labelInsets.top += TAB_OVERLAP;
         }
      }

      getLabel().setIconOffset(0);

      getLabel().setForeground(getTextColor());
      setLabelInsets(labelInsets);
      setButtonInsets(buttonInsets);

      updateGlass();

      revalidate();
      repaint();
   }

   protected void updateGlass () {
      Color c1, c2, c3;

      c1 = colGlassCenterFocused.value();
      c2 = colGlassLightFocused.value();
      c3 = colGlassBoundaryFocused.value();
      c1 = c1 == null ? colorStackTabTopSelectedFocused.value() : c1;
      c2 = c2 == null ? CColor.BrighterColor(colorStackTabTopSelectedFocused.value()) : c2;
      c3 = c3 == null ? colorStackTabBottomSelectedFocused.value() : c3;

      IGlassParameterFactory f = getGlassParameterFactory();
      glassFocused = f.getFocusedGlassParameters();
      if (glassFocused != null) {
         glassFocused.colorCenter = c1;
         glassFocused.colorSuperLight = c2;
         glassFocused.colorBoundary = c3;
      }

      c1 = colGlassCenterSelected.value();
      c2 = colGlassLightSelected.value();
      c3 = colGlassBoundarySelected.value();
      c1 = c1 == null ? colorStackTabTopSelected.value() : c1;
      c2 = c2 == null ? CColor.BrighterColor(colorStackTabTopSelected.value()) : c2;
      c3 = c3 == null ? colorStackTabBottomSelected.value() : c3;

      glassSelected = f.getSelectedGlassParameters();
      if (glassSelected != null) {
         glassSelected.colorCenter = c1;
         glassSelected.colorSuperLight = c2;
         glassSelected.colorBoundary = c3;
      }

      c1 = colGlassCenterUnSelected.value();
      c2 = colGlassLightUnSelected.value();
      c3 = colGlassBoundaryUnSelected.value();
      c1 = c1 == null ? colorStackTabTop.value() : c1;
      c2 = c2 == null ? CColor.BrighterColor(colorStackTabTop.value()) : c2;
      c3 = c3 == null ? colorStackTabBottom.value() : c3;

      glassUnSelected = f.getUnSelectedGlassParameters();
      if (glassUnSelected != null) {
         glassUnSelected.colorCenter = c1;
         glassUnSelected.colorSuperLight = c2;
         glassUnSelected.colorBoundary = c3;
      }

      c1 = colGlassCenterDisabled.value();
      c2 = colGlassLightDisabled.value();
      c3 = colGlassBoundaryDisabled.value();
      c1 = c1 == null ? colorStackTabTopDisabled.value() : c1;
      c2 = c2 == null ? CColor.BrighterColor(colorStackTabTopDisabled.value()) : c2;
      c3 = c3 == null ? colorStackTabBottomDisabled.value() : c3;
      
      glassDisabled = f.getDisabledGlassParameters();
      if (glassUnSelected != null) {
          glassUnSelected.colorCenter = c1;
          glassUnSelected.colorSuperLight = c2;
          glassUnSelected.colorBoundary = c3;
       }
      
      try {
         if (getButtons() != null) {
            if (isSelected() && isFocused()) {
               c2 = colGlassLightFocused.value();
               c2 = c2 == null ? CColor.BrighterColor(colorStackTabTopSelectedFocused.value()) : c2;
               getButtons().setBackground(CColor.GradientColor(Color.BLACK, c2, 0.55));
            }
            else if (isSelected()) {
               c2 = colGlassLightSelected.value();
               c2 = c2 == null ? CColor.BrighterColor(colorStackTabTopSelected.value()) : c2;
               getButtons().setBackground(CColor.GradientColor(Color.LIGHT_GRAY, c2, 0.55));
            }
            else {
               getButtons().setBackground(Color.LIGHT_GRAY);
            }
         }
      }
      catch (Exception e) {}
   }

   private Color getTextColor () {
      boolean focusTemporarilyLost = isFocusTemporarilyLost();
      Color c;

      if( !isEnabled() ){
    	  c = colorStackTabTextDisabled.value();
      }
      else if (isFocused() && !focusTemporarilyLost) {
         c = colorStackTabTextSelectedFocused.value();
      }
      else if (isFocused() && focusTemporarilyLost) {
         c = colorStackTabTextSelectedFocusLost.value();
      }
      else if (isSelected()) {
         c = colorStackTabTextSelected.value();
      }
      else {
         c = colorStackTabText.value();
      }

      return (c);
   }

   @Override
   public boolean contains (int x, int y) {
      if ( !super.contains(x, y)) {
         return (false);
      }

      if( containsButton( x, y )){
    	  return true;
      }
      
      boolean bRet = true;
      if (isSelected()) {
         Shape s = createSelectedTabShape(getWidth(), getHeight(), false);
         bRet = (s.contains(x, y));
      }
      else {
         bRet = super.contains(x, y);
      }

      return (bRet);
   }

   @Override
   public void paintBackground (Graphics g) {
      if (getWidth() != 0 && getHeight() != 0) {
    	  updateGlass();
         Graphics2D g2d = (Graphics2D)g.create();

         int iState = 0;

         if (isFocused() && !isFocusTemporarilyLost()) {
            iState = 2;
         }
         else if (isFocused() && isFocusTemporarilyLost() || isSelected()) {
            iState = 1;
         }
         else {
            iState = 0;
         }

         if (iState == 2 || iState == 1) {
            paintSelected(g2d, iState == 2);
         }
         else {
            paintUnselected(g2d);
         }

         g2d.dispose();
      }
   }

   protected int getSelectedIndex () {
      return (getPane().getSelectedIndex());
   }

   protected boolean isHorizontal () {
      return (getOrientation() == TabPlacement.TOP_OF_DOCKABLE || getOrientation() == TabPlacement.BOTTOM_OF_DOCKABLE);
   }

   /**
    * Paints a unselected / unfocused tab.
    * @param g
    */
   protected void paintUnselected (Graphics g) {

      Dimension dImg;
      Color lineColor = colorStackBorder.value();

      int x = 0;
      int y = 0;
      int w = getWidth();
      int h = getHeight();

      if (w > 0 && h > 0) {
         Graphics2D g2d = (Graphics2D)g.create();
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         Shape sTab = createUnSelectedTabShape(isHorizontal() ? w : w, isHorizontal() ? h : h, false, true);

         BufferedImage img;
         dImg = new Dimension(isHorizontal() ? w + CORNER_RADIUS : h + CORNER_RADIUS, isHorizontal() ? h : w);
         if (glassUnSelected != null) {
            img = new BufferedImage(dImg.width, dImg.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gg = img.createGraphics();

            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setColor(Color.WHITE);
            gg.fill(sTab);

            gg.setComposite(AlphaComposite.SrcIn);
            try {
               glass.Render2Graphics(dImg, gg, glassUnSelected, true);
            }
            catch (Exception e) {
               glass.Render2Graphics(dImg, gg, CGlassFactory.VALUE_STEEL, true);
            }

            gg.dispose();

            if ( !isHorizontal()) {
               AffineTransform atTrans = AffineTransform.getTranslateInstance(x/* + w*/, y + h);
               atTrans.concatenate(COutlineHelper.tRot90CCW);

               g2d.drawImage(img, atTrans, null);
            }
            else {
               g2d.drawImage(img, 0, 0, null);
            }
         }
         // restore default clipping 
         sTab = createUnSelectedTabShape(w, h, getTabIndex() == 0, false);

         // draw Border
         g2d.setColor(lineColor);
         g2d.draw(sTab);

         switch (getOrientation()) {
            case TOP_OF_DOCKABLE:
               g2d.drawLine(0, h - 1, w - 1, h - 1);
               break;
            case BOTTOM_OF_DOCKABLE:
               g2d.drawLine(0, 0, w - 1, 0);
               break;
            case LEFT_OF_DOCKABLE:
               g2d.drawLine(w - 1, 0, w - 1, h - 1);
               break;
            case RIGHT_OF_DOCKABLE:
               g2d.drawLine(0, 0, 0, h - 1);
               break;
         }

         g2d.dispose();
      }
   }

   /**
    * Paints the selected or focused tab (with round edges)
    * @param g
    * @param bActive
    */
   protected void paintSelected (Graphics g, boolean bActive) {
      int x = 0;
      int y = 0;
      int w = getWidth();
      int h = getHeight();
      Dimension dImg;

      if (w > 0 && h > 0) {
         Shape sTab = createSelectedTabShape(w, h, false);

         Color lineColor = colorStackBorder.value();

         Graphics2D g2d = (Graphics2D)g.create();
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         if (getTabIndex() > 0) {
            // paint a little bit of the previous tab, because previous tab should also overlap at the begin of the selected tab
            paintWorkAround(g2d);
         }

         // draw glass 
         // first render to image because glass is transparent and we would see the inactive tab in background
         BufferedImage bimg = null;
         dImg = new Dimension(isHorizontal() ? w : h, isHorizontal() ? h : w);
         if( !isEnabled() ){
        	 if(glassDisabled != null){
        		 try {
                     bimg = glass.RenderBufferedImage(glassDisabled, dImg, true);
				 }
				 catch( Exception e ) {
					 bimg = glass.RenderBufferedImage( CGlassFactory.VALUE_GRAY, dImg, true );
				 } 
        	 }
         }
         else if (bActive) {
            if (glassFocused != null) {
               try {
                  bimg = glass.RenderBufferedImage(glassFocused, dImg, true);
               }
               catch (Exception e) {
                  bimg = glass.RenderBufferedImage(CGlassFactory.VALUE_STEEL, dImg, true);
               }
            }
         }
         else {
            if (glassSelected != null) {
               try {
                  bimg = glass.RenderBufferedImage(glassSelected, dImg, true);
               }
               catch (Exception e) {
                  bimg = glass.RenderBufferedImage(CGlassFactory.VALUE_DARKENED_PLAIN, dImg, true);
               }
            }
         }

         if (bimg != null) {
            // glass is translucent, so we could see the sharp edge of an unselected tab behind
            // So, we paint the glass image onto a other image were only the glass part is visible (white)
            BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gg2d = b.createGraphics();
            gg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            gg2d.setColor(Color.WHITE);
            gg2d.fill(sTab);

            gg2d.setClip(sTab);
            gg2d.setComposite(AlphaComposite.SrcAtop);
            if ( !isHorizontal()) {
               AffineTransform atTrans = AffineTransform.getTranslateInstance(0/*w*/, h);
               atTrans.concatenate(COutlineHelper.tRot90CCW);

               gg2d.drawImage(bimg, atTrans, null);
            }
            else {

               gg2d.drawImage(bimg, 0, 0, null);
            }

            gg2d.dispose();

            g2d.drawImage(b, x, y, null);
         }

         // draw Border
         g2d.setColor(lineColor);

         sTab = createSelectedTabShape(w, h, getTabIndex() == 0);
         g2d.draw(sTab);

         switch (getOrientation()) {
            case TOP_OF_DOCKABLE:
               g2d.drawLine(0, h - 1, w - 1, h - 1);
               break;
            case BOTTOM_OF_DOCKABLE:
               g2d.drawLine(0, 0, w - 1, 0);
               break;
            case LEFT_OF_DOCKABLE:
               g2d.drawLine(w - 1, 0, w - 1, h - 1);
               break;
            case RIGHT_OF_DOCKABLE:
               g2d.drawLine(0, 0, 0, h - 1);
               break;
         }

         g2d.dispose();
      }
   }

   /**
    * An unselected tab before the selected tab is not painted behind the selected tab.
    * When we draw the selected tab, we first draw a little bit of the unselected tab in
    * background.
    * 
    * @param g2d
    */
   private void paintWorkAround (Graphics2D g2d) {
      Dimension dImg;

      Rectangle r = getPane().getTabsList().get(getTabIndex() - 1).getComponent().getBounds();

      BufferedImage bimg = null;
      dImg = new Dimension(isHorizontal() ? r.width + CORNER_RADIUS : getHeight() + CORNER_RADIUS, isHorizontal() ? getHeight() : r.width);

      if (dImg.width > 0 && dImg.height > 0) {
         if (glassUnSelected != null) {
            try {
               bimg = glass.RenderBufferedImage(glassUnSelected, dImg, true);
            }
            catch (Exception e) {
               bimg = glass.RenderBufferedImage(CGlassFactory.VALUE_RED, dImg, true);
            }

            if ( !isHorizontal()) {
               AffineTransform atTrans = AffineTransform.getTranslateInstance(/*r.width*/0, CORNER_RADIUS/*-getHeight()*/);
               atTrans.concatenate(COutlineHelper.tRot90CCW);

               g2d.drawImage(bimg, atTrans, null);
            }
            else {
               g2d.drawImage(bimg, -r.width, 0, null);
            }
         }
      }
   }

   /**
    * Creates the tab outline for an unselected tab.
    * @param w
    * @param h
    * @param bFirst
    * @param forClip
    * @return
    */
   protected Shape createUnSelectedTabShape (int w, int h, boolean bFirst, boolean forClip) {
      Shape sTab = null;
      int x, y;
      boolean bBeforeSelected = getTabIndex() < getSelectedIndex();
      switch (getOrientation()) {
         case LEFT_OF_DOCKABLE:
            x = -1;
            y = bBeforeSelected ? 0 : -1;
            sTab = COutlineHelper.CreateUnselectedTabShape(CORNER_RADIUS, h + 1, w + 1, bFirst, forClip, bBeforeSelected);
            if ( !forClip) {
               sTab = COutlineHelper.Modify4LeftSide(sTab);
            }
            else {
               sTab = COutlineHelper.tSclX.createTransformedShape(sTab);
               sTab = COutlineHelper.TranslateShapeTo(h + 1, 0, sTab);
            }
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case RIGHT_OF_DOCKABLE:
            x = 0;
            y = bBeforeSelected ? 0 : -1;
            sTab = COutlineHelper.CreateUnselectedTabShape(CORNER_RADIUS, h + 1, w + 1, bFirst, forClip, bBeforeSelected);
            if ( !forClip) {
               sTab = COutlineHelper.Modify4RightSide(sTab);
            }
            else {
               sTab = COutlineHelper.tSclX.createTransformedShape(sTab);
               sTab = COutlineHelper.tSclY.createTransformedShape(sTab);
               sTab = COutlineHelper.TranslateShapeTo(h, w, sTab);
            }
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case BOTTOM_OF_DOCKABLE:
            x = bBeforeSelected ? 0 : -1;
            y = 0;
            sTab = COutlineHelper.CreateUnselectedTabShape(CORNER_RADIUS, w + 1, h + 1, bFirst, forClip, bBeforeSelected);
            sTab = COutlineHelper.Modify4BottomSide(sTab);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case TOP_OF_DOCKABLE:
            x = bBeforeSelected ? 0 : -1;
            y = -1;
            sTab = COutlineHelper.CreateUnselectedTabShape(CORNER_RADIUS, w + 1, h + 1, bFirst, forClip, bBeforeSelected);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
      }

      return (sTab);
   }

   /**
    * Creates the tab outline for the selected tab.
    * @param w
    * @param h
    * @param bFirst Is it the first tab.
    * @return
    */
   protected Shape createSelectedTabShape (int w, int h, boolean bFirst) {
      Shape sTab = null;
      int x, y;
      switch (getOrientation()) {
         case LEFT_OF_DOCKABLE:
            x = -1;
            y = getTabIndex() == 0 ? -1 : 0;
            sTab = COutlineHelper.CreateSelectedTabShape(CORNER_RADIUS, h, w + 1, bFirst);
            sTab = COutlineHelper.Modify4LeftSide(sTab);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case RIGHT_OF_DOCKABLE:
            x = 0;
            y = getTabIndex() == 0 ? -1 : 0;
            sTab = COutlineHelper.CreateSelectedTabShape(CORNER_RADIUS, h, w + 1, bFirst);
            sTab = COutlineHelper.Modify4RightSide(sTab);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case BOTTOM_OF_DOCKABLE:
            x = getTabIndex() == 0 ? -1 : 0;
            y = 0;
            sTab = COutlineHelper.CreateSelectedTabShape(CORNER_RADIUS, w, h + 1, bFirst);
            sTab = COutlineHelper.Modify4BottomSide(sTab);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
         case TOP_OF_DOCKABLE:
            x = getTabIndex() == 0 ? -1 : 0;
            y = -1;
            sTab = COutlineHelper.CreateSelectedTabShape(CORNER_RADIUS, w, h + 1, bFirst);
            sTab = COutlineHelper.TranslateShapeTo(x, y, sTab);
            break;
      }

      return (sTab);
   }

   /**
    * Special glass color.
    * @author Thomas Hilbert
    *
    */
   protected class CGlassColor extends TabColor {
      public CGlassColor (String id, DockStation station, Dockable dock, Color backup) {
         super(id, station, dock, backup);
      }

      @Override
      protected void changed (Color oldColor, Color newColor) {
         updateGlass();
         repaint();
      }
   }

   protected static class CTabPainter implements TabPainter {
      CBorderListener placementListener;

      /**
       * Listens to changes of the TabPlacement property and updates all used CEclipseBorder's.
       * The borders are stored in a WeakHashMap so there will be no strong reference and unused borders
       * are removed automatically by the garbage collector.
       * @author Thomas Hilbert
       *
       */
      protected class CBorderListener implements DockPropertyListener<TabPlacement> {
         protected WeakHashMap<CEclipseBorder, Void> wmap = new WeakHashMap<CEclipseBorder, Void>();

         public void addBorder (CEclipseBorder border) {
            wmap.put(border, null);
         }

         public void propertyChanged (DockProperties properties, PropertyKey<TabPlacement> property, TabPlacement oldValue, TabPlacement newValue) {
            for (CEclipseBorder b : wmap.keySet()) {
               b.update4Placement(newValue);
            }
         }

      }

      public CTabPainter () {
         placementListener = new CBorderListener();
      }

      public TabComponent createTabComponent (EclipseTabPane pane, Dockable dockable) {
         return new CGlassEclipseTabPainter(pane, dockable);
      }

      public TabPanePainter createDecorationPainter (EclipseTabPane pane) {
         return new CGlassStripPainter(pane);
      }

      public InvisibleTab createInvisibleTab (InvisibleTabPane pane, Dockable dockable) {
         return new DefaultInvisibleTab(pane, dockable);
      }

      public Border getFullBorder (BorderedComponent owner, DockController controller, Dockable dockable) {

         controller.getProperties().removeListener(StackDockStation.TAB_PLACEMENT, placementListener);
         controller.getProperties().addListener(StackDockStation.TAB_PLACEMENT, placementListener);

         CEclipseBorder border = new CEclipseBorder(controller, CORNER_RADIUS, owner, CEclipseBorder.TOP_LEFT | CEclipseBorder.BOTTOM_RIGHT);

         if (controller != null) {
            border.update4Placement(controller.getProperties().get(StackDockStation.TAB_PLACEMENT));
         }

         placementListener.addBorder(border);
         return (border);
      }
   }

   /**
    * This factory creates instances of {@link CGlassEclipseTabPainter}.
    * Normal tab size.
    */
   public static final CTabPainter FACTORY = new CTabPainter();
}
