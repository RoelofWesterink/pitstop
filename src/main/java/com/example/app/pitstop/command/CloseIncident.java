package com.example.app.pitstop.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;
@Value
@Builder
@TrackSelf
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CloseIncident implements Request<Void> {
    IncidentId incidentId;

    @HandleCommand
    IncidentId handle() {
        return FluxCapacitor.loadAggregate(getIncidentId()).assertAndApply(this).get().getIncidentId();
    }

    @Apply
    Incident apply(Incident incident, Message message) {
        return incident.toBuilder().end(message.getTimestamp()).build();
    }

    @AssertLegal
    void existingIncident(@Nullable Incident incident){
        if(incident == null){
            throw new IllegalCommandException("Incident not found");
        }
    }
}
