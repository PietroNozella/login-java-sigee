package com.pfc.sigee.controller;

import com.pfc.sigee.entity.Cliente;
import com.pfc.sigee.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
// API JSON de clientes (mesma regra de acesso da API de chamados).
// A tela de listagem vive em HomeController (/clientes).
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public Cliente novoCliente(@RequestBody Cliente cliente) {
        return clienteService.novoCliente(cliente);
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }
}
