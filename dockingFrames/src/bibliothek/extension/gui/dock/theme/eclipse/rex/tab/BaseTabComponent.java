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
package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.Window;
import java.awt.event.*;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.color.ColorCodes;

/**
 * A base implementation ob {@link TabComponent}.
 * @author Benjamin Sigg
 */
@ColorCodes({"stack.tab.border", "stack.tab.border.selected", "stack.tab.border.selected.focused", "stack.tab.border.selected.focuslost",
    "stack.tab.top", "stack.tab.top.selected", "stack.tab.top.selected.focused","stack.tab.top.selected.focuslost",
    "stack.tab.bottom", "stack.tab.bottom.selected", "stack.tab.bottom.selected.focused", "stack.tab.bottom.selected.focuslost",
    "stack.tab.text", "stack.tab.text.selected", "stack.tab.text.selected.focused", "stack.tab.text.selected.focuslost",
    "stack.border" })
public abstract class BaseTabComponent extends JComponent implements TabComponent{
    protected final TabColor colorStackTabBorder;
    protected final TabColor colorStackTabBorderSelected;
    protected final TabColor colorStackTabBorderSelectedFocused;
    protected final TabColor colorStackTabBorderSelectedFocusLost;
    
    protected final TabColor colorStackTabTop;
    protected final TabColor colorStackTabTopSelected;
    protected final TabColor colorStackTabTopSelectedFocused;
    protected final TabColor colorStackTabTopSelectedFocusLost;
    
    protected final TabColor colorStackTabBottom;
    protected final TabColor colorStackTabBottomSelected;
    protected final TabColor colorStackTabBottomSelectedFocused;
    protected final TabColor colorStackTabBottomSelectedFocusLost;
    
    protected final TabColor colorStackTabText;
    protected final TabColor colorStackTabTextSelected;
    protected final TabColor colorStackTabTextSelectedFocused;
    protected final TabColor colorStackTabTextSelectedFocusLost;
    
    protected final TabColor colorStackBorder;
    
    private TabColor[] colors;
    
    private Dockable dockable;
    private StackDockStation station;
    private DockController controller;
    
    private boolean paintIconWhenInactive = false;
    
    private ButtonPanel buttons;
    private Listener dockableListener = new Listener();
    
    private boolean hasFocus;
    private boolean isSelected;
    private RexTabbedComponent tabbedComponent;

    private int tabIndex;
    
