package models.graphbased;

public abstract class AbstractGraphElement implements AttributeMapOwner {

	private final AttributeMap map;

	public AbstractGraphElement() {
		map = new AttributeMap();
	}

	public String getLabel() {
		return map.get(AttributeMap.LABEL, "no label");
	}

	public AttributeMap getAttributeMap() {
		return map;
	}

	public String toString() {
		if(getLabel()==null){
			return map.get("Original id", "no id");
		}
		return getLabel();
	}

}
