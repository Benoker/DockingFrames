package glass.eclipse.theme.factory;

import kux.glass.IGlassFactory.SGlassParameter;

/**
 * Glass parameter factory interface.
 * Each method should return a new SGlassParameter structure, because these parameters will be modified by a dockable (color).
 * It is allowed that they return <code>null</code>. In this case no glass effect will be rendered for the associated state.
 * @author Thomas Hilbert
 *
 */
public interface IGlassParameterFactory {
	/**
	 * Creates the glass parameters for the selected tabs.
	 * @return
	 */
	SGlassParameter getSelectedGlassParameters();
	
	/**
	 * Creates the glass parameters for the unselected tabs.
	 * @return
	 */
	SGlassParameter getUnSelectedGlassParameters();
	
	/**
	 * Creates the glass parameter for the focused tab.
	 * @return
	 */
	SGlassParameter getFocusedGlassParameters();
	
	/**
	 * Creates the glass parameter for the background strip painter.
	 * @return
	 */
	SGlassParameter getStripBGGlassParameters();
	
	/**
	 * Creates the glass parameters for a disabled tab.
	 * @return the new parameter
	 */
	SGlassParameter getDisabledGlassParameters();
}
