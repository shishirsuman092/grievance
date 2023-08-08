package org.upsmf.grievance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.enums.Roles;
import org.upsmf.grievance.model.enums.StatusType;
import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.model.request.SearchRequest;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.SearchService;

import java.util.Optional;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private TicketRepository esTicketRepository;
    @Override
    public Optional<Ticket> searchForGrievanceAdmin(SearchRequest searchRequest) {
        Optional<Ticket> esTicketDetails = null;
        if(searchRequest.getRoles().toString().equalsIgnoreCase(String.valueOf(Roles.GRIEVANCEADMIN))){
            if(searchRequest.getStatusType().toString().equalsIgnoreCase(String.valueOf(StatusType.NOTASSIGNED)))
            esTicketDetails = esTicketRepository.findOneByTicketId(-1);
        }
        return esTicketDetails;
    }

//    @Override
//    public Ticket searchForNodalOfficer(SearchRequest searchRequest) {
//        return null;
//    }
//
//    @Override
//    public Ticket searchForSecretary(SearchRequest searchRequest) {
//        return null;
//    }

}
