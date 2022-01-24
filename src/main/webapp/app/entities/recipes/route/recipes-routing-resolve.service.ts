import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRecipes, Recipes } from '../recipes.model';
import { RecipesService } from '../service/recipes.service';

@Injectable({ providedIn: 'root' })
export class RecipesRoutingResolveService implements Resolve<IRecipes> {
  constructor(protected service: RecipesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRecipes> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((recipes: HttpResponse<Recipes>) => {
          if (recipes.body) {
            return of(recipes.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Recipes());
  }
}
