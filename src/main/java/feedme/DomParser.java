package feedme;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

class DomParser {
    private static final String HEADER = "header";
    private static final String BODY = "body";

    private Document document;
    private List<Element> types;

    DomParser(final String xmlApiFileName) {
        try {
            File xmlApiFile = new ClassPathResource(xmlApiFileName).getFile();
            SAXBuilder saxBuilder = new SAXBuilder();
            document = saxBuilder.build(xmlApiFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Root element :" + document.getRootElement().getName());

        types = document.getRootElement().getChildren();
    }

    Element getTypeElement(final String typeName){
        for( Element type : types){
            if (type.getName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    Element getTypeContent(final String typeName, final String contentName){
        for( Element element : getTypeElement(typeName).getChildren()){
            if (contentName.equals(element.getName())) {
                return element;
            }
        }
        return null;
    }

    Element getTypeHeader(final String typeName){
        for( Element element : getTypeElement(typeName).getChildren()){
            if (HEADER.equals(element.getName())) {
                return element;
            }
        }
        return null;
    }

    Element getTypeBody(final String typeName){
        for( Element element : getTypeElement(typeName).getChildren()){
            if (BODY.equals(element.getName())) {
                return element;
            }
        }
        return null;
    }


    public List<Element> getTypes() {
        return types;
    }

}
