import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRecipes, getRecipesIdentifier } from '../recipes.model';

export type EntityResponseType = HttpResponse<IRecipes>;
export type EntityArrayResponseType = HttpResponse<IRecipes[]>;

@Injectable({ providedIn: 'root' })
export class RecipesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/recipes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(recipes: IRecipes): Observable<EntityResponseType> {
    return this.http.post<IRecipes>(this.resourceUrl, recipes, { observe: 'response' });
  }

  update(recipes: IRecipes): Observable<EntityResponseType> {
    return this.http.put<IRecipes>(`${this.resourceUrl}/${getRecipesIdentifier(recipes) as number}`, recipes, { observe: 'response' });
  }

  partialUpdate(recipes: IRecipes): Observable<EntityResponseType> {
    return this.http.patch<IRecipes>(`${this.resourceUrl}/${getRecipesIdentifier(recipes) as number}`, recipes, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRecipes>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRecipes[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addRecipesToCollectionIfMissing(recipesCollection: IRecipes[], ...recipesToCheck: (IRecipes | null | undefined)[]): IRecipes[] {
    const recipes: IRecipes[] = recipesToCheck.filter(isPresent);
    if (recipes.length > 0) {
      const recipesCollectionIdentifiers = recipesCollection.map(recipesItem => getRecipesIdentifier(recipesItem)!);
      const recipesToAdd = recipes.filter(recipesItem => {
        const recipesIdentifier = getRecipesIdentifier(recipesItem);
        if (recipesIdentifier == null || recipesCollectionIdentifiers.includes(recipesIdentifier)) {
          return false;
        }
        recipesCollectionIdentifiers.push(recipesIdentifier);
        return true;
      });
      return [...recipesToAdd, ...recipesCollection];
    }
    return recipesCollection;
  }
}
