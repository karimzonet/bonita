import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'recipes',
        data: { pageTitle: 'bonitaApp.recipes.home.title' },
        loadChildren: () => import('./recipes/recipes.module').then(m => m.RecipesModule),
      },
      {
        path: 'comment',
        data: { pageTitle: 'bonitaApp.comment.home.title' },
        loadChildren: () => import('./comment/comment.module').then(m => m.CommentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
