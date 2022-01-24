package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recipes;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Recipes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipesRepository extends JpaRepository<Recipes, Long> {
    @Query("select recipes from Recipes recipes where recipes.user.login = ?#{principal.username}")
    List<Recipes> findByUserIsCurrentUser();
}
