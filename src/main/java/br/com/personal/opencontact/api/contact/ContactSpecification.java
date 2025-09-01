package br.com.personal.opencontact.api.contact;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContactSpecification {

    public static Specification<Contact> filterBy(UUID agendaId, String nameContains, String phoneContains) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("agenda").get("id"), agendaId));

            if (nameContains != null && !nameContains.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + nameContains.toLowerCase() + "%"
                ));
            }

            if (phoneContains != null && !phoneContains.isBlank()) {
                // Criamos um predicado que busca em areaCode + phoneNumber
                var fullPhoneNumber = criteriaBuilder.concat(root.get("areaCode"), root.get("phoneNumber"));
                predicates.add(criteriaBuilder.like(fullPhoneNumber, "%" + phoneContains + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

