package com.example.app.pitstop.command;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.common.Message;
import io.fluxcapacitor.javaclient.modeling.AssertLegal;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.tracking.TrackSelf;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.Request;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@TrackSelf
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ReportIncident implements Request<IncidentId> {
    IncidentId incidentId = IncidentId.newValue();
    IncidentDetails details;

    @HandleCommand
    IncidentId handle() {
        IncidentId newIncidentId = FluxCapacitor.loadAggregate(getIncidentId()).assertAndApply(this).get().getIncidentId();
        return newIncidentId;
    }

    @Apply
    Incident apply(Message message, Sender sender) {
        return Incident.builder().incidentId(incidentId).details(details).reporter(sender.getUserId()).start(message.getTimestamp()).build();
    }

    @AssertLegal
    void existingIncident(Incident incident){
        throw new IllegalCommandException("Incident already exists");
    }
}
