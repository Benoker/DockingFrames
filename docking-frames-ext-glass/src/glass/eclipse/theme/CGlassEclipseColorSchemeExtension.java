package glass.eclipse.theme;

import java.awt.*;
import bibliothek.extension.gui.dock.theme.eclipse.rex.*;
import bibliothek.gui.*;
import bibliothek.gui.dock.themes.color.*;
import bibliothek.gui.dock.util.laf.*;
import glass.eclipse.theme.utils.*;


public class CGlassEclipseColorSchemeExtension extends DefaultColorScheme {
   protected CDockColorMap colMap;

   /**
    * Creates the new color scheme
    */
   public CGlassEclipseColorSchemeExtension () {
      updateUI();
   }

   /**
    * Creates the new color scheme
    */
   public CGlassEclipseColorSchemeExtension (CDockColorMap map) {
      if (map != null) {
         colMap = map;
      }
      updateUI();
   }

   @Override
   protected void updateUI () {
      super.updateUI();

      if (colMap == null) {
         colMap = new CDockColorMap();
      }

      setColor("stack.tab.border.glass", DockUI.getColor(LookAndFeelColors.PANEL_BACKGROUND));
      setColor("stack.tab.border.selected.glass", RexSystemColor.getInactiveColorGradient());
      setColor("stack.tab.border.selected.focused.glass", Color.DARK_GRAY);
      setColor("stack.tab.border.selected.focuslost.glass", RexSystemColor.getInactiveColorGradient());

      setColor("stack.tab.top.glass", DockUI.getColor(LookAndFeelColors.PANEL_BACKGROUND));
      setColor("stack.tab.top.selected.glass", RexSystemColor.getInactiveColor());
      setColor("stack.tab.top.selected.focused.glass", RexSystemColor.getActiveColor());
      setColor("stack.tab.top.selected.focuslost.glass", RexSystemColor.getInactiveColor());

      setColor("stack.tab.text.glass", Color.DARK_GRAY);
      setColor("stack.tab.text.selected.glass", Color.DARK_GRAY);
      setColor("stack.tab.text.selected.focused.glass", Color.WHITE);
      setColor("stack.tab.text.selected.focuslost.glass", new Color(80, 80, 80));

      setColor("glass.selected.center", colMap.colSelectedGlassCenter);
      setColor("glass.selected.light", colMap.colSelectedGlassLight);
      setColor("glass.selected.boundary", colMap.colSelectedGlassBoundary);

      setColor("glass.focused.center", colMap.colFocusedGlassCenter);
      setColor("glass.focused.light", colMap.colFocusedGlassLight);
      setColor("glass.focused.boundary", colMap.colFocusedGlassBoundary);

      setColor("glass.unselected.center", colMap.colUnSelectedGlassCenter);
      setColor("glass.unselected.light", colMap.colUnSelectedGlassLight);
      setColor("glass.unselected.boundary", colMap.colUnSelectedGlassBoundary);

      setColor("glass.disabled.center", colMap.colDisabledGlassCenter);
      setColor("glass.disabled.light", colMap.colDisabledGlassLight);
      setColor("glass.disabled.boundary", colMap.colDisabledGlassBoundary);
      
      setColor("stack.border.glass", new Color(96, 96, 96));

      setColor("selection.border.glass", RexSystemColor.getBorderColor());

      setColor("glass.paint.divider", Color.BLACK);
      setColor("glass.paint.insertion", Color.GRAY);
      setColor("glass.paint.line", Color.GRAY);

      // colors of externalized window borders
      //      setColor("line", Color.RED);
      //      setColor("line.highlight.inner", Color.BLUE);
      //      setColor("line.highlight.outer", Color.CYAN);
      //      setColor("line.shadow.inner", Color.GRAY);
      //      setColor("line.shadow.outer", Color.MAGENTA);

      //      setColor("highlight.inner");
      //      setColor("highlight.outer");
      //      setColor("shadow.inner");
      //      setColor("shadow.outer");
      //       
      //      setColor("hover", Color.GREEN);
      //            setColor("highlight.inner.hover");
      //            setColor("highlight.outer.hover");
      //            setColor("shadow.inner.hover");
      //            setColor("shadow.outer.hover");
      //       
      //      setColor("selected");
      //      setColor("highlight.inner.selected");
      //      setColor("highlight.outer.selected");
      //      setColor("shadow.inner.selected");
      //      setColor("shadow.outer.selected");
   }
}
