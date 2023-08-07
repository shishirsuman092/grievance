package org.upsmf.grievance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.DateUtil;

import java.time.format.DateTimeFormatter;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository esTicketRepository;

    @Autowired
    private org.upsmf.grievance.repository.TicketRepository ticketRepository;

    @Override
    public Ticket save(Ticket ticket) {
        // TODO validate request
        // TODO validate OTP
        // save ticket in postgres
        org.upsmf.grievance.model.Ticket psqlTicket = ticketRepository.save(ticket);
        // covert to ES ticket object
        org.upsmf.grievance.model.es.Ticket esticket = convertToESTicketObj(ticket);
        // save ticket in ES
        esTicketRepository.save(esticket);
        // TODO send mail
        return psqlTicket;
    }

    private org.upsmf.grievance.model.es.Ticket convertToESTicketObj(Ticket ticket) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DEFAULT_DATE_FORMAT);
        // TODO get user details based on iD
        return org.upsmf.grievance.model.es.Ticket.builder()
                .ticketId(ticket.getId())
                .firstName(ticket.getFirstName())
                .lastName(ticket.getLastName())
                .phone(ticket.getPhone())
                .email(ticket.getEmail())
                .requesterType(ticket.getRequesterType())
                .assignedToId(ticket.getAssignedToId())
                .assignedToName("") // get user details based on ID
                .description(ticket.getDescription())
                .junk(ticket.isJunk())
                .createdDate(ticket.getCreatedDate().toLocalDateTime().format(dateTimeFormatter))
                .createdDateTS(ticket.getCreatedDate().getTime())
                .updatedDate(ticket.getUpdatedDate().toLocalDateTime().format(dateTimeFormatter))
                .updatedDateTS(ticket.getUpdatedDate().getTime())
                .lastUpdatedBy(ticket.getLastUpdatedBy())
                .escalated(ticket.isEscalated())
                .escalatedDate(ticket.getEscalatedDate().toLocalDateTime().format(dateTimeFormatter))
                .escalatedDateTS(ticket.getEscalatedDate().getTime())
                .status(ticket.getStatus())
                .requestType(ticket.getRequestType())
                .priority(ticket.getPriority())
                .escalatedBy(ticket.getEscalatedBy())
                .escalatedTo(ticket.getEscalatedTo())
                .comments(ticket.getComments())
                .raiserAttachmentURLs(ticket.getRaiserAttachmentURLs())
                .assigneeAttachmentURLs(ticket.getAssigneeAttachmentURLs()).build();
    }
}
