package bibliothek.help.control.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.action.actions.SimpleDropDownAction;
import bibliothek.help.control.URListener;
import bibliothek.help.control.URManager;
import bibliothek.help.control.URManager.Step;

public class RedoDockAction extends SimpleDropDownAction implements URListener{
	private URManager manager;
	private SimpleRedo redo;
	
	public RedoDockAction( URManager manager ){
		this.manager = manager;
		setText( "Redo" );
		
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
	
	private class SimpleRedo extends SimpleButtonAction implements ActionListener{
		public SimpleRedo(){
			addActionListener( this );
			setText( "Redo" );
		}
		
		public void actionPerformed( ActionEvent e ){
			manager.redo();
		}
	}
	
	private class EntryRedo extends SimpleButtonAction implements ActionListener{
		private int index;
		
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
