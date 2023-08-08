package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.dto.TicketRequest;
import org.upsmf.grievance.dto.UpdateTicketRequest;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;
import org.upsmf.grievance.model.AssigneeTicketAttachment;
import org.upsmf.grievance.model.Comments;
import org.upsmf.grievance.model.RaiserTicketAttachment;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.repository.AssigneeTicketAttachmentRepository;
import org.upsmf.grievance.repository.CommentRepository;
import org.upsmf.grievance.repository.RaiserTicketAttachmentRepository;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.DateUtil;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    @Qualifier("esTicketRepository")
    private TicketRepository esTicketRepository;

    @Autowired
    @Qualifier("ticketRepository")
    private org.upsmf.grievance.repository.TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AssigneeTicketAttachmentRepository assigneeTicketAttachmentRepository;

    @Autowired
    private RaiserTicketAttachmentRepository raiserTicketAttachmentRepository;

    /**
     *
     * @param ticket
     * @return
     */
    @Transactional
    public Ticket saveWithAttachment(Ticket ticket, List<String> attachments) {
        // save ticket in postgres
        org.upsmf.grievance.model.Ticket psqlTicket = ticketRepository.save(ticket);
        // update attachments if present
        if(attachments != null) {
            for(String url : attachments) {
                RaiserTicketAttachment raiserTicketAttachment = RaiserTicketAttachment.builder()
                        .attachment_url(url)
                        .ticketId(ticket.getId())
                        .build();
                raiserTicketAttachmentRepository.save(raiserTicketAttachment);
            }
        }
        // covert to ES ticket object
        org.upsmf.grievance.model.es.Ticket esticket = convertToESTicketObj(ticket);
        // save ticket in ES
        esTicketRepository.save(esticket);
        // TODO send mail
        return psqlTicket;
    }

    @Override
    @Transactional
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
    @Transactional
    public Ticket save(TicketRequest ticketRequest) throws Exception {
        // TODO validate request
        // TODO validate OTP
        // set default value for creating ticket
        Ticket ticket = createTicketWithDefault(ticketRequest);
        // create ticket
        return saveWithAttachment(ticket, ticketRequest.getAttachmentURls());

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
                .assignedToId(ticketRequest.getCc())
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
                .build();
    }

    /**
     *
     * @param updateTicketRequest
     * @return
     */
    @Override
    @Transactional
    public Ticket update(UpdateTicketRequest updateTicketRequest) {
        // TODO validate ticket
        // check if the ticket exists
        Optional<Ticket> ticketDetails = getTicketDetailsByID(updateTicketRequest.getId());
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
        ticket = getTicketById(ticket.getId());
        // check if ticket exists in ES
        Optional<org.upsmf.grievance.model.es.Ticket> esTicketDetails = esTicketRepository.findOneByTicketId(updateTicketRequest.getId());
        org.upsmf.grievance.model.es.Ticket updatedESTicket = convertToESTicketObj(ticket);
        if(esTicketDetails.isPresent()) {
            // TODO revisit this
            esTicketRepository.deleteById(esTicketDetails.get().getId());
        }
        esTicketRepository.save(updatedESTicket);
        return ticket;
    }

    @Override
    public Ticket getTicketById(long id) {
        if(id <= 0) {
            throw new RuntimeException("Invalid Ticket ID");
        }
        Optional<Ticket> ticketDetails = getTicketDetailsByID(id);
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
        ticket.setAssignedToId(updateTicketRequest.getCc());
        ticket.setPriority(updateTicketRequest.getPriority());
        // update assignee comments
        Comments comments = Comments.builder().comment(updateTicketRequest.getComment())
                        .userId(updateTicketRequest.getRequestedBy())
                        .ticketId(ticket.getId())
                        .build();
        commentRepository.save(comments);
        // update assignee attachment url
        if(updateTicketRequest.getAssigneeAttachmentURLs() != null) {
            for (String url : updateTicketRequest.getAssigneeAttachmentURLs()) {
                AssigneeTicketAttachment assigneeTicketAttachment = AssigneeTicketAttachment.builder()
                        .userId(updateTicketRequest.getRequestedBy())
                        .ticketId(ticket.getId())
                        .attachment_url(url).build();
                assigneeTicketAttachmentRepository.save(assigneeTicketAttachment);
            }
        }
    }

    /**
     *
     * @param ticket
     * @return
     */
    private org.upsmf.grievance.model.es.Ticket convertToESTicketObj(Ticket ticket) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DEFAULT_DATE_FORMAT);
        ObjectMapper mapper = new ObjectMapper();
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
                .escalatedDate(ticket.getEscalatedDate()!=null?ticket.getEscalatedDate().toLocalDateTime().format(dateTimeFormatter):null)
                .escalatedDateTS(ticket.getEscalatedDate()!=null?ticket.getEscalatedDate().getTime():-1)
                .status(ticket.getStatus())
                .requestType(ticket.getRequestType())
                .priority(ticket.getPriority())
                .escalatedBy(ticket.getEscalatedBy())
                .escalatedTo(ticket.getEscalatedTo()).build();
                //.comments(ticket.getComments()!=null?ticket.getComments():Collections.EMPTY_LIST)
                //.raiserAttachmentURLs(ticket.getRaiserTicketAttachmentURLs()!=null?mapper.writeValueAsString(ticket.getRaiserTicketAttachmentURLs()):"")
                //.assigneeAttachmentURLs(ticket.getAssigneeTicketAttachment()!=null?mapper.writeValueAsString(ticket.getAssigneeTicketAttachment()):"").build();
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
    public Optional<Ticket> getTicketDetailsByID(long id) {
        return ticketRepository.findById(id);
    }
}
