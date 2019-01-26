import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager } from 'ng-jhipster';

import { CapConfig } from './cap-config.model';
import { CapConfigService } from './cap-config.service';

@Component({
    selector: 'jhi-cap-config-detail',
    templateUrl: './cap-config-detail.component.html'
})
export class CapConfigDetailComponent implements OnInit, OnDestroy {

    capConfig: CapConfig;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private capConfigService: CapConfigService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInCapConfigs();
    }

    load(id) {
        this.capConfigService.find(id)
            .subscribe((capConfigResponse: HttpResponse<CapConfig>) => {
                this.capConfig = capConfigResponse.body;
            });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInCapConfigs() {
        this.eventSubscriber = this.eventManager.subscribe(
            'capConfigListModification',
            (response) => this.load(this.capConfig.id)
        );
    }
}
