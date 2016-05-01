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
package bibliothek.gui.dock.common.theme;

import java.util.Map;

import javax.swing.Icon;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.action.CPanelPopup;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.action.panel.EclipsePanelPopupGenerator;
import bibliothek.gui.dock.common.intern.action.panel.PanelDropDownGenerator;
import bibliothek.gui.dock.common.intern.action.panel.PanelMenuGenerator;
import bibliothek.gui.dock.common.intern.color.BasicButtonTitleTransmitter;
import bibliothek.gui.dock.common.intern.color.EclipseTabTransmitter;
import bibliothek.gui.dock.common.intern.font.FontBridgeFactory;
import bibliothek.gui.dock.common.intern.font.TabTitleFontTransmitter;
import bibliothek.gui.dock.themes.ColorBridgeFactory;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.themes.color.TabColor;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.themes.font.TitleFont;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.font.FontBridge;
import bibliothek.gui.dock.util.font.FontManager;
import bibliothek.util.ClientOnly;

/**
 * A wrapper around a {@link EclipseTheme}, allows to use the {@link ColorMap} of
 * {@link CDockable}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class CEclipseTheme extends CDockTheme<EclipseTheme>{
    /**
     * Creates a new theme.
     * @param control the controller for which this theme will be used
     * @param theme the theme that gets encapsulated
     */
    public CEclipseTheme( CControl control, EclipseTheme theme ){
        super( theme );
        init( control );
    }
    
    /**
     * Creates a new theme. This theme can be used directly with a 
     * {@link CControl}.
     * @param control the controller for which this theme will be used.
     */
    public CEclipseTheme( CControl control ){
        this( new EclipseTheme() );
        init( control );
    }
    
    /**
     * Creates a new theme.
     * @param theme the delegate which will do most of the work
     */
    private CEclipseTheme( EclipseTheme theme ){
        super( theme, new NoStackTheme( theme ) );
    }
    
    /**
     * Initializes the properties of this theme.
     * @param control the controller for which this theme will be used
     */
    private void init( final CControl control ){
        putColorBridgeFactory( TabColor.KIND_TAB_COLOR, new ColorBridgeFactory(){
            public ColorBridge create( ColorManager manager ) {
                EclipseTabTransmitter transmitter = new EclipseTabTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });

        putColorBridgeFactory( TitleColor.KIND_FLAP_BUTTON_COLOR, new ColorBridgeFactory(){
        	public ColorBridge create(ColorManager manager) {
        		BasicButtonTitleTransmitter transmitter = new BasicButtonTitleTransmitter( manager );
        		transmitter.setControl( control );
        		return transmitter;
        	}
        });
        
        initDefaultFontBridges( control );
        putFontBridgeFactory( TitleFont.KIND_TAB_TITLE_FONT, new FontBridgeFactory(){
            public FontBridge create( FontManager manager ) {
                TabTitleFontTransmitter transmitter = new TabTitleFontTransmitter( manager );
                transmitter.setControl( control );
                return transmitter;
            }
        });
    }
    
    @Override
    public void install( DockController controller ) {
        super.install( controller );
        IconManager manager = controller.getIcons();
        Map<String, Icon> icons = DockUtilities.loadIcons(
                "data/bibliothek/gui/dock/common/icons/eclipse/icons.ini", null, CEclipseTheme.class.getClassLoader() );
        for( Map.Entry<String, Icon> entry : icons.entrySet() ){
            manager.setIconTheme( entry.getKey(), entry.getValue() );
        }
        ActionViewConverter converter = controller.getActionViewConverter();
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.TITLE, new EclipsePanelPopupGenerator());
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.MENU, new PanelMenuGenerator() );
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.DROP_DOWN, new PanelDropDownGenerator() );
    }
    
    @Override
    public void uninstall( DockController controller ) {
        super.uninstall( controller );
        controller.getIcons().clear( Priority.THEME );
        ActionViewConverter converter = controller.getActionViewConverter();
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.TITLE, null );
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.MENU, null );
    	converter.putTheme( CPanelPopup.PANEL_POPUP, ViewTarget.DROP_DOWN, null );
    }
}
