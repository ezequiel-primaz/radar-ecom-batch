package com.radarecom.radarecom.search.filter;

import com.radarecom.radarecom.exception.BusinessException;
import com.radarecom.radarecom.search.enums.LogisticTypeEnum;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilter {

    @Min(0)
    private Long minPrice;

    @Min(1)
    private Long maxPrice;

    private boolean onlyCatalog;
    
    private boolean onlyDefault;

    private Boolean onlyOfficialStore;

    private Boolean international;

    private Boolean freeShipping;

    private boolean onlyLogisticTypeFull;

    private Set<LogisticTypeEnum> logisticTypes;

    @Min(0)
    private Integer page;

    public boolean hasTypeFilter(){
        return onlyCatalog || onlyDefault;
    }

    public void validateFilters(){
        if (nonNull(international)){
            if (onlyLogisticTypeFull && international) {
                throw new BusinessException("Both [Full] and [International] filters in the same request is not allowed.");
            }
        }
        if (onlyCatalog && onlyDefault){
            throw new BusinessException("Both [OnlyCatalog] and [OnlyDefault] filters in the same request is not allowed.");
        }
    }

    public boolean hasLogisticTypeFilter(){
        return nonNull(logisticTypes) && !logisticTypes.isEmpty();
    }

}