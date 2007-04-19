/*
 * Bibliothek License
 * ==================
 * 
 * Except where otherwise noted, all of the documentation and software included
 * in the bibliothek package is copyrighted by Benjamin Sigg.
 * 
 * Copyright (C) 2001-2005 Benjamin Sigg. All rights reserved.
 * 
 * This software is provided "as-is," without any express or implied warranty.
 * In no event shall the author be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter and redistribute it,
 * provided that the following conditions are met:
 * 
 * 1. All redistributions of source code files must retain all copyright
 *    notices that are currently in place, and this list of conditions without
 *    modification.
 * 
 * 2. All redistributions in binary form must retain all occurrences of the
 *    above copyright notice and web site addresses that are currently in
 *    place (for example, in the About boxes).
 * 
 * 3. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software to
 *    distribute a product, an acknowledgment in the product documentation
 *    would be appreciated but is not required.
 * 
 * 4. Modified versions in source or binary form must be plainly marked as
 *    such, and must not be misrepresented as being the original software.
 * 
 * 
 * Benjamin sigg
 * benjamin_sigg@gmx.ch
 * 
 */
 
/*
 * Created on 13.05.2004
 */
package bibliothek.util.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Liest eine INI-Datei aus. Eine INI-Datei besteht aus einer Folge von
 * <code>key = value</code> Paaren. Dazwischen k�nnen einzeilige Kommentare
 * mit "//" oder mehrzeilige Kommentare mit "/*" und "*\/" stehen
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public class IniReader {
	public static Properties readIni( File file ) throws IOException {
		BufferedInputStream in = new BufferedInputStream( new FileInputStream( file ));
		Properties p = readIni( in );
		in.close();
		return p;
	}
	
	public static Properties readIni( URL url ) throws IOException{
		BufferedInputStream in = new BufferedInputStream( url.openStream() );
		Properties p = readIni( in );
		in.close();
		return p;		
	}
	
	/**
	 * Liest eine INI-Datei aus, aber schliesst den Stream nicht. Es wird
	 * solange gelesen, bis der Stream -1 zur�ckgibt.
	 * @param in Der Stream mit den Informationen
	 * @return Die gelesenen Informationen
	 * @throws IOException Bei Problemen
	 */
	public static Properties readIni( InputStream in ) throws IOException{
		Properties prop = new Properties();
		
		
		StringBuffer buffer = new StringBuffer();
		
		boolean normalComment = false;
		boolean multiLineComment = false;
		
		int read = in.read();
		char last = (char)read;
		
		while( read != -1 ){
			read = in.read();
			char newChar = (char)read;
			
			if( normalComment ){
				if( newChar == '\n' || newChar == '\r' ){
					normalComment = false;
					read = jump( in, 1 );
					newChar = (char)read;
				}
			}
			else if( multiLineComment ){
				if( last == '*' && newChar == '/'){
					multiLineComment = false;
					read = jump( in, 1 );
					newChar = (char)read;
				}
			}
			else{
				if( last == '/' && newChar == '*' ){
					multiLineComment = true;
					read = jump( in, 1 );
					newChar = (char)read;
				}
				else if( last == '/' && newChar == '/' ){
					normalComment = true;
					read = jump( in, 1 );
					newChar = (char)read;
				}
				else{
					if( read == -1 || newChar == '\n' || newChar == '\r' ){
						if( buffer.length() > 0 ){
							buffer.append( last );
							finish( prop, buffer );
							buffer = new StringBuffer();
							
							read = jump( in, 1 );
							newChar = (char)read;
						}
					}
					else if( !(last == '\n') && !(last == '\r') )
						buffer.append( last );
				}
			}
			
			last = newChar;
		}
		
		return prop;
	}
	
	private static int jump( InputStream in, int n ) throws IOException{
		int read = -1;
		
		for( int i = 0; i < n; i++ )
			read = in.read();
		
		return read;
	}
	
	private static void finish( Properties prop, StringBuffer buffer ) throws IOException {
		int index = buffer.indexOf( "=" );
		if( index == -1 )
			throw new IOException("Non valid line: " + buffer );
		
		String key = mini( buffer.substring( 0, index ) );
		String value = mini( buffer.substring( index+1 ) );
		
		if( key.length() == 0 )
			throw new IOException("Non valid key: empty" );
		
		prop.put( key, value );
	}
	
	private static String mini( String original ){
		while( original.endsWith( " " ) || original.endsWith( "\t" ))
			original = original.substring( 0, original.length()-1 );
		
		while( original.startsWith( " " ) || original.startsWith( "\t"))
			original = original.substring( 1 );
		
		return original;
	}
}
