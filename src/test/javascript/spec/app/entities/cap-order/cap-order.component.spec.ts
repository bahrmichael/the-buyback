/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TheBuybackTestModule } from '../../../test.module';
import { CapOrderComponent } from '../../../../../../main/webapp/app/entities/cap-order/cap-order.component';
import { CapOrderService } from '../../../../../../main/webapp/app/entities/cap-order/cap-order.service';
import { CapOrder } from '../../../../../../main/webapp/app/entities/cap-order/cap-order.model';

describe('Component Tests', () => {

    describe('CapOrder Management Component', () => {
        let comp: CapOrderComponent;
        let fixture: ComponentFixture<CapOrderComponent>;
        let service: CapOrderService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [CapOrderComponent],
                providers: [
                    CapOrderService
                ]
            })
            .overrideTemplate(CapOrderComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(CapOrderComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CapOrderService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new CapOrder('123')],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.capOrders[0]).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
