package org.upsmf.grievance.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "ticket", createIndex = false)
@Data
public class Ticket {

    @Id
    private String id;

    @Field(name = "requester_first_name")
    private String firstName;

    @Field(name = "requester_last_name")
    private String lastName;

    @Field(name = "requester_phone")
    private String phone;

    @Field(name = "requester_email")
    private String email;

    @Field(name = "requester_type")
    private String requesterType;

    @Field(name = "assigned_to")
    private String assignedTo;

    @Field(name = "description")
    private String description;

    @Field(name = "is_junk")
    private Boolean isJunk = false;

}
