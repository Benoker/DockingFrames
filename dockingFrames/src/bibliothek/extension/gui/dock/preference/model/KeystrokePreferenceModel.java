/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.model;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.PreferenceTable;
import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.DockPropertyPreference;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.control.DockableSelector;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * A model that lists the keystrokes which are used in the framework.
 * @author Benjamin Sigg
 */
public class KeystrokePreferenceModel extends DefaultPreferenceModel{
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        
        DockController controller = new DockController();
        KeystrokePreferenceModel model = new KeystrokePreferenceModel( controller.getProperties() );
        model.read();
        
        frame.add( new JScrollPane( new PreferenceTable( model )) );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
    
    /**
     * Creates a new model.
     * @param properties the properties to read and write from
     */
    public KeystrokePreferenceModel( DockProperties properties ){
        if( properties == null )
            throw new IllegalArgumentException( "properties must not be null" );
        
        add( keystroke( "init_selection", DockableSelector.INIT_SELECTION, properties,
                KeyStroke.getKeyStroke( KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK ),
                new Path( "dock.DockableSelector.INIT_SELECTION" )));
        
        add( keystroke( "maximize_accelerator", SplitDockStation.MAXIMIZE_ACCELERATOR, properties,
                KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK ),
                new Path( "dock.SplitDockStation.MAXIMIZE_ACCELERATOR" )) );
        
        add( keystroke( "hide_accelerator", DockFrontend.HIDE_ACCELERATOR, properties,
                KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK ),
                new Path( "dock.DockFrontend.HIDE_ACCELERATOR" )));
    }
    
    private Preference<KeyStroke> keystroke( String prefix, PropertyKey<KeyStroke> key, DockProperties properties, KeyStroke defaultValue, Path path ){
        return new DockPropertyPreference<KeyStroke>(
                DockUI.getDefaultDockUI().getBundle(),
                "preference." + prefix, 
                properties,
                key,
                defaultValue,
                Path.TYPE_KEYSTROKE_PATH,
                path );
    }
}
