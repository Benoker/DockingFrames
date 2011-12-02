/**
 * Contains a set of different {@link bibliothek.gui.dock.action.DockAction}s
 * and supporting classes.<br> 
 * The actions in this package will create views, which show {@link javax.swing.Icon}s,
 * text, tooltips and other gimmicks. They are designed to give a reasonable
 * subset of features a {@link javax.swing.JButton} or a similar 
 * {@link javax.swing.JComponent} would offer.<br>
 * The actions in this package are divided in two groups:
 * <ul>
 * <li>The simple-actions have one set of properties which they use regardless
 * for which {@link bibliothek.gui.Dockable} they are shown.</li>
 * <li>The grouped-actions have several sets of properties. They decide for each
 * <code>Dockable</code> to which group it belongs, and then uses
 * the set of properties associated with that group. <code>Dockable</code>
 * can change its group at any time</li>
 * </ul> 
 */
package bibliothek.gui.dock.action.actions;