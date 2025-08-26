package com.drc.server.persistence;

import com.drc.server.entity.Game;
import com.drc.server.entity.Role;
import com.drc.server.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepo extends CrudRepository<Game, Integer> {

}

