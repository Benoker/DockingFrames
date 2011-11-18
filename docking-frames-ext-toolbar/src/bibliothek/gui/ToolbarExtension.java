package bibliothek.gui;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.control.relocator.DefaultDockRelocator;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.ToolbarMiniButton;
import bibliothek.gui.dock.station.ToolbarTabDockStationFactory;
import bibliothek.gui.dock.station.screen.ScreenToolbarDisplayerFactory;
import bibliothek.gui.dock.station.screen.ScreenToolbarDockTitleFactory;
import bibliothek.gui.dock.station.screen.magnet.AttractorStrategy;
import bibliothek.gui.dock.station.toolbar.DefaultToolbarDockTitle;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerPropertyFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarAttractorStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationMerger;
import bibliothek.gui.dock.station.toolbar.ToolbarDockTitle;
import bibliothek.gui.dock.station.toolbar.ToolbarDockTitleGrip;
import bibliothek.gui.dock.station.toolbar.ToolbarDockableDisplayer;
import bibliothek.gui.dock.station.toolbar.ToolbarFullscreenFilter;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationMerger;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockTitle;
import bibliothek.gui.dock.station.toolbar.ToolbarDockTitlePoint;
import bibliothek.gui.dock.station.toolbar.ToolbarPartDockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarPropertyFactory;
import bibliothek.gui.dock.themes.DockThemeExtension;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.action.BasicButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicMenuHandler;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleManager;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.NullTitleFactory;
import bibliothek.gui.dock.toolbar.expand.ExpandManager;
import bibliothek.gui.dock.toolbar.expand.ExpandedActionGuard;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.TextManager;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * Allows seamless integration of the toolbar extension into the core and common
 * library without them having any references back to the toolbar project.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarExtension implements Extension{
	/** unique flag for marking {@link DockTitle}s shown above a toolbar */
	public static final ViewTarget<BasicTitleViewItem<JComponent>> TOOLBAR_TITLE = new ViewTarget<BasicTitleViewItem<JComponent>>(
			"target TOOLBAR TITLE");

	@Override
	public void install( DockController controller ){
		ActionViewConverter converter = controller.getActionViewConverter();
		converter
				.putDefault(
						ActionType.BUTTON,
						TOOLBAR_TITLE,
						new ViewGenerator<ButtonDockAction, BasicTitleViewItem<JComponent>>(){
							@Override
							public BasicTitleViewItem<JComponent> create(
									ActionViewConverter converter,
									ButtonDockAction action, Dockable dockable ){
								BasicButtonHandler handler = new BasicButtonHandler(
										action, dockable);
								ToolbarMiniButton button = new ToolbarMiniButton(
										handler, handler);
								handler.setModel(button.getModel());
								return handler;
							}
						});

		converter
				.putDefault(
						ActionType.MENU,
						TOOLBAR_TITLE,
						new ViewGenerator<MenuDockAction, BasicTitleViewItem<JComponent>>(){
							@Override
							public BasicTitleViewItem<JComponent> create(
									ActionViewConverter converter,
									MenuDockAction action, Dockable dockable ){
								BasicMenuHandler handler = new BasicMenuHandler(
										action, dockable);
								ToolbarMiniButton button = new ToolbarMiniButton(
										handler, handler);
								handler.setModel(button.getModel());
								return handler;
							}
						});

		IconManager icons = controller.getIcons();
		icons.setIconDefault("toolbar.item.expand", loadIcon("expand.png"));
		icons.setIconDefault("toolbar.item.shrink", loadIcon("shrink.png"));
		icons.setIconDefault("toolbar.item.larger", loadIcon("larger.png"));
		icons.setIconDefault("toolbar.item.smaller", loadIcon("smaller.png"));

		controller.addActionGuard(new ExpandedActionGuard(controller));
		new ExpandManager(controller);

		// add or remove a filter for preventing fullscreen
		final ToolbarFullscreenFilter filter = new ToolbarFullscreenFilter(
				controller);
		controller.getRegister().addDockRegisterListener(
				new DockRegisterAdapter(){
					@Override
					public void dockStationRegistering(
							DockController controller, DockStation station ){
						if (station instanceof ScreenDockStation){
							((ScreenDockStation) station)
									.addFullscreenFilter(filter);
						}
					}

					@Override
					public void dockStationUnregistered(
							DockController controller, DockStation station ){
						if (station instanceof ScreenDockStation){
							((ScreenDockStation) station)
									.removeFullscreenFilter(filter);
						}
					}
				});
	}

	private Icon loadIcon( String name ){
		try{
			InputStream in = getClass().getResourceAsStream(
					"/data/bibliothek/gui/toolbar/" + name);
			if (in == null){
				throw new FileNotFoundException("cannot find file '" + name
						+ "'");
			}
			ImageIcon icon = new ImageIcon(ImageIO.read(in));
			in.close();
			return icon;
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void uninstall( DockController controller ){
		ActionViewConverter converter = controller.getActionViewConverter();
		converter.putDefault(ActionType.BUTTON, TOOLBAR_TITLE, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Collection<E> load( DockController controller,
			ExtensionName<E> extension ){
		if (extension.getName().equals(PropertyTransformer.FACTORY_EXTENSION)){
			return (Collection<E>) createPropertyFactoryExtension();
		}
		if (extension.getName().equals(DefaultDockRelocator.MERGE_EXTENSION)){
			return (Collection<E>) createMergerExtension();
		}
		if (extension.getName().equals(
				ScreenDockStation.ATTRACTOR_STRATEGY_EXTENSION)){
			return (Collection<E>) createAttractorStrategies();
		}
		if (extension.getName().equals(DockSituation.DOCK_FACTORY_EXTENSION)){
			return (Collection<E>) createDockFactories();
		}
		if (extension.getName().equals(DockThemeExtension.DOCK_THEME_EXTENSION)){
			return (Collection<E>) createDockThemeExtension();
		}
		if (extension.getName().equals(
				DockTitleVersion.DOCK_TITLE_VERSION_EXTENSION)){
			return (Collection<E>) createTitleFactories((DockTitleVersion) extension
					.get(DockTitleVersion.DOCK_TITLE_VERSION_EXTENSION_PARAMETER));
		}
		if (extension.getName().equals(DisplayerFactory.DISPLAYER_EXTENSION)){
			return (Collection<E>) createDisplayerFactories(controller,
					(String) extension
							.get(DisplayerFactory.DISPLAYER_EXTENSION_ID));
		}
		if (extension.getName().equals(TextManager.TEXT_EXTENSION)){
			return (Collection<E>) createBundles((Locale) extension
					.get(TextManager.TEXT_EXTENSION_LOCALE));
		}

		return null;
	}

	private Collection<DockablePropertyFactory> createPropertyFactoryExtension(){
		List<DockablePropertyFactory> result = new ArrayList<DockablePropertyFactory>();
		result.add(new ToolbarPropertyFactory());
		result.add(new ToolbarContainerPropertyFactory());
		return result;
	}

	private Collection<Merger> createMergerExtension(){
		List<Merger> result = new ArrayList<Merger>();
		result.add(new ToolbarGroupDockStationMerger());
		result.add(new ToolbarDockStationMerger());
		return result;
	}

	private Collection<AttractorStrategy> createAttractorStrategies(){
		List<AttractorStrategy> result = new ArrayList<AttractorStrategy>();
		result.add(new ToolbarAttractorStrategy());
		return result;
	}

	private Collection<DockFactory<?, ?, ?>> createDockFactories(){
		List<DockFactory<?, ?, ?>> result = new ArrayList<DockFactory<?, ?, ?>>();
		result.add(new ToolbarPartDockFactory());
		result.add(new ToolbarDockStationFactory());
		result.add(new ToolbarGroupDockStationFactory());
		result.add(new ToolbarContainerDockStationFactory());
		result.add(new ToolbarTabDockStationFactory());
		return result;
	}

	private Collection<DockTitleFactory> createTitleFactories(
			DockTitleVersion version ){
		if (version.getID().equals(ScreenDockStation.TITLE_ID)){
			List<DockTitleFactory> result = new ArrayList<DockTitleFactory>();
			result.add(new ScreenToolbarDockTitleFactory(version
					.getController()));
			return result;
		}
		return null;
	}

	private Collection<DisplayerFactory> createDisplayerFactories(
			DockController controller, String id ){
		if (id.equals(ScreenDockStation.DISPLAYER_ID)){
			List<DisplayerFactory> result = new ArrayList<DisplayerFactory>();
			result.add(new ScreenToolbarDisplayerFactory(controller));
			return result;
		}
		return null;
	}

	private Collection<ResourceBundle> createBundles( Locale language ){
		List<ResourceBundle> result = new ArrayList<ResourceBundle>();
		result.add(ResourceBundle.getBundle(
				"data.bibliothek.gui.toolbar.locale.toolbar", language, this
						.getClass().getClassLoader()));
		return result;
	}

	private Collection<DockThemeExtension> createDockThemeExtension(){
		DockThemeExtension extension = new DockThemeExtension(){

			@Override
			public void installed( DockController controller, DockTheme theme ){
				ThemeManager manager = controller.getThemeManager();
				manager.put(Priority.THEME, ThemeManager.DISPLAYER_FACTORY
						+ ".toolbar", ThemeManager.DISPLAYER_FACTORY_TYPE,
						ToolbarDockableDisplayer.createColorBorderFactory(
								new Color(255, 150, 150), false));
				manager.put(Priority.THEME, ThemeManager.DISPLAYER_FACTORY
						+ ".toolbar.group",
						ThemeManager.DISPLAYER_FACTORY_TYPE,
						ToolbarDockableDisplayer.createColorBorderFactory(
								new Color(255, 100, 100), false));
				manager.put(Priority.THEME, ThemeManager.DISPLAYER_FACTORY
						+ ".toolbar.container",
						ThemeManager.DISPLAYER_FACTORY_TYPE,
						ToolbarDockableDisplayer.createColorBorderFactory(
								new Color(255, 50, 50), false));
				manager.put(Priority.THEME, ThemeManager.DISPLAYER_FACTORY
						+ ".toolbar.screen",
						ThemeManager.DISPLAYER_FACTORY_TYPE,
						ToolbarDockableDisplayer.createColorBorderFactory(
								Color.ORANGE, true));

				DockTitleManager titles = controller.getDockTitleManager();
				// titles.registerTheme(ToolbarDockStation.TITLE_ID,
				// ToolbarDockTitlePoint
				// .createFactory(new Color(255, 0, 0)));
				titles.registerTheme(ToolbarDockStation.TITLE_ID,
						NullTitleFactory.INSTANCE);
				titles.registerTheme(ToolbarGroupDockStation.TITLE_ID,
						NullTitleFactory.INSTANCE);
				titles.registerTheme(ToolbarContainerDockStation.TITLE_ID,
						ToolbarDockTitleGrip.createFactory(new Color(80, 80,
								80)));
				titles.registerTheme(ScreenToolbarDockTitleFactory.TITLE_ID,
						ToolbarDockTitle.createFactory(Color.RED));
			}

			@Override
			public void install( DockController controller, DockTheme theme ){
				// nothing
			}

			@Override
			public void uninstall( DockController controller, DockTheme theme ){
				// nothing
			}

		};
		return Collections.singleton(extension);
	}
}
