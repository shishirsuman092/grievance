package org.upsmf.grievance.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.upsmf.grievance.model.enums.RequesterType;
import org.upsmf.grievance.model.enums.TicketPriority;
import org.upsmf.grievance.model.enums.TicketStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "requester_first_name")
    private String firstName;

    @Column(name = "requester_last_name")
    private String lastName;

    @Column(name = "requester_phone")
    private String phone;

    @Column(name = "requester_email")
    private String email;

    @Column(name = "requester_type")
    private RequesterType requesterType;

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(name = "description")
    private String description;

    @Column(name = "is_junk")
    private boolean junk = false;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @Column(name = "is_escalated")
    private boolean escalated;

    @Column(name = "escalated_date")
    private Timestamp escalatedDate;

    @Column(name = "escalated_to")
    private Long escalatedTo;

    @Column(name = "status")
    private TicketStatus status = TicketStatus.OPEN;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "priority")
    private TicketPriority priority = TicketPriority.P3;

    // if the ticket is escalated by system, value will be -1 else superAdmin ID
    @Column(name = "escalated_by")
    private Long escalatedBy = -1L;

    @Column(name = "comments")
    private List<String> comments;

    @Column(name = "raiser_attachment_urls")
    private List<String> raiserAttachmentURLs;

    @Column(name = "assignee_attachment_urls")
    private List<String> assigneeAttachmentURLs;
}
