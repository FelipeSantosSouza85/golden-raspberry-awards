package br.com.outsera.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestProfile(ProducerAwardIntervalResourceTieTest.TieProfile.class)
class ProducerAwardIntervalResourceTieTest {

    private static final String ENDPOINT = "/v1/producers/award-intervals";

    @Test
    void should_returnAllTiedProducers_whenMinAndMaxIntervalsHaveTies() {
        Response response = given()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .extract().response();

        List<Map<String, Object>> minItems = response.jsonPath().getList("min");
        List<Map<String, Object>> maxItems = response.jsonPath().getList("max");

        assertNotNull(minItems, "lista 'min' nao deve ser nula");
        assertNotNull(maxItems, "lista 'max' nao deve ser nula");
        assertEquals(2, minItems.size(), "deve retornar todos os produtores empatados no menor intervalo");
        assertEquals(2, maxItems.size(), "deve retornar todos os produtores empatados no maior intervalo");

        assertContainsInterval(minItems, "Producer Min A", 1, 2000, 2001);
        assertContainsInterval(minItems, "Producer Min B", 1, 2010, 2011);
        assertContainsInterval(maxItems, "Producer Max A", 10, 1990, 2000);
        assertContainsInterval(maxItems, "Producer Max B", 10, 2005, 2015);
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

    public static final class TieProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("app.csv.file-name", "Movielist-tie-min-max.csv");
        }
    }
}
