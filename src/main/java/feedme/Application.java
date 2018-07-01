package feedme;
;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        final int NUMBER_OF_PACKETS_TO_PROCESS = 500;
        final String TCP_ADDRESS = "tcp://*:5556";
        final String XML_API_FILE_NAME = "types.xml";

        JsonServer jsonServer = new JsonServer(XML_API_FILE_NAME);
        JsonConsumer jsonConsumer1 = new JsonConsumer("feedme", 1);
        JsonConsumer jsonConsumer2 = new JsonConsumer("feedme", 2);

        ExecutorService executor = Executors.newWorkStealingPool();

        Future<Boolean> serverFuture = executor.submit(()->{
            jsonServer.start(NUMBER_OF_PACKETS_TO_PROCESS, TCP_ADDRESS, 2);
            return true;
        });

        Future<Boolean> clientFuture1 = executor.submit(()->{
            jsonConsumer1.start(TCP_ADDRESS);
            return true;
        });

        Future<Boolean> clientFuture2 = executor.submit(()->{
            jsonConsumer2.start(TCP_ADDRESS);
            return true;
        });


        boolean ServerFinished = serverFuture.get();
        boolean Client1Finished = clientFuture1.get();
        boolean Client2Finished = clientFuture2.get();
    }
}
