import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { CapOrder } from './cap-order.model';
import { CapOrderPopupService } from './cap-order-popup.service';
import { CapOrderService } from './cap-order.service';

@Component({
    selector: 'jhi-cap-order-delete-dialog',
    templateUrl: './cap-order-delete-dialog.component.html'
})
export class CapOrderDeleteDialogComponent {

    capOrder: CapOrder;

    constructor(
        private capOrderService: CapOrderService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.capOrderService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'capOrderListModification',
                content: 'Deleted an capOrder'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-cap-order-delete-popup',
    template: ''
})
export class CapOrderDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private capOrderPopupService: CapOrderPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.capOrderPopupService
                .open(CapOrderDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
