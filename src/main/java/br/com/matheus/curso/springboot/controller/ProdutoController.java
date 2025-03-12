package br.com.matheus.curso.springboot.controller;

import br.com.matheus.curso.springboot.dto.ProdutoDTO;
import br.com.matheus.curso.springboot.model.Produto;
import br.com.matheus.curso.springboot.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProdutoController {

    @Autowired
    ProdutoRepository produtoRepository;

    @PostMapping("/produto")
    public ResponseEntity<Produto> salvarProduto(@RequestBody @Valid ProdutoDTO produtoDTO) {
        var produto = new Produto();
        BeanUtils.copyProperties(produtoDTO, produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produto));
    }

    @GetMapping("/produto")
    public ResponseEntity<List<Produto>> buscarTodosProdutos() {
        List<Produto> listaProduto = produtoRepository.findAll();
        if(!listaProduto.isEmpty()){
            for (Produto produto : listaProduto){
                UUID id = produto.getId();
                produto.add(linkTo(methodOn(ProdutoController.class).buscarUmProduto(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(listaProduto);
    }

    @GetMapping("/produto/{id}")
    public ResponseEntity<Object> buscarUmProduto(@PathVariable(value = "id") UUID id) {
        Optional<Produto> produt0 = produtoRepository.findById(id);
        if (produt0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
        produt0.get().add(linkTo(methodOn(ProdutoController.class).buscarTodosProdutos()).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(produt0.get());
    }

    @PutMapping("/produto/{id}")
    public ResponseEntity<Object> atualizarProduto(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProdutoDTO produtoDTO) {

        Optional<Produto> produt0 = produtoRepository.findById(id);
        if (produt0.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado para atualização");
        }
        var produto = produt0.get();
        BeanUtils.copyProperties(produtoDTO, produto);
        return ResponseEntity.status(HttpStatus.OK).body(produtoRepository.save(produto));

    }

    @DeleteMapping("/produto/{id}")
    public ResponseEntity<Object> deletarPRoduto(@PathVariable(value = "id") UUID id){
        Optional<Produto> produt0 = produtoRepository.findById(id);
        if (produt0.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encotrado");
        }
        produtoRepository.delete(produt0.get());
        return ResponseEntity.status(HttpStatus.OK).body("Produto deletado com Sucesso");
    }
}

