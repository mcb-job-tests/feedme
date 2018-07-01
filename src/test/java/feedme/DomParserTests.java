package feedme;

import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DomParserTests {

    private final String HEADER = "header";
    private final String BODY = "body";
    private final String EVENT = "event";
    private final String MARKET = "market";
    private final String OUTCOME = "outcome";

    DomParser domParser = new DomParser("types.xml");

    @Test
    public void consumeXmlFile_typesContainsValidChildren() {
        List<Element> types = domParser.getTypes();
        types.forEach(type->{
            System.out.println(type.getName());
        });

        Assert.assertEquals(EVENT, types.get(0).getName());
        Assert.assertEquals(MARKET, types.get(1).getName());
        Assert.assertEquals(OUTCOME, types.get(2).getName());
    }

    @Test
    public void consumeXmlFile_getTypeElementsFromValidNames_isNotNull(){
        Element element = domParser.getTypeElement(EVENT);
        Assert.assertNotNull(element);

        element = domParser.getTypeElement(MARKET);
        Assert.assertNotNull(element);

        element = domParser.getTypeElement(OUTCOME);
        Assert.assertNotNull(element);
    }

    @Test
    public void consumeXmlFile_getTypeElementsFromName_isNull(){
        Element element = domParser.getTypeElement("Not-vaLiD-tYpE!");
        Assert.assertNull(element);
    }

    @Test
    public void consumeXmlFile_getTypeElementHeaderFromNameIsOk(){
        Element element = domParser.getTypeElement(EVENT);
        List<Element> elements =  element.getChildren();
        Assert.assertEquals(HEADER, elements.get(0).getName());

        element = domParser.getTypeElement(MARKET);
        elements =  element.getChildren();
        Assert.assertEquals(HEADER, elements.get(0).getName());

        element = domParser.getTypeElement(OUTCOME);
        elements =  element.getChildren();
        Assert.assertEquals(HEADER, elements.get(0).getName());
    }

    @Test
    public void consumeXmlFile_getTypeElementBodyFromNameIsOk(){
        Element element = domParser.getTypeElement(EVENT);
        List<Element> elements =  element.getChildren();
        Assert.assertEquals(BODY, elements.get(1).getName());

        element = domParser.getTypeElement(MARKET);
        elements =  element.getChildren();
        Assert.assertEquals(BODY, elements.get(1).getName());

        element = domParser.getTypeElement(OUTCOME);
        elements =  element.getChildren();
        Assert.assertEquals(BODY, elements.get(1).getName());
    }

    @Test
    public void consumeXmlFile_getHeaderContentFromPacketType_correctSize(){
        Element element = domParser.getTypeContent(EVENT, HEADER);
        Assert.assertEquals(4, element.getChildren().size());

        element = domParser.getTypeContent(MARKET, HEADER);
        Assert.assertEquals(4, element.getChildren().size());

        element = domParser.getTypeContent(OUTCOME, HEADER);
        Assert.assertEquals(4, element.getChildren().size());
    }

    @Test
    public void consumeXmlFile_getBodyContentFromPacketType_correctSize(){
        Element element = domParser.getTypeContent(EVENT, BODY);
        Assert.assertEquals(7, element.getChildren().size());

        element = domParser.getTypeContent(MARKET, BODY);
        Assert.assertEquals(5, element.getChildren().size());

        element = domParser.getTypeContent(OUTCOME, BODY);
        Assert.assertEquals(6, element.getChildren().size());
    }
}
