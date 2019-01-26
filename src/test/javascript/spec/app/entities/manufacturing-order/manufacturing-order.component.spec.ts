/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TheBuybackTestModule } from '../../../test.module';
import { ManufacturingOrderComponent } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.component';
import { ManufacturingOrderService } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.service';
import { ManufacturingOrder } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.model';

describe('Component Tests', () => {

    describe('ManufacturingOrder Management Component', () => {
        let comp: ManufacturingOrderComponent;
        let fixture: ComponentFixture<ManufacturingOrderComponent>;
        let service: ManufacturingOrderService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [ManufacturingOrderComponent],
                providers: [
                    ManufacturingOrderService
                ]
            })
            .overrideTemplate(ManufacturingOrderComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ManufacturingOrderComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ManufacturingOrderService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new ManufacturingOrder('123')],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.manufacturingOrders[0]).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
