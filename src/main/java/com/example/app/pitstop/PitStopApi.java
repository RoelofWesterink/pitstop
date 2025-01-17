package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.command.AcceptOffer;
import com.example.app.pitstop.command.CloseIncident;
import com.example.app.pitstop.command.OfferAssistance;
import com.example.app.pitstop.command.ReportIncident;
import com.example.app.pitstop.query.GetIncidents;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import io.fluxcapacitor.javaclient.web.PathParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Path("/api")
public class PitStopApi {

    @HandlePost("incidents")
    IncidentId reportIncident(IncidentDetails details) {
        return FluxCapacitor.sendCommandAndWait(ReportIncident.builder().details(details).build());
    }

    @HandleGet("incidents")
    List<Incident> getIncidents() {
        return FluxCapacitor.queryAndWait(GetIncidents.builder().build());
    }


    @HandlePost("incidents/{incidentId}/offers")
    OfferId offerAssistance(@PathParam IncidentId incidentId, OfferDetails details) {
        return FluxCapacitor.sendCommandAndWait(OfferAssistance.builder().incidentId(incidentId).details(details).build());
    }

    @HandlePost("incidents/{incidentId}/offers/{offerId}/accept")
    void acceptOffer(@PathParam IncidentId incidentId, @PathParam OfferId offerId) {
        FluxCapacitor.sendCommandAndWait(AcceptOffer.builder().incidentId(incidentId).offerId(offerId).build());
    }

    @HandlePost("incidents/{incidentId}/close")
    void closeIncident(@PathParam IncidentId incidentId) {
        FluxCapacitor.sendCommandAndWait(CloseIncident.builder().incidentId(incidentId).build());
    }

}
