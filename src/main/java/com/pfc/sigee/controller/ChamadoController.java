package com.pfc.sigee.controller;

import com.pfc.sigee.entity.Chamado;
import com.pfc.sigee.service.ChamadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/chamados")
// API JSON de chamados (leitura para todos autenticados, escrita só ADMIN/OPERADOR via SecurityConfig).
// As telas MVC correspondentes vivem em ChamadoViewController (/chamados).
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping
    public Chamado criarChamado(@RequestBody Chamado chamado) {
        return chamadoService.criarChamado(chamado);
    }

    @GetMapping
    public List<Chamado> listarChamados() {
        return chamadoService.listarChamados();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chamado> atualizarChamado(@PathVariable String id, @RequestBody Chamado chamadoAtualizado) {
        Chamado chamado = chamadoService.atualizarChamado(id, chamadoAtualizado);
        return ResponseEntity.ok(chamado);
    }
}
