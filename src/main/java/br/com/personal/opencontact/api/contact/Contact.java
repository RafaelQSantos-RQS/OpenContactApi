package br.com.personal.opencontact.api.contact;

import br.com.personal.opencontact.api.agenda.Agenda;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ContactType type;

    @Column(name = "area_code", nullable = false)
    private String areaCode;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    public Contact(String name, ContactType type, String areaCode, String phoneNumber, Agenda agenda) {
        this.name = name;
        this.type = type;
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
        this.agenda = agenda;
    }

    public void updateInfo(String name, ContactType type, String areaCode, String phoneNumber) {
        this.name = name;
        this.type = type;
        this.areaCode = areaCode;
        this.phoneNumber = phoneNumber;
    }
}
