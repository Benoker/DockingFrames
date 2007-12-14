/**
 * A set of classes that can be used to create basic applications. The subset
 * of DockingFrames allows to create clients capable of doing all the basic
 * operations one does expect: minimizing, maximizing or put a {@link Dockable}
 * out of the main-frame. Store and load the layout, hide and show <code>Dockable</code>s.<br>
 * Clients should start by creating a {@link FControl} as basic command central,
 * then add new instances of {@link FSingleDockable} and {@link FMultipleDockable}
 * to this control.
 */
package bibliothek.gui.dock.facile;

import bibliothek.gui.Dockable;