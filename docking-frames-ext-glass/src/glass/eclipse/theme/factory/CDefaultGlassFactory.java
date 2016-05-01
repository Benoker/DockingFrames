package glass.eclipse.theme.factory;

import java.awt.*;
import kux.glass.IGlassFactory.SGlassParameter;


/**
 * Default glass parameter factory.
 * Each method should return a new SGlassParameter structure, because these parameters will be modified by a dockable (color).
 * It is allowed that they return <code>null</code>. In this case no glass effect will be rendered for the associated state.
 * @author Thomas Hilbert
 *
 */
public class CDefaultGlassFactory implements IGlassParameterFactory {
   static CDefaultGlassFactory INSTANCE = new CDefaultGlassFactory();

   public static CDefaultGlassFactory getInstance () {
      return (INSTANCE);
   }

   public SGlassParameter getSelectedGlassParameters () {
      return (new SGlassParameter(0.0D, 0.0D, 0.4D, 0.63D, 0.0D, 0.0D, null, null, null, 0.27D, 0.85D, 0.85D, 0.49D));
   }

   public SGlassParameter getUnSelectedGlassParameters () {
      return (new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.75D, 0.25D));
   }

   public SGlassParameter getFocusedGlassParameters () { 
      return (new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.85D, 1.0D));
   }

   public SGlassParameter getStripBGGlassParameters () {
      return (new SGlassParameter(0.25, 0.0, 0.5, 0.0, 0.0, 0.21, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), 0.0, 1.0, 0.7, 0.2));
   }
   
   public SGlassParameter getDisabledGlassParameters(){
	   return (new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.75D, 0.25D));
	}
}
