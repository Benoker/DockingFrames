/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic.action.buttons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelAdapter;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModelListener;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.themes.font.ButtonFont;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.swing.OrientedLabel;

/**
 * This (invisible) {@link Component} can be used by buttons to show an {@link Icon} and a text that originate from
 * a {@link BasicButtonModel}. The contents of this {@link Component} will be update automatically.
 * @author Benjamin Sigg
 */
public class MiniButtonContent extends JComponent {
	private BasicButtonModel model;
	private OrientedLabel label;
	
	/** the original foreground color of {@link #label} */
	private Color labelOriginalColor;
    
    /** the foreground color */
    private ActionColor textColor;
    
    /** the foreground color if disabled */
    private ActionColor textColorDisabled;
    
    /** whether the foreground color of the label was set */
    private boolean foregroundColorSet = false;
	
    /** the expected minimum size of icons */
    private PropertyValue<Dimension> minimumIconSize = new PropertyValue<Dimension>( IconManager.MINIMUM_ICON_SIZE ){
    	@Override
    	protected void valueChanged( Dimension oldValue, Dimension newValue ){
    		revalidate();
    	}
	};
	
	/** this listener is added to {@link #model} and will be informed if any property changes */
	private BasicButtonModelListener listener = new BasicButtonModelAdapter(){
		@Override
		public void orientationChanged( BasicButtonModel model, Orientation old, Orientation orientation ){
			label.setHorizontal( orientation.isHorizontal() );
		}
		
		@Override
		public void enabledStateChanged( BasicButtonModel model, boolean enabled ){
			updateLabelEnabled();
		}
		
		public void textChanged( BasicButtonModel model, String oldText, String text ){
			label.setText( text );
		}
		
		@Override
		public void bound( BasicButtonModel model, DockController controller ){
			minimumIconSize.setProperties( controller );
			font.connect( controller );
			if( textColor != null ){
				textColor.connect( controller );
				textColorDisabled.connect( controller );
			}
		}
		
    	@Override
    	public void unbound( BasicButtonModel model, DockController controller ){
    		minimumIconSize.setProperties( (DockController)null );
    		font.connect( null );
    		if( textColor != null ){
    			textColor.connect( null );
    			textColorDisabled.connect( null );
    		}
    	}
	};
	
	/** font used by this component */
	private ButtonFont font;
	
	/**
	 * Creates a new content component
	 */
	public MiniButtonContent(){
		label = new OrientedLabel(){
			@Override
			public boolean contains( int x, int y ){
				return false;
			}
			@Override
			public void updateUI(){
				Color current = getForeground();
				if( labelOriginalColor != null ){
					setForeground( labelOriginalColor );
				}
				super.updateUI();
				labelOriginalColor = getForeground();
				if( labelOriginalColor != null ){
					setForeground( current );
				}
			}
		};
		labelOriginalColor = label.getForeground();
	
		setFocusable( false );
		label.setFocusable( false );
		
		setLayout( null );
		setOpaque( false );
		add( label );
		
		label.setIconOffset( 0 );
		label.setIconTextDistance( 2 );
		
		// we already build our own disabled version of the icon
		label.setPaintDisabledIcon( false );
	}
	
	/**
	 * Creates a new {@link ActionColor} using <code>id</code> as unique identifier. This color
	 * will be used as {@link #setForeground(Color) foreground} color. This method requires
	 * that the {@link #setModel(BasicButtonModel) model} is already set, but not yet bound.
	 * @param id the unique identifier of the color
	 */
	public void setForegroundColorId( String id, String disabledId ){
		if( textColor == null ){
			textColor = new ActionColor( id, model.getDockable(), model.getAction(), null ){
				@Override
				protected void changed( Color oldValue, Color newValue ){
					if( oldValue != newValue ){
						if( model.isEnabled() ){
							setLabelForeground( newValue );
						}
					}
				}
			};
			textColorDisabled = new ActionColor( disabledId, model.getDockable(), model.getAction(), null ){
				@Override
				protected void changed( Color oldValue, Color newValue ){
					if( oldValue != newValue ){
						if( !model.isEnabled() ){
							setLabelForeground( newValue );
						}
					}
				}
			};
		}
		else{
			textColor.setId( id );
		}
	}
	
