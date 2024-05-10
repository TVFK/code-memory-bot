package ru.taf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.taf.entity.TgUser;

@Repository
public interface TgUserRepository extends JpaRepository<TgUser, Long> {
}
