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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import javax.swing.JComponent;

import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

/**
 * This {@link LayoutManager2} is used by the {@link BaseTabComponent} to lay out its children.
 * @author Benjamin Sigg
 */
public class BaseTabComponentLayoutManager implements LayoutManager2{
	private BaseTabComponent component;
	private JComponent label;
	private JComponent buttons;
	
	/**
	 * Creates a new layout manager.
	 * @param component the {@link Container} using this manager
	 * @param label the label {@link Component}
	 * @param buttons the button {@link Component}
	 */
	public BaseTabComponentLayoutManager( BaseTabComponent component, JComponent label, JComponent buttons ){
		this.component = component;
		this.label = label;
		this.buttons = buttons;
	}
	
	public Dimension minimumLayoutSize( Container parent ){
		return preferredLayoutSize( parent );
	}
	
	public Dimension maximumLayoutSize( Container target ){
		return preferredLayoutSize( target );
	}
	
	public Dimension preferredLayoutSize( Container parent ){
    	if( label == null )
    		return new Dimension( 1, 1 );

    	Dimension labelSize = label.getPreferredSize();
    	Dimension buttonSize = buttons.getPreferredSize();

    	TabPlacement orientation = component.getOrientation();
    	Insets labelInsets = component.getLabelInsets();
    	Insets buttonInsets = component.getButtonInsets();
    	
    	if( orientation.isHorizontal() ){
    		return new Dimension(
    				labelSize.width + buttonSize.width + labelInsets.left + labelInsets.right + buttonInsets.left + buttonInsets.right,
    				Math.max( labelSize.height + labelInsets.top + labelInsets.bottom, buttonSize.height + buttonInsets.top + buttonInsets.bottom ));
    	}
    	else{
    		return new Dimension(
    				Math.max( labelSize.width + labelInsets.left + labelInsets.right, buttonSize.width + buttonInsets.left + buttonInsets.right ),
    				labelSize.height + buttonSize.height + labelInsets.top + labelInsets.bottom + buttonInsets.top + buttonInsets.bottom );
    	}
	}
	
	public void layoutContainer( Container parent ){
    	if( label != null && buttons != null ){
    		Dimension labelSize = label.getPreferredSize();
    		Dimension buttonSize = buttons.getPreferredSize();
    		
    		int width = component.getWidth();
    		int height = component.getHeight();

        	TabPlacement orientation = component.getOrientation();
        	Insets labelInsets = component.getLabelInsets();
        	Insets buttonInsets = component.getButtonInsets();
        	
    		if( orientation.isHorizontal() ){
    			int labelHeight = labelSize.height + labelInsets.top + labelInsets.bottom;
    			int buttonHeight = buttonSize.height + buttonInsets.top + buttonInsets.bottom;
    			int buttonX = Math.max( 0, width - buttonSize.width - buttonInsets.right );
    			int labelWidth = Math.min( labelSize.width, Math.max( 0, buttonX - labelInsets.left ));
    			
    			label.setBounds( labelInsets.left, (height-labelHeight)/2 + labelInsets.top, labelWidth, labelSize.height );
    			buttons.setBounds( buttonX, (height-buttonHeight)/2 + buttonInsets.top, buttonSize.width, buttonSize.height );
    		}
    		else{
    			int labelWidth = labelSize.width + labelInsets.left + labelInsets.right;
    			int buttonWidth = buttonSize.width + buttonInsets.left + buttonInsets.right;
    			int buttonY = Math.max( 0, height - buttonSize.height - buttonInsets.bottom );
    			int labelHeight = Math.min( labelSize.height, Math.max( 0, buttonY - labelInsets.top ));
    			
    			label.setBounds( (width-labelWidth)/2 + labelInsets.left, labelInsets.top, labelSize.width, labelHeight );
    			buttons.setBounds( (width - buttonWidth)/2 + buttonInsets.left, buttonY, buttonSize.width, buttonSize.height );
    		}
    		
    		component.repaint();
    	}
	}
	
	public void addLayoutComponent( Component comp, Object constraints ){
		// ignore
	}
	
	public void addLayoutComponent( String name, Component comp ){
		// ignore	
	}
	
	public float getLayoutAlignmentX( Container target ){
		return 0;
	}
	
	public float getLayoutAlignmentY( Container target ){
		return 0;
	}
	
	public void invalidateLayout( Container target ){
		// ignore	
	}
	
	public void removeLayoutComponent( Component comp ){
		// ignore
	}
}
