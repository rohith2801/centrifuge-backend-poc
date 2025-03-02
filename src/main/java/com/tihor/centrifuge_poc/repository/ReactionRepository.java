package com.tihor.centrifuge_poc.repository;

import com.tihor.centrifuge_poc.model.Reaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CrudRepository<Reaction, Long> {
}
