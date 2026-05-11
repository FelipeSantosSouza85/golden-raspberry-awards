package br.com.outsera.api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
@TestProfile(ProducerAwardIntervalResourceSameYearTest.SameYearProfile.class)
class ProducerAwardIntervalResourceSameYearTest {

    private static final String ENDPOINT = "/v1/producers/award-intervals";

    @Test
    void should_notReturnZeroInterval_whenSameProducerWinsTwiceInSameYear() {
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
        assertFalse(hasInterval(minItems, 0), "lista 'min' nao deve conter interval 0");
        assertFalse(hasInterval(maxItems, 0), "lista 'max' nao deve conter interval 0");
        assertContainsInterval(minItems, "Producer Same Year", 20, 1990, 2010);
        assertContainsInterval(maxItems, "Producer Same Year", 20, 1990, 2010);
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

    private static boolean hasInterval(List<Map<String, Object>> items, int interval) {
        return items.stream()
                .anyMatch(item -> interval == intValue(item.get("interval")));
    }

    private static int intValue(Object value) {
        return ((Number) value).intValue();
    }

    public static final class SameYearProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("app.csv.file-name", "Movielist-same-producer-same-year.csv");
        }
    }
}
