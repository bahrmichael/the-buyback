import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { CapOrder } from './cap-order.model';
import { CapOrderService } from './cap-order.service';

@Component({
    selector: 'jhi-cap-order-detail',
    templateUrl: './cap-order-detail.component.html'
})
export class CapOrderDetailComponent implements OnInit, OnDestroy {

    capOrder: CapOrder;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private capOrderService: CapOrderService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCapOrders();
    }

    load(id) {
        this.capOrderService.find(id)
            .subscribe((capOrderResponse: HttpResponse<CapOrder>) => {
                this.capOrder = capOrderResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCapOrders() {
        this.eventSubscriber = this.eventManager.subscribe(
            'capOrderListModification',
            (response) => this.load(this.capOrder.id)
        );
    }
}
