import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { TypeBuybackRate } from './type-buyback-rate.model';
import { TypeBuybackRateService } from './type-buyback-rate.service';

@Injectable()
export class TypeBuybackRatePopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private modalService: NgbModal,
        private router: Router,
        private typeBuybackRateService: TypeBuybackRateService

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
                this.typeBuybackRateService.find(id)
                    .subscribe((typeBuybackRateResponse: HttpResponse<TypeBuybackRate>) => {
                        const typeBuybackRate: TypeBuybackRate = typeBuybackRateResponse.body;
                        this.ngbModalRef = this.typeBuybackRateModalRef(component, typeBuybackRate);
                        resolve(this.ngbModalRef);
                    });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.typeBuybackRateModalRef(component, new TypeBuybackRate());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    typeBuybackRateModalRef(component: Component, typeBuybackRate: TypeBuybackRate): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.typeBuybackRate = typeBuybackRate;
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
