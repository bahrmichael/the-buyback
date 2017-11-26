import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

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
        private alertService: JhiAlertService,
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

    private subscribeToSaveResponse(result: Observable<CapConfig>) {
        result.subscribe((res: CapConfig) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: CapConfig) {
        this.eventManager.broadcast({ name: 'capConfigListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
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