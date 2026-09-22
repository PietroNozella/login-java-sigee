package com.pfc.sigee.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Expõe o nome do tema visual ativo para todos os templates Thymeleaf.
// O layout usa "tema-<nome>" como classe do <body>; a troca de tema
// é só um novo arquivo static/css/tema-<nome>.css + APP_TEMA_NOME no .env.
@ControllerAdvice
public class TemaControllerAdvice {

    @Value("${app.tema.nome:padrao}")
    private String temaNome;

    @ModelAttribute("temaNome")
    public String temaNome() {
        return temaNome;
    }
}
