/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { TheBuybackTestModule } from '../../../test.module';
import { TypeBuybackRateDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate-delete-dialog.component';
import { TypeBuybackRateService } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.service';

describe('Component Tests', () => {

    describe('TypeBuybackRate Management Delete Component', () => {
        let comp: TypeBuybackRateDeleteDialogComponent;
        let fixture: ComponentFixture<TypeBuybackRateDeleteDialogComponent>;
        let service: TypeBuybackRateService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [TypeBuybackRateDeleteDialogComponent],
                providers: [
                    TypeBuybackRateService
                ]
            })
            .overrideTemplate(TypeBuybackRateDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(TypeBuybackRateDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TypeBuybackRateService);
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
