import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RecipesComponent } from '../list/recipes.component';
import { RecipesDetailComponent } from '../detail/recipes-detail.component';
import { RecipesUpdateComponent } from '../update/recipes-update.component';
import { RecipesRoutingResolveService } from './recipes-routing-resolve.service';

const recipesRoute: Routes = [
  {
    path: '',
    component: RecipesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RecipesDetailComponent,
    resolve: {
      recipes: RecipesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RecipesUpdateComponent,
    resolve: {
      recipes: RecipesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RecipesUpdateComponent,
    resolve: {
      recipes: RecipesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(recipesRoute)],
  exports: [RouterModule],
})
export class RecipesRoutingModule {}
