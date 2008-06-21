package bibliothek.help.control.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.action.actions.SimpleDropDownAction;
import bibliothek.help.control.URListener;
import bibliothek.help.control.URManager;
import bibliothek.help.control.URManager.Step;
import bibliothek.help.util.ResourceSet;

/**
 * A {@link DockAction} that redoes a click on a link. This action either redoes
 * the last undone click (like "forward" in a browser) or offers a menu with some
 * pages that were visited earlier and then jumped over by an 
 * {@link UndoDockAction undo}.
 * @author Benjamin Sigg
 *
 */
public class RedoDockAction extends SimpleDropDownAction implements URListener{
    /** the set of available undo/redo-steps */
	private URManager manager;
	/** the logic for redoing the last undo-action */
	private SimpleRedo redo;
	
	/**
	 * Creates a new action
	 * @param manager set of available undo/redo-steps
	 */
	public RedoDockAction( URManager manager ){
		this.manager = manager;
		setText( "Redo" );
		setTooltip( "Redo last undone selection" );
		setIcon( ResourceSet.ICONS.get( "redo" ) );
		
		redo = new SimpleRedo();
		add( redo );
		setSelection( redo );
		add( SeparatorAction.MENU_SEPARATOR );
		
		manager.addListener( this );
	}
	
	public void changed( URManager manager ){
		setEnabled( manager.isRedoable() );
		
		int size = size();
		size--;
		while( size >= 1 )
			remove( size-- );
		
		int current = manager.getCurrent();
		Step[] entries = manager.stack();
		
		if( current+1 < entries.length ){
			add( SeparatorAction.MENU_SEPARATOR );
			
			for( int i = current+1; i < entries.length; i++ )
				add( new EntryRedo( entries[ i ], i ) );
		}
	}
	
	/**
	 * An action that calls {@link URManager#redo()} when triggered.
	 * @author Benjamin Sigg
	 *
	 */
	private class SimpleRedo extends SimpleButtonAction implements ActionListener{
		public SimpleRedo(){
			addActionListener( this );
			setText( "Redo" );
			setText( "Redo (ctrl+r)" );
			setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK ));
		}
		
		public void actionPerformed( ActionEvent e ){
			manager.redo();
		}
	}
	
	/**
	 * An action that calls {@link URManager#moveTo(int)} when triggered.
	 * @author Benjamin Sigg
	 *
	 */
	private class EntryRedo extends SimpleButtonAction implements ActionListener{
	    /** the argument for <code>moveTo</code> */
		private int index;
		
		/**
		 * Creates a new action.
		 * @param entry the description of the step that will be redone by
		 * this action.
		 * @param index the index of <code>entry</code>, that's also the
		 * argument used for {@link URManager#moveTo(int)}
		 */
		public EntryRedo( Step entry, int index ){
			this.index = index;
			setText( entry.getTitle() );
			setDropDownSelectable( false );
			addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e ){
			manager.moveTo( index );
		}
	}
}
