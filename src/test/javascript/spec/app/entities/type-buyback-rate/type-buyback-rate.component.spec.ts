/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TheBuybackTestModule } from '../../../test.module';
import { TypeBuybackRateComponent } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.component';
import { TypeBuybackRateService } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.service';
import { TypeBuybackRate } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.model';

describe('Component Tests', () => {

    describe('TypeBuybackRate Management Component', () => {
        let comp: TypeBuybackRateComponent;
        let fixture: ComponentFixture<TypeBuybackRateComponent>;
        let service: TypeBuybackRateService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [TypeBuybackRateComponent],
                providers: [
                    TypeBuybackRateService
                ]
            })
            .overrideTemplate(TypeBuybackRateComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(TypeBuybackRateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TypeBuybackRateService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new TypeBuybackRate('123')],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.typeBuybackRates[0]).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
