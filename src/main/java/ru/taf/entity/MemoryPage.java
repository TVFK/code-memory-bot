package ru.taf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "epitaph")
    private String epitaph;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
