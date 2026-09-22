package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
// Cadastro e listagem de clientes via ClienteRepository (coleção "clientes").
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente novoCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
}
