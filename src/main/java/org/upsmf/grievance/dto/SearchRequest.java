package org.upsmf.grievance.dto;

import lombok.*;
import org.upsmf.grievance.enums.TicketPriority;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchRequest {

    private String searchKeyword;

    private Map<String,Object> filter;

    private List<String> status;

    private String cc;

    private Timestamp date;

    private Boolean isJunk;

    private TicketPriority priority;

    private int offset;

    private int size;

    private Map<String, String> sort;

}
