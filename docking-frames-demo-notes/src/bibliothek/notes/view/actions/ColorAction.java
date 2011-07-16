package bibliothek.notes.view.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JColorChooser;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.action.actions.SimpleMenuAction;
import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;

/**
 * An action used to change the {@link Note#setColor(Color) color}-property of 
 * a {@link Note}. This action is the parent of a set of child-actions, each
 * child represents a special color. When this action is triggered, a menu
 * will open and present the children. 
 * @author Benjamin Sigg
 */
public class ColorAction extends SimpleMenuAction{
    /** The Note whose color might be changed by this action */
	private Note note;
	
	/**
	 * Creates a new action
	 * @param note the Note whose color will be changed
	 */
	public ColorAction( Note note ){
		this.note = note;
		
		setText( "Color" );
		setIcon( ResourceSet.APPLICATION_ICONS.get( "color" ) );
		
		DefaultDockActionSource menu = new DefaultDockActionSource();
		
		menu.add( createAction( "Red", ResourceSet.APPLICATION_ICONS.get( "color.red" ), Color.RED ) );
		menu.add( createAction( "Blue", ResourceSet.APPLICATION_ICONS.get( "color.blue" ), Color.BLUE ) );
		menu.add( createAction( "Green", ResourceSet.APPLICATION_ICONS.get( "color.green" ), Color.GREEN ) );
		menu.add( createAction( "Yellow", ResourceSet.APPLICATION_ICONS.get( "color.yellow" ), Color.YELLOW ) );
		menu.add( createAction( "Orange", ResourceSet.APPLICATION_ICONS.get( "color.orange" ), Color.ORANGE ) );
		menu.add( createAction( "Pink", ResourceSet.APPLICATION_ICONS.get( "color.pink" ), Color.PINK ) );
		menu.add( createAction( "Purple", ResourceSet.APPLICATION_ICONS.get( "color.purple" ), new Color( 150, 0, 255 ) ) );
		menu.addSeparator();
		menu.add( createColorChooser() );
		
		setMenu( menu );
	}
	
	/**
	 * Creates a new item for the menu. The created item will change
	 * the color of {@link #note} to <code>color</code>.
	 * @param text the text of the item
	 * @param icon the image of the item
	 * @param color the color for which this item stands
	 * @return the new item
	 */
	private DockAction createAction( String text, Icon icon, final Color color ){
		SimpleButtonAction action = new SimpleButtonAction();
		action.setText( text );
		action.setIcon( icon );
		action.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				note.setColor( color );
			}
		});
		return action;
	}
	
	/**
	 * Creates a new item for the menu. The created item will open a
	 * {@link JColorChooser} when triggered.
	 * @return the new item
	 */
	private DockAction createColorChooser(){
		SimpleButtonAction action = new SimpleButtonAction(){
			@Override
			public void action( Dockable dockable ){
				super.action( dockable );
				Color color = JColorChooser.showDialog( dockable.getComponent(), "Color", note.getColor() );
				if( color != null )
					note.setColor( color );
			}
		};
		action.setText( "Choose..." );
		action.setIcon( ResourceSet.APPLICATION_ICONS.get( "color.chooser" ) );
		return action;
	}
}
