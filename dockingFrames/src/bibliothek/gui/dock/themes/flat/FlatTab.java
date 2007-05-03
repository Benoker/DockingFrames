/**
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

package bibliothek.gui.dock.themes.flat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.gui.dock.station.stack.StackDockComponent;

/**
 * A panel that works like a {@link JTabbedPane}, but the buttons to
 * change between the children are smaller and "flatter" than the
 * buttons of the <code>JTabbedPane</code>.
 * @author Benjamin Sigg
 */
public class FlatTab implements StackDockComponent{
    /** The panel which shows the children */
    private JPanel panel;
    
    /** A list of all buttons of this FlatTab */
    private List<FlatButton> buttons = new ArrayList<FlatButton>();
    
    /** A panel which displaies the buttons of this FlatTab */
    private JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.LEADING, 0, 0 ){
        @Override
        public Dimension minimumLayoutSize( Container target ){
            return preferredLayoutSize( target );
        }
        
        @Override
        public Dimension preferredLayoutSize( Container target ){
            if( target.getParent() == null )
                return super.preferredLayoutSize( target );
            
            int width = target.getParent().getWidth();
            
            int maxWidth = 0;
            int currentWidth = 0;
            int currentHeight = 0;
            int left = 0;
            int height = 0;
            
            for( int i = 0, n = target.getComponentCount(); i<n; i++ ){
                Dimension preferred = target.getComponent(i).getPreferredSize();
                
                if( left == 0 || currentWidth + preferred.width <= width ){
                    currentWidth += preferred.width;
                    currentHeight = Math.max( currentHeight, preferred.height );
                    left++;
                }
                else{
                    height += currentHeight;
                    maxWidth = Math.max( maxWidth, currentWidth );
                    left = 0;
                    
                    currentWidth = preferred.width;    
                    currentHeight = preferred.height;
                    left++;
                }
            }
            
            
            height += currentHeight;
            maxWidth = Math.max( maxWidth, currentWidth );
        
            return new Dimension( maxWidth, height );
        }
    });
    
    /** A list of all {@link Component Components} which are shown on this panel */
    private List<Component> components = new ArrayList<Component>();
    
    /** The panel which displays one of the children of this FlatTab */
    private JPanel componentPanel = new JPanel(){
    	@Override
    	public void doLayout(){
    		int w = getWidth();
    		int h = getHeight();
    		
    		for( int i = 0, n = getComponentCount(); i<n; i++ ){
    			getComponent(i).setBounds( 0, 0, w, h );
    		}
    	}
    	
    	@Override
    	public Dimension getMinimumSize() {
    		Dimension base = new Dimension( 0, 0 );
    		for( int i = 0, n = getComponentCount(); i<n; i++ ){
    			Dimension next = getComponent(i).getMinimumSize();
    			base.width = Math.max( base.width, next.width );
    			base.height = Math.max( base.height, next.height );
    		}
    		return base;
    	}
    };
    
    /** The index of the currently visible child */
    private int selectedIndex = -1;
    
    /** A list of listeners which have to informed when the selection changes */
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    /**
     * Constructs a new FlatTab
     */
    public FlatTab(){
        panel = new JPanel( null ){
            @Override
            @Deprecated
            public void reshape( int x, int y, int w, int h ){
                super.reshape( x, y, w, h );
                doLayout();
            }
            
            @Override
            public void doLayout() {
                Dimension preferred = buttonPanel.getPreferredSize();
                
                int height = Math.min( preferred.height, getHeight()-1 );
                
                componentPanel.setBounds( 0, 0, getWidth(), getHeight()-height );
                buttonPanel.setBounds( 0, getHeight()-height, getWidth(), height );
            }
            
            @Override
            public Dimension getMinimumSize() {
            	Dimension components = componentPanel.getMinimumSize();
            	Dimension buttons = buttonPanel.getMinimumSize();
            	
            	return new Dimension( components.width, components.height + buttons.height );
            }
        };
        panel.add( buttonPanel );
        panel.add( componentPanel );
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Sends a {@link ChangeEvent} to all {@link #addChangeListener(ChangeListener) registered}
     * {@link ChangeListener ChangeListeners}
     */
    protected void fireStateChange(){
        ChangeEvent event = new ChangeEvent( this );
        for( ChangeListener listener : listeners )
            listener.stateChanged( event );
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex( int index ) {
        if( selectedIndex != index ){
            if( selectedIndex >= 0 && selectedIndex < buttons.size() )
                buttons.get( selectedIndex ).repaint();
            selectedIndex = index;
            
            for( int i = 0, n = components.size(); i<n; i++ )
            	components.get( i ).setVisible( i == index );
            
            if( index >= 0 && index < buttons.size() ){
                buttons.get( index ).repaint();
                componentPanel.validate();
                componentPanel.repaint();
            }
            fireStateChange();
        }
    }

    public Rectangle getBoundsAt( int index ) {
        Rectangle bounds = buttons.get(index).getBounds();
        bounds.x += buttonPanel.getX();
        bounds.y += buttonPanel.getY();
        return bounds;
    }
    
    public void addTab( String title, Icon icon, Component comp ) {
        insertTab( title, icon, comp, getTabCount() );
    }

    public void insertTab( String title, Icon icon, Component comp, int index ) {
        FlatButton button = new FlatButton();
        button.setText( title );
        button.setIcon( icon );
        buttons.add( index, button );
        
        JPanel between = new JPanel( new GridLayout( 1, 1 ));
        between.add( comp );
        components.add( index, between );
        componentPanel.add( between );
        
        buttonPanel.removeAll();
        int count = 0;
        for( FlatButton b : buttons  ){
            buttonPanel.add( b );
            b.setIndex( count++ );
        }
        
        if( selectedIndex >= index )
            selectedIndex++;
        
        setSelectedIndex( index );
        fireStateChange();
    }

    public int getTabCount() {
        return buttons.size();
    }

    public void removeAll() {
        buttons.clear();
        components.clear();
        buttonPanel.removeAll();
        componentPanel.removeAll();
        selectedIndex = -1;
        fireStateChange();
    }

    public void remove( int index ) {
        if( index == selectedIndex ){
            if( index == 0 ){
                if( getTabCount() == 1 )
                    setSelectedIndex( -1 );
                else
                    setSelectedIndex( 1 );
            }
            else
                setSelectedIndex( 0 );
        }
        
        buttonPanel.remove( buttons.remove( index ));
        componentPanel.remove( components.remove( index ) );
        
        int count = 0;
        for( FlatButton b : buttons  ){
            b.setIndex( count++ );
        }
        
        if( selectedIndex >= index )
            selectedIndex--;
        
        fireStateChange();
    }

    public void setTitleAt( int index, String newTitle ) {
        buttons.get(index).setText( newTitle );
    }

    public void setIconAt( int index, Icon newIcon ) {
        buttons.get(index).setIcon( newIcon );
    }

    public Component getComponent() {
        return panel;
    }
    
    /**
     * A small button which can be clicked by the user.
     * @author Benjamin Sigg
     */
    private class FlatButton extends JLabel{
        /** The location of this button */
        private int index;
        
        @Override
        public Dimension getPreferredSize() {
            Dimension preferred = super.getPreferredSize();
            if( preferred.width < 10 || preferred.height < 10 ){
                preferred = new Dimension( preferred );
                preferred.width = Math.max( preferred.width, 10 );
                preferred.height = Math.max( preferred.height, 10 );
            }
            return preferred;
        }
        
        @Override
        public Dimension getMinimumSize() {
            Dimension min = super.getMinimumSize();
            if( min.width < 10 || min.height < 10 ){
                min = new Dimension( min );
                min.width = Math.max( min.width, 10 );
                min.height = Math.max( min.height, 10 );
            }
            return min;
        }
        
        /**
         * Constructs a new button
         */
        public FlatButton(){
            setOpaque( false );
            setFocusable( true );
            
            addMouseListener( new MouseAdapter(){
                @Override
                public void mousePressed( MouseEvent e ){
                    setSelectedIndex( index );
                    repaint();
                }
            });
            
            setBorder( new Border(){
                public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
                    Graphics2D g2 = (Graphics2D)g;
                    Paint oldPaint = g2.getPaint();
                    
                    Color dark = c.getBackground().darker();
                    Color bright = c.getBackground().brighter();
                    
                    if( isSelected() )
                        g2.setPaint( new GradientPaint( x, y, getBackground(), x, y+h/2, bright ));
                    else
                        g2.setPaint( new GradientPaint( x, y, getBackground(), x, y+h/2, dark ));
                    
                    g.drawLine( x, y, x, y+h/2 );
                    g.drawLine( x+w-1, y, x+w-1, y+h/2 );
                    
                    if( isSelected() )
                        g2.setPaint( new GradientPaint( x, y+h, getBackground(), x, y+h/2, bright ));
                    else
                        g2.setPaint( new GradientPaint( x, y+h, getBackground(), x, y+h/2, dark ));

                    g.drawLine( x, y+h, x, y+h/2 );
                    g.drawLine( x+w-1, y+h, x+w-1, y+h/2 );
                    
                    g2.setPaint( oldPaint );
                }
                public Insets getBorderInsets(Component c){
                    return new Insets( 0, 1, 0, 1 );
                }
                public boolean isBorderOpaque(){
                    return false;
                }
            });
        }
        
        /**
         * Sets the location of this button. The buttons knows
         * through the {@link FlatTab#selectedIndex selectedIndex}-property wether
         * it is slected or not.
         * @param index the location
         */
        public void setIndex( int index ) {
            this.index = index;
            repaint();
        }
        
        /**
         * Determins wether this button is selected or not.
         * @return <code>true</code> if the button is selected
         */
        public boolean isSelected() {
            return index == getSelectedIndex();
        }
        
        @Override
        public void paintComponent( Graphics g ){
            Graphics2D g2 = (Graphics2D)g;
            Paint oldPaint = g2.getPaint();
            
            int w = getWidth();
            int h = getHeight();
            
            if( isSelected() ){
                GradientPaint gradient = new GradientPaint( 0, 0, getBackground().brighter(),
                        0, h, getBackground() );
                g2.setPaint( gradient );
            }
            else{
                g.setColor( getBackground() );
            }
            g.fillRect( 0, 0, w, h );
            
            g2.setPaint( oldPaint );
            
            super.paintComponent( g );
        }
    }
}
