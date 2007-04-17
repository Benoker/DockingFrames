/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.themes;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewGenerator;
import bibliothek.gui.dock.action.views.ViewTarget;
import bibliothek.gui.dock.action.views.buttons.ButtonMiniButtonHandler;
import bibliothek.gui.dock.action.views.buttons.DropDownMiniButton;
import bibliothek.gui.dock.action.views.buttons.DropDownMiniButtonHandler;
import bibliothek.gui.dock.action.views.buttons.MenuMiniButtonHandler;
import bibliothek.gui.dock.action.views.buttons.MiniButton;
import bibliothek.gui.dock.action.views.buttons.SelectableMiniButtonHandler;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.FlapDockStation;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.themes.flat.FlatButtonTitle;
import bibliothek.gui.dock.themes.flat.FlatCombiner;
import bibliothek.gui.dock.themes.flat.FlatDisplayerFactory;
import bibliothek.gui.dock.themes.flat.FlatStationPaint;
import bibliothek.gui.dock.themes.flat.FlatTab;
import bibliothek.gui.dock.themes.flat.FlatTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * A {@link DockTheme theme} that uses very few borders.
 * @author Benjamin Sigg
 */
public class FlatTheme extends DefaultTheme{
    
    /** A special factory for the {@link SplitDockStation} */
    protected DisplayerFactory splitDisplayFactory = new FlatDisplayerFactory( true );
    
    /** A listener to a controller */
    private Listener listener = new Listener();
    
    /**
     * Creates a new theme
     */
    public FlatTheme() {
        setPaint( new FlatStationPaint() );
        setCombiner( new FlatCombiner());
        setTitleFactory( new FlatTitleFactory() );
        setDisplayerFactory( new FlatDisplayerFactory( false ));
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
        
    	controller.addDockControllerListener( listener );
    	
        for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( !(stack.getStackComponent() instanceof FlatTab) )
        			stack.setStackComponent( new FlatTab() );
        	}
        }
        
        controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, 
        		new ViewGenerator<ButtonDockAction, TitleViewItem<JComponent>>(){
        	public TitleViewItem<JComponent> create( ActionViewConverter converter, ButtonDockAction action, Dockable dockable ){
        		return new ButtonMiniButtonHandler( action, dockable, createTitleMiniButton() );
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, 
        		new ViewGenerator<SelectableDockAction, TitleViewItem<JComponent>>(){
        	public TitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
        		return new SelectableMiniButtonHandler.Check( action, dockable, createTitleMiniButton() );
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, 
        		new ViewGenerator<MenuDockAction, TitleViewItem<JComponent>>(){
        	public TitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
        		return new MenuMiniButtonHandler( action, dockable, createTitleMiniButton() );
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, 
        		new ViewGenerator<SelectableDockAction, TitleViewItem<JComponent>>(){
        	public TitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
        		return new SelectableMiniButtonHandler.Radio( action, dockable, createTitleMiniButton() );
        	}
        });
        
        controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE,
        		new ViewGenerator<DropDownAction, TitleViewItem<JComponent>>(){
        	public TitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
        		DropDownMiniButton button = new DropDownMiniButton();
        		button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
        		return new DropDownMiniButtonHandler<DropDownAction, DropDownMiniButton>( action, button, dockable );
        	}
        });
    }
    
    /**
     * Creates a {@link MiniButton} in a flat look.
     * @return the new button
     */
    private MiniButton createTitleMiniButton(){
    	MiniButton button = new MiniButton();
    	button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
    	return button;
    }

    @Override
    public void uninstall(DockController controller) {
    	super.uninstall(controller);
    	controller.removeDockControllerListener(listener);
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
    
    /**
     * A listener to the Controller
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
		@Override
		public void dockableRegistered(DockController controller, Dockable dockable) {
			if( dockable instanceof StackDockStation ){
				StackDockStation stack = (StackDockStation)dockable;
				if( !(stack.getStackComponent() instanceof FlatTab) ){
					stack.setStackComponent( new FlatTab() );
				}
			}
		}
    }
}
