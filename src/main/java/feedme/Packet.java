package feedme;

import java.util.Arrays;
import java.util.regex.Pattern;

class Packet {
    final static String PIPE_DELIMINATOR = "|";
    final static String REGEX = "(?<!\\\\)" + Pattern.quote(PIPE_DELIMINATOR);
    final static int OPERATION_NAME_INDEX = 1;
    final static int TYPE_NAME_INDEX = 2;

    private String[] packetStrings;

    Packet(final String packet){
        System.out.println(packet);
        String[] rawPacketStrings = packet.split(REGEX);

        // remove first whitespace element
        packetStrings = Arrays.copyOfRange(rawPacketStrings, 1, rawPacketStrings.length);
    }

    String[] getPacketStrings() {
        return packetStrings;
    }

    String getTypeName(){
        return packetStrings[TYPE_NAME_INDEX];
    }

    String getOperationName(){
        return packetStrings[OPERATION_NAME_INDEX];
    }
}
