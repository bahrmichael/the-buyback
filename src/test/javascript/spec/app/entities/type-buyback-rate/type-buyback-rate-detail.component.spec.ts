/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { TheBuybackTestModule } from '../../../test.module';
import { TypeBuybackRateDetailComponent } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate-detail.component';
import { TypeBuybackRateService } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.service';
import { TypeBuybackRate } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.model';

describe('Component Tests', () => {

    describe('TypeBuybackRate Management Detail Component', () => {
        let comp: TypeBuybackRateDetailComponent;
        let fixture: ComponentFixture<TypeBuybackRateDetailComponent>;
        let service: TypeBuybackRateService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [TypeBuybackRateDetailComponent],
                providers: [
                    TypeBuybackRateService
                ]
            })
            .overrideTemplate(TypeBuybackRateDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(TypeBuybackRateDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TypeBuybackRateService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new TypeBuybackRate('123')
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith('123');
                expect(comp.typeBuybackRate).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
