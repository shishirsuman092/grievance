package org.upsmf.grievance.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.es.Ticket;

import java.util.Optional;

@Repository("esTicketRepository")
public interface TicketRepository extends ElasticsearchRepository<Ticket, String> {
    @Query("{'ticket_id': ?0}")
    Optional<Ticket> findOneByTicketId(long id);
//    @Query("{'assigned_to_id': ?0}")
//    Optional<Ticket> findNotAssignedTickets(long id);
//    @Query("{'is_escalated': ?0, 'status': ?1}")
//    Optional<Ticket> findPendingTickets(Boolean id, String status);
//    @Query("{'is_escalated': ?0, 'status': ?1}")
//    Optional<Ticket> findResolvedTickets(Boolean id, String status);
//    @Query("{'is_escalated': ?0, 'status': ?1}")
//    Optional<Ticket> findJunkTickets(Boolean id, String status);
//    @Query("{'is_escalated': ?0, 'status': ?1}")
//    Optional<Ticket> findAllEsclatedTickets(Boolean id, String status);
//    @Query("{'is_escalated': ?0, 'status': ?1}")
//    Optional<Ticket> findAllEsclatedTickets(Boolean id, String status);


}
