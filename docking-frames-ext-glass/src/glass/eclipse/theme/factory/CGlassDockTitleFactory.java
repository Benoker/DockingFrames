package glass.eclipse.theme.factory;

import bibliothek.gui.dock.title.*;
import glass.eclipse.theme.*;


public class CGlassDockTitleFactory implements DockTitleFactory {
   public static CGlassDockTitleFactory FACTORY = new CGlassDockTitleFactory();

   protected CGlassDockTitleFactory () {}

   public void request (DockTitleRequest request) {
      request.answer(new CGlassEclipseButtonTitle(request.getTarget(), request.getVersion()));
   }

   public void uninstall (DockTitleRequest request) {
      // not needed
   }

   public void install (DockTitleRequest request) {
      // not needed
   }
}
