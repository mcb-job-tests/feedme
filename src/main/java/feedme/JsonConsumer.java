package feedme;

import com.mongodb.client.result.UpdateResult;
import org.zeromq.ZMQ;

import java.util.StringTokenizer;

class JsonConsumer {

    private static final int END_OF_STREAM = -1;
    private int zmqTopic;
    private NoSqlConnection feedmeDbConnection;

    JsonConsumer(String databaseName, int zmqTopic){
        this.zmqTopic = zmqTopic;
        feedmeDbConnection = new NoSqlConnection(databaseName);
    }

    void start(String tcpAddress) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.SUB);
        socket.connect(tcpAddress);

        socket.subscribe(Integer.toString(zmqTopic).getBytes());
        socket.subscribe(Integer.toString(END_OF_STREAM).getBytes());

        StringTokenizer stringTokenizer;

        while ( (stringTokenizer = getMessageTockenizer(socket)) != null) {
            String typeName = stringTokenizer.nextToken();
            String operationName = stringTokenizer.nextToken();
            StringBuilder jsonStringBuilder = new StringBuilder(stringTokenizer.nextToken());

            while (stringTokenizer.hasMoreElements()) {
                jsonStringBuilder.append(stringTokenizer.nextToken());
            }
            writeJsonNoSqlDatabase(jsonStringBuilder.toString(), typeName, operationName);
            System.out.println(jsonStringBuilder);
        }
        socket.close();
        context.term();
    }

    StringTokenizer getMessageTockenizer(ZMQ.Socket socket){
        //  Use trim to remove the tailing '0' character
        String messageString = socket.recvStr(0).trim();
        StringTokenizer stringTokenizer = new StringTokenizer(messageString, " ");

        if (Integer.valueOf(stringTokenizer.nextToken()) == END_OF_STREAM){
            stringTokenizer = null;
        }

        return stringTokenizer;
    }

     UpdateResult writeJsonNoSqlDatabase(String jsonString, String typeName, String operationName) {
         UpdateResult updateResult = null;
         switch (typeName) {
             case "event":
                 switch (operationName) {
                     case "create":
                         feedmeDbConnection.createEventFixture(jsonString);
                         break;
                     case "update":
                         updateResult = feedmeDbConnection.updateEventFixture(jsonString);
                         break;
                     default:
                         break;
                 }
                 break;
             case "market":
                 switch (operationName) {
                     case "create":
                         updateResult = feedmeDbConnection.createMarketInEventFixture(jsonString);
                         break;
                     case "update":
                         updateResult = feedmeDbConnection.updateMarketInEventFixture(jsonString);
                         break;
                     default:
                         break;
                 }
                 break;
             case "outcome":
                 switch (operationName) {
                     case "create":
                         updateResult = feedmeDbConnection.createOutcomeInMarket(jsonString);
                         break;
                     case "update":
                         updateResult = feedmeDbConnection.updateOutcomeInMarket(jsonString);
                         break;
                     default:
                         break;
                 }
                 break;
             default:
                 break;
         }
         return updateResult;
     }
}
