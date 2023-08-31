package com.example.demo.Controller;

import com.example.demo.model.person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.personRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.RequestMapping;



import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/prueba")
public class personController {
    @Autowired
    private personRepository repository;


    public personController(personRepository repository) {
        this.repository = repository;
    }

    //paginacion
    @GetMapping("/persons")
    public Page<person> allPersons(Pageable pageable) {
        return repository.findAll(pageable);
    }


    //Listar todas
    @GetMapping("/allpersons")
    public List<person> allPersons(){
        return repository.findAll();
    }

    //traer por un ID dad
    @GetMapping("/persons/{id}")
    public ResponseEntity<person> getPersonById(@PathVariable Long id) {
        Optional<person> personOptional = repository.findById(id);

        return personOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    //agregar nuevo usuario
    @PostMapping("/person")
    public person createPerson(@RequestBody person person) {
        return repository.save(person);
    }

    //modificar el estado
    @PutMapping("/persons/{id}/update-state")
    public ResponseEntity<String> updatePersonState(@PathVariable Long id, @RequestBody Map<String, String> updateData) {
        Optional<person> personOptional = repository.findById(id);

        if (personOptional.isPresent()) {
            person person = personOptional.get();

            // Validación para asegurarse de que solo se modifique el campo de estado
            if (updateData.containsKey("estado")) {
                String newEstado = updateData.get("estado");
                person.setEstado(newEstado);

                repository.save(person); // Guardar los cambios en la base de datos

                return ResponseEntity.ok("Estado actualizado correctamente.");
            } else {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud debe contener el campo 'estado'.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
