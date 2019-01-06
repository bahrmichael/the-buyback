import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";

@Component({
               selector: 'jhi-doctrine-stock', templateUrl: './doctrine-stock.component.html', styles: []
           })
export class DoctrineStockComponent implements OnInit {

    loading: boolean;
    data: any;
    showStocked = false;
    showMissingOnly = false;
    potentialProfit: number;
    showProfitExplanation = false;
    showPricingExplanation = false;
    hubs: any;
    selectedHub: any;

    constructor(private http: HttpClient,
                public router: Router) {
        this.hubs = [{'systemName': 'GE-8JV', 'structureType': 'Faction Fort', 'id': 1027847407700}];
        this.selectedHub = this.hubs[0];
    }

    selectHub(hub: any) {
        this.selectedHub = hub;
        this.loading = true;
        this.http.get('api/stock/doctrines/' + this.selectedHub['id']).subscribe((data) => {
            this.data = data;
            this.loading = false;
        });
    }

    ngOnInit() {
        this.load();
    }

    doShowStocked() {
        this.showStocked = true;
        this.showMissingOnly = false;
    }

    doHideStocked() {
        this.showStocked = false;
    }

    doShowOnlyMissing() {
        this.showMissingOnly = true;
        this.showStocked = false;
    }

    doShowNotOnlyMissing() {
        this.showMissingOnly = false;
    }

    private load() {
        this.loading = true;
        this.http.get('api/stock/hubs').subscribe((data) => {
            this.hubs = data;
        });
        this.http.get('api/stock/doctrines/' + this.selectedHub['id']).subscribe((data) => {
            this.data = data;
            this.loading = false;
        });
    }

    showEntry(availability: string) {
        if (this.showMissingOnly && availability === 'MISSING') {
            return true;
        }
        if (this.showMissingOnly && availability !== 'MISSING') {
            return false;
        }
        if (this.showStocked && availability === 'STOCKED') {
            return true;
        }
        if (!this.showStocked && availability === 'STOCKED') {
            return false;
        }
        return true;
    }

}
