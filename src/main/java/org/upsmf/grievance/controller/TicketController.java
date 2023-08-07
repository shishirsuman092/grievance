package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.service.TicketService;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody Ticket ticket) {
        Ticket responseTicket = ticketService.save(ticket);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }
}
