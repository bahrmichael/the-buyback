/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { TheBuybackTestModule } from '../../../test.module';
import { ManufacturingOrderDetailComponent } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order-detail.component';
import { ManufacturingOrderService } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.service';
import { ManufacturingOrder } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.model';

describe('Component Tests', () => {

    describe('ManufacturingOrder Management Detail Component', () => {
        let comp: ManufacturingOrderDetailComponent;
        let fixture: ComponentFixture<ManufacturingOrderDetailComponent>;
        let service: ManufacturingOrderService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [ManufacturingOrderDetailComponent],
                providers: [
                    ManufacturingOrderService
                ]
            })
            .overrideTemplate(ManufacturingOrderDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ManufacturingOrderDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ManufacturingOrderService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new ManufacturingOrder('123')
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith('123');
                expect(comp.manufacturingOrder).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
