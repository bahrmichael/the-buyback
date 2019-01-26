import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { TypeBuybackRate } from './type-buyback-rate.model';
import { TypeBuybackRateService } from './type-buyback-rate.service';

@Component({
    selector: 'jhi-type-buyback-rate-detail',
    templateUrl: './type-buyback-rate-detail.component.html'
})
export class TypeBuybackRateDetailComponent implements OnInit, OnDestroy {

    typeBuybackRate: TypeBuybackRate;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private typeBuybackRateService: TypeBuybackRateService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInTypeBuybackRates();
    }

    load(id) {
        this.typeBuybackRateService.find(id)
            .subscribe((typeBuybackRateResponse: HttpResponse<TypeBuybackRate>) => {
                this.typeBuybackRate = typeBuybackRateResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInTypeBuybackRates() {
        this.eventSubscriber = this.eventManager.subscribe(
            'typeBuybackRateListModification',
            (response) => this.load(this.typeBuybackRate.id)
        );
    }
}
