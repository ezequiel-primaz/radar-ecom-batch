package com.radarecom.radarecom.search.dto;

import com.radarecom.radarecom.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchItem {

    private String id;
    private String url;

    public String getId(){
        if (id.startsWith("MLB-")){
            return id.replaceFirst("^MLB-", "MLB");
        }
        return id;
    }

    public ProductType getType(){

        Matcher matcher = Pattern.compile(
                "^(?:(?<catalog>https?:\\/\\/(?:www\\.)?mercadolivre\\.com\\.br\\/.*?\\/p\\/(?!MLBU)[A-Z]+[0-9]+)|" +
                        "(?<userproduct>https?:\\/\\/(?:www\\.)?mercadolivre\\.com\\.br\\/.*?\\/up\\/MLBU[0-9]+)|" +
                        "(?<default>https?:\\/\\/produto\\.mercadolivre\\.com\\.br\\/MLB-[0-9]+))")
                .matcher(url);

        if (matcher.find()) {
            if (matcher.group("catalog") != null) return ProductType.CATALOG;
            else if (matcher.group("userproduct") != null) return ProductType.USER_PRODUCT;
            else if (matcher.group("default") != null) return ProductType.DEFAULT;
            else return ProductType.DEFAULT;
        }
        return ProductType.DEFAULT;
    }

}
