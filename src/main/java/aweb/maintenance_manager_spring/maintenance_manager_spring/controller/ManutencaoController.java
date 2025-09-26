package br.com.aweb.maintenance_manager_spring.controller;

import java.time.LocalDateTime;
import java.util.Map;

import br.com.aweb.maintenance_manager_spring.model.Manutencao;
import br.com.aweb.maintenance_manager_spring.repository.ManutencaoRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/manutencoes")
public class ManutencaoController {

    @Autowired
    private ManutencaoRepository manutencaoRepository;

    // ----------------- LISTAR -----------------
    @GetMapping
    public ModelAndView list() {
        // Retorna todas as manutenções, ordenadas da mais recente para a mais antiga
        var manutencoes = manutencaoRepository.findAll(Sort.by("dataHoraSolicitacao").descending());
        return new ModelAndView("list", Map.of("manutencoes", manutencoes));
    }

    // ----------------- CRIAR -----------------
    @GetMapping("/create")
    public ModelAndView createForm() {
        // Exibe o formulário vazio para criação de uma nova manutenção
        return new ModelAndView("form", Map.of("manutencao", new Manutencao()));
    }

    @PostMapping("/create")
    public String create(@Valid Manutencao manutencao, BindingResult result) {
        if (result.hasErrors()) {
            // Caso haja erros de validação, retorna ao formulário
            return "form";
        }
        // Define a data/hora da solicitação como o momento atual
        manutencao.setDataHoraSolicitacao(LocalDateTime.now());
        manutencaoRepository.save(manutencao);
        return "redirect:/manutencoes";
    }

    // ----------------- EDITAR -----------------
    @GetMapping("/edit/{id}")
    public ModelAndView editForm(@PathVariable Long id) {
        // Busca a manutenção pelo ID
        var manutencaoOpt = manutencaoRepository.findById(id);
        if (manutencaoOpt.isPresent() && manutencaoOpt.get().getDataHoraConclusao() == null) {
            // Só permite editar se ainda não tiver sido concluída
            return new ModelAndView("form", Map.of("manutencao", manutencaoOpt.get()));
        }
        // Retorna erro 404 caso não encontre ou já tenha sido finalizada
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid Manutencao manutencao, BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        }
        // Atualiza a manutenção no banco
        manutencaoRepository.save(manutencao);
        return "redirect:/manutencoes";
    }

    // ----------------- DELETAR -----------------
    @GetMapping("/delete/{id}")
    public ModelAndView deleteForm(@PathVariable Long id) {
        // Busca a manutenção pelo ID para confirmação
        var manutencaoOpt = manutencaoRepository.findById(id);
        if (manutencaoOpt.isPresent()) {
            return new ModelAndView("delete", Map.of("manutencao", manutencaoOpt.get()));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete/{id}")
    public String delete(Manutencao manutencao) {
        // Remove a manutenção selecionada
        manutencaoRepository.delete(manutencao);
        return "redirect:/manutencoes";
    }

    // ----------------- FINALIZAR -----------------
    @PostMapping("/finish/{id}")
    public String finish(@PathVariable Long id) {
        // Busca a manutenção pelo ID
        var manutencaoOpt = manutencaoRepository.findById(id);
        if (manutencaoOpt.isPresent()) {
            var manutencao = manutencaoOpt.get();
            if (manutencao.getDataHoraConclusao() == null) {
                // Marca a data/hora de conclusão se ainda não finalizada
                manutencao.setDataHoraConclusao(LocalDateTime.now());
                manutencaoRepository.save(manutencao);
                return "redirect:/manutencoes";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
