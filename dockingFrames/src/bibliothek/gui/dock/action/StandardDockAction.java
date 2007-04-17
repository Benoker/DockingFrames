package bibliothek.gui.dock.action;

import javax.swing.Icon;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A StandardDockAction is an object that is shown as graphical interface (like a button)
 * on some Components. The user can trigger an action, for example by pressing
 * a button.<br>
 * StandardDockActions are linked with one or many {@link Dockable Dockables}.<br>
 * Note: this interface allows that one action is used for many Dockables with
 * unspecified type. However, some implementations may have restrictions, read 
 * the documentation of those actions carefully.
 * 
 * @author Benjamin Sigg
 *
 */
public interface StandardDockAction extends DockAction {
	/**
     * Gets the Icon of this DockAction, when this DockAction is shown
     * together with <code>dockable</code>.
     * @param dockable The {@link Dockable} for which the action-icon
     * should be chosen.
     * @return The icon to show for this action when the action is associated
     * with <code>dockable</code>, or <code>null</code>.
     */
    public Icon getIcon( Dockable dockable );

	/**
     * Gets the Icon of this DockAction, when this DockAction is shown
     * together with <code>dockable</code> and is not enabled.
     * @param dockable The {@link Dockable} for which the action-icon
     * should be chosen.
     * @return The icon to show for this action when the action is associated
     * with <code>dockable</code>, or <code>null</code>.
     */
    public Icon getDisabledIcon( Dockable dockable );
    
    /**
     * Gets the text of this DockActon, when this DockAction is
     * shown together with <code>dockable</code>.
     * @param dockable The {@link Dockable} for which the action-text 
     * should be chosen.
     * @return The text to show for this action when the action is
     * associated with <code>dockable</code>, or <code>null</code>.
     */
    public String getText( Dockable dockable );

    /**
     * Gets a tooltip for this DockActon, when this DockAction is
     * shown together with <code>dockable</code>.
     * @param dockable The {@link Dockable} for which the action-tooltip 
     * should be chosen.
     * @return The tooltip to show for this action when the action is
     * associated with <code>dockable</code>, or <code>null</code>.
     */
    public String getTooltipText( Dockable dockable );
    
    /**
     * Adds a listener to this DockAction. The listener should be triggered
     * whenever an icon, a text, a tooltip, or the selected/enabled state
     * changes.
     * @param listener The listener to add
     */
    public void addDockActionListener( StandardDockActionListener listener );
    
    /**
     * Removes a listener from this DockStation. Note that this can happen
     * at any time, even while this DockAction is sending an event.
     * @param listener The listener to remove
     */
    public void removeDockActionListener( StandardDockActionListener listener );
    
    /**
     * Tells whether this DockAction can be triggered together with
     * the <code>dockable</code>.
     * @param dockable The {@link Dockable} for which this action maybe
     * triggered.
     * @return <code>true</code> if the user should be able to trigger
     * this action, <code>false</code> otherwise
     */
    public boolean isEnabled( Dockable dockable );
}
