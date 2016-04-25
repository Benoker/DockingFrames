package bibliothek.test;

public interface InspectionNodeListener {
	public void updated();
	public void updated( InspectionNode[] oldChildren, InspectionNode[] newChildren );
}
