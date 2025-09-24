package br.com.personal.opencontact.api.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID>, JpaSpecificationExecutor<Contact> {
    /**
     * Returns whether a contact with the given {@code agendaId}, {@code areaCode} and {@code phoneNumber} already exists.
     *
     * @param agendaId the id of the agenda
     * @param areaCode the area code of the contact
     * @param phoneNumber the phone number of the contact
     * @return whether a contact with the given parameters already exists
     */
    boolean existsByAgendaIdAndAreaCodeAndPhoneNumber(UUID agendaId, String areaCode, String phoneNumber);

    /**
     * Returns a contact with the given {@code agendaId}, {@code areaCode} and {@code phoneNumber}.
     *
     * @param agendaId the id of the agenda
     * @param areaCode the area code of the contact
     * @param phoneNumber the phone number of the contact
     * @return an optional containing the contact if found, an empty optional otherwise
     */
    Optional<Contact> findByAgendaIdAndAreaCodeAndPhoneNumber(UUID agendaId, String areaCode, String phoneNumber);

    /**
     * Returns whether any contact with the given {@code agendaId} exists.
     *
     * @param agendaId the id of the agenda
     * @return whether any contact with the given id exists
     */
    boolean existsByAgendaId(UUID agendaId);

    /**
     * Deletes all contacts with the given {@code agendaId} and whose name starts with the given {@code namePrefix}, ignoring case.
     *
     * @param agendaId the id of the agenda
     * @param namePrefix the prefix of the contact name
     */
    @Modifying
    @Query("DELETE FROM Contact c WHERE c.agenda.id = :agendaId AND lower(c.name) LIKE lower(concat(:namePrefix, '%'))")
    void deleteByAgendaIdAndNameStartingWithIgnoreCase(@Param("agendaId") UUID agendaId, @Param("namePrefix") String namePrefix);
}