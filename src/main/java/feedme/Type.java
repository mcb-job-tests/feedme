package feedme;

import lombok.Getter;
import org.jdom2.Element;

@Getter
class Type {
    private final String name;
    private final HeaderMap headerMap;
    private final BodyMap bodyMap;

    Type(final String name, final Element headerElement, final Element bodyElement){
        this.name = name;
        headerMap = new HeaderMap(headerElement);
        bodyMap = new BodyMap(bodyElement);
    }
}
