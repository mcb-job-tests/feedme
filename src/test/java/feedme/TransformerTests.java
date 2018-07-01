package feedme;

/*import jdk.nashorn.internal.ir.ObjectNode;
import org.json.simple.JSONObject;*/
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class TransformerTests {

    String packet1 = "|1|create|event|1530208832360|fff91fc2-5661-465f-bb6f-ce6999582e7a|Football|Sky Bet Championship|\\|Wolves\\| vs \\|Sunderland\\||1530208835846|0|1|";
    String packet2 = "|39|create|event|1530208943500|ffd16cdf-e36e-4819-a1ab-fa4d021701b5|Football|Sky Bet Championship|\\|Hull\\| vs \\|Sunderland\\||1530208889687|0|1|";
    String packet3 = "|153|update|outcome|1530208983671|d7425f6f-1401-4ed6-996a-c89b0bb7d319|6b5a280a-8e92-4f15-9d69-b51f771fa9be|\\|Hull\\| -1|1/25|1|0|";
    String packet4 = "|232|update|outcome|1530209123408|a29b61fe-a70e-4694-bdf8-b0eee89d9ece|50861233-bd4b-4b5f-89e6-c30247e735f5|\\|Hull\\| +1|1/7|1|0|";
    String packet5 = "|311|update|outcome|1530209146444|ffda4e1f-0fa4-4da7-8fd7-b20f5f4995fc|186f67d6-9965-4bfb-8d37-136481514b25|\\|Carlisle\\| +1|3/1|1|0|";
    String packet6 = "|390|update|outcome|1530209169747|471b6d91-e964-47f6-97fc-69af0445170c|2974c121-87da-4fee-89a5-11df7292ed9f|\\|Colchester\\| +1|1/2|1|0|";
    String packet7 = "|268|create|market|1530209674774|fdf53291-3f3a-4dc2-adbb-ba8a7837e0c0|ec9a436d-fa23-4ea2-8a90-716d711ba5b9|Match Result|0|1|";
    String packet8 = "|263|create|market|1530209672296|fedb9608-aed7-43ae-9124-6f5d74c7c6b6|2e30bde0-9704-4ac0-8636-45c6c0bcbc64|Goal Handicap (+2)|0|1|";

    @Test
    public void packetTransform(){
        Transformer transformer = new Transformer("types.xml");
        Packet packet = new Packet(packet1);
        ObjectNode json = transformer.createJsonObject(packet);
        Assert.assertNotNull(json);
    }
}
