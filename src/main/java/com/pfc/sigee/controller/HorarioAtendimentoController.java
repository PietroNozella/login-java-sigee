package com.pfc.sigee.controller;

import com.pfc.sigee.entity.HorarioAtendimento;
import com.pfc.sigee.service.HorarioAtendimentoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ajustes-horarios")
// API JSON de horários de atendimento (escrita restrita a ADMIN/OPERADOR via SecurityConfig).
// A tela vive em HomeController (/ajustes-horarios).
public class HorarioAtendimentoController {

    @Autowired
    private HorarioAtendimentoService horarioAtendimentoService;

    @GetMapping
    public List<HorarioAtendimento> listarHorarios() {
        return horarioAtendimentoService.listarTodos();
    }

    @PostMapping
    public HorarioAtendimento salvarHorario(@RequestBody HorarioAtendimento horarioAtendimento) {
        return horarioAtendimentoService.salvar(horarioAtendimento);
    }

    @DeleteMapping("/{id}")
    public void deletarHorario(@PathVariable String id) {
        horarioAtendimentoService.deletar(id);
    }
}
