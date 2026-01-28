package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MelidataAvailableQuantityPicker {

    Pattern pattern = Pattern.compile("\\((?:\\+)?(\\d+)");

    @JsonProperty("description")
    private String description;

    public Integer getQuantity(){
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }else {
            return 1;
        }
    }

}
