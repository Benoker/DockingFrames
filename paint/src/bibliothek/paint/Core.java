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
package bibliothek.paint;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import bibliothek.demonstration.Monitor;
import bibliothek.gui.dock.common.menu.RootMenuPiece;
import bibliothek.gui.dock.common.menu.SubmenuPiece;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.menu.FLayoutChoiceMenuPiece;
import bibliothek.gui.dock.facile.menu.FLookAndFeelMenuPiece;
import bibliothek.gui.dock.facile.menu.FSingleDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.FThemeMenuPiece;
import bibliothek.gui.dock.support.menu.SeparatingMenuPiece;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.view.ViewManager;

/**
 * Class used to startup an application.
 * @author Benjamin Sigg
 *
 */
public class Core {
    /** whether the application runs in a secure environment or not */
    private boolean secure;
    
    /** the manager managing all the elements of the view */
    private ViewManager view;
    
    /**
     * Creates a new core.
     * @param secure whether the application runs in a secure environment or not
     */
    public Core( boolean secure ){
        this.secure = secure;
    }
    
    /**
     * Starts a new main-frame.
     */
    public void startup( final Monitor monitor ){
        if( monitor != null )
            monitor.startup();
        final JFrame frame = new JFrame( "Paint" );
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        
        FControl control = new FControl( frame, secure );

        RootMenuPiece settings = new RootMenuPiece( "View", false );
        settings.add( new FSingleDockableListMenuPiece( control ));
        settings.add( new SeparatingMenuPiece( new FLayoutChoiceMenuPiece( control, false ), true, false, false ));
        
        RootMenuPiece layout = new RootMenuPiece( "Layout", false );
        layout.add( new SubmenuPiece( "LookAndFeel", true, new FLookAndFeelMenuPiece( control )));
        layout.add( new SubmenuPiece( "Layout", true, new FThemeMenuPiece( control )));
        JMenuBar bar = new JMenuBar();
        bar.add( settings.getMenu() );
        bar.add( layout.getMenu() );
        frame.setJMenuBar( bar );
        
        frame.getContentPane().add( control.getCenter().getComponent() );
        
        PictureRepository pictures = new PictureRepository();
        view = new ViewManager( control, pictures );
        
        frame.setBounds( 20, 20, 600, 500 );
        
        // read and write settings
        if( secure ){
            InputStream in = Core.class.getResourceAsStream( "/data/bibliothek/paint/config.data" );
            if( in != null ){
                try{
                    DataInputStream dataIn = new DataInputStream( in );
                    read( dataIn );
                    dataIn.close();
                }
                catch( IOException ex ){
                    ex.printStackTrace();
                }
            }
        }
        else{
            try{
                DataInputStream in = new DataInputStream( new FileInputStream( "config.data" ));
                read( in );
                in.close();
            }
            catch( IOException ex ){
                ex.printStackTrace();
            }
        }
        
        frame.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                try{
                    frame.setVisible( false );
                    view.getControl().destroy();
                    
                    if( !secure ){
                        try{
                            DataOutputStream out = new DataOutputStream( new FileOutputStream( "config.data" ));
                            write( out );
                            out.close();
                        }
                        catch( IOException ex ){
                            ex.printStackTrace();
                        }
                    }
                }
                finally{
                    if( monitor != null ){
                        monitor.shutdown();
                    }
                    else{
                        System.exit( 0 );
                    }
                }
            }
        });
        
        // startup finished
        frame.setVisible( true );
        if( monitor != null )
            monitor.running();
    }
    
    /**
     * Writes all the settings of this application.
     * @param out the stream to write into
     * @throws IOException if an I/O error occurs
     */
    public void write( DataOutputStream out ) throws IOException{
        view.getPictures().write( out );
        view.getControl().getResources().writeStream( out );
    }
    
    /**
     * Reads all the settings of this application.
     * @param in the stream to read from
     * @throws IOException if an I/O error occurs
     */
    public void read( DataInputStream in ) throws IOException{
        view.getPictures().read( in );
        view.getControl().getResources().readStream( in );
    }
}
