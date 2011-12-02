/**
 * Elements handling the view of {@link bibliothek.gui.dock.action.DockAction}s.<br>
 * They way from a <code>DockAction</code> to its view normally involves four objects:
 * <ol>
 * 	<li>The <code>DockAction</code> itself, providing the basic set of properties</li>
 *  <li>A {@link bibliothek.gui.dock.themes.basic.action.BasicHandler handler} listening to changes of the
 *  action and forwarding the changes to the model. Selecting only those properties
 *  which are necessary for the view.</li>	
 *  <li>A {@link bibliothek.gui.dock.themes.basic.action.BasicButtonModel model} containing and
 *  translating properties into a form the view can use.</li>
 * 	<li>Some view like a {@link bibliothek.gui.dock.themes.basic.action.buttons.MiniButton} using the model
 *  to paint itself</li>
 * </ol>
 * The model may be omitted when there is not much to translate between 
 * handler and view. In that case, the handler directly accesses the view.<br>
 * It is even possible that the view directly accesses the action, but that is
 * only used in very simple situations (like the 
 * {@link bibliothek.gui.dock.action.actions.SeparatorAction}, which does
 * not do anything).<br>
 * The view creates and provides the model. The handler has to be created and
 * connected by the code of the {@link bibliothek.gui.DockTheme}. Normally
 * view and handler are created and connected in the
 * {@link bibliothek.gui.dock.action.view.ActionViewConverter}, using a
 * {@link bibliothek.gui.dock.action.view.ViewGenerator}. However, details
 * may differ for other themes than the {@link bibliothek.gui.dock.themes.BasicTheme}
 */
package bibliothek.gui.dock.themes.basic.action;