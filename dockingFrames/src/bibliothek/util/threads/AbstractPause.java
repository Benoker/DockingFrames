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
 
package bibliothek.util.threads;


/**
 * Diese Pause kann jederzeit aktiviert werden.
 * 
 * @author Benjamin Sigg
 * @version 1.0
 */
public abstract class AbstractPause implements Pause {
	private boolean pause = false;
	private boolean interrupted = false;
	
	private final Object WAIT = new Object();
	
	public void pause() {
		synchronized( WAIT ){
			if( pause ){
				try {
					tellPauseBegins();
					WAIT.wait();
				} catch (InterruptedException e) {
				}
				tellPauseEnds();
			}
		}
	}
	
	/**
	 * Setzt den Wert der Pause.
	 * @param pause True, wenn alle Threads die {@link #pause()} aufrufen
	 * gestoppt werden sollen, false wenn alle Threads weiterfahren dürfen.
	 */
	public void setPause( boolean pause ){
		synchronized( WAIT ){
			if( pause != this.pause ){
				if( !pause ){
					WAIT.notifyAll();
				}
			}
			
			this.pause = pause;
		}
	}
	
	/**
	 * Gibt den Wert der Pause-Flag an.
	 * @return Der Wert der zuletzt bei {@link #setPause(boolean)} angegeben
	 * wurde.
	 */
	public boolean isPause(){
		return pause;
	}

	/**
	 * Wird aufgerufen, kurz bevor eine Pause beginnt (zu diesem Zeitpunkt
	 * ist es nicht mehr möglich, die Pause abzubrechen).
	 */
	protected abstract void tellPauseBegins();
	
	/**
	 * Wird aufgerufen, wenn eine tatsächlich begonnene Pause beendet wird.
	 * Diese Methode wird also nur dann aufgerufen, wenn {@link #tellPauseBegins()}
	 * zuvor aufgerufen wurde.
	 */
	protected abstract void tellPauseEnds();
	
	public boolean isInterrupted() {
		return interrupted;
	}
	
	public void setInterrupted( boolean interrupted ){
		this.interrupted = interrupted;
	}
}
