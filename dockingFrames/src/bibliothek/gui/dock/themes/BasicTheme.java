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

package bibliothek.gui.dock.themes;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.dockable.DockableMovingImageFactory;
import bibliothek.gui.dock.event.UIListener;
import bibliothek.gui.dock.focus.DockableSelection;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.basic.BasicColorScheme;
import bibliothek.gui.dock.themes.basic.BasicCombiner;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicDockTitleFactory;
import bibliothek.gui.dock.themes.basic.BasicDockableSelection;
import bibliothek.gui.dock.themes.basic.BasicMovingImageFactory;
import bibliothek.gui.dock.themes.basic.BasicStackDockComponent;
import bibliothek.gui.dock.themes.basic.BasicStationPaint;
import bibliothek.gui.dock.themes.color.ActionColor;
import bibliothek.gui.dock.themes.color.DisplayerColor;
import bibliothek.gui.dock.themes.color.DockableSelectionColor;
import bibliothek.gui.dock.themes.color.StationPaintColor;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.gui.dock.util.laf.LookAndFeelColorsListener;

/**
 * A {@link DockTheme theme} that does not install anything and uses the
 * default-implementations off all factories. It is possible to replace 
 * any of the factories.
 * @author Benjamin Sigg
 */
@ThemeProperties(
		nameBundle="theme.basic", 
		descriptionBundle="theme.basic.description",
		authors={"Benjamin Sigg"},
		webpages={})
		public class BasicTheme implements DockTheme{
	/** combines several Dockables */
	private Combiner combiner;

	/** paints on stations */
	private StationPaint paint;

	/** creates panels for Dockables */
	private DisplayerFactory displayerFactory;

	/** creates titles Dockables */
	private DockTitleFactory titleFactory;

	/** selects the image which should be displayed when moving a dockable*/
	private DockableMovingImageFactory movingImage;

	/** the factory used to create components for {@link StackDockStation} */
	private StackDockComponentFactory stackDockComponentFactory;

	/** the key to set the {@link ColorScheme} of this theme */
	public static final PropertyKey<ColorScheme> BASIC_COLOR_SCHEME = 
		new PropertyKey<ColorScheme>( "dock.ui.BasicTheme.ColorScheme", new BasicColorScheme(), true );

	/** the color scheme used in this theme */
	private PropertyValue<ColorScheme> colorScheme = new PropertyValue<ColorScheme>( BASIC_COLOR_SCHEME ){
		@Override
		protected void valueChanged( ColorScheme oldValue, ColorScheme newValue ) {
			ColorScheme scheme = getValue();
			scheme.updateUI();
			updateColors();
		}
	};

	/** how to select a new focused dockable */
	private DockableSelection selection;

	/** the controller which is installed */
	private DockController controller;

	/** a listener waiting for changed {@link LookAndFeel}s */
	private UIListener uiListener = new UIListener(){
		public void updateUI( DockController controller ){
			BasicTheme.this.updateUI();
		}
		public void themeChanged(DockController controller, DockTheme oldTheme,
				DockTheme newTheme) {
			// ignore
		}
		public void themeWillChange(DockController controller,
				DockTheme oldTheme, DockTheme newTheme) {
			// ignore
		}
	};

	/** a listener waiting for changes in the {@link LookAndFeelColors} */
	private LookAndFeelColorsListener colorListener = new LookAndFeelColorsListener(){
		public void colorChanged( String key ) {
			colorsChanged();
		}
		public void colorsChanged() {
			if( colorScheme.getValue().updateUI() )
				updateColors();
		}
	};

	/**
	 * Creates a new <code>BasicTheme</code>.
	 */
	public BasicTheme() {
		setCombiner( new BasicCombiner() );
		setPaint( new BasicStationPaint() );
		setDisplayerFactory( new BasicDisplayerFactory() );
		setTitleFactory( new BasicDockTitleFactory() );
		setMovingImageFactory( new BasicMovingImageFactory() );
		setStackDockComponentFactory( new StackDockComponentFactory(){
			public StackDockComponent create( StackDockStation station ) {
				return new BasicStackDockComponent( station );
			}
		});
		setDockableSelection( new BasicDockableSelection() );
	}

	public void install( DockController controller ) {
		if( this.controller != null )
			throw new IllegalStateException( "Theme is already in use" );

		this.controller = controller;

		controller.addUIListener( uiListener );
		DockUI.getDefaultDockUI().addLookAndFeelColorsListener( colorListener );
		updateUI();

		controller.getProperties().set( StackDockStation.COMPONENT_FACTORY, stackDockComponentFactory );
		
		colorScheme.setProperties( controller );
		
		updateColors();
	}

	public void uninstall( DockController controller ){
		if( this.controller != controller )
			throw new IllegalArgumentException( "Trying to uninstall a controller which is not installed" );

		controller.getProperties().toDefault( StackDockStation.COMPONENT_FACTORY );
		controller.getColors().clear( Priority.THEME );
		controller.removeUIListener( uiListener );

		DockUI.getDefaultDockUI().removeLookAndFeelColorsListener( colorListener );

		colorScheme.setProperties( (DockProperties)null );
		this.controller = null;
	}

	/**
	 * Called when the {@link LookAndFeel} changed, should update colors, fonts, ...
	 */
	public void updateUI(){
		if( colorScheme.getValue().updateUI() ){
			updateColors();
		}
		if( selection != null ){
			SwingUtilities.updateComponentTreeUI( selection.getComponent() );
		}
	}

	/**
	 * Called when the the colors of the {@link ColorManager} have to be updated.
	 * Subclasses should override this method and explicitly call
	 * {@link #updateColor(String, Color) updateColor} and
	 * {@link #updateColorBridge(Path) updateColorProvider}
	 * for all {@link Color}s and {@link UIBridge}s that will be used by
	 * this theme. Since {@link ColorScheme}s can create new colors and providers 
	 * lazily, just reading out all colors will ensure that all colors 
	 * and providers exists and are registered at the {@link ColorManager}s.
	 */
	protected void updateColors(){
		if( controller != null ){
			controller.getColors().lockUpdate();
			controller.getColors().clear( Priority.THEME );

			colorScheme.getValue().transmitAll( Priority.THEME, controller.getColors() );

			updateColor( "title.active.left", null );
			updateColor( "title.inactive.left", null );
			updateColor( "title.active.right", null );
			updateColor( "title.inactive.right", null );
			updateColor( "title.active.text", null );
			updateColor( "title.inactive.text", null );

			updateColor( "title.station.active", null );
			updateColor( "title.station.active.text", null );
			updateColor( "title.station.inactive", null );
			updateColor( "title.station.inactive.text", null );
			
			updateColor( "title.flap.active", null );
			updateColor( "title.flap.active.text", null );
			updateColor( "title.flap.inactive", null );
			updateColor( "title.flap.inactive.text", null );
			updateColor( "title.flap.selected", null );
			updateColor( "title.flap.selected.text", null );
			
			updateColor( "stack.tab.foreground", null );
			updateColor( "stack.tab.foreground.selected", null );
			updateColor( "stack.tab.foreground.focused", null );
			updateColor( "stack.tab.background", null );
			updateColor( "stack.tab.background.selected", null );
			updateColor( "stack.tab.background.focused", null );

			updateColor( "paint.line", null );
			updateColor( "paint.divider", null );
			updateColor( "paint.insertion", null );

			updateColorBridge( DockColor.KIND_DOCK_COLOR );
			updateColorBridge( TabColor.KIND_TAB_COLOR );
			updateColorBridge( TitleColor.KIND_TITLE_COLOR );
			updateColorBridge( ActionColor.KIND_ACTION_COLOR );
			updateColorBridge( DisplayerColor.KIND_DISPLAYER_COLOR );
			updateColorBridge( StationPaintColor.KIND_STATION_PAINT_COLOR );
			updateColorBridge( DockableSelectionColor.KIND_DOCKABLE_SELECTION_COLOR );

			controller.getColors().unlockUpdate();
		}
	}

	/**
	 * Changes the color of the {@link ColorManager}s to the color obtained
	 * through the {@link ColorScheme} or to <code>backup</code> if the scheme
	 * returns a <code>null</code> value.
	 * @param id the id of the new color
	 * @param backup backup color in case that the scheme does not
	 * know what to use
	 */
	protected void updateColor( String id, Color backup ){
		Color color = colorScheme.getValue().getColor( id );
		if( color == null )
			color = backup;

		controller.getColors().put( Priority.THEME, id, color );
	}

	/**
	 * Transmits the {@link ColorBridge} for <code>kind</code> to the {@link ColorManager}
	 * @param kind the kind of provider that should be published
	 */
	protected <P extends DockColor> void updateColorBridge( Path kind ){
	    ColorBridgeFactory factory = colorScheme.getValue().getBridgeFactory( kind );

		if( factory != null ){
			ColorBridge bridge = factory.create( controller.getColors() );
			controller.getColors().publish( Priority.THEME, kind, bridge );
		}
	}

	/**
	 * Gets the currently installed controller
	 * @return the controller
	 */
	public DockController getController(){
		return controller;
	}

	/**
	 * Sets the key that will be used to read the {@link ColorScheme} of this
	 * theme from the {@link DockProperties}.
	 * @param key the new key, not <code>null</code>
	 * @see #setColorScheme(ColorScheme)
	 */
	protected void setColorSchemeKey( PropertyKey<ColorScheme> key ){
		if( key == null )
			throw new IllegalArgumentException( "key must not be null" );

		colorScheme.setKey( key );
	}

	/**
	 * Sets the currently used set of colors. The colors of all {@link DockController}s
	 * will change immediately.
	 * @param colorScheme the new scheme, <code>null</code> will 
	 * activate the default color scheme.
	 */
	public void setColorScheme( ColorScheme colorScheme ) {
		this.colorScheme.setValue( colorScheme );
	}

	/**
	 * Gets the currently used color scheme
	 * @return the scheme
	 */
	public ColorScheme getColorScheme() {
		return colorScheme.getValue();
	}

	/**
	 * Sets the factory which will be used to create components for 
	 * {@link StackDockStation}. Note that this property has to be set
	 * before the theme is installed. Otherwise it will take no effect.
	 * @param stackDockComponentFactory the factory or <code>null</code>
	 */
	public void setStackDockComponentFactory(
			StackDockComponentFactory stackDockComponentFactory ) {
		this.stackDockComponentFactory = stackDockComponentFactory;
	}

	/**
	 * Sets the movingImage-property. The movignImage is needed to show an
	 * image when the user grabs a {@link Dockable}
	 * @param movingImage the new factory, not <code>null</code>
	 */
	public void setMovingImageFactory( DockableMovingImageFactory movingImage ) {
		if( movingImage == null )
			throw new IllegalArgumentException( "argument must not be null" );

		this.movingImage = movingImage;
	}

	/**
	 * Sets the {@link Combiner} of this theme. The combiner is used to
	 * merge two Dockables.
	 * @param combiner the combiner, not <code>null</code>
	 */
	public void setCombiner( Combiner combiner ) {
		if( combiner == null )
			throw new IllegalArgumentException( "argument must not be null" );

		this.combiner = combiner;
	}

	/**
	 * Sets the {@link StationPaint} of this theme. The paint is used to
	 * draw markings on stations.
	 * @param paint the paint, not <code>null</code>
	 */
	public void setPaint( StationPaint paint ) {
		if( paint == null )
			throw new IllegalArgumentException( "argument must not be null" );

		this.paint = paint;
	}

	/**
	 * Sets the {@link DisplayerFactory} of this theme. The factory is needed
	 * to create {@link DockableDisplayer}.
	 * @param factory the factory, not <code>null</code>
	 */
	public void setDisplayerFactory( DisplayerFactory factory ) {
		if( factory == null )
			throw new IllegalArgumentException( "argument must not be null" );

		displayerFactory = factory;
	}

	/**
	 * Sets the {@link DockTitleFactory} of this station. The factory is
	 * used to create {@link DockTitle DockTitles} for some Dockables.
	 * @param titleFactory the factory, not <code>null</code>
	 */
	public void setTitleFactory( DockTitleFactory titleFactory ) {
		if( titleFactory == null )
			throw new IllegalArgumentException( "argument must not be null" );

		this.titleFactory = titleFactory;
	}

	/**
	 * Sets how the user can select the focused {@link Dockable}.
	 * @param selection the new selector, not <code>null</code>
	 */
	public void setDockableSelection( DockableSelection selection ){
		if( selection == null )
			throw new IllegalArgumentException( "selection must not be null" );

		this.selection = selection;
	}

	public DockableMovingImageFactory getMovingImageFactory( DockController controller ) {
		return movingImage;
	}

	public Combiner getCombiner( DockStation station ) {
		return combiner;
	}

	public StationPaint getPaint( DockStation station ) {
		return paint;
	}

	public DisplayerFactory getDisplayFactory( DockStation station ) {
		return displayerFactory;
	}

	public DockTitleFactory getTitleFactory( DockController controller ) {
		return titleFactory;
	}

	public DockableSelection getDockableSelection( DockController controller ) {
		return selection;
	}
}
