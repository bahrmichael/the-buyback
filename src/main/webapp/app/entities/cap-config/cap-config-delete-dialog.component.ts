import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CapConfig } from './cap-config.model';
import { CapConfigPopupService } from './cap-config-popup.service';
import { CapConfigService } from './cap-config.service';

@Component({
    selector: 'jhi-cap-config-delete-dialog',
    templateUrl: './cap-config-delete-dialog.component.html'
})
export class CapConfigDeleteDialogComponent {

    capConfig: CapConfig;

    constructor(
        private capConfigService: CapConfigService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.capConfigService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'capConfigListModification',
                content: 'Deleted an capConfig'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-cap-config-delete-popup',
    template: ''
})
export class CapConfigDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private capConfigPopupService: CapConfigPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.capConfigPopupService
                .open(CapConfigDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
