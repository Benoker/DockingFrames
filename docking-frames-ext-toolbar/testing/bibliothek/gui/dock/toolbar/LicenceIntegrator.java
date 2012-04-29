/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LicenceIntegrator {
	private File licence;
	private List<File> directories = new ArrayList<File>();
	
	public static void main( String[] args ) throws IOException{
		File workspace = new File("..");
		
		LicenceIntegrator integrator = new LicenceIntegrator();
		integrator.setLicence( new File( "licence_small.txt" ));
		integrator.addDirectory( new File( "src" ) );
		integrator.addDirectory( new File( "testing" ) );
		integrator.addDirectory( new File( workspace, "docking-frames-ext-toolbar-common/src" ) );
		integrator.addDirectory( new File( workspace, "docking-frames-ext-toolbar-common/testing" ) );
		integrator.run();
	}
	
	public void setLicence( File licence ){
		this.licence = licence;
	}
	
	public void addDirectory( File directory ){
		directories.add( directory );
	}
	
	public void run() throws IOException {
		String intro = read( licence );
		for( File directory : directories ){
			modify( directory, intro );
		}
	}
	
	private void modify( File file, String intro ) throws IOException{
		if( file.isDirectory() ){
			File[] children = file.listFiles();
			if( children != null ){
				for( File child : children ){
					modify( child, intro );
				}
			}
		}
		else if( file.getName().endsWith( ".java" )){
			String content = read( file );
			int index = content.indexOf( "package" );
			if( index == -1 ){
				System.err.println( "ignoring: " + file.getPath() + ", package info not found" );
			}
			else{
				content = content.substring( index );
				write( file, intro, content );
			}
		}
	}
	
	private String read( File file ) throws IOException{
		StringBuilder builder = new StringBuilder();
		FileReader reader = new FileReader( file );
		int next;
		while( (next = reader.read()) != -1 ){
			builder.append( (char)next );
		}
		reader.close();
		return builder.toString();
	}
	
	private void write( File file, String intro, String content ) throws IOException{
		System.out.println( "writing: " + file.getPath() );
		FileWriter writer = new FileWriter( file );
		writer.append( intro );
		writer.append( content );
		writer.close();
	}
}
