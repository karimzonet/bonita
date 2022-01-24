import { IRecipes } from 'app/entities/recipes/recipes.model';

export interface IComment {
  id?: number;
  commentContent?: string;
  recipes?: IRecipes | null;
}

export class Comment implements IComment {
  constructor(public id?: number, public commentContent?: string, public recipes?: IRecipes | null) {}
}

export function getCommentIdentifier(comment: IComment): number | undefined {
  return comment.id;
}
