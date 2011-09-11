package tutorial.common.basics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.util.FocusedWindowProvider;

@Tutorial( id="MultiFrame", title="Multiple Frames" )
public class MultiFrameExample {
	/* This example demonstrates how the framework deals with multiple JFrames, and how an application must
	 * be modified to support multiple JFrames.
	 * 
	 * The example allows up to 4 JFrames to show up. All JFrames have the exact same properties, and there is no
	 * way to tell which one is the "main" JFrame. Also users can close and open new JFrames at any time. 
	 * 
	 * Clients with a similar layout will need to use the FocusedWindowProvider and should not call
	 * CControl.getContentArea(). Otherwise no additional configuration is needed. */
	
	public static void main( String[] args ){
		/* Since there is not one but many main-Frames, it is hard to specify which one is the root-window. The
		 * FocusedWindowProvider always assumes that the window that is or was focused is the root-window. */
		FocusedWindowProvider windows = new FocusedWindowProvider();
		
		/* One of the constructors of CControl allows to set the root-window provider directly */
		CControl control = new CControl( windows );
		
		/* We need some kind of application object that helps with managing the JFrames */
		Application application = new Application( control, windows );
		for( int i = 0; i < 4; i++ ){
			application.switchFrame( i );
		}
	}
	
	/* We are going to show some MultipleCDockables. While this factory could create these dockables, it is actually
	 * never used. It exists only because the API forces us to provide a factory. */
	private static class Factory extends EmptyMultipleCDockableFactory<MultipleCDockable>{
		@Override
		public MultipleCDockable createDockable(){
			return newDockable( this );
		}		
	}
	
	/* This method actually creates new Dockables that we are going to show. Each Dockable shows a panel
	 * with a randomly chosen color. */
	private static MultipleCDockable newDockable( Factory factory ){
		int random = (int)(Math.random() * 255 * 255 * 255);
		DefaultMultipleCDockable dockable = new DefaultMultipleCDockable( factory );
		dockable.setTitleText( String.valueOf( random ) );
		dockable.setRemoveOnClose( true );
		dockable.setCloseable( true );
		JPanel panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( new Color( random ) );
		dockable.add( panel );
		return dockable;
	}

	/* This is a panel showing four JToggleButtons. A button that is selected represents a JFrame
	 * that is visible, a button that is not selected represents a JFrame that is invisible. 
	 * 
	 * Each JFrame will show one of this panels. */
	private static class FrameSwitchPanel extends JPanel{
		private JToggleButton[] buttons = new JToggleButton[4];
		private Application application;
		
		public FrameSwitchPanel( Application application ){
			setLayout( new GridLayout( 2, 2 ) );
			this.application = application;
			for( int i = 0; i < buttons.length; i++ ){
				buttons[i] = initButton( i );
				add( buttons[i] );
			}
		}
		
		private JToggleButton initButton( final int index ){
			JToggleButton button = new JToggleButton( String.valueOf( index ));
			button.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					/* If the user clicks on the button we make the JFrame visible or invisible */
					application.switchFrame( index );
				}
			});
			return button;
		}
		
		public void setOpened( int index, boolean open ){
			buttons[index].setSelected( open );
		}
	}
	
	/* A JFrame containing one CContentArea */
	private static class ExampleFrame extends JTutorialFrame{
		private FrameSwitchPanel switcher;
		private CContentArea area;
		
		public ExampleFrame( final int location, final Application application ){
			super( MultiFrameExample.class );

			/* Registering some control buttons, and also making sure all references to this
			 * JFrame are removed once this frame has been closed. */
			switcher = new FrameSwitchPanel( application );
			application.addSwitcher( switcher );
			addWindowListener( new WindowAdapter(){
				@Override
				public void windowClosed( WindowEvent e ){
					application.removeSwitcher( switcher );
					application.getControl().removeStationContainer( area );
				}
			});
			
			/* We create a new CContentArea that is shown on this JFrame. We choose a unique identifier that does
			 * not collide with existing identifiers. */
			CControl control = application.getControl();
			area = control.createContentArea( "area " + location );
			
			/* Inform the framework about the existence of this Window */
			application.getWindows().add( this );
			
			/* A button for creating new Dockables */
			JButton add = new JButton("New dockable");
			add.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					application.createDockable( location );
				}
			});
			
			/* Now we add all Components to this JFrame. */
			JPanel north = new JPanel( new GridBagLayout() );
			north.add( switcher, new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ));
			north.add( add, new GridBagConstraints( 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ));
			add( north, BorderLayout.NORTH );
			add( area, BorderLayout.CENTER );
			
			/* Finally setting a location such that all JFrames will appear next to each other */
			switch( location ){
				case 0:
					setBounds( 20, 20, 400, 400 );
					break;
				case 1:
					setBounds( 420, 20, 400, 400 );
					break;
				case 2:
					setBounds( 20, 420, 400, 400 );
					break;
				case 3:
					setBounds( 420, 420, 400, 400 );
					break;
			}
		}
		
		public CContentArea getArea(){
			return area;
		}
	}
	
	/* The Application object is responsible for connecting the JFrames */
	private static class Application {
		private List<FrameSwitchPanel> switchers = new ArrayList<FrameSwitchPanel>();
		private ExampleFrame[] frames = new ExampleFrame[4];
		private CControl control;
		private Factory factory;
		private FocusedWindowProvider windows;
		
		/* Informs the FrameSwitchPanel which JFrames are visible and which are not */
		private WindowListener framesListener = new WindowAdapter(){
			public void windowOpened( WindowEvent e ){
				int index = indexOf( (ExampleFrame)e.getWindow() );
				for( FrameSwitchPanel switcher : switchers ){
					switcher.setOpened( index, true );
				}
			}
			
			public void windowClosed( WindowEvent e ){
				int index = indexOf( (ExampleFrame)e.getWindow() );
				for( FrameSwitchPanel switcher : switchers ){
					switcher.setOpened( index, false );
				}
				windows.remove( e.getWindow() );
				frames[index].removeWindowListener( this );
				frames[index].dispose();
				frames[index] = null;
			}
		};
		
		public Application( CControl control, FocusedWindowProvider windows ){
			this.control = control;
			this.windows = windows;
			factory = new Factory();
			control.addMultipleDockableFactory( "color", factory );
		}
		
		private int indexOf( ExampleFrame frame ){
			for( int i = 0; i < frames.length; i++ ){
				if( frames[i] == frame ){
					return i;
				}
			}
			return -1;
		}
		
		/* Either shows or hides the JFrame 'index' */
		public void switchFrame(int index){
			if(frames[index] == null){
				frames[index] = new ExampleFrame( index, this );
				frames[index].addWindowListener( framesListener );
				frames[index].setVisible( true );
			}
			else{
				frames[index].dispose();
			}
		}
		
		/* Creates a new Dockable and adds it to the JFrame 'index' */
		public void createDockable( int index ){
			MultipleCDockable dockable = newDockable( factory );
			control.addDockable( dockable );
			dockable.setLocation( CLocation.base( frames[index].getArea() ).normal() );
			dockable.setVisible( true );
		}
		
		public void addSwitcher(FrameSwitchPanel panel){
			switchers.add( panel );
			for( int i = 0; i < frames.length; i++ ){
				panel.setOpened( i, frames[i] != null );
			}
		}
		
		public void removeSwitcher( FrameSwitchPanel panel ){
			switchers.remove( panel );
		}
		
		public CControl getControl(){
			return control;
		}
		
		public FocusedWindowProvider getWindows(){
			return windows;
		}
	}
}
