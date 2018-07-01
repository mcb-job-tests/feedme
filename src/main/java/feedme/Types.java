package feedme;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

class Types {
    private final String HEADER = "header";
    private final String BODY = "body";

    private List<Type> typeList = new ArrayList<>();

    Types(final String xmlApiFileName){
        DomParser domParser = new DomParser(xmlApiFileName);
        List<Element> typeElements = domParser.getTypes();
        for(Element typeElement : typeElements){
            String name = typeElement.getName();
            Element header = typeElement.getChild(HEADER);
            Element body = typeElement.getChild(BODY);
            typeList.add(new Type(name, header, body));
        }
    }

    List<Type> getList() {
        return typeList;
    }

    Type getType(String typeName){
        for(Type type : typeList){
            if (typeName.equals(type.getName())){
                return type;
            }
        }
        return null;
    }

}
