package org.upsmf.grievance.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.Ticket;

@Repository
public interface TicketRepository extends ElasticsearchRepository<Ticket, String> {
}
