package bibliothek.gui.dock.extension.css.intern;

import java.util.PriorityQueue;

import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssSchedulable;
import bibliothek.gui.dock.extension.css.transition.scheduler.CssScheduler;

/**
 * This {@link CssScheme} uses specialized code allowing it to run in JUnit tests:
 * <ul>
 * 	<li>The specialized {@link CssScheduler} must be started from outside and does execute the animations
 * synchronously </li>
 * </ul>
 * @author Benjamin Sigg
 */
public class TestCssScheme extends CssScheme{
	private TestScheduler scheduler = new TestScheduler();
	
	public TestCssScheme(){
		setScheduler( scheduler );
	}
	
	public void runAnimations( int deltaMilliseconds ){
		scheduler.run( deltaMilliseconds );
	}
	
	private class TestScheduler implements CssScheduler{
		private int timeGone = 0;
		private PriorityQueue<Job> jobs = new PriorityQueue<Job>();
		private Job executing;
		
		@Override
		public void step( CssSchedulable job ){
			step( job, 20 );
		}

		@Override
		public void step( CssSchedulable job, int delay ){
			jobs.add( new Job( job, delay ) );
		}
		
		public void run( int milliseconds ){
			int limit = timeGone + milliseconds;
			
			while( !jobs.isEmpty() ){
				Job job = jobs.peek();
				if( job.time <= limit ){
					jobs.poll();
					
					int delta = job.time - timeGone;
					timeGone += delta;
					milliseconds -= delta;
					
					job.execute();
				}
				else{
					break;
				}
			}
			
			timeGone = limit;
		}
		
		private class Job implements Comparable<Job>{
			private CssSchedulable job;
			private int time;
			private int delay;
			private boolean repeat;
			
			public Job( CssSchedulable job, int delay ){
				this.job = job;
				this.time = delay + timeGone;
				this.delay = delay;
				
				repeat = executing != null && executing.job == job;
			}
			
			@Override
			public int compareTo( Job o ){
				if( time < o.time ){
					return -1;
				}
				else if( time > o.time ){
					return 1;
				}
				return 0;
			}
			
			public void execute(){
				try{
					executing = this;
					if( repeat ){
						job.step( TestScheduler.this, delay );
					}
					else{
						job.step( TestScheduler.this, -1 );
					}
				}
				finally{
					executing = null;
				}
			}
		}
	}
}
