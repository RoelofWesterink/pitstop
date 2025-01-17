package com.example.app.pitstop.api;

import com.example.app.user.api.UserId;
import io.fluxcapacitor.javaclient.modeling.Aggregate;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Aggregate(searchable = true, timestampPath ="start", endPath = "end")
@Builder(toBuilder = true)
@Value
public class Incident {
    IncidentId incidentId;
    IncidentDetails details;

    UserId reporter;

    Instant start, end;

    @Singular
    List<Offer> offers;

    Assistance assistance;

    @SuppressWarnings("unused")
    public Optional<Offer> getAcceptedOffer() {
        return offers.stream().filter(Offer::isAccepted).findFirst();
    }

    public Optional<Offer> getOffer(OfferId offerId){
        return offers.stream().filter(o -> o.getOfferId().equals(offerId)).findFirst();
    }

    public boolean isClosed(){
        return this.end != null;
    }
}
