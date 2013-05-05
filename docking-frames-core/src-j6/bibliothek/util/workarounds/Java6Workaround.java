package bibliothek.util.workarounds;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.DnDAutoSelectSupport;

import com.sun.awt.AWTUtilities;
import com.sun.awt.AWTUtilities.Translucency;

/**
 * Workarounds necessary for Java 1.6.
 * @author Benjamin Sigg
 */
public class Java6Workaround implements Workaround{
	/** whether to print any warnings */
	private static boolean printWarnings = false;
	
	/**
	 * Sets whether the methods of the {@link Java6Workaround} can print warnings or not. Calling this method
	 * affects <b>all</b> workarounds. The default behavior is <b>not</b> to print warnings.
	 * @param printWarnings whether to print warnings
	 */
	public static void setPrintWarnings( boolean printWarnings ){
		Java6Workaround.printWarnings = printWarnings;
	}
	
	/**
	 * Tells whether all {@link Java6Workaround}s are allowed to print warnings.
	 * @return whether to print warnings
	 * @see #setPrintWarnings(boolean)
	 */
	public static boolean isPrintWarnings(){
		return printWarnings;
	}
	
	/** ensures the warning message is printed out at most once */
	private boolean invocationTargetException = false;
	
	@Override
	public void setup( DockController controller ){
		controller.getProperties().set( StackDockStation.DND_AUTO_SELECT_SUPPORT, new DnDAutoSelectSupport() );
	}
	
	public void markAsGlassPane( Component component ){
		try{
			AWTUtilities.setComponentMixingCutoutShape( component, new Rectangle() );
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchMethodError ex ){
			// ignore
		}
		catch( SecurityException ex ){
			// ignore
		}
		catch( IllegalArgumentException e ){
			// ignore
		}
	}
	
	private boolean supports( Translucency translucency ){
		// AWTUtilities.isTranslucencySupported(Translucency)
		
		try{
			return AWTUtilities.isTranslucencySupported( translucency );
		}
		catch( SecurityException ex ){
			// ignore
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchMethodError ex ){
			// ignore
		}
		catch( Exception ex ){
			if( printWarnings && !invocationTargetException ){
				invocationTargetException = true;
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean supportsPerpixelTranslucency( Window window ){
		try{
			return supports( Translucency.PERPIXEL_TRANSLUCENT );
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchFieldError ex ){
			// ignore
		}
		return false;
	}
	
	public boolean supportsPerpixelTransparency( Window window ){
		if( window instanceof Dialog && !((Dialog)window).isUndecorated() ){
			return false;
		}
		if( window instanceof Frame && !((Frame)window).isUndecorated() ){
			return false;
		}
		
		try{
			return supports( Translucency.PERPIXEL_TRANSPARENT );
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchFieldError ex ){
			// ignore
		}
		return false;
	}
	
	public boolean setTranslucent( Window window ){
		if( !supportsPerpixelTranslucency( window )){
			return false;
		}
		
		try{
			AWTUtilities.setWindowOpaque( window, false );
			return true;
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchMethodError ex ){
			// ignore
		}
		catch( SecurityException ex ){
			// ignore
		}
		catch( Exception ex ){
			if( printWarnings && !invocationTargetException ){
				invocationTargetException = true;
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean setTransparent( Window window, Shape shape ){
		if( !supportsPerpixelTransparency( window )){
			return false;
		}
		
		try{
			AWTUtilities.setWindowShape( window, shape );
			return true;
		}
		catch( NoClassDefFoundError ex ){
			// ignore
		}
		catch( NoSuchMethodError ex ){
			// ignore
		}
		catch( SecurityException ex ){
			// ignore
		}
		catch( Exception ex ){
			if( printWarnings && !invocationTargetException ){
				invocationTargetException = true;
				ex.printStackTrace();
			}
		}
		return false;
	}
}
