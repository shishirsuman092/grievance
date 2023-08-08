package org.upsmf.grievance.service;

import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.request.TicketRequest;
import org.upsmf.grievance.model.request.UpdateTicketRequest;

public interface TicketService {

    Ticket save(Ticket ticket);

    Ticket save(TicketRequest ticketRequest) throws Exception;

    Ticket update(UpdateTicketRequest updateTicketRequest);
}
