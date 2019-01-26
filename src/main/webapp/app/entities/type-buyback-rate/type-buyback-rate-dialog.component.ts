import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { TypeBuybackRate } from './type-buyback-rate.model';
import { TypeBuybackRatePopupService } from './type-buyback-rate-popup.service';
import { TypeBuybackRateService } from './type-buyback-rate.service';

@Component({
    selector: 'jhi-type-buyback-rate-dialog',
    templateUrl: './type-buyback-rate-dialog.component.html'
})
export class TypeBuybackRateDialogComponent implements OnInit {

    typeBuybackRate: TypeBuybackRate;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private typeBuybackRateService: TypeBuybackRateService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.typeBuybackRate.id !== undefined) {
            this.subscribeToSaveResponse(
                this.typeBuybackRateService.update(this.typeBuybackRate));
        } else {
            this.subscribeToSaveResponse(
                this.typeBuybackRateService.create(this.typeBuybackRate));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<TypeBuybackRate>>) {
        result.subscribe((res: HttpResponse<TypeBuybackRate>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: TypeBuybackRate) {
        this.eventManager.broadcast({ name: 'typeBuybackRateListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-type-buyback-rate-popup',
    template: ''
})
export class TypeBuybackRatePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private typeBuybackRatePopupService: TypeBuybackRatePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.typeBuybackRatePopupService
                    .open(TypeBuybackRateDialogComponent as Component, params['id']);
            } else {
                this.typeBuybackRatePopupService
                    .open(TypeBuybackRateDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
