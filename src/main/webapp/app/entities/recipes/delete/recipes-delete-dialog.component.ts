import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRecipes } from '../recipes.model';
import { RecipesService } from '../service/recipes.service';

@Component({
  templateUrl: './recipes-delete-dialog.component.html',
})
export class RecipesDeleteDialogComponent {
  recipes?: IRecipes;

  constructor(protected recipesService: RecipesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.recipesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