    /**
     * Creates a new {@link TabComponent}
     * @param component the owner of this tab
     * @param controller the controller from which this component should read colors
     * @param station the station on which this component is
     * @param dockable the element which is represented by this component
     * @param index the location of this tab relative to other tabs
     */
    public BaseTabComponent( RexTabbedComponent component, DockController controller, StackDockStation station, Dockable dockable, int index ){
        this.tabbedComponent = component;
        this.dockable = dockable;
        this.station = station;
        this.controller = controller;
        this.tabIndex = index;
        
        colorStackTabBorder = new BorderTabColor( "stack.tab.border", Color.WHITE );
        colorStackTabBorderSelected = new BorderTabColor( "stack.tab.border.selected", Color.WHITE );
        colorStackTabBorderSelectedFocused = new BorderTabColor( "stack.tab.border.selected.focused", Color.WHITE );
        colorStackTabBorderSelectedFocusLost = new BorderTabColor( "stack.tab.border.selected.focuslost", Color.WHITE );
        
        colorStackTabTop = new ShapedTabColor( "stack.tab.top", Color.LIGHT_GRAY );
        colorStackTabTopSelected = new ShapedTabColor( "stack.tab.top.selected", Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocused = new ShapedTabColor( "stack.tab.top.selected.focused", Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocusLost = new ShapedTabColor( "stack.tab.top.selected.focuslost", Color.LIGHT_GRAY );
        
        colorStackTabBottom = new ShapedTabColor( "stack.tab.bottom", Color.WHITE );
        colorStackTabBottomSelected = new ShapedTabColor( "stack.tab.bottom.selected", Color.WHITE );
        colorStackTabBottomSelectedFocused = new ShapedTabColor( "stack.tab.bottom.selected.focused", Color.WHITE );
        colorStackTabBottomSelectedFocusLost = new ShapedTabColor( "stack.tab.bottom.selected.focuslost", Color.WHITE );
        
        colorStackTabText = new ShapedTabColor( "stack.tab.text", Color.BLACK );
        colorStackTabTextSelected = new ShapedTabColor( "stack.tab.text.selected", Color.BLACK );
        colorStackTabTextSelectedFocused = new ShapedTabColor( "stack.tab.text.selected.focused", Color.BLACK );
        colorStackTabTextSelectedFocusLost = new ShapedTabColor( "stack.tab.text.selected.focuslost", Color.BLACK );
        
        colorStackBorder = new ShapedTabColor( "stack.border", Color.BLACK );
        
        colors = new TabColor[]{
                colorStackTabBorder,
                colorStackTabBorderSelected,
                colorStackTabBorderSelectedFocused,
                colorStackTabBorderSelectedFocusLost,
                colorStackTabTop,
                colorStackTabTopSelected,
                colorStackTabTopSelectedFocused,
                colorStackTabTopSelectedFocusLost,
                colorStackTabBottom,
                colorStackTabBottomSelected,
                colorStackTabBottomSelectedFocused,
                colorStackTabBottomSelectedFocusLost,
                colorStackTabText,
                colorStackTabTextSelected,
                colorStackTabTextSelectedFocused,
                colorStackTabTextSelectedFocusLost,
                colorStackBorder
        };
        
        addHierarchyListener( new WindowActiveObserver() );
        addMouseListener( new MouseAdapter(){
            @Override
            public void mouseClicked( MouseEvent e ) {
                if( e.getClickCount() == 2 ){
                    if( BaseTabComponent.this.controller != null ){
                        BaseTabComponent.this.controller.getDoubleClickController().send( 
                                BaseTabComponent.this.dockable, e );
                    }
                }
            }
        });
        
        setFocusable( false );
        setFocusTraversalPolicyProvider( true );
        setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy() );
        buttons = new ButtonPanel( false );
    }
    
    /**
     * Called when one of the border colors changed
     */
    protected abstract void updateBorder();
    
    public void bind() {
        if( buttons != null )
            buttons.set( dockable, new EclipseDockActionSource(
                    tabbedComponent.getTheme(), dockable.getGlobalActionOffers(), dockable, true ) );
        dockable.addDockableListener( dockableListener );
        
        for( TabColor color : colors )
            color.connect( controller );
        
        revalidate();
    }
    
    public void unbind() {
        if( buttons != null )
            buttons.set( null );
        dockable.removeDockableListener( dockableListener );
        
        for( TabColor color : colors )
            color.connect( null );
    }

    public Dockable getDockable() {
        return dockable;
    }
    
    public DockController getController() {
        return controller;
    }
    
    public StackDockStation getStation() {
        return station;
    }
    
    public ButtonPanel getButtons() {
        return buttons;
    }
    
    public RexTabbedComponent getTabbedComponent() {
        return tabbedComponent;
    }

    public Component getComponent(){
        return this;
    }
    
    public void setFocused( boolean focused ){
        hasFocus = focused;
        updateBorder();
        repaint();
    }
    
    public boolean isFocused(){
        return hasFocus;
    }
    
    public void setSelected( boolean selected ){
        isSelected = selected;
        updateBorder();
        revalidate();
    }
    
    public boolean isSelected(){
        return isSelected;
    }
    
    public void setIndex( int index ){
        tabIndex = index;
        repaint();
    }
    
    public int getIndex(){
        return tabIndex;
    }
    
    public boolean doPaintIconWhenInactive() {
        return paintIconWhenInactive;
    }

    public void setPaintIconWhenInactive(boolean paintIconWhenInactive) {
        this.paintIconWhenInactive = paintIconWhenInactive;
        revalidate();
        repaint();
    }
    
    /**
     * A color used in the border
     * @author Benjamin Sigg
     */
    private class BorderTabColor extends TabColor{
        public BorderTabColor( String id, Color backup ){
            super( id, TabColor.class, station, dockable, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            updateBorder();
        }
    }

    /**
     * A color used on this tab
     * @author Benjamin Sigg
     */
    private class ShapedTabColor extends TabColor{
        public ShapedTabColor( String id, Color backup ){
            super( id, TabColor.class, station, dockable, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            repaint();
        }
    }
    

    /**
     * Listens to the window ancestor of this {@link TabComponent} and updates
     * color when the focus is lost.
     * @author Benjamin Sigg
     */
    private class WindowActiveObserver extends WindowAdapter implements HierarchyListener{
        private Window window;
        
        public void hierarchyChanged( HierarchyEvent e ){
            if( window != null ){
                window.removeWindowListener( this );
                window = null;
            }
            
            window = SwingUtilities.getWindowAncestor( BaseTabComponent.this );
            
            if( window != null ){
                window.addWindowListener( this );
                updateBorder();
                repaint();
            }
        }
        
        @Override
        public void windowActivated( WindowEvent e ){
            updateBorder();
            repaint();
        }
        
        @Override
        public void windowDeactivated( WindowEvent e ){
            updateBorder();
            repaint();
        }
    }
    
    /**
     * A listener to the {@link Dockable} of this {@link TabComponent}, is informed
     * when the title or icon changes.
     * @author Benjamin Sigg
     */
    private class Listener implements DockableListener {
        public void titleBound( Dockable dockable, DockTitle title ) {
            // ignore
        }

        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            repaint();
            revalidate();
        }

        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            repaint();
            revalidate();
        }

        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // ignore
        }
        
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            // ignore
        }
    }
}
