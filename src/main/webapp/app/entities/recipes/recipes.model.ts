import { IUser } from 'app/entities/user/user.model';

export interface IRecipes {
  id?: number;
  recipesName?: string;
  user?: IUser | null;
}

export class Recipes implements IRecipes {
  constructor(public id?: number, public recipesName?: string, public user?: IUser | null) {}
}

export function getRecipesIdentifier(recipes: IRecipes): number | undefined {
  return recipes.id;
}
