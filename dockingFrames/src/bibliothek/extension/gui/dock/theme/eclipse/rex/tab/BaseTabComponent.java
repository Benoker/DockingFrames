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
import java.awt.Point;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
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
    
    private boolean paintIconWhenInactive = false;
    
    private ButtonPanel buttons;
    private Listener dockableListener = new Listener();
    
    private boolean hasFocus;
    private boolean isSelected;
    private EclipseTabPane pane;

    private int tabIndex;
    
    /**
     * Creates a new {@link TabComponent}
     * @param pane the owner of this tab
     * @param dockable the element which is represented by this component
     * @param index the location of this tab relative to other tabs
     */
    public BaseTabComponent( EclipseTabPane pane, Dockable dockable, int index ){
        this.pane = pane;
        this.dockable = dockable;
        this.tabIndex = index;
        
        DockStation station = pane.getStation();
        
        colorStackTabBorder = new BorderTabColor( "stack.tab.border", station, Color.WHITE );
        colorStackTabBorderSelected = new BorderTabColor( "stack.tab.border.selected", station, Color.WHITE );
        colorStackTabBorderSelectedFocused = new BorderTabColor( "stack.tab.border.selected.focused", station, Color.WHITE );
        colorStackTabBorderSelectedFocusLost = new BorderTabColor( "stack.tab.border.selected.focuslost", station, Color.WHITE );
        
        colorStackTabTop = new BaseTabColor( "stack.tab.top", station, Color.LIGHT_GRAY );
        colorStackTabTopSelected = new BaseTabColor( "stack.tab.top.selected", station, Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocused = new BaseTabColor( "stack.tab.top.selected.focused", station, Color.LIGHT_GRAY );
        colorStackTabTopSelectedFocusLost = new BaseTabColor( "stack.tab.top.selected.focuslost", station, Color.LIGHT_GRAY );
        
        colorStackTabBottom = new BaseTabColor( "stack.tab.bottom", station, Color.WHITE );
        colorStackTabBottomSelected = new BaseTabColor( "stack.tab.bottom.selected", station, Color.WHITE );
        colorStackTabBottomSelectedFocused = new BaseTabColor( "stack.tab.bottom.selected.focused", station, Color.WHITE );
        colorStackTabBottomSelectedFocusLost = new BaseTabColor( "stack.tab.bottom.selected.focuslost", station, Color.WHITE );
        
        colorStackTabText = new BaseTabColor( "stack.tab.text", station, Color.BLACK );
        colorStackTabTextSelected = new BaseTabColor( "stack.tab.text.selected", station, Color.BLACK );
        colorStackTabTextSelectedFocused = new BaseTabColor( "stack.tab.text.selected.focused", station, Color.BLACK );
        colorStackTabTextSelectedFocusLost = new BaseTabColor( "stack.tab.text.selected.focuslost", station, Color.BLACK );
        
        colorStackBorder = new BaseTabColor( "stack.border", station, Color.BLACK );
        
        fontFocused = new BaseTabFont( DockFont.ID_TAB_FOCUSED, station );
        fontSelected = new BaseTabFont( DockFont.ID_TAB_SELECTED, station );
        fontUnselected = new BaseTabFont( DockFont.ID_TAB_UNSELECTED, station );
        
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
            		pane.getTheme(), dockable.getGlobalActionOffers(), dockable, true ) );
        dockable.addDockableListener( dockableListener );
        
        DockController controller = pane.getController();
        
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
    
    public boolean isUsedAsTitle() {
        return true;
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
        return pane.getController();
    }
    
    public DockStation getStation() {
        return pane.getStation();
    }
    
    /**
     * Gets the parent of this component.
     * @return the owner
     */
    public EclipseTabPane getPane(){
		return pane;
	}
    
    public ButtonPanel getButtons() {
        return buttons;
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
        public BorderTabColor( String id, DockStation station, Color backup ){
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
        public BaseTabColor( String id, DockStation station, Color backup ){
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
        public BaseTabFont( String id, DockStation station ){
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
