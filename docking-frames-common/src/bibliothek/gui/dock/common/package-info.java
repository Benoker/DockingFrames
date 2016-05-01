/**
 * A set of classes that can be used to create basic applications. The subset
 * of DockingFrames allows to create clients capable of doing all the basic
 * operations one does expect: minimizing, maximizing or put a {@link bibliothek.gui.Dockable}
 * out of the main-frame. Store and load the layout, hide and show <code>Dockable</code>s.<br>
 * Clients should start by creating a {@link bibliothek.gui.dock.common.CControl}
 * as basic command central, then add new instances of 
 * {@link bibliothek.gui.dock.common.SingleCDockable} and 
 * {@link bibliothek.gui.dock.common.MultipleCDockable} to this control.
 */
package bibliothek.gui.dock.common;
