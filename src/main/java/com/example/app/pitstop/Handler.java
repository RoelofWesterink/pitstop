package com.example.app.pitstop;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.command.CloseIncident;
import com.example.app.pitstop.command.ReportIncident;
import com.example.app.pitstop.query.GetIncidents;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class Handler {

    @HandleQuery()
    List<Incident> getIncidents(GetIncidents getIncidents){
        return FluxCapacitor.search(Incident.class).fetchAll();
    }

    @HandleEvent()
    void scheduleClose(ReportIncident reportIncident){
        FluxCapacitor.scheduleCommand(CloseIncident.builder().incidentId(reportIncident.getIncidentId()).build(), Duration.ofHours(24));
    }
}
