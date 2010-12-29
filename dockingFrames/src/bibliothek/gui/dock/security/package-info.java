/**
 * A package dealing with the issues of Applets and Wepstart-applications.<br>
 * <i>DockingFrames</i> monitors mouse- and other events globally. That is not
 * allowed in a restricted (or secure) environment, the {@link java.lang.SecurityManager}
 * prevents it. The classes in this package are capable to "simulate" global
 * monitors without really creating them. The reason why these classes are not
 * used in a normal application is, that they do not work very efficient.<br>
 * Clients can use the contents of this package as follows:
 * <ul>
 * 	<li>Instead of a {@link bibliothek.gui.DockController} they have to create 
 * 	a {@link bibliothek.gui.dock.security.SecureDockController}.</li>
 * <li>Instead of {@link bibliothek.gui.dock.FlapDockStation}s they
 * 	have to create {@link bibliothek.gui.dock.security.SecureFlapDockStation}s.</li>
 * <li>Instead of a {@link bibliothek.gui.dock.ScreenDockStation} they
 * have to create a {@link bibliothek.gui.dock.security.SecureScreenDockStation}.</li>
 * <li>Instead of a {@link bibliothek.gui.dock.SplitDockStation} they
 * have to create a {@link bibliothek.gui.dock.security.SecureSplitDockStation}.</li>
 * <li>Instead of a {@link bibliothek.gui.dock.StackDockStation} they
 * have to create a {@link bibliothek.gui.dock.security.SecureStackDockStation}.</li>
 * <li>All other elements which show a {@link bibliothek.gui.Dockable} have to be
 * wrapped into a {@link bibliothek.gui.dock.security.GlassedPane}.</li>
 * </ul>
 */
package bibliothek.gui.dock.security;