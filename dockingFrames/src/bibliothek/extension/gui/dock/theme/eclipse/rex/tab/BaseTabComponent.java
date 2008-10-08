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

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexTabbedComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.font.TabFont;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.color.ColorCodes;
import bibliothek.gui.dock.util.font.DockFont;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.gui.dock.util.swing.DComponent;

/**
 * A base implementation of {@link TabComponent}.
 * @author Benjamin Sigg
 */
@ColorCodes({"stack.tab.border", "stack.tab.border.selected", "stack.tab.border.selected.focused", "stack.tab.border.selected.focuslost",
    "stack.tab.top", "stack.tab.top.selected", "stack.tab.top.selected.focused","stack.tab.top.selected.focuslost",
    "stack.tab.bottom", "stack.tab.bottom.selected", "stack.tab.bottom.selected.focused", "stack.tab.bottom.selected.focuslost",
    "stack.tab.text", "stack.tab.text.selected", "stack.tab.text.selected.focused", "stack.tab.text.selected.focuslost",
    "stack.border" })
public abstract class BaseTabComponent extends DComponent implements TabComponent{
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
    
    protected final TabFont fontSelected;
    protected final TabFont fontFocused;
    protected final TabFont fontUnselected;
    
    private TabColor[] colors;
    private TabFont[] fonts;
    
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
        
        colorStackTabTop = new BaseTabColor( "stack.tab.top", Color.LIGHT_GRAY );
        colorStackTabTopSelected = new BaseTabColor( "stack.tab.top.selected", Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocused = new BaseTabColor( "stack.tab.top.selected.focused", Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocusLost = new BaseTabColor( "stack.tab.top.selected.focuslost", Color.LIGHT_GRAY );
        
        colorStackTabBottom = new BaseTabColor( "stack.tab.bottom", Color.WHITE );
        colorStackTabBottomSelected = new BaseTabColor( "stack.tab.bottom.selected", Color.WHITE );
        colorStackTabBottomSelectedFocused = new BaseTabColor( "stack.tab.bottom.selected.focused", Color.WHITE );
        colorStackTabBottomSelectedFocusLost = new BaseTabColor( "stack.tab.bottom.selected.focuslost", Color.WHITE );
        
        colorStackTabText = new BaseTabColor( "stack.tab.text", Color.BLACK );
        colorStackTabTextSelected = new BaseTabColor( "stack.tab.text.selected", Color.BLACK );
        colorStackTabTextSelectedFocused = new BaseTabColor( "stack.tab.text.selected.focused", Color.BLACK );
        colorStackTabTextSelectedFocusLost = new BaseTabColor( "stack.tab.text.selected.focuslost", Color.BLACK );
        
        colorStackBorder = new BaseTabColor( "stack.border", Color.BLACK );
        
        fontFocused = new BaseTabFont( DockFont.ID_TAB_FOCUSED );
        fontSelected = new BaseTabFont( DockFont.ID_TAB_SELECTED );
        fontUnselected = new BaseTabFont( DockFont.ID_TAB_UNSELECTED );
        
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
        
        fonts = new TabFont[]{
                fontFocused,
                fontSelected,
                fontUnselected
        };
        
        addHierarchyListener( new WindowActiveObserver() );
        setFocusable( false );
        setFocusTraversalPolicyProvider( true );
        setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy() );
        buttons = new ButtonPanel( false );
    }
    
    /**
     * Called when one of the border colors changed
     */
    protected abstract void updateBorder();
    
    /**
     * Called when the font of this component has to be updated
     */
    protected void updateFont(){
        TabFont font = null;
        if( isFocused() ){
            font = fontFocused;
        }
        else if( isSelected() ){
            font = fontSelected;
        }
        else{
            font = fontUnselected;
        }
        
        setFontModifier( font.font() );
    }

    public void bind() {
        if( buttons != null )
            buttons.set( dockable, new EclipseDockActionSource(
                    tabbedComponent.getTheme(), dockable.getGlobalActionOffers(), dockable, true ) );
        dockable.addDockableListener( dockableListener );
        
        for( TabColor color : colors )
            color.connect( controller );
        for( TabFont font : fonts )
            font.connect( controller );
        
        setToolTipText( dockable.getTitleToolTip() );
        revalidate();
    }
    
    public void unbind() {
        if( buttons != null )
            buttons.set( null );
        dockable.removeDockableListener( dockableListener );
        
        for( TabColor color : colors )
            color.connect( null );
        for( TabFont font : fonts )
            font.connect( null );
        
        setToolTipText( null );
    }

    public Dockable getDockable() {
        return dockable;
    }
    
    public DockElement getElement() {
        return dockable;
    }
    
    public void addMouseInputListener( MouseInputListener listener ) {
        addMouseListener( listener );
        addMouseMotionListener( listener );
    }
    
    public void removeMouseInputListener( MouseInputListener listener ) {
        removeMouseListener( listener );
        removeMouseMotionListener( listener );
    }
    
    public Point getPopupLocation( Point click, boolean popupTrigger ) {
        if( popupTrigger )
            return click;
        
        return null;
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
        updateFont();
        repaint();
    }
    
    public boolean isFocused(){
        return hasFocus;
    }
    
    public void setSelected( boolean selected ){
        isSelected = selected;
        updateBorder();
        updateFont();
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
            super( id, station, dockable, backup );
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
    private class BaseTabColor extends TabColor{
        public BaseTabColor( String id, Color backup ){
            super( id, station, dockable, backup );
        }
        @Override
        protected void changed( Color oldColor, Color newColor ) {
            repaint();
        }
    }

    /**
     * A font used on this tab
     * @author Benjamin Sigg
     */
    private class BaseTabFont extends TabFont{
        public BaseTabFont( String id ){
            super( id, station, dockable );
        }
        @Override
        protected void changed( FontModifier oldValue, FontModifier newValue ) {
            updateFont();
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
        
        public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip ) {
            setToolTipText( newToolTip );
        }

        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // ignore
        }
        
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            // ignore
        }
    }
}
