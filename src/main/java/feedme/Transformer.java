package feedme;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class Transformer {

    private final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    private Types types;

    Transformer(final String xmlApiFileName){
        types = new Types(xmlApiFileName);
    }


    ObjectNode createJsonObject(final Packet packet){
        String typeName = packet.getTypeName();
        ObjectNode json = jsonNodeFactory.objectNode();

        json.set(typeName, buildJsonType(packet));

        return json;
    }

    private ObjectNode buildJsonType(final Packet packet){
        ObjectNode rootNode = jsonNodeFactory.objectNode();

        rootNode.set("header", createJsonHeader(packet));
        rootNode.set("body", createJsonBody(packet));

        if (packet.getTypeName().equals("event")){
            rootNode.set("markets", jsonNodeFactory.arrayNode());
        }

        if (packet.getTypeName().equals("market")){
            rootNode.set("outcomes", jsonNodeFactory.arrayNode());
        }

        return rootNode;
    }

    private ObjectNode createJsonHeader(final Packet packet){
        Type type = types.getType(packet.getTypeName());

        return createJsonContent(packet, type.getHeaderMap());
    }

    private ObjectNode createJsonBody(final Packet packet){
        Type type = types.getType(packet.getTypeName());
        ObjectNode json = createJsonContent(packet, type.getBodyMap());

        return json;
    }

    private ObjectNode createJsonContent(final Packet packet, final ContentMap contentMap){
        String[] packetStrings = packet.getPacketStrings();
        ObjectNode json = jsonNodeFactory.objectNode();

        contentMap.forEach((index, field)-> addJsonFieldToBuilder(index, field, packetStrings, json));

        return json;
    }

    private void addJsonFieldToBuilder(final Integer index, final Field field, final String[] packetStrings, ObjectNode json){
        String key = field.name;
        String value = packetStrings[index];

        switch(field.datatype){
            case "integer":
                json.put(key, Long.parseLong(value));
                break;
            case "string":
                json.put(key, value);
                break;
            case "boolean":
                json.put(key, "1".equals(value));
                break;
            default:
                break;
        }
    }
}
