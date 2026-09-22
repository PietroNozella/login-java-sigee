package com.pfc.sigee.repository;

import com.pfc.sigee.entity.Chamado;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChamadoRepository extends MongoRepository<Chamado, String> {
    List<Chamado> findByStatus(String status);
    
    
}
