package glass.eclipse;

import java.awt.*;
import javax.swing.*;
import bibliothek.extension.gui.dock.theme.*;
import bibliothek.gui.dock.*;
import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.intern.theme.*;
import bibliothek.gui.dock.common.layout.*;
import bibliothek.gui.dock.station.stack.tab.layouting.*;
import bibliothek.gui.dock.util.*;
import glass.eclipse.theme.*;


public class CTestMain {

   /**
    * @param args
    */
   public static void main (String[] args) {
      //      SwingUtilities.invokeLater(new Runnable() {
      //
      //         public void run () {
      // TODO Auto-generated method stub
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
      SingleCDockable blue2 = create("Blue2 ", Color.BLUE);

      control.add(red);
      control.add(green);
      control.add(blue);

      updateTheme(control);

      CGrid grid = new CGrid(control);
      grid.add(0, 0, 100, 100, red);
      grid.add(50, 0, 50, 100, blue2);
      control.getContentArea().deploy(grid);

      //      red.setLocation(CLocation.base().normal());
      //      red.setVisible(true);

      green.setLocation(CLocation.base().minimalNorth());
      green.setVisible(true);

      blue.setLocation(CLocation.base().minimalNorth());
      blue.setVisible(true);

      //         }
      //      });
   }

   public static SingleCDockable create (String title, Color color) {
      JPanel background = new JPanel();
      background.setOpaque(true);
      background.setBackground(color);
      CTestPanel panel = new CTestPanel();

      DefaultSingleCDockable d = new DefaultSingleCDockable(title, title, panel);
      d.setMaximizable(true);

      //      d.setTitleIcon(new ImageIcon("D:/test.png"));
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

      cControl.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
      ((CEclipseTheme)cControl.intern().getController().getTheme()).intern().setMovingImageFactory(new CMiniPreviewMovingImageFactory(128), Priority.CLIENT);
      ((CEclipseTheme)cControl.intern().getController().getTheme()).intern().setPaint(new CGlassStationPaint(), Priority.CLIENT);
   }

   public static ImageIcon createIcon (String path) {
      ImageIcon icon = new ImageIcon(CGlassEclipseTabPainter.class.getResource(path));

      return (icon);
   }
}
