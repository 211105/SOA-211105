package com.example.demo.Controller;

import com.example.demo.model.person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.personRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
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
    public ResponseEntity<List<person>> getAllPersons() {
        List<person> persons = repository.findAll();
        if (persons.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron datos");
        }
        return ResponseEntity.ok(persons);
    }

    //traer por un ID dad
    @GetMapping("/{id}")
    public ResponseEntity<person> getPersonById(@PathVariable Long id) {
        Optional<person> personOptional = repository.findById(id);

        if (personOptional.isPresent()) {
            return ResponseEntity.ok(personOptional.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada con ID: " + id);
        }
    }
    //agregar nuevo usuario
    /**
     * Crea una nueva persona.
     *
     * @param person Datos de la persona a crear.
     * @return ResponseEntity con la persona creada y el código de estado 201 (CREATED).
     * @throws URISyntaxException Si hay un problema con la URI de respuesta.
     */
    @PostMapping("/person")
    public ResponseEntity<person> createPerson(@Valid @RequestBody person person) throws URISyntaxException {
        person createdPerson = repository.save(person);
        //ResponseEntity 201
        return ResponseEntity.created(new URI("/api/person/" + createdPerson.getId()))
                .body(createdPerson);
    }
    //modificar el estado
    @PutMapping("/persons/{id}/update-state")
    public ResponseEntity<String> updatePersonState(@PathVariable Long id, @RequestBody Map<String, String> updateData) {
        Optional<person> personOptional = repository.findById(id);

        if (personOptional.isPresent()) {
            person person = personOptional.get();

            // Verificar si la solicitud contiene el campo 'estado' y si no es nulo ni una cadena vacía
            if (updateData.containsKey("estado")) {
                String newEstado = updateData.get("estado");

                if (newEstado != null && !newEstado.trim().isEmpty()) {
                    // Validar que el campo 'estado' no sea igual en todos sus caracteres (por ejemplo, "111")
                    if (!newEstado.matches("^\\d+$")) {
                        person.setEstado(newEstado);

                        repository.save(person); // Guardar los cambios en la base de datos

                        return ResponseEntity.ok("Estado actualizado correctamente.");
                    } else {
                        return ResponseEntity.badRequest().body("El campo 'estado' no puede consistir solo en números iguales.");
                    }
                } else {
                    return ResponseEntity.badRequest().body("El campo 'estado' no puede estar vacío ni ser nulo.");
                }
            } else {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud debe contener el campo 'estado'.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePersonById(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Persona eliminada con éxito.");
        } else {
            return ResponseEntity.ok("No se pudo encontrar una persona con ID: " + id);
        }
    }

}
