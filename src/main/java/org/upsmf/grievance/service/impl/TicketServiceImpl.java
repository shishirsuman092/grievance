package org.upsmf.grievance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.enums.TicketPriority;
import org.upsmf.grievance.model.enums.TicketStatus;
import org.upsmf.grievance.model.request.TicketRequest;
import org.upsmf.grievance.model.request.UpdateTicketRequest;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.DateUtil;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    @Qualifier("esTicketRepository")
    private TicketRepository esTicketRepository;

    @Autowired
    @Qualifier("ticketRepository")
    private org.upsmf.grievance.repository.TicketRepository ticketRepository;

    /**
     *
     * @param ticket
     * @return
     */
    @Override
    public Ticket save(Ticket ticket) {
        // save ticket in postgres
        org.upsmf.grievance.model.Ticket psqlTicket = ticketRepository.save(ticket);
        // covert to ES ticket object
        org.upsmf.grievance.model.es.Ticket esticket = convertToESTicketObj(ticket);
        // save ticket in ES
        esTicketRepository.save(esticket);
        // TODO send mail
        return psqlTicket;
    }

    /**
     *
     * @param ticketRequest
     * @return
     * @throws Exception
     */
    @Override
    public Ticket save(TicketRequest ticketRequest) throws Exception {
        // TODO validate request
        // TODO validate OTP
        // set default value for creating ticket
        Ticket ticket = createTicketWithDefault(ticketRequest);
        // create ticket
        return save(ticket);

    }

    /**
     *
     * @param ticketRequest
     * @return
     * @throws Exception
     */
    private Ticket createTicketWithDefault(TicketRequest ticketRequest) throws Exception {
        Timestamp currentTimestamp = new Timestamp(DateUtil.getCurrentDate().getTime());
        return Ticket.builder()
                .createdDate(new Timestamp(DateUtil.getCurrentDate().getTime()))
                .firstName(ticketRequest.getFirstName())
                .lastName(ticketRequest.getLastName())
                .phone(ticketRequest.getPhone())
                .email(ticketRequest.getEmail())
                .requesterType(ticketRequest.getUserType())
                .assignedToId(ticketRequest.getAssignedToId())
                .description(ticketRequest.getDescription())
                .createdDate(currentTimestamp)
                .updatedDate(currentTimestamp)
                .lastUpdatedBy(-1l)
                .escalated(false)
                .escalatedDate(null)
                .escalatedTo(-1l)
                .status(TicketStatus.OPEN)
                .requestType(ticketRequest.getRequestType())
                .priority(TicketPriority.LOW)
                .escalatedBy(-1l)
                .comments(null)
                .raiserAttachmentURLs(ticketRequest.getAttachmentURls())
                .assigneeAttachmentURLs(null)
                .build();
    }

    /**
     *
     * @param updateTicketRequest
     * @return
     */
    @Override
    public Ticket update(UpdateTicketRequest updateTicketRequest) {
        // TODO validate ticket
        // check if the ticket exists
        Optional<Ticket> ticketDetails = getTicketByID(updateTicketRequest.getId());
        Ticket ticket = null;
        if(!ticketDetails.isPresent()) {
            // TODO throw exception
            throw new RuntimeException("Ticket does not exists");
        }
        ticket = ticketDetails.get();
        // set incoming values
        setUpdateTicket(updateTicketRequest, ticket);
        // update ticket in DB
        ticketRepository.save(ticket);
        // check if ticket exists in ES
        Optional<org.upsmf.grievance.model.es.Ticket> esTicketDetails = esTicketRepository.findOneByTicketId(updateTicketRequest.getId());
        org.upsmf.grievance.model.es.Ticket updatedESTicket = convertToESTicketObj(ticket);
        if(esTicketDetails.isPresent()) {
            updatedESTicket.setId(esTicketDetails.get().getId());
        }
        esTicketRepository.save(updatedESTicket);
        return ticket;
    }

    @Override
    public Ticket getTicketById(long id) {
        if(id <= 0) {
            throw new RuntimeException("Invalid Ticket ID");
        }
        Optional<Ticket> ticketDetails = getTicketByID(id);
        if(!ticketDetails.isPresent()) {
            throw new RuntimeException("Invalid Ticket ID");
        }
        return ticketDetails.get();
    }

    /**
     *
     * @param updateTicketRequest
     * @param ticket
     */
    private void setUpdateTicket(UpdateTicketRequest updateTicketRequest, Ticket ticket) {
        // TODO check request role and permission
        ticket.setStatus(updateTicketRequest.getStatus());
        ticket.setAssignedToId(updateTicketRequest.getAssignedTo());
        ticket.setPriority(updateTicketRequest.getPriority());
        ticket.setDescription(updateTicketRequest.getDescription());
    }

    /**
     *
     * @param ticket
     * @return
     */
    private org.upsmf.grievance.model.es.Ticket convertToESTicketObj(Ticket ticket) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DEFAULT_DATE_FORMAT);
        // TODO get user details based on ID
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

    /**
     *
     * @param id
     * @return
     */
    public Optional<org.upsmf.grievance.model.es.Ticket> getESTicketByID(long id) {
        return esTicketRepository.findOneByTicketId(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Ticket> getTicketByID(long id) {
        return ticketRepository.findById(id);
    }
}
