package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.model.request.SearchRequest;
import org.upsmf.grievance.service.SearchService;

import java.util.Optional;

@Controller
@RequestMapping("/search")
public class SearchTicketController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/getAllTickets")
    public ResponseEntity<Response> search (SearchRequest searchRequest) {
        Ticket responseTicket = searchService.search(searchRequest);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }
}