package com.example.demo.Controller;

import com.example.demo.model.person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.personRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prueba")
public class personController {
    @Autowired
    private personRepository repository;
    //Listar todas
    @GetMapping("/persons")
    public List<person> allPersons(){
        return repository.findAll();
    }

    //traer por un ID
    @GetMapping("/persons/{id}")
    public ResponseEntity<person> getPersonById(@PathVariable Long id) {
        Optional<person> personOptional = repository.findById(id);

        return personOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/person")
    public person createPerson(@RequestBody person person) {
        return repository.save(person);
    }

}
