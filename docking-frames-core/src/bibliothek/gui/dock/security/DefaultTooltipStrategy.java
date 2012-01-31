/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.security;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JToolTip;

/**
 * The default implementation of {@link TooltipStrategy} calls the methods of the {@link Component}
 * under the mouse whenever possible.
 * @author Benjamin Sigg
 */
public class DefaultTooltipStrategy implements TooltipStrategy{
	public void install( GlassedPane pane ){
		// ignore
	}
	public void uninstall( GlassedPane pane ){
		// ignore
	}
	
	public void setTooltipText( Component component, MouseEvent event, boolean overNewComponent, TooltipStrategyCallback callback ){
		if( component instanceof JComponent ){
        	JComponent jcomp = (JComponent)component;
            String tooltip = jcomp.getToolTipText( event );
            String thistip = callback.getToolTipText();

            if( tooltip != thistip || overNewComponent ){
                if( tooltip == null || thistip == null || !tooltip.equals( thistip ) || overNewComponent ){
                	callback.setToolTipText( tooltip );
                }
            }
        }
        else{
        	callback.setToolTipText( null );
        }
	}
	public JToolTip createTooltip( Component component, TooltipStrategyCallback callback ){
		if( component instanceof JComponent ){
    		return ((JComponent)component).createToolTip();
    	}
    	else{
    		return callback.createToolTip();
    	}
	}
}
