/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { TheBuybackTestModule } from '../../../test.module';
import { ManufacturingOrderDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order-delete-dialog.component';
import { ManufacturingOrderService } from '../../../../../../main/webapp/app/entities/manufacturing-order/manufacturing-order.service';

describe('Component Tests', () => {

    describe('ManufacturingOrder Management Delete Component', () => {
        let comp: ManufacturingOrderDeleteDialogComponent;
        let fixture: ComponentFixture<ManufacturingOrderDeleteDialogComponent>;
        let service: ManufacturingOrderService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [ManufacturingOrderDeleteDialogComponent],
                providers: [
                    ManufacturingOrderService
                ]
            })
            .overrideTemplate(ManufacturingOrderDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ManufacturingOrderDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ManufacturingOrderService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete('123');
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith('123');
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
