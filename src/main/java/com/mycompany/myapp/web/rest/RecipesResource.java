package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Recipes;
import com.mycompany.myapp.repository.RecipesRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Recipes}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RecipesResource {

    private final Logger log = LoggerFactory.getLogger(RecipesResource.class);

    private static final String ENTITY_NAME = "recipes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecipesRepository recipesRepository;

    public RecipesResource(RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    /**
     * {@code POST  /recipes} : Create a new recipes.
     *
     * @param recipes the recipes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipes, or with status {@code 400 (Bad Request)} if the recipes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipes")
    @PreAuthorize("hasAuthority('ROLE_CHEF')")
    public ResponseEntity<Recipes> createRecipes(@Valid @RequestBody Recipes recipes) throws URISyntaxException {
        log.debug("REST request to save Recipes : {}", recipes);
        if (recipes.getId() != null) {
            throw new BadRequestAlertException("A new recipes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Recipes result = recipesRepository.save(recipes);
        return ResponseEntity
            .created(new URI("/api/recipes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /recipes/:id} : Updates an existing recipes.
     *
     * @param id the id of the recipes to save.
     * @param recipes the recipes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipes,
     * or with status {@code 400 (Bad Request)} if the recipes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipes/{id}")
    @PreAuthorize("hasAuthority('ROLE_CHEF')")
    public ResponseEntity<Recipes> updateRecipes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Recipes recipes
    ) throws URISyntaxException {
        log.debug("REST request to update Recipes : {}, {}", id, recipes);
        if (recipes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Recipes result = recipesRepository.save(recipes);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipes.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /recipes/:id} : Partial updates given fields of an existing recipes, field will ignore if it is null
     *
     * @param id the id of the recipes to save.
     * @param recipes the recipes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipes,
     * or with status {@code 400 (Bad Request)} if the recipes is not valid,
     * or with status {@code 404 (Not Found)} if the recipes is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_CHEF')")
    public ResponseEntity<Recipes> partialUpdateRecipes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Recipes recipes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Recipes partially : {}, {}", id, recipes);
        if (recipes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Recipes> result = recipesRepository
            .findById(recipes.getId())
            .map(existingRecipes -> {
                if (recipes.getRecipesName() != null) {
                    existingRecipes.setRecipesName(recipes.getRecipesName());
                }

                return existingRecipes;
            })
            .map(recipesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipes.getId().toString())
        );
    }

    /**
     * {@code GET  /recipes} : get all the recipes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipes in body.
     */
    @GetMapping("/recipes")
    public ResponseEntity<List<Recipes>> getAllRecipes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Recipes");
        Page<Recipes> page = recipesRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /recipes/:id} : get the "id" recipes.
     *
     * @param id the id of the recipes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipes/{id}")
    public ResponseEntity<Recipes> getRecipes(@PathVariable Long id) {
        log.debug("REST request to get Recipes : {}", id);
        Optional<Recipes> recipes = recipesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(recipes);
    }

    /**
     * {@code DELETE  /recipes/:id} : delete the "id" recipes.
     *
     * @param id the id of the recipes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipes/{id}")
    @PreAuthorize("hasAuthority('ROLE_CHEF')")
    public ResponseEntity<Void> deleteRecipes(@PathVariable Long id) {
        log.debug("REST request to delete Recipes : {}", id);
        recipesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
