package feedme;

import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.List;

abstract class ContentMap extends HashMap<Integer, Field>{
    private static final int INDEX = 0;
    private static final int NAME = 1;
    private static final int DATA_TYPE = 2;

    ContentMap(final Element headerElement){
        List<Element> fieldElementList =  headerElement.getChildren();

        for (Element fieldElement : fieldElementList){
            List<Attribute> fieldAttributes = fieldElement.getAttributes();
            Integer index = Integer.parseInt(fieldAttributes.get(INDEX).getValue());
            Field field = new Field(fieldAttributes.get(NAME).getValue(), fieldAttributes.get(DATA_TYPE).getValue());

            this.put(index, field);
        }
    }
}
