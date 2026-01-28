package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FireBaseEvent {

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("event_data")
    private EventDataFireBase eventData;

}
