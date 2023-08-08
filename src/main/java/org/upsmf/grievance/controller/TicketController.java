package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.model.request.TicketRequest;
import org.upsmf.grievance.model.request.UpdateTicketRequest;
import org.upsmf.grievance.service.TicketService;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody TicketRequest ticketRequest) {
        Ticket responseTicket = null;
        try {
            responseTicket = ticketService.save(ticketRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Response> update(@RequestBody UpdateTicketRequest updateTicketRequest) {
        Ticket responseTicket = ticketService.update(updateTicketRequest);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<Response> getTicketById(@RequestParam(value = "id") Long id) {
        System.out.println("   " +id);
        Ticket responseTicket = ticketService.getTicketById(id);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }
}
