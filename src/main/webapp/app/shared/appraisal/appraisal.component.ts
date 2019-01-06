import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {Appraisal} from "./appraisal.model";
import {ClipboardService} from "./clipboard.service";

@Component({
    selector: 'jhi-appraisal',
    templateUrl: './appraisal.component.html'
})
export class AppraisalComponent implements OnInit {

    isLoadingAppraisal: boolean;
    submitDone: boolean;
    appraisal: Appraisal;

    showCopiedPrice: boolean;
    showCopiedLink: boolean;

    errorMessage: string;

    constructor(
        private http: Http,
        private clipboard: ClipboardService
    ) {
    }

    ngOnInit(): void {
        if (!this.appraisal) {
            this.appraisal = new Appraisal();
        }
    }

    executeAppraisal() {
        this.isLoadingAppraisal = true;
        this.submitDone = false;
        this.errorMessage = null;
        return this.http.post('api/appraisal', this.appraisal).subscribe((data) => {
            this.appraisal = data.json();
            this.isLoadingAppraisal = false;
        }, (err) => {
            if (err.status >= 500) {
                this.errorMessage = "Our hamsters failed to process your appraisal. Please try again later.";
            } else if (err.status >= 400) {
                this.errorMessage = "Your appraisal could not be parsed. Please make sure it is a valid evepraisal format. Go to https://evepraisal.com to try it out.";
            }
            this.isLoadingAppraisal = false;
        });
    }

    clearAppraisal() {
        this.appraisal = new Appraisal();
    }

    copyPrice() {
        this.clipboard.copy(this.appraisal.buybackPrice + '');

        this.showCopiedPrice = true;
        setTimeout(function() {
            this.showCopiedPrice = false;
        }.bind(this), 4000);
    }

    copyLink() {
        this.clipboard.copy(this.appraisal.link);

        this.showCopiedLink = true;
        setTimeout(function() {
            this.showCopiedLink = false;
        }.bind(this), 4000);
    }
}
