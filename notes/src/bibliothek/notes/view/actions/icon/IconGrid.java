package bibliothek.notes.view.actions.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import bibliothek.notes.model.Note;
import bibliothek.notes.util.ResourceSet;

public class IconGrid extends JPanel{
	public static IconGrid GRID = new IconGrid();
	
	private static final int ICON_SIZE = 16;
	private static final int GRID_SIZE = 24;
	
	private JPopupMenu popup;
	private Note note;
	private int selection = -1;
	
	private Listener listener = new Listener();
	
	public IconGrid(){
		setBackground( Color.WHITE );
		setForeground( new Color( 150, 150, 255 ) );
		setFocusable( true );
		
		addMouseListener( listener );
		addMouseMotionListener( listener );
		
		popup = new JPopupMenu();
		popup.add( this );
		popup.addPopupMenuListener( listener );
	}
	
	@Override
	protected void paintComponent( Graphics g ){
		super.paintComponent( g );
		
		int size = ResourceSet.NOTE_ICONS.size();
		int sqrt = (int)Math.sqrt( size );
		if( sqrt*sqrt < size )
			sqrt++;
		
		int index = 0;
		
		loop: for( int y = 0; y < sqrt; y++ ){
			for( int x = 0; x < sqrt; x++ ){
				if( index >= ResourceSet.NOTE_ICONS.size() )
					break loop;
				
				if( selection == index ){
					g.setColor( getForeground() );
					g.fillRect( x*GRID_SIZE, y*GRID_SIZE, GRID_SIZE, GRID_SIZE );
				}
				
				Icon icon = ResourceSet.NOTE_ICONS.get( index );
				icon.paintIcon( this, g, 
						x*GRID_SIZE + (GRID_SIZE-ICON_SIZE)/2, 
						y*GRID_SIZE + (GRID_SIZE-ICON_SIZE)/2 );
				
				index++;
			}
		}
	}
	
	private int iconAt( int x, int y ){
		int size = ResourceSet.NOTE_ICONS.size();
		int sqrt = (int)Math.sqrt( size );
		if( sqrt*sqrt < size )
			sqrt++;
		
		x /= GRID_SIZE;
		y /= GRID_SIZE;
		
		int index = x + y * sqrt;
		if( index < 0 || index > ResourceSet.NOTE_ICONS.size() )
			return -1;
		
		return index;
	}
	
	@Override
	public Dimension getPreferredSize(){
		int size = ResourceSet.NOTE_ICONS.size();
		int sqrt = (int)Math.sqrt( size );
		if( sqrt*sqrt < size )
			sqrt++;
		
		int leftover = sqrt*sqrt - size;
		
		return new Dimension( GRID_SIZE*sqrt, GRID_SIZE*(sqrt - leftover/sqrt) );
	}
	
	public void changeIcon( Note note, Component owner, int x, int y ){
		selection = -1;
		repaint();
		this.note = note;
		popup.show( owner, x, y );
	}
	
	private class Listener extends MouseInputAdapter implements PopupMenuListener{
		@Override
		public void mouseMoved( MouseEvent e ){
			selection = iconAt( e.getX(), e.getY() );
			repaint();
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			if( note != null ){
				int check = iconAt( e.getX(), e.getY() );
				if( check == selection && selection >= 0 ){
					note.setIcon( ResourceSet.NOTE_ICONS.get( check ) );
					note = null;
					popup.setVisible( false );
				}
			}
		}

		public void popupMenuCanceled( PopupMenuEvent e ){
			note = null;
		}

		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ){
			// ignore
		}

		public void popupMenuWillBecomeVisible( PopupMenuEvent e ){
			// ignore
		}
	}
}
