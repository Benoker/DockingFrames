package tutorial.toolbar;

import tutorial.TutorialExtension;
import tutorial.support.sets.BasicsSet;
import tutorial.support.sets.TutorialSet;
import tutorial.toolbar.core.BasicToolbarTutorials;

public class TutorialToolbarExtension implements TutorialExtension{

	@Override
	public Class<?>[] getTutorials( Class<? extends TutorialSet> set ){
		if( set.isAssignableFrom( BasicsSet.class )){
			return new Class[]{ BasicToolbarTutorials.class };
		}
		return null;
	}
	
}
