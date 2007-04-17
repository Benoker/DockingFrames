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
 * Created on 26.08.2004
 */
package bibliothek.util.threads;

import java.util.Hashtable;


/**
 * Ermöglicht einem Algorithmus Informationen und Fortschritt auf einfache
 * Weise weiterzugeben.
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public abstract class AbstractAlgorithmus implements Algorithmus {
	private Hashtable<String, Information> informations = new Hashtable<String, Information>();
	private String[] keys;
	
	private Progress[] progresses;

	/**
	 * Standartkonstruktor
	 * @param keys Die Schlüsselwerte zu diesem Algorithmus
	 * @param progresses Die Anzahl Fortschritte die aufgezeichnet werden
	 */
	public AbstractAlgorithmus( String[] keys, int progresses ){
		setKeys( keys );
		setProgressCount( progresses );
	}
	
	/**
	 * Setzt die Keys, für welche dieser Algorithmus Informationen bietet.
	 * @param keys Die Schlüssel
	 */
	protected void setKeys( String[] keys ){
		informations.clear();
		this.keys = keys;
		
		for( String x : keys )
			informations.put( x, new Information() );
	}
	
	/**
	 * Setzt die Information für den gegebenen Schlüssel. Der AbstractAlgorithmus
	 * wird einmal die Methode "toString" aufrufen, und danach das Objekt nie
	 * mehr benutzen. Der Aufruf von "toString" erfolgt bei der ersten Nachfrage
	 * nach diesem Wert.
	 * @param key Der Schlüssel
	 * @param information Die Information
	 */
	protected void setInformation( String key, Object information ){
		informations.get( key ).setValue( information );
	}
	
	public String[] getInformationKeys() {
		return keys;
	}

	public String getInformation(String key) {
		return informations.get( key ).toString();
	}

	/**
	 * Setzt die Anzahl Fortschritte, welche aufgezeichnet werden.
	 * @param count Die Anzahl Fortschritte
	 */
	protected void setProgressCount( int count ){
		progresses = new Progress[ count ];
		for( int i = 0; i < count; i++ )
			progresses[i] = new Progress();
	}
	
	/**
	 * Setzt den Minimalwert für den angegebenen Fortschritt.
	 * @param index Der Index des Fortschrittes
	 * @param minimum Der Minimalwert
	 */
	protected void setMinimum( int index, int minimum ){
		progresses[ index ].min = minimum;
	}

	/**
	 * Setzt den Maximalwert für den angegebenen Fortschritt.
	 * @param index Der Index des Fortschrittes
	 * @param maximum Der Maximalwert
	 */
	protected void setMaximum( int index, int maximum ){
		progresses[ index ].max = maximum;
	}
	
	/**
	 * Setzt den Wert für den angegebenen Fortschritt.
	 * @param index Der Index des Fortschrittes
	 * @param value Der Wert
	 */
	protected void setValue( int index, int value ){
		progresses[ index ].value = value;
	}
	
	public int getProgressCount() {
		return progresses == null ? 0 : progresses.length;
	}

	public int getMinimum(int index) {
		return progresses[index].min;
	}

	public int getMaximum(int index) {
		return progresses[index].max;
	}

	public int getValue(int index) {
		return progresses[index].value;
	}

	private static class Progress{
		public int min, max, value;
	}
	
	private static class Information{
		private Object box;
		private String translation;
		
        @Override
		public synchronized String toString(){
			if( translation == null )
				translation = box == null ? "null" : box.toString();
			
			return translation;
		}
		
		public synchronized void setValue( Object anObject ){
			box = anObject;
			translation = null;
		}
	}
}
