import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRecipes } from '../recipes.model';

@Component({
  selector: 'jhi-recipes-detail',
  templateUrl: './recipes-detail.component.html',
})
export class RecipesDetailComponent implements OnInit {
  recipes: IRecipes | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ recipes }) => {
      this.recipes = recipes;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
