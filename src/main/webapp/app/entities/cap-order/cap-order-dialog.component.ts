import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CapOrder } from './cap-order.model';
import { CapOrderPopupService } from './cap-order-popup.service';
import { CapOrderService } from './cap-order.service';

@Component({
    selector: 'jhi-cap-order-dialog',
    templateUrl: './cap-order-dialog.component.html'
})
export class CapOrderDialogComponent implements OnInit {

    capOrder: CapOrder;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private capOrderService: CapOrderService,
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
        if (this.capOrder.id !== undefined) {
            this.subscribeToSaveResponse(
                this.capOrderService.update(this.capOrder));
        } else {
            this.subscribeToSaveResponse(
                this.capOrderService.create(this.capOrder));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<CapOrder>>) {
        result.subscribe((res: HttpResponse<CapOrder>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: CapOrder) {
        this.eventManager.broadcast({ name: 'capOrderListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-cap-order-popup',
    template: ''
})
export class CapOrderPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private capOrderPopupService: CapOrderPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.capOrderPopupService
                    .open(CapOrderDialogComponent as Component, params['id']);
            } else {
                this.capOrderPopupService
                    .open(CapOrderDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
