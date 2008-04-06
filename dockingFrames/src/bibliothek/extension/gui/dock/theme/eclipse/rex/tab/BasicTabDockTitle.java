package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockActionSource;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTitle} normally used by the {@link DockTitleTab} to show
 * a very simple tab.
 * @author Benjamin Sigg
 *
 */
public class BasicTabDockTitle extends BasicDockTitle {
    /**
     * Gets a new {@link DockTitleFactory} using <code>theme</code> as
     * source of various properties.
     * @param theme the settings
     * @return the new factory
     */
    public static DockTitleFactory createFactory( final EclipseTheme theme ){
        return new DockTitleFactory(){
            public DockTitle createDockableTitle( Dockable dockable,
                    DockTitleVersion version ) {
                
                return new BasicTabDockTitle( theme, dockable, version );
            }
            
            public <D extends Dockable & DockStation> DockTitle createStationTitle(
                    D dockable, DockTitleVersion version ) {
            
                return new BasicTabDockTitle( theme, dockable, version );
            }
        };
    }
    
    /** the theme used to get theme-properties */
    private EclipseTheme theme;
    
    /** whether this tab is currently selected */
    private boolean selected = false;
    
    /** whether to paint the icon when this tab is not selected */
    private boolean paintIconWhenInactive = true;
    
    /**
     * Creates a new title
     * @param theme the properties needed to paint this title correctly
     * @param dockable the element for which this title is shown
     * @param origin the type of this title
     */
    public BasicTabDockTitle( EclipseTheme theme, Dockable dockable, DockTitleVersion origin ) {
        super( dockable, origin );
        this.theme = theme;
    }
    
    @Override
    protected DockActionSource getActionSourceFor( Dockable dockable ) {
        return new EclipseDockActionSource( theme, super.getActionSourceFor( dockable ), dockable, true );
    }
    
    @Override
    public void changed( DockTitleEvent event ) {
        super.changed( event );
        if( event instanceof EclipseDockTitleEvent ){
            EclipseDockTitleEvent e = (EclipseDockTitleEvent)event;
            selected = e.isSelected();
            paintIconWhenInactive = e.isPaintIconWhenInactive();
            updateTabIcon();
        }
    }
    
    @Override
    protected void setIcon( Icon icon ) {
        if( selected || paintIconWhenInactive )
            super.setIcon( icon );
        else
            super.setIcon( null );
    }
    
    /**
     * Ensures that the icon of the {@link #getDockable() Dockable} is
     * shown but only if this title is {@link #selected} or
     * {@link #paintIconWhenInactive} is <code>true</code>.
     */
    private void updateTabIcon(){
        if( selected || paintIconWhenInactive )
            setIcon( getDockable().getTitleIcon() );
        else
            setIcon( null );
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        
        setActiveLeftColor( RexSystemColor.getActiveColor() );
        setActiveRightColor( RexSystemColor.getActiveColorGradient() );
        setActiveTextColor( RexSystemColor.getActiveTextColor() );

        setInactiveLeftColor( RexSystemColor.getInactiveColor() );
        setInactiveRightColor( RexSystemColor.getInactiveColorGradient() );
        setInactiveTextColor( RexSystemColor.getInactiveTextColor() );
    }
}
