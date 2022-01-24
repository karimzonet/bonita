import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RecipesComponent } from './list/recipes.component';
import { RecipesDetailComponent } from './detail/recipes-detail.component';
import { RecipesUpdateComponent } from './update/recipes-update.component';
import { RecipesDeleteDialogComponent } from './delete/recipes-delete-dialog.component';
import { RecipesRoutingModule } from './route/recipes-routing.module';

@NgModule({
  imports: [SharedModule, RecipesRoutingModule],
  declarations: [RecipesComponent, RecipesDetailComponent, RecipesUpdateComponent, RecipesDeleteDialogComponent],
  entryComponents: [RecipesDeleteDialogComponent],
})
export class RecipesModule {}
