import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RecipesDetailComponent } from './recipes-detail.component';

describe('Recipes Management Detail Component', () => {
  let comp: RecipesDetailComponent;
  let fixture: ComponentFixture<RecipesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecipesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ recipes: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RecipesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RecipesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load recipes on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.recipes).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
