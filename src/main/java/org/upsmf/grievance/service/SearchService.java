package org.upsmf.grievance.service;

import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.model.request.SearchRequest;

import java.util.Optional;

public interface SearchService {

    Optional<Ticket> searchForGrievanceAdmin(SearchRequest searchRequest);
//    Ticket searchForNodalOfficer(SearchRequest searchRequest);
//    Ticket searchForSecretary(SearchRequest searchRequest);
}
