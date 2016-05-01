package glass.eclipse;

import glass.eclipse.theme.CGlassEclipseColorSchemeExtension;
import glass.eclipse.theme.CGlassEclipseTabPainter;
import glass.eclipse.theme.CGlassStationPaint;
import glass.eclipse.theme.CMiniPreviewMovingImageFactory;
import glass.eclipse.theme.EclipseThemeExtension;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.theme.CEclipseTheme;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;


public class CTestMain {

   /**
    * @param args
    */
   public static void main (String[] args) {
      //      SwingUtilities.invokeLater(new Runnable() {
      //
      //         public void run () {
      // TODO Auto-generated method stub
//	   UIManager.put("Panel.background", Color.BLACK);  

      JFrame frame = new JFrame();

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      CControl control = new CControl(frame);
      //      updateTheme(control);

      frame.setLayout(new GridLayout(1, 1));
      frame.add(control.getContentArea());
      frame.setSize(640, 480);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
      //      control.getContentArea().getCenter().setContinousDisplay(true);
      SingleCDockable red = create("Red", Color.RED);
      SingleCDockable green = create("Green", Color.GREEN);
      SingleCDockable blue = create("Blue ", Color.BLUE);
      SingleCDockable blue2 = create2("Blue2 ", Color.BLUE,control);

      control.addDockable(red);
      control.addDockable(green);
      control.addDockable(blue);

      updateTheme(control);

      CGrid grid = new CGrid(control);
      grid.add(0, 0, 100, 100, red,blue2);
      grid.add(50, 0, 50, 100, blue);
      control.getContentArea().deploy(grid);

      //      red.setLocation(CLocation.base().normal());
      //      red.setVisible(true);

      green.setLocation(CLocation.base().minimalNorth());
      green.setVisible(true);

//      blue.setLocation(CLocation.base().minimalNorth());
//      blue.setVisible(true);

      //         }
      //      });
   }

   public static SingleCDockable create (final String title, Color color) {
      JPanel background = new JPanel();
      background.setOpaque(true);
      background.setBackground(color);

      DefaultSingleCDockable d = new DefaultSingleCDockable(title, title, background);
      d.setMaximizable(true);

      //      d.setTitleIcon(new ImageIcon("D:/test.png"));
      return d;
   }

   public static SingleCDockable create2 (final String title, Color color,CControl cControl) {
	      DefaultSingleCDockable d = new DefaultSingleCDockable(title, title,new CGlassConfig(cControl));
	      d.setMaximizable(true);
	      return d;
	   }   
   
   /**
    * Sets icons, colors and tab painter.
    */
   public static void updateTheme (CControl cControl) {
      IconManager im = cControl.intern().getController().getIcons();

      im.setIconClient("locationmanager.maximize", createIcon("images/maximize.png"));
      im.setIconClient("locationmanager.normalize", createIcon("images/normalize.png"));
      im.setIconClient("locationmanager.externalize", createIcon("images/externalize.png"));
      im.setIconClient("locationmanager.minimize", createIcon("images/minimize.png"));
      im.setIconClient("close", createIcon("images/close_active.png"));
      im.setIconClient("flap.hold", createIcon("images/pin_active.png"));
      im.setIconClient("flap.free", createIcon("images/unpin_active.png"));
      im.setIconClient("overflow.menu", createIcon("images/overflow_menu.png"));

      cControl.putProperty(StackDockStation.TAB_PLACEMENT, TabPlacement.TOP_OF_DOCKABLE);
      cControl.putProperty(EclipseTheme.TAB_PAINTER, CGlassEclipseTabPainter.FACTORY);
      cControl.putProperty(EclipseTheme.ECLIPSE_COLOR_SCHEME, new CGlassEclipseColorSchemeExtension());
      cControl.putProperty(EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true);
//      cControl.putProperty(EclipseThemeExtension.GLASS_FACTORY, CDefaultGlassFactory.getInstance());
      cControl.putProperty(EclipseThemeExtension.GLASS_FACTORY, CGlassConfig.FACTORY);

      cControl.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
      ((CEclipseTheme)cControl.intern().getController().getTheme()).intern().setMovingImageFactory(new CMiniPreviewMovingImageFactory(128), Priority.CLIENT);
      ((CEclipseTheme)cControl.intern().getController().getTheme()).intern().setPaint(new CGlassStationPaint(), Priority.CLIENT);
   }

   public static ImageIcon createIcon (String path) {
      ImageIcon icon = new ImageIcon(CGlassEclipseTabPainter.class.getResource(path));

      return (icon);
   }
}
