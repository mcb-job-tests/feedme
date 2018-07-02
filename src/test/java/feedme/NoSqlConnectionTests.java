package feedme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bson.Document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class NoSqlConnectionTests {
    private final Transformer transformer = new Transformer("types.xml");
    private NoSqlConnection feedmeDbConnection = new NoSqlConnection("feedme");

    private String packet1 = "|1|create|event|1530208832360|fff91fc2-5661-465f-bb6f-ce6999582e7a|Football|Sky Bet Championship|\\|Wolves\\| vs \\|Sunderland\\||1530208835846|0|1|";
    private String packet2 = "|39|create|event|1530208943500|ffd16cdf-e36e-4819-a1ab-fa4d021701b5|Football|Sky Bet Championship|\\|Hull\\| vs \\|Sunderland\\||1530208889687|0|1|";
    private String packet5 = "|311|update|outcome|1530209146444|ffda4e1f-0fa4-4da7-8fd7-b20f5f4995fc|186f67d6-9965-4bfb-8d37-136481514b25|\\|Carlisle\\| +1|3/1|1|0|";
    private String packet6 = "|390|update|outcome|1530209169747|471b6d91-e964-47f6-97fc-69af0445170c|2974c121-87da-4fee-89a5-11df7292ed9f|\\|Colchester\\| +1|1/2|1|0|";
    private String packet7 = "|268|create|market|1530209674774|ffd16cdf-e36e-4819-a1ab-fa4d021701b5|ec9a436d-fa23-4ea2-8a90-716d711ba5b9|Match Result|0|1|";
    private String packet8 = "|263|create|market|1530209672296|fedb9608-aed7-43ae-9124-6f5d74c7c6b6|2e30bde0-9704-4ac0-8636-45c6c0bcbc64|Goal Handicap (+2)|0|1|";
    private String packet9 = "|444|create|event|1530208943500|ffd16cdf-e36e-4819-a1ab-fa4d021701b5|Football|Awesome Sky Bet Championship|\\|Hull\\| vs \\|Sunderland\\||1530208889687|0|1|";
    private String packet13 = "|333|update|outcome|1530208983671|ec9a436d-fa23-4ea2-8a90-716d711ba5b9|6b5a280a-8e92-4f15-9d69-b51f771fa9be|\\|Hull\\| -1|1/25|1|0|";
    private String packet14 = "|377|update|outcome|1530209123408|ec9a436d-fa23-4ea2-8a90-716d711ba5b9|50861233-bd4b-4b5f-89e6-c30247e735f5|\\|Hull\\| +1|1/7|1|0|";

    @Before
    public void clearFixturesCollection(){
        feedmeDbConnection.dropFixturesCollection();
    }

    @Test
    public void checkDatabaseConnectionIsOpen(){
        Assert.assertNotNull(feedmeDbConnection);
    }

    @Test
    public void insertCreateEventDocumentIntoDatabaseCollection_FindWithInValidEventID_ReturnsNullDocument() {
        Assert.assertEquals(0, feedmeDbConnection.getFixturesCollection().count());
        feedmeDbConnection.createEventFixture(transformer.createJsonObject( new Packet(packet1) ));
        Assert.assertEquals(1, feedmeDbConnection.getFixturesCollection().count());
        feedmeDbConnection.createEventFixture(transformer.createJsonObject( new Packet(packet2) ));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        Document document = feedmeDbConnection.getFixturesCollection().find(eq("eventId", "not-valid-event-id!")).first();
        Assert.assertNull(document);
    }

    @Test
    public void insertEventDocumentIntoEmptyDatabaseCollection_FindWithValidEventID_ReturnsCorrectDocument() {
        Assert.assertEquals(0, feedmeDbConnection.getFixturesCollection().count());
        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet1)));
        Assert.assertEquals(1, feedmeDbConnection.getFixturesCollection().count());
        feedmeDbConnection.createEventFixture(transformer.createJsonObject( new Packet(packet2) ));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        // get Document created from packet2
        Document event = feedmeDbConnection.getFixturesCollection().find( eq("eventId", "ffd16cdf-e36e-4819-a1ab-fa4d021701b5")).first();
        Assert.assertNotNull(event);

        int msgId = event.getInteger("msgId");
        Assert.assertEquals(39, msgId);

        String type = event.getString("type");
        Assert.assertEquals("event", type);

        String operation = event.getString("operation");
        Assert.assertEquals("create", operation);

        long timestamp = event.getLong("timestamp");
        Assert.assertEquals(1530208943500L, timestamp);

        String eventId = event.getString("eventId");
        Assert.assertEquals("ffd16cdf-e36e-4819-a1ab-fa4d021701b5", eventId);

        String subCategory = event.getString("subCategory");
        Assert.assertEquals("Sky Bet Championship", subCategory);

        String name = event.getString("name");
        Assert.assertEquals("\\|Hull\\| vs \\|Sunderland\\|", name);

        long startTinme = event.getLong("startTime");
        Assert.assertEquals(1530208889687L, startTinme);

        String category = event.getString("category");
        Assert.assertEquals("Football", category);

        boolean displayed = event.getBoolean("displayed");
        Assert.assertFalse(displayed);

        boolean suspended = event.getBoolean("suspended");
        Assert.assertTrue(suspended);

        System.out.println(event);
        prettyPrintDocument(event);

    }

    @Test
    public void addMarketToFixtureDocument_FindWithValidMarketID_ReturnsUpdatedDocument() {
        Assert.assertEquals(0, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet1)));
        Assert.assertEquals(1, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet2)));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(packet7)));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());


        // get Document created from packet2
        Document event = feedmeDbConnection.getFixturesCollection().find( eq("eventId", "ffd16cdf-e36e-4819-a1ab-fa4d021701b5")).first();
        Assert.assertNotNull(event);

        String eventId = event.getString("eventId");
        Assert.assertEquals("ffd16cdf-e36e-4819-a1ab-fa4d021701b5", eventId);

        System.out.println(event);
        prettyPrintDocument(event);

    }

    @Test
    public void replaceEventFixtureDocument_FindWithValidEventID_ReturnsReplacedDocument_WithEmptyMarkets() {
        Assert.assertEquals(0, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet1)));
        Assert.assertEquals(1, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet2)));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(packet7)));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet9)));
        Assert.assertEquals(2, feedmeDbConnection.getFixturesCollection().count());

        Document document = feedmeDbConnection.getFixturesCollection().find( eq("markets.marketId", "ec9a436d-fa23-4ea2-8a90-716d711ba5b9")).first();
        Assert.assertNull(document);
    }

    @Test
    public void addOutcomeToAssociatedMarketInEventFixtureDocument_FindWithValidOutcomeID_ReturnsUpdatedDocument() {

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet1)));
        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(packet2)));
        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(packet7)));
        feedmeDbConnection.createOutcomeInMarket(transformer.createJsonObject(new Packet(packet13)));
        feedmeDbConnection.createOutcomeInMarket(transformer.createJsonObject(new Packet(packet14)));

        Document document = feedmeDbConnection.getFixturesCollection().find( eq("markets.outcomes.outcomeId", "6b5a280a-8e92-4f15-9d69-b51f771fa9be")).first();
        Assert.assertNotNull(document);

        System.out.println(document);
        prettyPrintDocument(document);
    }

    @Test public void updateEventFixtureDocument_FindWithValidEventId_ReturnsUpdatedDocument(){
        String createEventPacket = "|39|create|event|1530390471900|fda2d9b3-7690-4d53-b86c-17915dc2784b|Football|Premier League|\\|Arsenal\\| vs \\|Manchester City\\||1530390482506|0|1|";
        String updateEventPacket = "|191|update|event|1530390482510|fda2d9b3-7690-4d53-b86c-17915dc2784b|Football|Premier League|\\|Arsenal\\| vs \\|Manchester City\\||1530390482506|1|0|";

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(createEventPacket)));

        Document event = feedmeDbConnection.getFixturesCollection().find( eq("eventId", "fda2d9b3-7690-4d53-b86c-17915dc2784b")).first();

        int msgId = event.getInteger("msgId");
        boolean displayed = event.getBoolean("displayed");
        boolean suspended = event.getBoolean("suspended");

        Assert.assertEquals(39, msgId);
        Assert.assertFalse(displayed);
        Assert.assertTrue(suspended);

        feedmeDbConnection.updateEventFixture(transformer.createJsonObject(new Packet(updateEventPacket)));

        event = feedmeDbConnection.getFixturesCollection().find( eq("eventId", "fda2d9b3-7690-4d53-b86c-17915dc2784b")).first();

        msgId = event.getInteger("msgId");
        displayed = event.getBoolean("displayed");
        suspended = event.getBoolean("suspended");

        Assert.assertEquals(191, msgId);
        Assert.assertTrue(displayed);
        Assert.assertFalse(suspended);
    }

    @Test
    public void updateEventFixtureOperations_preservesOrder(){
        String createEventPacket = "|77|create|event|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|Football|Sky Bet Championship|\\|Cardiff\\| vs \\|Bristol City\\||1530405913833|0|1|";
        String updateEventPacket = "|377|update|event|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|Football|Sky Bet Championship|\\|Cardiff\\| vs \\|Bristol City\\||1530405913833|1|1|";
        String updateEventOldPacket = "|376|update|event|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|Football|Sky Bet Championship|\\|Cardiff\\| vs \\|Bristol City\\||1530405913833|0|1|";

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(createEventPacket)));
        feedmeDbConnection.updateEventFixture(transformer.createJsonObject(new Packet(updateEventPacket)));
        feedmeDbConnection.updateEventFixture(transformer.createJsonObject(new Packet(updateEventOldPacket)));

        Document event = feedmeDbConnection.getFixturesCollection().find(eq("eventId","fdc72db0-10a9-45d4-b30a-797609b046bd" )).first();
        Assert.assertNotNull(event);
        int msgId = event.getInteger("msgId");
        Assert.assertEquals(377, msgId);
    }

    @Test
    public void updateMarketOperations_preservesOrder(){
        String createEventPacket = "|77|create|event|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|Football|Sky Bet Championship|\\|Cardiff\\| vs \\|Bristol City\\||1530405913833|0|1|";
        String createMarketPacket = "|82|create|market|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|76a08689-c58c-4f61-b969-d6ac1641a4a7|Both Teams To Score|0|1|";
        String createMarketPacket2 = "|87|create|market|1530209674774|fdc72db0-10a9-45d4-b30a-797609b046bd|ec9a436d-fa23-4ea2-8a90-716d711ba5b9|Match Result|0|1|";
        String createMarketPacket3 = "|93|create|market|1530209672296|fdc72db0-10a9-45d4-b30a-797609b046bd|2e30bde0-9704-4ac0-8636-45c6c0bcbc64|Goal Handicap (+2)|0|1|";
        String updateMarketPacket = "|298|update|market|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|76a08689-c58c-4f61-b969-d6ac1641a4a7|Both Teams To Score|0|1|";
        String updateMarketAgainPacket = "|299|update|market|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|76a08689-c58c-4f61-b969-d6ac1641a4a7|Both Teams To Score|1|0|";
        String updateMarketOldPacket = "|297|update|market|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|76a08689-c58c-4f61-b969-d6ac1641a4a7|Both Teams To Score|1|1|";

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(createEventPacket)));
        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(createMarketPacket)));
        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(createMarketPacket2)));
        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(createMarketPacket3)));
        feedmeDbConnection.updateMarketInEventFixture(transformer.createJsonObject(new Packet(updateMarketPacket)));
        feedmeDbConnection.updateMarketInEventFixture(transformer.createJsonObject(new Packet(updateMarketAgainPacket)));
        feedmeDbConnection.updateMarketInEventFixture(transformer.createJsonObject(new Packet(updateMarketOldPacket)));

        Document event = feedmeDbConnection.getFixturesCollection()
                .find(eq("markets.marketId","76a08689-c58c-4f61-b969-d6ac1641a4a7" )).first();
        Assert.assertNotNull(event);

        List<Document> markets =  (ArrayList<Document>) event.get("markets");
        Assert.assertNotNull(markets);

        int msgId = -1;
        for(Document market : markets){
            if (market.get("marketId").equals("76a08689-c58c-4f61-b969-d6ac1641a4a7")){
                msgId = market.getInteger("msgId");
            }
        }

        Assert.assertEquals(299, msgId);
    }

    @Test
    public void updateOutcomeOperations_preservcesOrder(){
        String createEventPacket = "|77|create|event|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|Football|Sky Bet Championship|\\|Cardiff\\| vs \\|Bristol City\\||1530405913833|0|1|";
        String createMarketPacket = "|82|create|market|1530405905035|fdc72db0-10a9-45d4-b30a-797609b046bd|76a08689-c58c-4f61-b969-d6ac1641a4a7|Both Teams To Score|0|1|";
        String createOutcomePacket = "|100|create|outcome|1530208983671|76a08689-c58c-4f61-b969-d6ac1641a4a7|6b5a280a-8e92-4f15-9d69-b51f771fa9be|\\|Cardiff\\| -1|1/25|1|0|";
        String createOutcomePacket2 = "|311|create|outcome|1530209146444|76a08689-c58c-4f61-b969-d6ac1641a4a7|186f67d6-9965-4bfb-8d37-136481514b25|\\|Bristol City\\| +1|3/1|1|0|";
        String updateOutcomePacket3 = "|312|update|outcome|1530209146444|76a08689-c58c-4f61-b969-d6ac1641a4a7|186f67d6-9965-4bfb-8d37-136481514b25|\\|Bristol City\\| +1|20/1|1|0|";
        String updateOutcomePacket4 = "|310|update|outcome|1530209146444|76a08689-c58c-4f61-b969-d6ac1641a4a7|186f67d6-9965-4bfb-8d37-136481514b25|\\|Bristol City\\| +1|7/1|1|0|";

        feedmeDbConnection.createEventFixture(transformer.createJsonObject(new Packet(createEventPacket)));
        feedmeDbConnection.createMarketInEventFixture(transformer.createJsonObject(new Packet(createMarketPacket)));
        feedmeDbConnection.createOutcomeInMarket(transformer.createJsonObject(new Packet(createOutcomePacket)));
        feedmeDbConnection.createOutcomeInMarket(transformer.createJsonObject(new Packet(createOutcomePacket2)));
        feedmeDbConnection.updateOutcomeInMarket(transformer.createJsonObject(new Packet(updateOutcomePacket3)));
        feedmeDbConnection.updateOutcomeInMarket(transformer.createJsonObject(new Packet(updateOutcomePacket4)));

        Document event = feedmeDbConnection.getFixturesCollection()
                .find(eq("markets.outcomes.outcomeId","186f67d6-9965-4bfb-8d37-136481514b25" )).first();

        Assert.assertNotNull(event);

        List<Document> markets =  (ArrayList<Document>) event.get("markets");
        Assert.assertNotNull(markets);

        Document outcome = null;
        for(Document market : markets){
            if (market.get("marketId").equals("76a08689-c58c-4f61-b969-d6ac1641a4a7")){
                List<Document> outcomes = (ArrayList<Document>) market.get("outcomes");
                for(Document document : outcomes){
                    if ("186f67d6-9965-4bfb-8d37-136481514b25".equals(document.get("outcomeId"))){
                        outcome = document;
                        break;
                    }
                }
            }
            if (outcome != null){
                break;
            }
        }

        Assert.assertNotNull(outcome);
        int msgId = outcome.getInteger("msgId");
        Assert.assertEquals(312, msgId);

    }

    private void prettyPrintDocument(Document document){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(document.toJson());
        String prettyJsonString = gson.toJson(jsonElement);
        System.out.println(prettyJsonString);
    }
}
