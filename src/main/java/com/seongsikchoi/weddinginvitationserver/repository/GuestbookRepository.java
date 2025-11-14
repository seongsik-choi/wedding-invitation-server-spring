package com.seongsikchoi.weddinginvitationserver.repository;

import com.seongsikchoi.weddinginvitationserver.entity.Guestbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestbookRepository extends JpaRepository<Guestbook, Integer> {
    
    Page<Guestbook> findByValidTrueOrderByTimestampDesc(Pageable pageable);
    
    @Query("SELECT COUNT(g) FROM Guestbook g WHERE g.valid = true")
    long countByValidTrue();
}

