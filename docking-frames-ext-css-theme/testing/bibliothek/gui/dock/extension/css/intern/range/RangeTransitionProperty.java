package bibliothek.gui.dock.extension.css.intern.range;

import bibliothek.gui.dock.extension.css.CssItem;
import bibliothek.gui.dock.extension.css.CssScheme;
import bibliothek.gui.dock.extension.css.CssType;
import bibliothek.gui.dock.extension.css.transition.CssContainerTransitionProperty;

public abstract class RangeTransitionProperty extends CssContainerTransitionProperty<Range>{
	public RangeTransitionProperty( CssScheme scheme, CssItem item ){
		super( scheme, item );
	}

	@Override
	public CssType<Range> getType( CssScheme scheme ){
		return scheme.getConverter( Range.class );
	}
}
