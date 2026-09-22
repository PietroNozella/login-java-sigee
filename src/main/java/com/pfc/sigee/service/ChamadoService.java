package com.pfc.sigee.service;

import com.pfc.sigee.entity.Chamado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.pfc.sigee.repository.ChamadoRepository;

@Service
// Regras de chamados: criar, listar, buscar, atualizar e cancelar (status=CANCELADO).
// Persistência via ChamadoRepository (coleção "chamados").
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    public Chamado criarChamado(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamados() {
        return chamadoRepository.findAll();
    }

    public Chamado buscarPorId(String id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado com id: " + id));
    }

    public Chamado cancelarChamado(String id) {
        Chamado chamado = buscarPorId(id);
        chamado.setStatus("CANCELADO");
        return chamadoRepository.save(chamado);
    }

    public Chamado atualizarChamado(String id, Chamado chamadoAtualizado) {
        return chamadoRepository.findById(id)
                .map(chamado -> {
                    chamado.setDescricao(chamadoAtualizado.getDescricao());
                    chamado.setStatus(chamadoAtualizado.getStatus());
                    chamado.setTipo(chamadoAtualizado.getTipo());
                    chamado.setTecnico(chamadoAtualizado.getTecnico());
                    chamado.setUsuario(chamadoAtualizado.getUsuario());
                    return chamadoRepository.save(chamado);
                })
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado com id: " + id));
    }

}
