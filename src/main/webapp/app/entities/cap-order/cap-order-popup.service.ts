import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { CapOrder } from './cap-order.model';
import { CapOrderService } from './cap-order.service';

@Injectable()
export class CapOrderPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private capOrderService: CapOrderService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.capOrderService.find(id)
                    .subscribe((capOrderResponse: HttpResponse<CapOrder>) => {
                        const capOrder: CapOrder = capOrderResponse.body;
                        capOrder.created = this.datePipe
                            .transform(capOrder.created, 'yyyy-MM-ddTHH:mm:ss');
                        this.ngbModalRef = this.capOrderModalRef(component, capOrder);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.capOrderModalRef(component, new CapOrder());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    capOrderModalRef(component: Component, capOrder: CapOrder): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.capOrder = capOrder;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
