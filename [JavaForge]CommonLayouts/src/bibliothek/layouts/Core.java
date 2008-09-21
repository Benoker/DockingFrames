package bibliothek.layouts;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;
import javax.swing.JFrame;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.facile.lookandfeel.DockableCollector;
import bibliothek.layouts.controlling.ModifySingleDockable;
import bibliothek.layouts.controlling.StorageDockable;
import bibliothek.layouts.testing.EnvironmentDockable;

public class Core implements Demonstration{
    private EnvironmentDockable environment;
    private StorageDockable storage;
    private ModifySingleDockable singleDockables;
    
    public String getHTML() {
        // TODO Auto-generated method stub
        return null;
    }

    public Icon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    public BufferedImage getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public EnvironmentDockable getEnvironment() {
        return environment;
    }
    
    public StorageDockable getStorage() {
        return storage;
    }
    
    public ModifySingleDockable getSingleDockables() {
        return singleDockables;
    }

    public void show( final Monitor monitor ) {
        if( monitor != null ){
            monitor.startup();
        }
        
        final JFrame frame = new JFrame( "Common Layout" );
        CControl control = new CControl( frame );
        frame.add( control.getContentArea() );
        
        CGrid grid = new CGrid( control );
        
        environment = new EnvironmentDockable();
        storage = new StorageDockable( this );
        singleDockables = new ModifySingleDockable( this );
        
        grid.add( 0, 0, 100, 100, environment );
        grid.add( 100, 0, 30, 100, storage );
        grid.add( 0, 100, 130, 30, singleDockables );
        control.getContentArea().deploy( grid );
        
        if( monitor != null ){
            monitor.publish( new DockableCollector( control.intern() ));
        }
        
        frame.setBounds( 20, 20, 500, 400 );
        
        if( monitor != null ){
            frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
            frame.addWindowListener( new WindowAdapter(){
                @Override
                public void windowClosing( WindowEvent e ) {
                    frame.dispose();
                    monitor.shutdown();
                }
            });
            
            try {
                monitor.invokeSynchron( new Runnable(){
                    public void run() {
                        frame.setVisible( true );
                    }
                });
            }
            catch( InvocationTargetException e ) {
                e.printStackTrace();
            }
            
            monitor.running();
        }
        else{
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setVisible( true );
        }
    }
}
