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

package bibliothek.extension.gui.dock.theme;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.flat.*;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.action.*;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.DropDownMiniButton;
import bibliothek.gui.dock.themes.basic.action.buttons.MiniButton;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTheme theme} that uses very few borders.
 * @author Benjamin Sigg
 */
@ThemeProperties(
        nameBundle="theme.flat", 
        descriptionBundle="theme.flat.description",
        authors={"Benjamin Sigg"},
        webpages={})
public class FlatTheme extends BasicTheme{
    
    /** A special factory for the {@link SplitDockStation} */
    protected DisplayerFactory splitDisplayFactory = new FlatDisplayerFactory( true );
    
    /**
     * Creates a new theme
     */
    public FlatTheme() {
        setPaint( new FlatStationPaint() );
        // setCombiner( new FlatCombiner());
        setTitleFactory( new FlatTitleFactory() );
        setDisplayerFactory( new FlatDisplayerFactory( false ));
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockStation station ) {
                return new FlatTab();
            }
        });
    }
    
    @Override
    public void install( DockController controller ) {
    	super.install(controller);
        controller.getDockTitleManager().registerTheme( FlapDockStation.BUTTON_TITLE_ID, new DockTitleFactory(){
            public DockTitle createDockableTitle( Dockable dockable, DockTitleVersion version ) {
                return new FlatButtonTitle( dockable, version );
            }

            public <D extends Dockable & DockStation> DockTitle createStationTitle( D dockable, DockTitleVersion version ) {
                return new FlatButtonTitle( dockable, version );
            }
        });
        
        controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, 
        		new ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>(){
        	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
                BasicButtonHandler handler = new BasicButtonHandler( action, dockable );
                MiniButton<BasicButtonModel> button = createTitleMiniButton( handler );
                handler.setModel( button.getModel() );
                return handler;
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, 
        		new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
        	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
                BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
                MiniButton<BasicButtonModel> button = createTitleMiniButton( handler );
                handler.setModel( button.getModel() );
                return handler;
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, 
        		new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
        	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
                BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
                MiniButton<BasicButtonModel> button = createTitleMiniButton( handler );
                handler.setModel( button.getModel() );
                return handler;
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, 
        		new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
        	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
                BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
                MiniButton<BasicButtonModel> button = createTitleMiniButton( handler );
                handler.setModel( button.getModel() );
                return handler;
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE,
        		new ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>(){
        	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
                BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
        		DropDownMiniButton button = new DropDownMiniButton( handler );
                handler.setModel( button.getModel() );
        		button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
        		return handler;
        	}
        });
    }
    
    /**
     * Creates a {@link MiniButton} in a flat look.
     * @param trigger the trigger to invoke when the button has been clicked
     * @return the new button
     */
    protected MiniButton<BasicButtonModel> createTitleMiniButton( BasicTrigger trigger ){
    	BasicMiniButton button = new BasicMiniButton( trigger );
    	button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
    	return button;
    }

    @Override
    public void uninstall(DockController controller) {
    	super.uninstall(controller);
    	controller.getDockTitleManager().clearThemeFactories();
    	
    	controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, null );
    	controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, null );
    	controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, null );
    	controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, null );
    	controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );
    	
        for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( stack.getStackComponent() instanceof FlatTab )
        			stack.setStackComponent( new DefaultStackDockComponent() );
        	}
        }
    }
    
    /**
     * Sets the {@link DisplayerFactory} that is used for the {@link SplitDockStation}.
     * Normally all displayers do not have any border, but the displayers on
     * a SplitDockStation may need a small border.
     * @param splitDisplayFactory the factory
     */
    public void setSplitDisplayFactory( DisplayerFactory splitDisplayFactory ) {
        this.splitDisplayFactory = splitDisplayFactory;
    }
    
    /**
     * Gets the special factory for the {@link SplitDockStation}.
     * @return the factory
     * @see #setSplitDisplayFactory(DisplayerFactory)
     */
    public DisplayerFactory getSplitDisplayFactory() {
        return splitDisplayFactory;
    }
    
    @Override
    public DisplayerFactory getDisplayFactory( DockStation station ) {
        if( station instanceof SplitDockStation )
            return splitDisplayFactory;
        
        return super.getDisplayFactory( station );
    }
}
