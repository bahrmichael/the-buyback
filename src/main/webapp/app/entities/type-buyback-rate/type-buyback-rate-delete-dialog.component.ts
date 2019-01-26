import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { TypeBuybackRate } from './type-buyback-rate.model';
import { TypeBuybackRatePopupService } from './type-buyback-rate-popup.service';
import { TypeBuybackRateService } from './type-buyback-rate.service';

@Component({
    selector: 'jhi-type-buyback-rate-delete-dialog',
    templateUrl: './type-buyback-rate-delete-dialog.component.html'
})
export class TypeBuybackRateDeleteDialogComponent {

    typeBuybackRate: TypeBuybackRate;

    constructor(
        private typeBuybackRateService: TypeBuybackRateService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.typeBuybackRateService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'typeBuybackRateListModification',
                content: 'Deleted an typeBuybackRate'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-type-buyback-rate-delete-popup',
    template: ''
})
export class TypeBuybackRateDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private typeBuybackRatePopupService: TypeBuybackRatePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.typeBuybackRatePopupService
                .open(TypeBuybackRateDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
