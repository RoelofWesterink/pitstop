package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.command.CloseIncident;
import com.example.app.refdata.api.OperatorId;
import io.fluxcapacitor.common.serialization.JsonUtils;
import io.fluxcapacitor.javaclient.test.TestFixture;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

class PitStopTest {

    final TestFixture testFixture = TestFixture.create(PitStopApi.class, Handler.class);

    @Test
    void createIndicent() {
        IncidentDetails details = IncidentDetails.builder().description("hoi").vehicle(Vehicle.builder().licensePlateNumber("06-11").build()).location(GeoLocation.builder().latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).build()).build();
        testFixture.whenPost("/api/incidents", JsonUtils.asJson(details)).<IncidentId>expectResult(IncidentId.class);
    }

    @Test
    void offerAssistance() {
        IncidentDetails incidentDetails = IncidentDetails.builder().description("hoi").vehicle(Vehicle.builder().licensePlateNumber("06-11").build()).location(GeoLocation.builder().latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).build()).build();
        OfferDetails offerDetails = OfferDetails.builder().operatorId(new OperatorId("0")).price(BigDecimal.TWO).build();
        testFixture.givenPost("/api/incidents", JsonUtils.asJson(incidentDetails))
                .givenPost("api/incidents/0/offers", JsonUtils.asJson(offerDetails))
                .whenGet("/api/incidents").<List<Incident>>expectResult(l -> !l.getFirst().getOffers().getFirst().isAccepted());
    }

    @Test
    void acceptOffer() {
        IncidentDetails incidentDetails = IncidentDetails.builder().description("hoi").vehicle(Vehicle.builder().licensePlateNumber("06-11").build()).location(GeoLocation.builder().latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).build()).build();
        OfferDetails offerDetails = OfferDetails.builder().operatorId(new OperatorId("0")).price(BigDecimal.TWO).build();
        testFixture.givenPost("/api/incidents", JsonUtils.asJson(incidentDetails))
                .givenPost("api/incidents/0/offers", JsonUtils.asJson(offerDetails))
                .givenPost("api/incidents/0/offers/1/accept", null)
                .whenGet("/api/incidents").<List<Incident>>expectResult(l -> l.getFirst().getOffers().getFirst().isAccepted());
    }

    @Test
    void closeIndicent() {
        IncidentDetails incidentDetails = IncidentDetails.builder().description("hoi").vehicle(Vehicle.builder().licensePlateNumber("06-11").build()).location(GeoLocation.builder().latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).build()).build();
        OfferDetails offerDetails = OfferDetails.builder().operatorId(new OperatorId("0")).price(BigDecimal.TWO).build();
        testFixture.givenPost("/api/incidents", JsonUtils.asJson(incidentDetails))
                .givenPost("api/incidents/0/offers", JsonUtils.asJson(offerDetails))
                .givenPost("api/incidents/0/close", null)
                .whenGet("/api/incidents").<List<Incident>>expectResult(l -> l.getFirst().isClosed());
    }

    @Test
    void closeScheduled() {
        IncidentDetails incidentDetails = IncidentDetails.builder().description("hoi").vehicle(Vehicle.builder().licensePlateNumber("06-11").build()).location(GeoLocation.builder().latitude(BigDecimal.ONE).longitude(BigDecimal.ONE).build()).build();
         testFixture.givenPost("/api/incidents", JsonUtils.asJson(incidentDetails))
                .whenTimeElapses(Duration.ofHours(25)).expectEvents(Is.isA(CloseIncident.class));
    }


    @Test
    void getViaApi() {
        testFixture.whenGet("/api/incidents").<List<?>>expectResult(List::isEmpty);
    }


}