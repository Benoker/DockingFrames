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
package bibliothek.extension.gui.dock.theme;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.eclipse.DefaultEclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseColorScheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDisplayerFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockTitleFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDockableSelection;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStackDockComponent;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStationPaint;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.RoundRectButton;
import bibliothek.extension.gui.dock.theme.eclipse.RoundRectDropDownButton;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.BasicTabDockTitle;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.DockTitleTab;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.RectGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.extension.gui.dock.theme.flat.FlatButtonTitle;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicSelectableHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.nostack.NoStackAcceptance;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;

/**
 * A theme imitating the look and feel of the Eclipse-IDE.
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
@ThemeProperties(
        authors = {"Janni Kovacs", "Benjamin Sigg"},
        descriptionBundle = "theme.eclipse.description",
        nameBundle = "theme.eclipse",
        webpages = {""})
        public class EclipseTheme extends BasicTheme {
    /** Tells whether icons on tabs that are not selected should be painted or not. */
    public static final PropertyKey<Boolean> PAINT_ICONS_WHEN_DESELECTED = 
        new PropertyKey<Boolean>( "EclipseTheme paint icons when deselected", false );

    /**
     *  Tells in which way the tabs should be painted.
     *  @see ShapedGradientPainter
     *  @see RectGradientPainter 
     */
    public static final PropertyKey<TabPainter> TAB_PAINTER =
        new PropertyKey<TabPainter>( "EclipseTheme tab painter", ShapedGradientPainter.FACTORY );

    /**
     * Provides additional dockable-wise information used to layout components
     * in the EclipseTheme. Note that changing this property will show full effect
     * only after re-installing the EclipseTheme.
     * @see DefaultEclipseThemeConnector
     */
    public static final PropertyKey<EclipseThemeConnector> THEME_CONNECTOR =
        new PropertyKey<EclipseThemeConnector>( 
                "EclipseTheme theme connector",
                new DefaultEclipseThemeConnector() );

    /** Access to the {@link ColorScheme} used for this theme */
    public static final PropertyKey<ColorScheme> ECLIPSE_COLOR_SCHEME =
        new PropertyKey<ColorScheme>( "dock.ui.EclipseTheme.ColorScheme", new EclipseColorScheme() );

    /**
     * The id of the {@link DockTitleVersion} that is intended to create
     * {@link DockTitle}s used as tabs by the {@link DockTitleTab}. Clients
     * which want to use {@link DockTitle}s as tabs, should exchange the
     * {@link TabPainter} by executing this code:<br>
     * <code>controller.getProperties().set( EclipseTheme.TAB_PAINTER, DockTitleTab.FACTORY );</code>
     */
    public static final String TAB_DOCK_TITLE = "eclipse.tab";

    /** An acceptance that permits combinations of dockables and stations that do not look good */
    private DockAcceptance acceptance = new NoStackAcceptance();

    /**
     * Creates a new theme
     */
    public EclipseTheme() {
        setColorSchemeKey( ECLIPSE_COLOR_SCHEME );
        setStackDockComponentFactory( new StackDockComponentFactory(){
            public StackDockComponent create( StackDockStation station ){
                return new EclipseStackDockComponent( EclipseTheme.this, station );
            }
        });
        setDisplayerFactory( new EclipseDisplayerFactory( this ) );
        setPaint( new EclipseStationPaint() );
        setMovingImageFactory( new DockableMovingImageFactory(){
            public MovingImage create( DockController controller, Dockable dockable ) {
                return null;
            }

            public MovingImage create( DockController controller, DockTitle snatched ) {
                return null;
            }
        });
        setTitleFactory( new BasicDockTitleFactory(){
            @Override
            public <D extends Dockable & DockStation> DockTitle createStationTitle(
                    D dockable, DockTitleVersion version ) {

                return createDockableTitle( dockable, version );
            }
        });
        setDockableSelection( new EclipseDockableSelection() );
    }

    @Override
    public void install( DockController controller ){
        DockTitleManager titleManager = controller.getDockTitleManager();
        titleManager.registerTheme( EclipseTheme.TAB_DOCK_TITLE, BasicTabDockTitle.createFactory( this ) );

        super.install( controller );

        Map<String, Icon> icons = DockUtilities.loadIcons( "data/eclipse/icons.ini", null, EclipseTheme.class.getClassLoader() );
        for( Map.Entry<String, Icon> entry : icons.entrySet() )
            controller.getIcons().setIconTheme( entry.getKey(), entry.getValue() );

        EclipseDockTitleFactory factory = new EclipseDockTitleFactory( this, new ControllerTitleFactory() );

        titleManager.registerTheme( SplitDockStation.TITLE_ID, factory );
        titleManager.registerTheme( FlapDockStation.WINDOW_TITLE_ID, factory );
        titleManager.registerTheme( ScreenDockStation.TITLE_ID, factory );
        titleManager.registerTheme( StackDockStation.TITLE_ID, factory );

        controller.addAcceptance( acceptance );

        titleManager.registerTheme( FlapDockStation.BUTTON_TITLE_ID, new DockTitleFactory(){
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
                RoundRectButton button = new RoundRectButton( handler );
                handler.setModel( button.getModel() );
                return handler;
            }
        });

        controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, 
                new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
            public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
                BasicSelectableHandler.Check handler = new BasicSelectableHandler.Check( action, dockable );
                RoundRectButton button = new RoundRectButton( handler );
                handler.setModel( button.getModel() );
                return handler;
            }
        });

        controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, 
                new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
            public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, MenuDockAction action, Dockable dockable ){
                BasicMenuHandler handler = new BasicMenuHandler( action, dockable );
                RoundRectButton button = new RoundRectButton( handler );
                handler.setModel( button.getModel() );
                return handler;
            }
        });

        controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, 
                new ViewGenerator<SelectableDockAction, BasicTitleViewItem<JComponent>>(){
            public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, SelectableDockAction action, Dockable dockable ){
                BasicSelectableHandler.Radio handler = new BasicSelectableHandler.Radio( action, dockable );
                RoundRectButton button = new RoundRectButton( handler );
                handler.setModel( button.getModel() );
                return handler;
            }
        });

        controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE,
                new ViewGenerator<DropDownAction, BasicTitleViewItem<JComponent>>(){
            public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, DropDownAction action, Dockable dockable ){
                BasicDropDownButtonHandler handler = new BasicDropDownButtonHandler( action, dockable );
                RoundRectDropDownButton button = new RoundRectDropDownButton( handler );
                handler.setModel( button.getModel() );
                return handler;
            }
        });
    }

    @Override
    protected void updateColors() {
        DockController controller = getController();
        if( controller != null ){
            controller.getColors().lockUpdate();

            super.updateColors();

            updateColor( "stack.tab.border", null );
            updateColor( "stack.tab.border.selected", null );
            updateColor( "stack.tab.border.selected.focused", null );
            updateColor( "stack.tab.border.selected.focuslost", null );

            updateColor( "stack.tab.top", null );
            updateColor( "stack.tab.top.selected", null );
            updateColor( "stack.tab.top.selected.focused", null );
            updateColor( "stack.tab.top.selected.focuslost", null );

            updateColor( "stack.tab.bottom", null );
            updateColor( "stack.tab.bottom.selected", null );
            updateColor( "stack.tab.bottom.selected.focused", null );
            updateColor( "stack.tab.bottom.selected.focuslost", null );

            updateColor( "stack.tab.text", null );
            updateColor( "stack.tab.text.selected", null );
            updateColor( "stack.tab.text.selected.focused", null );
            updateColor( "stack.tab.text.selected.focuslost", null );

            updateColor( "stack.border", null );

            updateColor( "selection.border", null );

            controller.getColors().unlockUpdate();
        }
        else{
            super.updateColors();
        }
    }

    @Override
    public void uninstall( DockController controller ){
        super.uninstall( controller );
        controller.getIcons().clearThemeIcons();
        controller.getDockTitleManager().clearThemeFactories();
        controller.removeAcceptance( acceptance );
        controller.getActionViewConverter().putTheme( ActionType.BUTTON, ViewTarget.TITLE, null );
        controller.getActionViewConverter().putTheme( ActionType.CHECK, ViewTarget.TITLE, null );
        controller.getActionViewConverter().putTheme( ActionType.MENU, ViewTarget.TITLE, null );
        controller.getActionViewConverter().putTheme( ActionType.RADIO, ViewTarget.TITLE, null );
        controller.getActionViewConverter().putTheme( ActionType.DROP_DOWN, ViewTarget.TITLE, null );
    }

    /**
     * Gets the connector which is used for decisions which are normally
     * altered by the client.
     * @param controller the controller in whose realm the decisions will take
     * effect.
     * @return the connector, either the connector that is installed in
     * the {@link DockProperties} under {@link #THEME_CONNECTOR} or
     * a default-value.
     */
    public EclipseThemeConnector getThemeConnector( DockController controller ) {
        EclipseThemeConnector connector = null;
        if( controller != null )
            connector = controller.getProperties().get( THEME_CONNECTOR );

        if( connector == null )
            connector = THEME_CONNECTOR.getDefault();

        return connector;
    }
}

