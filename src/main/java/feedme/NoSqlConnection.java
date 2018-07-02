package feedme;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.addToSet;
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
        Document event = Document.parse( fixture );
        String eventId = event.getString("eventId");

        return fixturesCollection.replaceOne(
                eq("eventId", eventId),
                event,
                new UpdateOptions().upsert(true).bypassDocumentValidation(true)
        );
    }

    void updateEventFixture(ObjectNode fixture){
        updateEventFixture(fixture.toString());
    }

    UpdateResult updateEventFixture(String fixture){
        Document event = Document.parse( fixture );
        String eventId = event.getString("eventId");

        return fixturesCollection.updateOne(
                eq("eventId", eventId),
                event,
                new UpdateOptions().arrayFilters(
                        Collections.singletonList(
                                and( eq("eventId", eventId),
                                     lt("msgId", event.getInteger("msgId")))
                        )
                )
        );
    }

    UpdateResult createMarketInEventFixture(ObjectNode json){
        return createMarketInEventFixture(json.toString());
    }

    UpdateResult createMarketInEventFixture(String jsonString){
        Document market = Document.parse( jsonString );
        String eventId = market.getString("eventId");

        return fixturesCollection.updateOne(
                eq("eventId", eventId),
                addToSet("markets", market)
        );
    }

    UpdateResult updateMarketInEventFixture(ObjectNode json){
        return updateMarketInEventFixture(json.toString());
    }

    UpdateResult updateMarketInEventFixture(String jsonString){
        Document market = Document.parse( jsonString );
        String marketId = market.getString("marketId");
        int msgId = market.getInteger("msgId");

        return fixturesCollection.updateOne(
                eq("markets.marketId", marketId),
                set("markets.$[m]", market),
                new UpdateOptions().arrayFilters(
                        Collections.singletonList(
                                and( eq("m.marketId", marketId),
                                     lt("m.msgId", msgId)
                                )
                        )
                )
        );
    }

    UpdateResult createOutcomeInMarket(ObjectNode json){
        return createOutcomeInMarket(json.toString());
    }

    UpdateResult createOutcomeInMarket(String jsonString){
        Document outcome = Document.parse( jsonString );
        String marketId = outcome.getString("marketId");

        return fixturesCollection.updateOne(
                eq("markets.marketId", marketId),
                addToSet("markets.$.outcomes", outcome)
        );
    }

    UpdateResult updateOutcomeInMarket(ObjectNode json){
        return updateOutcomeInMarket(json.toString());
    }

    UpdateResult updateOutcomeInMarket(String jsonString){
        Document outcome = Document.parse( jsonString );
        String outcomeId = outcome.getString("outcomeId");
        int msgId = outcome.getInteger("msgId");

        UpdateResult updateResult = fixturesCollection.updateOne(
                eq("markets.outcomes.outcomeId", outcomeId),
                set("markets.$.outcomes.$[o]", outcome),
                new UpdateOptions().arrayFilters(
                        Arrays.asList(
                                and( eq("o.outcomeId", outcomeId),
                                     lt("o.msgId", msgId)
                                )
                        )
                    )
        );
        return updateResult;
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
        Long count = fixturesCollection.count(Filters.eq("eventId", eventId));
        return !count.equals(0L);
    }
}
