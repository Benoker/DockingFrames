/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.sizeAndColor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.event.CVetoFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.layout.FullLockConflictResolver;
import bibliothek.gui.dock.common.menu.CLookAndFeelMenuPiece;
import bibliothek.gui.dock.common.menu.CThemeMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;

/**
 * The core is used to startup the application. The core contains some 
 * of the global available resources like the applications icon.
 * @author Benjamin Sigg
 */
public class Core implements Demonstration{
    public static void main( String[] args ) {
    	Core core = new Core();
        core.startup( null );
    }
    
    /** the icon of the application */
    private ImageIcon icon;
    /** a screenshot of the application */
    private BufferedImage snapshot;
    /** some explanation what the application does, in html */
    private String description;
    
    /**
     * Creates a new core, containing global resources.
     */
    public Core(){
        try {
            snapshot = ImageIO.read( Core.class.getResource( "/data/bibliothek/sizeAndColor/snapshot.png" ) );
            icon = new ImageIcon( Core.class.getResource( "/data/bibliothek/sizeAndColor/icon.png" ));
            
            Reader reader = new InputStreamReader( 
                    Core.class.getResourceAsStream( "/data/bibliothek/sizeAndColor/description.txt" ), "UTF-8" );
            StringBuilder builder = new StringBuilder();
            int next;
            while( (next=reader.read()) != -1 )
                builder.append( (char)next );
            reader.close();
            description = builder.toString();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Starts this application.
     * @param monitor a monitor used to inform about the state of this application
     */
    public void startup( final Monitor monitor ){   
        try{
            if( monitor != null ){
                monitor.startup();
            }

            final JFrame frame = new JFrame( "Size & Color" );
            frame.setIconImage( icon.getImage() );
            final CControl control = new CControl( frame, true );
            control.putProperty( CControl.RESIZE_LOCK_CONFLICT_RESOLVER, new FullLockConflictResolver() );
            
            LookAndFeelList list = monitor == null ? null : monitor.getGlobalLookAndFeel();
            RootMenuPiece laf = new RootMenuPiece( "Look And Feel", false, new CLookAndFeelMenuPiece( control, list ));
            RootMenuPiece theme = new RootMenuPiece( "Theme", false, new CThemeMenuPiece( control ));
            JMenuBar bar = new JMenuBar();
            bar.add( laf.getMenu() );
            bar.add( theme.getMenu() );
            frame.setJMenuBar( bar );

            control.addMultipleDockableFactory( "frame", Frame.FACTORY );
            frame.add( control.getContentArea() );
            
            control.addVetoFocusListener( new CVetoFocusListener(){
            	public boolean willLoseFocus( CDockable dockable ){
            		if( dockable instanceof Frame ){
            			if( !((Frame)dockable).isFocusLostAllowed() )
            				return false; 
            		}
            		
            		return true;
            	}
            	public boolean willGainFocus( CDockable dockable ){
	            	return true;
            	}
            });
            
            CGrid grid = new CGrid();
            for( int i = 0; i < 3; i++ ){
                for( int j = 0; j < 2; j++ ){
                    Frame f = new Frame();
                    control.addDockable( f );
                    grid.add( i, j, 1, 1, f );
                }
            }

            control.getContentArea().deploy( grid );

            frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
            frame.setBounds( 20, 20, 500, 500 );
            
            frame.addWindowListener( new WindowAdapter(){
                @Override
                public void windowClosing( WindowEvent e ) {
                    if( monitor == null ){
                        System.exit( 0 );
                    }
                    else{
                        try{
                            frame.dispose();
                            control.destroy();
                        }
                        finally{
                            monitor.shutdown();
                        }
                    }
                }
            });
            
            frame.setVisible( true );
        }
        finally{
            if( monitor != null )
                monitor.running();
        }
    }

    public String getHTML() {
        return description;
    }

    public Icon getIcon() {
        return icon;
    }

    public BufferedImage getImage() {
        return snapshot;
    }

    public String getName() {
        return "Size & Color";
    }

    public void show( Monitor monitor ) {
        startup( monitor );
    }
}
