package br.com.personal.opencontact.api.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
