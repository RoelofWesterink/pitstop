package com.example.app.pitstop.command;

import com.example.app.pitstop.api.*;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@TrackSelf
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class OfferAssistance implements Request<OfferId> {
    OfferId offerId = OfferId.newValue();
    @NotNull IncidentId incidentId;
    @NotNull OfferDetails details;

    @HandleCommand
    OfferId handle() {
        FluxCapacitor.loadAggregate(getIncidentId()).assertAndApply(this);
        return getOfferId();
    }

    @Apply
    Incident apply(Incident incident) {
        return incident.toBuilder().offer(Offer.builder().offerId(offerId).details(details).build()).build();
    }

    @AssertLegal
    void existingIncident(@Nullable Incident incident){
        if(incident == null){
            throw new IllegalCommandException("Incident not found");
        }
    }

}
