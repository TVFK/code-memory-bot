package ru.taf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "memory_page")
public class MemoryPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tg_user_id", referencedColumnName = "id")
    private TgUser author;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @Override
    public String toString() {
        return "MemoryPage\n" +
                "author=" + author.getUserName() +
                "\nperson=" + person.getFullName();
    }
}