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

package bibliothek.gui.dock.station.stack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * A {@link StackDockComponent} which is a combination of other components.
 * @author Benjamin Sigg
 *
 * @param <C> the type of the tabs
 */
public abstract class CombinedStackDockComponent<C extends CombinedTab> implements StackDockComponent{
    /** The panel which shows the children */
    private JPanel panel;
    
    /** A list of all buttons of this FlatTab */
    private List<C> buttons = new ArrayList<C>();
    
    /** The controller for which this component renders its content */
    private DockController controller;
    
    /** A panel which displays the buttons of this FlatTab */
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
    public CombinedStackDockComponent(){
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
    
    public void setController( DockController controller ){
    	this.controller = controller;
    }
    
    /**
     * Gets the controller for which this component renders its content.
     * @return the indirect owner of this component
     */
    public DockController getController(){
		return controller;
	}
    
    /**
     * Gets the index'th tab of this component.
     * @param index the index of the tab
     * @return the tab
     */
    public C getTab( int index ){
    	return buttons.get( index );
    }
    
    /**
     * Creates a new tab which will be shown on this component.
     * @param dockable the Dockable for which the tab will be used
     * @return the new tab
     */
    protected abstract C createTab( Dockable dockable );
    
    /**
     * Deletes a tab that was earlier created by {@link #createTab(Dockable)}.
     * @param tab the tab which is no longer needed
     */
    protected abstract void destroy( C tab );
    
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
                buttons.get( selectedIndex ).getComponent().repaint();
            selectedIndex = index;
            
            for( int i = 0, n = components.size(); i<n; i++ )
            	components.get( i ).setVisible( i == index );
            
            if( index >= 0 && index < buttons.size() ){
                buttons.get( index ).getComponent().repaint();
                componentPanel.revalidate();
                componentPanel.repaint();
            }
            fireStateChange();
        }
    }

    public Rectangle getBoundsAt( int index ) {
        Rectangle bounds = buttons.get(index).getComponent().getBounds();
        bounds.x += buttonPanel.getX();
        bounds.y += buttonPanel.getY();
        return bounds;
    }
    
    public void addTab( String title, Icon icon, Component comp, Dockable dockable ) {
        insertTab( title, icon, comp, dockable, getTabCount() );
    }

    public void insertTab( String title, Icon icon, Component comp, Dockable dockable, int index ) {
        C button = createTab( dockable );
        button.setText( title );
        button.setIcon( icon );
        buttons.add( index, button );
        
        JPanel between = new JPanel( new GridLayout( 1, 1 ));
        between.add( comp );
        components.add( index, between );
        componentPanel.add( between );
        
        buttonPanel.removeAll();
        int count = 0;
        for( CombinedTab b : buttons  ){
            buttonPanel.add( b.getComponent() );
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
    	components.clear();
        buttonPanel.removeAll();
        componentPanel.removeAll();
        selectedIndex = -1;
        
        for( C c : buttons )
    		destroy( c );
    	
        buttons.clear();
        
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
        
        C button = buttons.remove( index );
        buttonPanel.remove( button.getComponent() );
        destroy( button );
        componentPanel.remove( components.remove( index ) );
        
        int count = 0;
        for( CombinedTab b : buttons  ){
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

    public JComponent getComponent() {
        return panel;
    }

}
