package tutorial.common.basics;

import tutorial.support.Tutorial;
import tutorial.support.sets.TutorialSet;

@Tutorial(title="Basic", id="BasicsCommon")
public class BasicCommonSet extends TutorialSet{
	public BasicCommonSet(){
		super( SingleDockableFactoryExample.class,
			SelectPerspectivesExample.class,
			GroupingDockablesExample.class,
			NewCStationExample.class,
			MultiFrameExample.class,
			TitleWithTextFieldExample.class,
			OpeningEditorsExample.class,
			JDesktopPaneExample.class,
			SplittingExternalizedDockablesExample.class,
			HideCloseActionExample.class,
			EclipseLikeCloseButtonExample.class );
	}
}
