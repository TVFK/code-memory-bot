package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.MemoryPage;

import java.util.List;

@Repository
public interface MemoryPageRepository extends JpaRepository<MemoryPage, Long> {
    List<MemoryPage> findMemoryPagesByAuthor_Id(Long id);
}
