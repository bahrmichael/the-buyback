import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CapConfig } from './cap-config.model';
import { CapConfigPopupService } from './cap-config-popup.service';
import { CapConfigService } from './cap-config.service';

@Component({
    selector: 'jhi-cap-config-dialog',
    templateUrl: './cap-config-dialog.component.html'
})
export class CapConfigDialogComponent implements OnInit {

    capConfig: CapConfig;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private capConfigService: CapConfigService,
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
        if (this.capConfig.id !== undefined) {
            this.subscribeToSaveResponse(
                this.capConfigService.update(this.capConfig));
        } else {
            this.subscribeToSaveResponse(
                this.capConfigService.create(this.capConfig));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<CapConfig>>) {
        result.subscribe((res: HttpResponse<CapConfig>) =>
            this.onSaveSuccess(res.body), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess(result: CapConfig) {
        this.eventManager.broadcast({ name: 'capConfigListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-cap-config-popup',
    template: ''
})
export class CapConfigPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private capConfigPopupService: CapConfigPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.capConfigPopupService
                    .open(CapConfigDialogComponent as Component, params['id']);
            } else {
                this.capConfigPopupService
                    .open(CapConfigDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
