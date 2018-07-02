package feedme;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.zeromq.ZMQ;

import java.io.IOException;

class JsonServer {

    private final static int IO_THREADS = 1;
    private final static int SOCKET_TYPE =  ZMQ.PUB;
    private final static int STOP_TOPIC = -1;
    private final Transformer transformer;

    JsonServer(String xmlApiFileName){
        transformer = new Transformer(xmlApiFileName);
    }

    void start(final int numberOfPacketsToProcess, final String tcpAddress, final int numberOfTopics) throws InterruptedException, IOException {
        ZMQ.Context context = ZMQ.context(IO_THREADS);
        ZMQ.Socket publisher = context.socket(SOCKET_TYPE);
        publisher.bind(tcpAddress);
        FeedMeClient feedMeClient = new FeedMeClient();
        feedMeClient.createSocketConnection("localhost", 8282);

        int count = 0;
        int topic = 1;

        while ( count < numberOfPacketsToProcess && !Thread.currentThread().isInterrupted() ) {
            Packet packet = new Packet(feedMeClient.sendMessage(""));
            ObjectNode json = transformer.createJsonObject(packet);
            String typeName = packet.getTypeName();
            String operation = packet.getOperationName();

            String message = String.format("%d %s %s %s", topic, typeName, operation, json.toString());
            publisher.send(message, 0);

            if (topic == numberOfTopics) {
                topic = 1;
            } else {
                topic++;
            }

            count++;
        }

        // Notify Consumers to Stop
        String message = String.format("%d %s %s %s", STOP_TOPIC, "STOP", "STOP", "STOP");
        publisher.send(message, 0);

        Thread.sleep(1000);
        publisher.close ();
        context.term ();
    }

}
