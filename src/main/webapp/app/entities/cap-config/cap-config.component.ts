import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs/Subscription';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { CapConfig } from './cap-config.model';
import { CapConfigService } from './cap-config.service';
import { Principal } from '../../shared';

@Component({
    selector: 'jhi-cap-config',
    templateUrl: './cap-config.component.html'
})
export class CapConfigComponent implements OnInit, OnDestroy {
capConfigs: CapConfig[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private capConfigService: CapConfigService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.capConfigService.query().subscribe(
            (res: HttpResponse<CapConfig[]>) => {
                this.capConfigs = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInCapConfigs();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: CapConfig) {
        return item.id;
    }
    registerChangeInCapConfigs() {
        this.eventSubscriber = this.eventManager.subscribe('capConfigListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.jhiAlertService.error(error.message, null, null);
    }
}
