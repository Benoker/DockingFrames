package bibliothek.demonstration;


import java.lang.reflect.InvocationTargetException;

import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;

/**
 * A monitor for a Demonstration.
 * @author Benjamin Sigg
 */
public interface Monitor {
	/**
	 * Called by a demonstration when the demonstration is starting up
	 */
	public void startup( );
	
	/**
	 * Publishes a set of components which are used to update the {@link javax.swing.LookAndFeel}
	 * of the application
	 * @param collector an object which can present a list of some components of
	 * the application
	 */
	public void publish( ComponentCollector collector );
	
	/**
	 * Called by a demonstration when it is running
	 */
	public void running();
	
	/**
	 * Executes a statement in the EventDispatcherThread.
	 * @param run the statement to run
	 * @throws InvocationTargetException if <code>run</code> throws an exception
	 */
	public void invokeSynchron( Runnable run ) throws InvocationTargetException;
	
	/**
	 * Called by a Demonstration when it is no longer running
	 */
	public void shutdown();
	
	/**
	 * Gets a list of the all look and feels.
	 * @return the list of look and feels
	 */
	public LookAndFeelList getGlobalLookAndFeel();
}
