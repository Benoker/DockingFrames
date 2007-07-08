package bibliothek.help;

import java.io.File;

import javax.swing.JFrame;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.URManager;
import bibliothek.help.control.actions.RedoDockAction;
import bibliothek.help.control.actions.UndoDockAction;
import bibliothek.help.model.HelpModel;
import bibliothek.help.view.SelectingView;
import bibliothek.help.view.TypeHierarchyView;

public class Test {
    public static void main( String[] args ) throws Exception {
        DockFrontend frontend = new DockFrontend();
        SplitDockStation station = new SplitDockStation();
        frontend.addRoot( station, "root" );
        frontend.getController().setTheme( new NoStackTheme( new FlatTheme() ) );
        
        HelpModel model = new HelpModel( new File( "help/help.data" ) );
        LinkManager links = new LinkManager();
        links.setModel( model );
        
        URManager ur = links.getUR();
        UndoDockAction actionUndo = new UndoDockAction( ur );
        RedoDockAction actionRedo = new RedoDockAction( ur );
        
        final DefaultDockActionSource actions = new DefaultDockActionSource( actionUndo, actionRedo );
        frontend.getController().addActionGuard( new ActionGuard(){
        	public boolean react( Dockable dockable ){
        		return dockable.asDockStation() == null;
        	}
        	
        	public DockActionSource getSource( Dockable dockable ){
        		return actions;
        	}
        });
        
        SelectingView viewPackage = new SelectingView( links, "Packages", "package-list" );
        SelectingView viewClasses = new SelectingView( links, "Classes", "class-list" );
        SelectingView viewFields = new SelectingView( links, "Fields", "field-list" );
        SelectingView viewConstructors = new SelectingView( links, "Constructors", "constructor-list" );
        SelectingView viewMethods = new SelectingView( links, "Methods", "method-list" );
        SelectingView viewContent = new SelectingView( links, "Content", "class",
                "constructor-list", "constructor",
                "field-list", "field",
                "method-list", "method" );
        TypeHierarchyView viewHierarchy = new TypeHierarchyView( links );
        
        links.select( "package-list:root" );
        
        SplitDockGrid grid = new SplitDockGrid(  );
        grid.addDockable( 0, 0, 1, 1, viewPackage );
        grid.addDockable( 0, 1, 1, 2, viewClasses );
        grid.addDockable( 0, 3, 1, 1, viewConstructors );
        grid.addDockable( 1, 3, 1, 1, viewFields );
        grid.addDockable( 2, 3, 1, 1, viewMethods );
        grid.addDockable( 1, 0, 2, 3, viewContent );
        grid.addDockable( 3, 0, 1, 3, viewHierarchy );
        
        station.dropTree( grid.toTree() );
        
        JFrame frame = new JFrame();
        frame.add( station );
        frame.setBounds( 20, 20, 800, 600 );
        frame.setTitle( "Help - Demonstration of DockingFrames" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
