package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.Children;

@Repository
public interface ChildrenRepository extends JpaRepository<Children, Long> {
}
