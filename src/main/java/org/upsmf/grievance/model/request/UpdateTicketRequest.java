package org.upsmf.grievance.model.request;

import lombok.*;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.enums.TicketPriority;
import org.upsmf.grievance.model.enums.TicketStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class UpdateTicketRequest {

    private long id;
    private long requestedBy;
    private TicketStatus status;
    private Long assignedTo;
    private TicketPriority priority;
    private String description;
    private String comment;
}
