package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// CRUD MVC mínimo dos chamados (telas). A API JSON vive em /api/chamados.
@Controller
@RequestMapping("/chamados")
public class ChamadoViewController {

    @Autowired
    private ChamadoService chamadoService;

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("chamado", new Chamado());
        return "chamados/novo";
    }

    @PostMapping("/novo")
    public String novo(@ModelAttribute Chamado chamado) {
        if (chamado.getStatus() == null || chamado.getStatus().isBlank()) {
            chamado.setStatus("ABERTO");
        }
        chamadoService.criarChamado(chamado);
        return "redirect:/chamados";
    }

    @GetMapping("/atualizar/{id}")
    public String atualizarForm(@PathVariable String id, Model model) {
        model.addAttribute("chamado", chamadoService.buscarPorId(id));
        return "chamados/atualizar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable String id, @ModelAttribute Chamado chamado) {
        chamadoService.atualizarChamado(id, chamado);
        return "redirect:/chamados";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable String id) {
        chamadoService.cancelarChamado(id);
        return "redirect:/chamados";
    }
}
