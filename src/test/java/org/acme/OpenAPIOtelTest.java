package org.acme;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusTest
public class OpenAPIOtelTest {

    @BeforeEach
    void resetTraces() {
        given().get("/reset").then().statusCode(HttpStatus.SC_OK);
        await().atMost(5, SECONDS).until(() -> getSpans().size() == 0);
    }

    @Test
    void openAPINotTraced() throws InterruptedException {
        endpointNotTraced("/q/openapi");
    }

    @Test
    void swaggerUINotTraced() throws InterruptedException {
        endpointNotTraced("/q/swagger-ui/");
    }

    private void endpointNotTraced(String path) throws InterruptedException {
        given()
                .get(path)
                .then()
                .statusCode(HttpStatus.SC_OK);
        Thread.sleep(5000);
        assertEquals(0, getSpans().size());
    }

    private List<Map<String, Object>> getSpans() {
        return get("/export").body().as(new TypeRef<>() {
        });
    }
}
