package feedme;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class Transformer {

    private final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    private Types types;

    Transformer(final String xmlApiFileName){
        types = new Types(xmlApiFileName);
    }


    public ObjectNode createJsonObject(final Packet packet){
        ObjectNode rootNode = jsonNodeFactory.objectNode();

        createJsonFromHeader(packet, rootNode);
        createJsonFromBody(packet, rootNode);

        if (packet.getTypeName().equals("event")){
            rootNode.set("markets", jsonNodeFactory.arrayNode());
        }

        if (packet.getTypeName().equals("market")){
            rootNode.set("outcomes", jsonNodeFactory.arrayNode());
        }

        return rootNode;
    }

    private void createJsonFromHeader(final Packet packet, ObjectNode json){
        Type type = types.getType(packet.getTypeName());
        createJsonContent(packet, type.getHeaderMap(), json);
    }

    private void createJsonFromBody(final Packet packet, ObjectNode json){
        Type type = types.getType(packet.getTypeName());
        createJsonContent(packet, type.getBodyMap(), json);
    }

    private void createJsonContent(final Packet packet, final ContentMap contentMap, ObjectNode json){
        String[] packetStrings = packet.getPacketStrings();
        contentMap.forEach((index, field)-> addJsonFieldToBuilder(index, field, packetStrings, json));
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
