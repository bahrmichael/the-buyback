/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { TheBuybackTestModule } from '../../../test.module';
import { TypeBuybackRateDialogComponent } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate-dialog.component';
import { TypeBuybackRateService } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.service';
import { TypeBuybackRate } from '../../../../../../main/webapp/app/entities/type-buyback-rate/type-buyback-rate.model';

describe('Component Tests', () => {

    describe('TypeBuybackRate Management Dialog Component', () => {
        let comp: TypeBuybackRateDialogComponent;
        let fixture: ComponentFixture<TypeBuybackRateDialogComponent>;
        let service: TypeBuybackRateService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [TypeBuybackRateDialogComponent],
                providers: [
                    TypeBuybackRateService
                ]
            })
            .overrideTemplate(TypeBuybackRateDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(TypeBuybackRateDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(TypeBuybackRateService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new TypeBuybackRate('123');
                        spyOn(service, 'update').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.typeBuybackRate = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.update).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'typeBuybackRateListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );

            it('Should call create service on save for new entity',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        const entity = new TypeBuybackRate();
                        spyOn(service, 'create').and.returnValue(Observable.of(new HttpResponse({body: entity})));
                        comp.typeBuybackRate = entity;
                        // WHEN
                        comp.save();
                        tick(); // simulate async

                        // THEN
                        expect(service.create).toHaveBeenCalledWith(entity);
                        expect(comp.isSaving).toEqual(false);
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalledWith({ name: 'typeBuybackRateListModification', content: 'OK'});
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
