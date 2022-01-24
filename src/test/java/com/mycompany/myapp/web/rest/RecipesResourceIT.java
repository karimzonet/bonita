package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Recipes;
import com.mycompany.myapp.repository.RecipesRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RecipesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipesResourceIT {

    private static final String DEFAULT_RECIPES_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RECIPES_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RECIPES_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_RECIPES_AUTHOR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipesRepository recipesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipesMockMvc;

    private Recipes recipes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipes createEntity(EntityManager em) {
        Recipes recipes = new Recipes().recipesName(DEFAULT_RECIPES_NAME);
        return recipes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipes createUpdatedEntity(EntityManager em) {
        Recipes recipes = new Recipes().recipesName(UPDATED_RECIPES_NAME);
        return recipes;
    }

    @BeforeEach
    public void initTest() {
        recipes = createEntity(em);
    }

    @Test
    @Transactional
    void createRecipes() throws Exception {
        int databaseSizeBeforeCreate = recipesRepository.findAll().size();
        // Create the Recipes
        restRecipesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipes)))
            .andExpect(status().isCreated());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeCreate + 1);
        Recipes testRecipes = recipesList.get(recipesList.size() - 1);
        assertThat(testRecipes.getRecipesName()).isEqualTo(DEFAULT_RECIPES_NAME);
    }

    @Test
    @Transactional
    void createRecipesWithExistingId() throws Exception {
        // Create the Recipes with an existing ID
        recipes.setId(1L);

        int databaseSizeBeforeCreate = recipesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipes)))
            .andExpect(status().isBadRequest());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRecipesNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipesRepository.findAll().size();
        // set the field null
        recipes.setRecipesName(null);

        // Create the Recipes, which fails.

        restRecipesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipes)))
            .andExpect(status().isBadRequest());

        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecipes() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        // Get all the recipesList
        restRecipesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipes.getId().intValue())))
            .andExpect(jsonPath("$.[*].recipesName").value(hasItem(DEFAULT_RECIPES_NAME)));
    }

    @Test
    @Transactional
    void getRecipes() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        // Get the recipes
        restRecipesMockMvc
            .perform(get(ENTITY_API_URL_ID, recipes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipes.getId().intValue()))
            .andExpect(jsonPath("$.recipesName").value(DEFAULT_RECIPES_NAME));
    }

    @Test
    @Transactional
    void getNonExistingRecipes() throws Exception {
        // Get the recipes
        restRecipesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRecipes() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();

        // Update the recipes
        Recipes updatedRecipes = recipesRepository.findById(recipes.getId()).get();
        // Disconnect from session so that the updates on updatedRecipes are not directly saved in db
        em.detach(updatedRecipes);
        updatedRecipes.recipesName(UPDATED_RECIPES_NAME);

        restRecipesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecipes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRecipes))
            )
            .andExpect(status().isOk());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
        Recipes testRecipes = recipesList.get(recipesList.size() - 1);
        assertThat(testRecipes.getRecipesName()).isEqualTo(UPDATED_RECIPES_NAME);
    }

    @Test
    @Transactional
    void putNonExistingRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, recipes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(recipes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecipesWithPatch() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();

        // Update the recipes using partial update
        Recipes partialUpdatedRecipes = new Recipes();
        partialUpdatedRecipes.setId(recipes.getId());

        partialUpdatedRecipes.recipesName(UPDATED_RECIPES_NAME);

        restRecipesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipes))
            )
            .andExpect(status().isOk());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
        Recipes testRecipes = recipesList.get(recipesList.size() - 1);
        assertThat(testRecipes.getRecipesName()).isEqualTo(UPDATED_RECIPES_NAME);
    }

    @Test
    @Transactional
    void fullUpdateRecipesWithPatch() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();

        // Update the recipes using partial update
        Recipes partialUpdatedRecipes = new Recipes();
        partialUpdatedRecipes.setId(recipes.getId());

        partialUpdatedRecipes.recipesName(UPDATED_RECIPES_NAME);

        restRecipesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecipes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipes))
            )
            .andExpect(status().isOk());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
        Recipes testRecipes = recipesList.get(recipesList.size() - 1);
        assertThat(testRecipes.getRecipesName()).isEqualTo(UPDATED_RECIPES_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recipes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(recipes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipes() throws Exception {
        int databaseSizeBeforeUpdate = recipesRepository.findAll().size();
        recipes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(recipes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recipes in the database
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecipes() throws Exception {
        // Initialize the database
        recipesRepository.saveAndFlush(recipes);

        int databaseSizeBeforeDelete = recipesRepository.findAll().size();

        // Delete the recipes
        restRecipesMockMvc
            .perform(delete(ENTITY_API_URL_ID, recipes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recipes> recipesList = recipesRepository.findAll();
        assertThat(recipesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
