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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import bibliothek.demonstration.Monitor;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.menu.CLayoutChoiceMenuPiece;
import bibliothek.gui.dock.common.menu.CLookAndFeelMenuPiece;
import bibliothek.gui.dock.common.menu.CPreferenceMenuPiece;
import bibliothek.gui.dock.common.menu.CThemeMenuPiece;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.facile.menu.SubmenuPiece;
import bibliothek.gui.dock.support.menu.SeparatingMenuPiece;
import bibliothek.paint.model.PictureRepository;
import bibliothek.paint.util.Resources;
import bibliothek.paint.view.ViewManager;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

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
    
    /** whether to use a xml or a binary file to store persistent data */
    private boolean formatXML = true;
    
    /**
     * Creates a new core.
     * @param secure whether the application runs in a secure environment or not
     */
    public Core( boolean secure ){
        this.secure = secure;
    }
    
    /**
     * Starts a new main-frame.
     * @param monitor the callback informing the caller about the state
     * of this application
     */
    public void startup( final Monitor monitor ){
        if( monitor != null )
            monitor.startup();
        final JFrame frame = new JFrame( "Paint" );
        frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        frame.setIconImage( Resources.toImage( Resources.getIcon( "application" ) ) );
        
        final CControl control = new CControl( frame, secure );

        RootMenuPiece settings = new RootMenuPiece( "View", false );
        settings.add( new SingleCDockableListMenuPiece( control ));
        settings.add( new SeparatingMenuPiece( new CLayoutChoiceMenuPiece( control, false ), true, false, false ));
        
        RootMenuPiece layout = new RootMenuPiece( "Layout", false );
        layout.add( new SubmenuPiece( "LookAndFeel", true, new CLookAndFeelMenuPiece( control )));
        layout.add( new SubmenuPiece( "Layout", true, new CThemeMenuPiece( control )));
        layout.add( CPreferenceMenuPiece.setup( control ));
        
        JMenuBar bar = new JMenuBar();
        bar.add( settings.getMenu() );
        bar.add( layout.getMenu() );

        frame.setJMenuBar( bar );
        
        frame.getContentPane().add( control.getContentArea() );
        
        PictureRepository pictures = new PictureRepository();
        view = new ViewManager( control, pictures );
        
        frame.setBounds( 20, 20, 600, 500 );
        
        // read and write settings
        if( secure ){
            // InputStream in = Core.class.getResourceAsStream( "/data/bibliothek/paint/config.xml" );
            InputStream in = Core.class.getResourceAsStream( "/data/bibliothek/paint/config.xml" );
            if( in != null ){
                try{
                	if( formatXML ){
	                    readXML( XIO.readUTF( in ) );
	                    in.close();
                	}
                	else{
	                    DataInputStream dataIn = new DataInputStream( in );
	                    read( dataIn );
	                    dataIn.close();
                	}
                }
                catch( IOException ex ){
                    ex.printStackTrace();
                }
            }
        }
        else{
            try{
            	if( formatXML ){
	                InputStream in = new BufferedInputStream( new FileInputStream( "config.xml" ));
	                readXML( XIO.readUTF( in ) );
	                in.close();
            	}
            	else{
            		DataInputStream in = new DataInputStream( new FileInputStream( "paint.config" ));
            		read( in );
            		in.close();
            	}
            }
            catch( IOException ex ){
                ex.printStackTrace();
            }
        }
        
        view.getWorkingArea().setVisible( true );
        
        frame.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                try{
                    frame.dispose();
                    
                    if( !secure ){
                        try{
                        	if( formatXML ){
	                            XElement element = new XElement( "config" );
	                            writeXML( element );
	                            OutputStream out = new BufferedOutputStream( new FileOutputStream( "config.xml" ));
	                            XIO.writeUTF( element, out );
                        	}
                        	else{
	                            DataOutputStream out = new DataOutputStream( new FileOutputStream( "paint.config" ));
	                            write( out );
	                            out.close();
                        	}
                        }
                        catch( IOException ex ){
                            ex.printStackTrace();
                        }
                    }
                    
                    view.getControl().destroy();
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
     * Writes all the settings of this application.
     * @param element the xml element to write into
     */
    public void writeXML( XElement element ){
        view.getPictures().writeXML( element.addElement( "pictures" ) );
        view.getControl().getResources().writeXML( element.addElement( "resources" ) );
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
    
    /**
     * Reads all the settings of this application.
     * @param element the element to read from
     */
    public void readXML( XElement element ){
        view.getPictures().readXML( element.getElement( "pictures" ) );
        view.getControl().getResources().readXML( element.getElement( "resources" ) );
    }
}
