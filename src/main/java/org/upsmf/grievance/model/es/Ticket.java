package org.upsmf.grievance.model.es;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.upsmf.grievance.enums.RequesterType;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;

import java.util.List;

@Document(indexName = "ticket", createIndex = false)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class Ticket {

    @Id
    private String id;

    @Field(name = "ticket_id")
    private Long ticketId;

    @Field(name = "requester_first_name")
    private String firstName;

    @Field(name = "requester_last_name")
    private String lastName;

    @Field(name = "requester_phone")
    private String phone;

    @Field(name = "requester_email")
    private String email;

    @Field(name = "requester_type")
    private RequesterType requesterType;

    @Field(name = "assigned_to_id")
    private Long assignedToId;

    @Field(name = "assigned_to_name")
    private String assignedToName;

    @Field(name = "description")
    private String description;

    @Field(name = "is_junk")
    private Boolean junk = false;

    @Field(name = "created_date")
    private String createdDate;

    @Field(name = "updated_date")
    private String updatedDate;

    @Field(name = "created_date_ts")
    private Long createdDateTS;

    @Field(name = "updated_date_ts")
    private Long updatedDateTS;

    @Field(name = "last_updated_by")
    private Long lastUpdatedBy;

    @Field(name = "is_escalated")
    private Boolean escalated;

    @Field(name = "escalated_date")
    private String escalatedDate;

    @Field(name = "escalated_date_ts")
    private Long escalatedDateTS;

    @Field(name = "escalated_to")
    private Long escalatedTo;

    @Field(name = "status")
    private TicketStatus status = TicketStatus.OPEN;

    @Field(name = "request_type")
    private String requestType;

    @Field(name = "priority")
    private TicketPriority priority = TicketPriority.LOW;

    // if the ticket is escalated by system, value will be -1 else superAdmin ID
    @Field(name = "escalated_by")
    private Long escalatedBy = -1L;

    @Field(name = "comments")
    private List<String> comments;

    @Field(name = "raiser_attachment_urls")
    private List<String> raiserAttachmentURLs;

    @Field(name = "assignee_attachment_urls")
    private List<String> assigneeAttachmentURLs;

}
