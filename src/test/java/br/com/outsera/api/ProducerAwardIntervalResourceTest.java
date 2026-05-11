package br.com.outsera.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
class ProducerAwardIntervalResourceTest {

    private static final String ENDPOINT = "/v1/producers/award-intervals";

    @Test
    void should_returnExpectedMinAndMaxProducersForDefaultCsv() {
        given()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("min", hasSize(1))
                .body("min[0].producer", equalTo("Joel Silver"))
                .body("min[0].interval", equalTo(1))
                .body("min[0].previousWin", equalTo(1990))
                .body("min[0].followingWin", equalTo(1991))
                .body("max", hasSize(1))
                .body("max[0].producer", equalTo("Matthew Vaughn"))
                .body("max[0].interval", equalTo(13))
                .body("max[0].previousWin", equalTo(2002))
                .body("max[0].followingWin", equalTo(2015));
    }

    @Test
    void should_calculateProducerInterval_whenWinnerLineHasMultipleProducersSeparatedByAnd() {
        Response response = given()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .extract().response();

        List<Map<String, Object>> minItems = response.jsonPath().getList("min");

        assertNotNull(minItems, "lista 'min' nao deve ser nula");
        assertContainsInterval(minItems, "Joel Silver", 1, 1990, 1991);
    }

    @Test
    void should_shareSameIntervalAcrossAllMinAndMaxItems() {
        Response response = given()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .extract().response();

        JsonPath json = response.jsonPath();
        List<Map<String, Object>> minItems = json.getList("min");
        List<Map<String, Object>> maxItems = json.getList("max");

        assertNotNull(minItems, "lista 'min' nao deve ser nula");
        assertNotNull(maxItems, "lista 'max' nao deve ser nula");
        assertFalse(minItems.isEmpty(), "lista 'min' nao deve estar vazia para o CSV padrao");
        assertFalse(maxItems.isEmpty(), "lista 'max' nao deve estar vazia para o CSV padrao");

        int minInterval = intValue(minItems.get(0).get("interval"));
        int maxInterval = intValue(maxItems.get(0).get("interval"));

        assertTrue(minInterval <= maxInterval, "interval de 'min' deve ser <= interval de 'max'");

        assertIntervalInvariants(minItems, minInterval);
        assertIntervalInvariants(maxItems, maxInterval);
    }

    private static void assertIntervalInvariants(List<Map<String, Object>> items, int expectedInterval) {
        for (Map<String, Object> item : items) {
            String producer = (String) item.get("producer");
            int interval = intValue(item.get("interval"));
            int previousWin = intValue(item.get("previousWin"));
            int followingWin = intValue(item.get("followingWin"));

            assertNotNull(producer, "producer nao deve ser nulo");
            assertFalse(producer.isBlank(), "producer nao deve estar em branco");
            assertEquals(expectedInterval, interval, "todos os itens devem compartilhar o mesmo interval");
            assertTrue(previousWin < followingWin, "previousWin deve ser < followingWin");
            assertEquals(interval, followingWin - previousWin, "interval deve ser igual a (followingWin - previousWin)");
        }
    }

    private static void assertContainsInterval(
            List<Map<String, Object>> items,
            String expectedProducer,
            int expectedInterval,
            int expectedPreviousWin,
            int expectedFollowingWin
    ) {
        boolean found = items.stream()
                .anyMatch(item -> expectedProducer.equals(item.get("producer"))
                        && expectedInterval == intValue(item.get("interval"))
                        && expectedPreviousWin == intValue(item.get("previousWin"))
                        && expectedFollowingWin == intValue(item.get("followingWin")));

        assertTrue(found, () -> "intervalo esperado nao encontrado para o produtor: " + expectedProducer);
    }

    private static int intValue(Object value) {
        return ((Number) value).intValue();
    }
}
