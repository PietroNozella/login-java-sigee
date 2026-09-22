package com.pfc.sigee.repository;

import com.pfc.sigee.entity.Cliente;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteRepository extends MongoRepository<Cliente, String> {
    List<Cliente> findByNome(String nome);
}
