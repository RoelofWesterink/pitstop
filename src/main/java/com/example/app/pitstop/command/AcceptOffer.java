package com.example.app.pitstop.command;

import com.example.app.pitstop.api.*;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@TrackSelf
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AcceptOffer implements Request<Void> {
    OfferId offerId;
    IncidentId incidentId;

    @HandleCommand
    void handle() {
        FluxCapacitor.loadAggregate(getIncidentId()).assertAndApply(this);
    }

    @Apply
    Incident apply(Incident incident) {
        List<Offer> updatedOffers = incident.getOffers().stream().map(o -> o.getOfferId().equals(offerId) ? o.toBuilder().accepted(true).build() : o).toList();
        return incident.withOffers(updatedOffers);
    }

    @AssertLegal
    void assertAuthorized(Incident incident, Sender sender) {
        if(incident.getOffer(offerId)
                .map(Offer::getDetails)
                .map(OfferDetails::getOperatorId).filter(o -> o.equals(sender.getOperator())).isEmpty() && !sender.isAdmin())
            throw new IllegalCommandException("Not authorized to accept");
    }

    @AssertLegal
    void containsOffer(Incident incident){
        if(incident.getOffer(offerId).isEmpty()){
            throw new IllegalCommandException("Offer not found");
        }
    }

    @AssertLegal
    void existingIncident(@Nullable Incident incident){
        if(incident == null){
            throw new IllegalCommandException("Incident not found");
        }
    }
}
