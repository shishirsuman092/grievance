package org.upsmf.grievance.service;

import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.request.SearchRequest;

public interface SearchService {
    Ticket search(SearchRequest searchRequest);
}
