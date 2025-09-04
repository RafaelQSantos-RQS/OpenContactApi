package br.com.personal.opencontact.api.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, UUID> {
    /**
     * Returns whether an agenda with the given {@code name} already exists,
     * ignoring case.
     *
     * @param name the name of the agenda
     * @return whether an agenda with the given name already exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Returns an optional containing an agenda with the given {@code name},
     * ignoring case, if found. Otherwise, an empty optional is returned.
     *
     * @param name the name of the agenda
     * @return an optional containing the agenda if found, an empty optional otherwise
     */
    Optional<Agenda> findByNameIgnoreCase(String name);
}
