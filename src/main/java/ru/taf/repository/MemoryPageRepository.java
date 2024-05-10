package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.MemoryPage;

@Repository
public interface MemoryPageRepository extends JpaRepository<MemoryPage, Long> {
}
