package glass.eclipse.theme;

import java.awt.Color;

import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;


public class CGlassEclipseColorSchemeExtension extends DefaultColorScheme {
   /**
    * Creates the new color scheme
    */
   public CGlassEclipseColorSchemeExtension() {
      updateUI();
   }

   @Override
   public boolean updateUI() {
      super.updateUI();

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
      setColor("stack.tab.text.selected.focuslost.glass", Color.DARK_GRAY);

      setColor("glass.selected.center", new Color(222, 222, 222));
      setColor("glass.selected.light", new Color(222, 222, 222));
      setColor("glass.selected.boundary", new Color(0, 40, 255));

      setColor("glass.focused.center", new Color(0, 0, 150));
      setColor("glass.focused.light", new Color(100, 200, 255));
      setColor("glass.focused.boundary", new Color(0, 40, 80));

      setColor("stack.border.glass", Color.DARK_GRAY);

      setColor("selection.border.glass", RexSystemColor.getBorderColor());
      return true;
   }
}
