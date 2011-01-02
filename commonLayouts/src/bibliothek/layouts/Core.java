package bibliothek.layouts;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFrame;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.facile.lookandfeel.DockableCollector;
import bibliothek.layouts.controlling.ModifyMultiDockable;
import bibliothek.layouts.controlling.ModifySingleDockable;
import bibliothek.layouts.controlling.StorageDockable;
import bibliothek.layouts.testing.EnvironmentDockable;

public class Core implements Demonstration{
    private EnvironmentDockable environment;
    private StorageDockable storage;
    private ModifySingleDockable singleDockables;
    private ModifyMultiDockable multiDockables;

    private BufferedImage image;
    private String html;

    public String getHTML() {
        if( html == null ){
            try{
                InputStream in = Core.class.getResourceAsStream( "/data/bibliothek/commonLayouts/info.html" );
                InputStreamReader reader = new InputStreamReader( in );

                StringBuilder builder = new StringBuilder();
                int c;
                while( (c = reader.read()) != -1 ){
                    builder.append( (char)c );
                }

                reader.close();
                in.close();

                html = builder.toString();
            }
            catch( IOException ex ){
                // that should not happen
                ex.printStackTrace();
                html = "";
            }
        }

        return html;
    }

    public Icon getIcon() {
        return Icons.get( "icon" );
    }

    public BufferedImage getImage() {
        if( image == null ){
            try {
                image = ImageIO.read( Core.class.getResource( "/data/bibliothek/commonLayouts/image.png" ) );
            }
            catch( IOException e ) {
                // that should not happen
                e.printStackTrace();
            }
        }
        return image;
    }

    public String getName() {
        return "Common Layouts";
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

    public ModifyMultiDockable getMultiDockables() {
        return multiDockables;
    }

    public void show( final Monitor monitor ) {
        if( monitor != null ){
            monitor.startup();
        }

        final JFrame frame = new JFrame( "Common Layout" );
        frame.setIconImage( Icons.getIconImage() );
        CControl control = new CControl( frame, true );
        frame.add( control.getContentArea() );

        CGrid grid = new CGrid( control );

        environment = new EnvironmentDockable();
        storage = new StorageDockable( this, control );
        singleDockables = new ModifySingleDockable( this, control );
        multiDockables = new ModifyMultiDockable( this, control );

        grid.add( 0, 0, 100, 100, environment );
        grid.add( 100, 0, 50, 100, storage );
        grid.add( 0, 100, 65, 50, singleDockables );
        grid.add( 65, 100, 65, 50, multiDockables );
        control.getContentArea().deploy( grid );

        if( monitor != null ){
            monitor.publish( new DockableCollector( control.intern() ));
        }

        frame.setBounds( 20, 20, 600, 400 );

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
