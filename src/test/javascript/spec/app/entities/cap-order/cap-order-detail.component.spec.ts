/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { TheBuybackTestModule } from '../../../test.module';
import { CapOrderDetailComponent } from '../../../../../../main/webapp/app/entities/cap-order/cap-order-detail.component';
import { CapOrderService } from '../../../../../../main/webapp/app/entities/cap-order/cap-order.service';
import { CapOrder } from '../../../../../../main/webapp/app/entities/cap-order/cap-order.model';

describe('Component Tests', () => {

    describe('CapOrder Management Detail Component', () => {
        let comp: CapOrderDetailComponent;
        let fixture: ComponentFixture<CapOrderDetailComponent>;
        let service: CapOrderService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [CapOrderDetailComponent],
                providers: [
                    CapOrderService
                ]
            })
            .overrideTemplate(CapOrderDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(CapOrderDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CapOrderService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new CapOrder('123')
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith('123');
                expect(comp.capOrder).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
