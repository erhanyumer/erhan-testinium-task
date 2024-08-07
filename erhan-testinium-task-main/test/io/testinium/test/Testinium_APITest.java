package io.codeshake.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class TrelloApiTests {
    private String apiKey = "YOUR_API_KEY"; //401 at https://trello.com/app-key
    private String token = "YOUR_TOKEN";
    private String boardId;
    private String cardId1;
    private String cardId2;

    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "https://api.trello.com/1";
    }

    @Test
    public void testTrelloAPI() {
        // Create a board
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Test Board");
        queryParams.put("key", apiKey);
        queryParams.put("token", token);

        Response response = given()
                .queryParams(queryParams)
                .post("/boards");

        Assert.assertEquals(response.getStatusCode(), 200);
        boardId = response.jsonPath().getString("id");

        // Create two cards
        queryParams.put("idList", getFirstListId(boardId));
        queryParams.put("name", "Test Card 1");

        response = given()
                .queryParams(queryParams)
                .post("/cards");

        Assert.assertEquals(response.getStatusCode(), 200);
        cardId1 = response.jsonPath().getString("id");

        queryParams.put("name", "Test Card 2");

        response = given()
                .queryParams(queryParams)
                .post("/cards");

        Assert.assertEquals(response.getStatusCode(), 200);
        cardId2 = response.jsonPath().getString("id");

        // Update one of the two cards randomly
        String cardIdToUpdate = new Random().nextBoolean() ? cardId1 : cardId2;
        queryParams.put("name", "Updated Test Card");

        response = given()
                .pathParam("id", cardIdToUpdate)
                .queryParams(queryParams)
                .put("/cards/{id}");

        Assert.assertEquals(response.getStatusCode(), 200);

        // Delete the cards
        deleteCard(cardId1);
        deleteCard(cardId2);

        // Delete the board
        deleteBoard(boardId);
    }

    @AfterTest
    public void tearDown() {
        // Clean up in case of test failures
        if (cardId1 != null) deleteCard(cardId1);
        if (cardId2 != null) deleteCard(cardId2);
        if (boardId != null) deleteBoard(boardId);
    }

    private String getFirstListId(String boardId) {
        Response response = given()
                .pathParam("id", boardId)
                .queryParam("key", apiKey)
                .queryParam("token", token)
                .get("/boards/{id}/lists");

        Assert.assertEquals(response.getStatusCode(), 200);
        return response.jsonPath().getString("id[0]");
    }

    private void deleteCard(String cardId) {
        if (cardId != null) {
            Response response = given()
                    .pathParam("id", cardId)
                    .queryParam("key", apiKey)
                    .queryParam("token", token)
                    .delete("/cards/{id}");

            Assert.assertEquals(response.getStatusCode(), 200);
        }
    }

    private void deleteBoard(String boardId) {
        if (boardId != null) {
            Response response = given()
                    .pathParam("id", boardId)
                    .queryParam("key", apiKey)
                    .queryParam("token", token)
                    .delete("/boards/{id}");

            Assert.assertEquals(response.getStatusCode(), 200);
        }
    }
}
