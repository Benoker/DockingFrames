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
 * A {@link DockAction} that undoes a click of the user (like the "return"
 * button in a browser), or presents a list of pages which were visited earlier.
 * @author Benjamin Sigg
 *
 */
public class UndoDockAction extends SimpleDropDownAction implements URListener{
    /** the set of available undo/redo-steps */
	private URManager manager;
	/** the action responsible for the simple undo (the "return") */
	private SimpleUndo undo;
	
	/**
	 * Creates a new action
	 * @param manager set of available undo/redo-steps
	 */
	public UndoDockAction( URManager manager ){
		this.manager = manager;
		setText( "Undo" );
		setIcon( ResourceSet.ICONS.get( "undo" ) );
				
		undo = new SimpleUndo();
		add( undo );
		setSelection( undo );
		add( SeparatorAction.MENU_SEPARATOR );
		
		manager.addListener( this );
	}
	
	public void changed( URManager manager ){
		setEnabled( manager.isUndoable() );
		
		int size = size();
		size--;
		while( size >= 1 )
			remove( size-- );
		
		int current = manager.getCurrent();
		if( current > 0 ){
			add( SeparatorAction.MENU_SEPARATOR );
			Step[] entries = manager.stack();
			for( int i = current-1; i >= 0; i-- )
				add( new EntryUndo( entries[ i ], i ) );
		}
	}
	
	/**
	 * An action that calls {@link URManager#undo()} when triggered.
	 * @author Benjamin Sigg
	 *
	 */
	private class SimpleUndo extends SimpleButtonAction implements ActionListener{
	    /**
	     * Creates a new action
	     */
		public SimpleUndo(){
			addActionListener( this );
			setText( "Undo" );
			setTooltip( "Undo last selection" );
			setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK ));
		}
		
		public void actionPerformed( ActionEvent e ){
			manager.undo();
		}
	}
	
	/**
	 * An action that calls {@link URManager#moveTo(int)} when triggered.
	 * @author Benjamin Sigg
	 *
	 */
	private class EntryUndo extends SimpleButtonAction implements ActionListener{
	    /** the argument for <code>moveTo</code> */
		private int index;
		
		/**
		 * Creates a new action
		 * @param entry the step whose successors that will be undone by this action.
		 * @param index the index of <code>entry</code>, that's also the argument
		 * for {@link URManager#moveTo(int)}
		 */
		public EntryUndo( Step entry, int index ){
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
