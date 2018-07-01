package feedme;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class NoSqlConnection {
    private static final String HOST_URL = "localhost";
    private static final int PORT = 27017;
    private static final String FIXTURES = "fixtures";

    private MongoClient client = new MongoClient(HOST_URL, PORT);
    private MongoDatabase database;
    private MongoCollection<Document> fixturesCollection;

    NoSqlConnection(final String dataBaseName){
        database = client.getDatabase(dataBaseName);
        fixturesCollection = database.getCollection(FIXTURES);
    }

    void createEventFixture(ObjectNode fixture){
        createEventFixture(fixture.toString());
    }

    UpdateResult createEventFixture(String fixture){
        Document document = Document.parse( fixture );
        String eventId = getEventIdFromCollectionRootDocument(document);
        return fixturesCollection.replaceOne(
                eq("event.body.eventId", eventId),
                document,
                new UpdateOptions().upsert(true).bypassDocumentValidation(true));
    }

    void updateEventFixture(ObjectNode fixture){
        updateEventFixture(fixture.toString());
    }

    UpdateResult updateEventFixture(String fixture){
        Document document = Document.parse( fixture );
        Document event = document.get("event", Document.class);
        Document body = event.get("body", Document.class);
        Document header = event.get("header", Document.class);
        String eventId = body.getString("eventId");

        return fixturesCollection.updateOne(
                and( eq("event.body.eventId", eventId), lt("event.header.msgId", header.getInteger("msgId"))),
                combine( set("event.header", header), set("event.body", body) ) );
    }

    UpdateResult createMarketInEventFixture(ObjectNode json){
        return createMarketInEventFixture(json.toString());
    }

    UpdateResult createMarketInEventFixture(String jsonString){
        Document document = Document.parse( jsonString );
        Document market = document.get("market", Document.class);
        Document body = market.get("body", Document.class);
        String eventId = body.getString("eventId");

        return fixturesCollection.updateOne(
                eq("event.body.eventId", eventId),
                addToSet("event.markets", document));
    }

    UpdateResult updateMarketInEventFixture(ObjectNode json){
        return updateMarketInEventFixture(json.toString());
    }

    UpdateResult updateMarketInEventFixture(String jsonString){
        Document document = Document.parse( jsonString );
        Document market = document.get("market", Document.class);
        Document header = market.get("header", Document.class);
        Document body = market.get("body", Document.class);
        String marketId = body.getString("marketId");

        return fixturesCollection.updateOne(
                and( eq("event.markets.market.body.marketId", marketId),
                     lt("event.markets.market.header.msgId", header.getInteger("msgId"))),
                set("event.markets.$.market", market));
    }

    UpdateResult createOutcomeInMarket(ObjectNode json){
        return createOutcomeInMarket(json.toString());
    }

    UpdateResult createOutcomeInMarket(String jsonString){
        Document document = Document.parse( jsonString );
        Document outcome = document.get("outcome", Document.class);
        Document body = outcome.get("body", Document.class);
        String marketId = body.getString("marketId");

        return fixturesCollection.updateOne(
                eq("event.markets.market.body.marketId", marketId),
                addToSet("event.markets.$.market.outcomes", document));
    }

    UpdateResult updateOutcomeInMarket(ObjectNode json){
        return updateOutcomeInMarket(json.toString());
    }

    UpdateResult updateOutcomeInMarket(String jsonString){
        Document document = Document.parse( jsonString );
        Document outcome = document.get("outcome", Document.class);
        Document body = outcome.get("body", Document.class);
        String outcomeId = body.getString("outcomeId");
        String marketId = body.getString("marketId");

        return fixturesCollection.updateOne(
                eq("event.markets.market.body.marketId", marketId),
                set("event.markets.$.market.outcomes.$[o]", document),
                new UpdateOptions().arrayFilters(
                        Collections.singletonList(
                                Filters.eq("o.outcome.body.outcomeId", outcomeId))));

    }

    private String getEventIdFromCollectionRootDocument(Document document){
        Document event = document.get("event", Document.class);
        Document body = event.get("body", Document.class);

        return body.getString("eventId");
    }

    MongoCollection<Document> getFixturesCollection() {
        return fixturesCollection;
    }

    void dropFixturesCollection(){
        fixturesCollection.drop();
    }

    public boolean collectionExists(final String collectionName) {
        return database.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
    }

    boolean isEventDocumentExists(String eventId){
        Long count = fixturesCollection.count(Filters.eq("event.body.eventId", eventId));
        return !count.equals(0L);
    }
}
