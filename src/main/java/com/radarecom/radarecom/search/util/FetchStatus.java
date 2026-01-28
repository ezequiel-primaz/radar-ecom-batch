package com.radarecom.radarecom.search.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FetchStatus {
    private boolean success;
    private LocalDateTime lastCall;
}
