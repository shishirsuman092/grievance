package org.upsmf.grievance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends CrudRepository<org.upsmf.grievance.model.Ticket, Long> {
}