	/**
	 * Sets the foreground color of the label.
	 * @param color the new color, can be <code>null</code>
	 */
	public void setLabelForeground( Color color ){
		if( color == null ){
			label.setForeground( labelOriginalColor );
		}
		else{
			label.setForeground( color );
		}
		foregroundColorSet = color != null;
		if( foregroundColorSet ){
			label.setEnabled( true );
		}
		else{
			label.setEnabled( model.isEnabled() );
		}
	}
	
	/**
	 * Changes the enable state of the label.
	 */
	public void updateLabelEnabled(){
		boolean enabled = model.isEnabled();
		
		if( textColor != null ){
			if( enabled ){
				setLabelForeground( textColor.color() );
			}
			else{
				setLabelForeground( textColorDisabled.color() );
			}
		}
		else{
			if( !foregroundColorSet ){
				label.setEnabled( enabled );
			}
			else{
				label.setEnabled( true );
			}
		}
	}
	
	@Override
	public Dimension getPreferredSize(){
		return label.getPreferredSize();
	}
	
	@Override
	public Dimension getMinimumSize(){
		return label.getMinimumSize();
	}
	
	@Override
	public Dimension getMaximumSize(){
		return label.getMaximumSize();
	}
	
	@Override
	public void doLayout(){
		Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
		Dimension preferred = label.getPreferredSize();
		
	    int dw = Math.max( 0, bounds.width - preferred.width );
	    int dh = Math.max( 0, bounds.height - preferred.height );
	    
	    bounds.x += dw / 2;
	    bounds.y += dh / 2;
	    bounds.width -= dw;
	    bounds.height -= dh;
	    
	    label.setBounds( bounds );	
	}
	
	public boolean contains( int x, int y ) {
		return false;
	}
	
	/**
	 * Sets the model from which to read content.
	 * @param model the model or <code>null</code>, the model must not be bound.
	 */
	public void setModel( BasicButtonModel model ){
		if( this.model != null ){
			throw new IllegalStateException( "the model can be set only once" );
		}
		
		if( this.model != null ){
			this.model.removeListener( listener );
		}
		
		this.model = model;
		
		if( this.model != null ){
			this.model.addListener( listener );
		}
		
		if( textColor != null ){
			textColor.connect( null );
			textColorDisabled.connect( null );
			String id = textColor.getId();
			String disabledId = textColorDisabled.getId();
			textColor = null;
			textColorDisabled = null;
			setForeground( null );
			setForegroundColorId( id, disabledId );
		}
		
		updateFont();
		updateContent();
	}
	
	private void updateFont(){
		if( font != null ){
			font.connect( null );
		}
		
		font = null;
		
		if( model != null ){
			font = new ButtonFont( DockFont.ID_BUTTON, model.getDockable(), model.getAction(), null ){
				@Override
				protected void changed( FontModifier oldValue, FontModifier newValue ){
					label.setFontModifier( newValue );
				}
			};
		}
	}
	
	/**
	 * Updates the content (icon, text, orientation, ...) of this {@link MiniButtonContent}.
	 */
	protected void updateContent(){
		if( model == null ){
			label.setIcon( null );
			label.setText( null );
			label.setHorizontal( true );
			label.setEnabled( true );
		}
		else{
			label.setIcon( new ForwardIcon() );
			label.setText( model.getText() );
			label.setHorizontal( model.getOrientation().isHorizontal() );
			label.setEnabled( model.isEnabled() );
		}
	}
	
    
    /**
     * Gets the expected minimum size of any icon.
     * @return the expected minimum size of any icon
     */
    protected Dimension getMinimumIconSize(){
    	return minimumIconSize.getValue();
    }
    
	private class ForwardIcon implements Icon{
		public void paintIcon( Component c, Graphics g, int x, int y ){
			Icon icon = model.getPaintIcon();
			if( icon != null ){
				Dimension min = getMinimumIconSize();
				Dimension max = model.getMaxIconSize();
				
				int dx = (Math.max( max.width, min.width ) - icon.getIconWidth()) / 2;
				int dy = (Math.max( max.height, min.height ) - icon.getIconHeight()) / 2;
				
				icon.paintIcon( c, g, x+dx, y+dy );
			}
		}

		public int getIconWidth(){
			Dimension min = getMinimumIconSize();
			return Math.max( min.width, model.getMaxIconSize().width );
		}

		public int getIconHeight(){
			Dimension min = getMinimumIconSize();
			return Math.max( min.height, model.getMaxIconSize().height );
		}
		
	}
}
