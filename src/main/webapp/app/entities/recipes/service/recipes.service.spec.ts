import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRecipes, Recipes } from '../recipes.model';

import { RecipesService } from './recipes.service';

describe('Recipes Service', () => {
  let service: RecipesService;
  let httpMock: HttpTestingController;
  let elemDefault: IRecipes;
  let expectedResult: IRecipes | IRecipes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RecipesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      recipesName: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Recipes', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Recipes()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Recipes', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          recipesName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Recipes', () => {
      const patchObject = Object.assign(
        {
          recipesName: 'BBBBBB',
        },
        new Recipes()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Recipes', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          recipesName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Recipes', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRecipesToCollectionIfMissing', () => {
      it('should add a Recipes to an empty array', () => {
        const recipes: IRecipes = { id: 123 };
        expectedResult = service.addRecipesToCollectionIfMissing([], recipes);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recipes);
      });

      it('should not add a Recipes to an array that contains it', () => {
        const recipes: IRecipes = { id: 123 };
        const recipesCollection: IRecipes[] = [
          {
            ...recipes,
          },
          { id: 456 },
        ];
        expectedResult = service.addRecipesToCollectionIfMissing(recipesCollection, recipes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Recipes to an array that doesn't contain it", () => {
        const recipes: IRecipes = { id: 123 };
        const recipesCollection: IRecipes[] = [{ id: 456 }];
        expectedResult = service.addRecipesToCollectionIfMissing(recipesCollection, recipes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recipes);
      });

      it('should add only unique Recipes to an array', () => {
        const recipesArray: IRecipes[] = [{ id: 123 }, { id: 456 }, { id: 24090 }];
        const recipesCollection: IRecipes[] = [{ id: 123 }];
        expectedResult = service.addRecipesToCollectionIfMissing(recipesCollection, ...recipesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const recipes: IRecipes = { id: 123 };
        const recipes2: IRecipes = { id: 456 };
        expectedResult = service.addRecipesToCollectionIfMissing([], recipes, recipes2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recipes);
        expect(expectedResult).toContain(recipes2);
      });

      it('should accept null and undefined values', () => {
        const recipes: IRecipes = { id: 123 };
        expectedResult = service.addRecipesToCollectionIfMissing([], null, recipes, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recipes);
      });

      it('should return initial array if no Recipes is added', () => {
        const recipesCollection: IRecipes[] = [{ id: 123 }];
        expectedResult = service.addRecipesToCollectionIfMissing(recipesCollection, undefined, null);
        expect(expectedResult).toEqual(recipesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
