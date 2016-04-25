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

import java.awt.image.BufferedImage;

import javax.swing.Icon;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.paint.util.Resources;

/**
 * A class allowing to startup this application in a secure
 * environment.
 * @author Benjamin Sigg
 */
public class Webstart implements Demonstration {
    public String getHTML() {
    	return Resources.getText();
    }

    public Icon getIcon() {
    	return Resources.getIcon( "application" );
    }

    public BufferedImage getImage() {
    	return Resources.getScreenshot();
    }

    public String getName() {
        return "Paint";
    }

    public static void main( String[] args ){
    	Core core = new Core( true );
        core.startup( null );
	}
    
    public void show( Monitor monitor ) {
        Core core = new Core( true );
        core.startup( monitor );
    }

}
