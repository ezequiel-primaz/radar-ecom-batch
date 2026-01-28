package com.radarecom.radarecom.search.dto.melidata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MelidataSales {

    Pattern pattern = Pattern.compile("(\\d+)");

    @JsonProperty("subtitle")
    private String subtitle;

    public Integer getSales(){
        Matcher matcher = pattern.matcher(subtitle);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 0;
    }

}
