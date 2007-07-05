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

public class ColorAction extends SimpleMenuAction{
	private Note note;
	
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
