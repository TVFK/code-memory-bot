package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
