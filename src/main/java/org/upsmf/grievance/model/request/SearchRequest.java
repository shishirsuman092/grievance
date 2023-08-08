package org.upsmf.grievance.model.request;

import lombok.*;
import org.upsmf.grievance.model.enums.TicketPriority;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchRequest {

    @SuppressWarnings("rawtypes")
    private List<Map> properties;

    private String searchKeyword;

    private Map<String,Object> filters;

    private List<String> fields;

    private List<String> status;

    private String cc;

    private Timestamp date;

    private Boolean isJunk;

    private TicketPriority priority;

    private int offset;

    private int size;

    private Map<String, String> sort;

    private String operation;

    @SuppressWarnings("rawtypes")
    public SearchRequest(List<Map> properties, String operation, int size) {
        super();
        this.properties = properties;
        this.operation = operation;
        this.size = size;
    }
    @SuppressWarnings("rawtypes")
    public List<Map> getProperties() {
        return properties;
    }
    @SuppressWarnings("rawtypes")
    public void setProperties(List<Map> properties) {
        this.properties = properties;
    }

    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
