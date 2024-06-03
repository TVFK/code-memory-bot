package ru.taf.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import ru.taf.enums.UserState;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tg_user")
@Log4j2
public class TgUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_user_id")
    private Long telegramUserId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_login_date")
    private LocalDateTime firstLoginDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state")
    private UserState userState;

    @Column(name = "temp_data")
    private String tempData;

    @OneToMany(mappedBy = "author")
    private List<MemoryPage> memoryPages;

    public Person getTempData() {
        Person tempData = null;
        try {
            byte[] serializedObject = Base64.getDecoder().decode(this.tempData);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedObject);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            objectInputStream.close();
            tempData = (Person) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            log.error("error with deserialization: " + e);
        }
        log.info("Человек" + tempData);
        return tempData;
    }

    public void setTempData(Person tempData) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(tempData);
            objectOutputStream.flush();
            objectOutputStream.close();
            byte[] serializedObject = byteArrayOutputStream.toByteArray();
            this.tempData = Base64.getEncoder().encodeToString(serializedObject);
        } catch (IOException e) {
            log.error("error with serialization: " + e);
        }

    }
}
