package feedme;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

// Run 'docker-compose up' in project root directory, to start feed-me stream service
public class FeedMeClientTests {

    @Test
    public void openSocketConnectionOK() throws IOException, InterruptedException {
        FeedMeClient client = new FeedMeClient();
        client.createSocketConnection("localhost", 8282);
        Assert.assertNotNull(client);
        Assert.assertTrue(client.getClientSocket().isConnected());

        for (int i = 0; i < 500; i++) {
            Packet packet = new Packet(client.sendMessage(""));
            System.out.println(packet.toString());
        }

        client.stopConnection();
    }

}
