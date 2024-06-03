package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.MemoryPage;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemoryPageRepository extends JpaRepository<MemoryPage, Long> {
    Optional<List<MemoryPage>> findMemoryPagesByAuthor_Id(Long id);
}
