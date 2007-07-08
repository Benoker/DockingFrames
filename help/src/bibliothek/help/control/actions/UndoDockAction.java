package bibliothek.help.control.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.action.actions.SimpleDropDownAction;
import bibliothek.help.control.URListener;
import bibliothek.help.control.URManager;
import bibliothek.help.control.URManager.Step;

public class UndoDockAction extends SimpleDropDownAction implements URListener{
	private URManager manager;
	private SimpleUndo undo;
	
	public UndoDockAction( URManager manager ){
		this.manager = manager;
		setText( "Undo" );
		
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
	
	private class SimpleUndo extends SimpleButtonAction implements ActionListener{
		public SimpleUndo(){
			addActionListener( this );
			setText( "Undo" );
		}
		
		public void actionPerformed( ActionEvent e ){
			manager.undo();
		}
	}
	
	private class EntryUndo extends SimpleButtonAction implements ActionListener{
		private int index;
		
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
