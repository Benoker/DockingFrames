package test;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;

public class TestAside {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );
		
		CControl control = new CControl( frame );

		CToolbarContentArea area = new CToolbarContentArea( control, "center" );
		control.addStationContainer( area );
		frame.add( area );
	
		CopyItem start = new CopyItem( "i" );
		
		CToolbarAreaLocation east = area.getEastToolbar().getStationLocation();
		
		start.setLocation( east.group( 0 ).toolbar( 0, 0 ).item( 0 ));
		control.addDockable( start );
		
		start.setVisible( true );
		
		frame.setVisible( true );
	}
	
	private static class CopyItem extends CToolbarItem {
		private int count = 0;
		
		public CopyItem(String id){
			super(id);
			
			for( ExpandedState state : ExpandedState.values() ){
				JPanel actions = new JPanel( new GridLayout( 2, 1 ));
				setItem( actions, state );
				
				JButton copy = new JButton( "copy " + id );
				JButton delete = new JButton( "delete" );
				actions.add( copy );
				actions.add( delete );
				
				copy.addActionListener( new ActionListener(){
					@Override
					public void actionPerformed( ActionEvent e ){
						CControl control = getControl();
						CopyItem copy = new  CopyItem( getUniqueId() + "_" + (++count) );
						control.addDockable( copy );
						copy.setLocationsAside( CopyItem.this );
						copy.setVisible( true );		
					}
				});
				
				delete.addActionListener( new ActionListener(){
					@Override
					public void actionPerformed( ActionEvent e ){
						setVisible( false );
					}
				} );
			}
		}
		
	}
}
