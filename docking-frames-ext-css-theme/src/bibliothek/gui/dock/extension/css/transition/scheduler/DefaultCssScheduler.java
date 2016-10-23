/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.transition.scheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.Timer;

/**
 * This {@link CssScheduler} ignores multiple calls to its <code>step</code> methods,
 * and executes all <code>steps</code> in the <code>EventDispatcherThread</code>. 
 * @author Benjamin Sigg
 */
public class DefaultCssScheduler implements CssScheduler{
	private final Object LOCK = new Object();
	
	private PriorityQueue<Call> queue = new PriorityQueue<Call>();
	private Map<CssSchedulable, Call> calls = new HashMap<CssSchedulable, Call>();
	
	private volatile int pendingCalls = 0;
	private volatile Call executing;
	
	private ActionListener callback = new ActionListener(){
		@Override
		public void actionPerformed( ActionEvent e ){
			step();	
		}
	};
	
	@Override
	public void step( CssSchedulable job ){
		step( job, 20 );
	}

	@Override
	public void step( CssSchedulable job, int delay ){
		synchronized( LOCK ){
			Call next = new Call( job, delay );
			Call pending = calls.get( job );
			
			if( pending != null ){
				if( pending.nanoScheduled > next.nanoScheduled ){
					queue.remove( pending );
				}
				else{
					next = null;
				}
			}
			
			if( next != null ){
				calls.put( job, next );
				queue.add( next );
				schedule( delay );
			}
		}
	}
	
	private void schedule( int delay ){
		synchronized( LOCK ){
			pendingCalls++;
			Timer timer = new Timer( delay, callback );
			timer.setRepeats( false );
			timer.start();
		}
	}
	
	private void step(){
		synchronized( LOCK ){
			pendingCalls--;
		}
		long now = System.nanoTime();
		while( true ){
			boolean execute = false;
			Call call = null;
			synchronized( LOCK ){
				call = queue.peek();
				if( call == null ){
					return;
				}
				
				if( call.nanoScheduled <= now ){
					execute = true;
					queue.poll();
					calls.remove( call.job );
				}
				else{
					if( pendingCalls == 0 ){
						schedule( Math.max( 1, (int)((call.nanoScheduled - now)/1000000) ) );
					}
				}
			}
			
			if( execute ){
				call.execute( now );
			} else {
				break;
			}
		}
	}

	private class Call implements Comparable<Call>{
		private CssSchedulable job;
		private boolean repeat;
		private long nanoStart;
		private long nanoScheduled;
		private long nanoExecuting;
		
		public Call( CssSchedulable job, int delay ){
			this.job = job;
			
			if( executing != null && executing.job == job ){
				nanoStart = executing.nanoExecuting;
				repeat = true;
			}
			else{
				nanoStart = System.nanoTime();
				repeat = false;
			}
			nanoScheduled = nanoStart + 1000000 * delay;
		}
		
		@Override
		public int compareTo( Call o ){
			if( nanoScheduled < o.nanoScheduled ){
				return -1;
			}
			else if( nanoScheduled > o.nanoScheduled ){
				return 1;
			}
			return 0;
		}
		
		public void execute( long nanoNow ){
			nanoExecuting = nanoNow;
			int delay = -1;
			if( repeat ){
				delay = (int)((nanoNow - nanoStart) / 1000000);
			}
			try{
				executing = this;
				job.step( DefaultCssScheduler.this, delay );
			}
			finally{
				executing = null;
			}
		}
	}
}
