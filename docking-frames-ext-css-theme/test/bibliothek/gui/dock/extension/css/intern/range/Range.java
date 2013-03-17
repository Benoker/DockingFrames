package bibliothek.gui.dock.extension.css.intern.range;

import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.CssDeclarationValue;
import bibliothek.gui.dock.extension.css.transition.TransitionalCssProperty;
import bibliothek.gui.dock.extension.css.transition.types.AbstractTransitionalCssProperty;

public interface Range extends CssPropertyContainer{
	public static CssType<Range> TYPE = new CssType<Range>(){
		@Override
		public TransitionalCssProperty<Range> createTransition(){
			return new AnimatedRangedInteger();
		}
		
		@Override
		public Range convert( CssDeclarationValue value ){
			return new DefaultRange( value.getValue() );
		}
	};
	
	public String getName();
	
	public int getMin();
	
	public int getMax();
	

	public static class AnimatedRangedInteger extends AbstractTransitionalCssProperty<Range>{
		@Override
		protected void update(){
			Range source = getSource();
			Range target = getTarget();
			if( source == null && target == null ){
				getCallback().set( null );
			}
			else{
				int smin = 0;
				int smax = 0;
				int tmin = 0;
				int tmax = 0;
				String sname = null;
				String tname = null;
				
				if( source != null ){
					smin = source.getMin();
					smax = source.getMax();
					sname = source.getName();
				}
				if( target != null ){
					tmin = target.getMin();
					tmax = target.getMax();
					tname = target.getName();
				}
				
				double t = getTransition();
				
				int min = (int)( smin * (1-t) + tmin * t );
				int max = (int)( smax * (1-t) + tmax * t );
				
				String name;
				if( sname != null && tname != null ){
					name = sname + " -> " + tname;
				} 
				else if( sname != null ){
					name = sname;
				}
				else{
					name = tname;
				}
				
				getCallback().set( new SimpleRange(name, min, max ) );
			}
		}
	}
}
