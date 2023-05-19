package com.example.movieinfoservice;

import com.example.movieinfoservice.controller.FluxAndMonoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {

        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.TYPE)
                .hasSize(3);
    }

    @Test
    void flux02() {

        var flux = webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();


        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void flux03() {

        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(list -> {
                    var responseBody = list.getResponseBody();
                    assertThat(responseBody.get(0), equalTo(1));
                    assertThat(responseBody.get(1), equalTo(2));
                    assertThat(responseBody.get(2), equalTo(3));
                });
    }

    @Test
    void mono() {

        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(result -> {
                    var response = result.getResponseBody();
                    assertThat(response, equalTo("Hello World"));
                });
    }

    @Test
    void stream() {

        var flux = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();


        StepVerifier.create(flux)
                .expectNext(0L, 1L, 2L, 3L)
                // cancel signal from this method
                .thenCancel()
                .verify();
    }
}