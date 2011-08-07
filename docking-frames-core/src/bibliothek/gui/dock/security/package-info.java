/**
 * A package dealing with the issues of Applets and Wepstart-applications.<br>
 * <i>DockingFrames</i> monitors mouse- and other events globally. That is not
 * allowed in a restricted (or secure) environment, the {@link java.lang.SecurityManager}
 * prevents it. The classes in this package are capable to "simulate" global
 * monitors without really creating them. The reason why these classes are not
 * used in a normal application is, that they do not work very efficient.
 */
package bibliothek.gui.dock.security;