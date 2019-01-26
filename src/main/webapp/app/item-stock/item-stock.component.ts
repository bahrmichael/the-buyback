import {AfterViewInit, Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {ActivatedRoute, Router} from "@angular/router";
import {Chart} from 'chart.js';
import {ClipboardService} from "../shared/appraisal/clipboard.service";
import {HttpClient} from "@angular/common/http";

@Component({
               selector: 'jhi-item-stock', templateUrl: './item-stock.component.html', styles: []
           })
export class ItemStockComponent implements OnInit, AfterViewInit {

    loading = true;
    data: any;
    systemName: string;
    showCopiedPrice: boolean;
    private typeId: number;

    constructor(public router: Router, private route: ActivatedRoute, private http: HttpClient,
                private location: Location, private clipboard: ClipboardService) {
    }

    backClicked() {
        this.location.back();
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.systemName = params['systemName'];
            this.typeId = params['typeId'];
        });
    }

    ngAfterViewInit() {
        this.load(this.typeId, this.systemName);
    }

    private load(typeId: number, systemName: string) {
        this.http.get<any>('api/stock/doctrines/item-stock/' + systemName + '/' + typeId).subscribe((data) => {
            this.data = data;
            this.loading = false;
            this.setUpStockChart(data.stockHistory);
            this.setUpPriceChart(data.stockHistory);
        });
    }

    private setUpStockChart(stockHistory: any) {
        const stockChartCanvas = document.getElementById("stockChart");
        const myChart = new Chart(stockChartCanvas, {
            type: 'line',
            data: {
                datasets: [{
                    label: 'Min #',
                    data: this.prepareData(stockHistory, "minQuantity", false),
                    // backgroundColor: ['rgba(0, 0, 0, 0.05)'],
                    borderColor: ['rgba(122, 122, 122, 0.8)'],
                    borderWidth: 1,
                    fill: false

                }, {
                    label: 'Max #',
                    data: this.prepareData(stockHistory, "maxQuantity", false),
                    backgroundColor: ['rgba(0, 0, 0, 0.05)'],
                    borderColor: ['rgba(122, 122, 122, 0.8)'],
                    borderWidth: 1,
                    fill: "-1"
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        stacked: false,
                    }], xAxes: [{
                        type: 'time',
                        time: {
                            unit: 'day'
                        }
                    }]
                },
                elements: {
                    line: {
                        tension: 0
                    },
                    point:{
                        radius: 0
                    }
                },
                responsive: true,
                maintainAspectRatio: true
            }
        });
        console.log(myChart);
    }

    private setUpPriceChart(stockHistory: any[]) {
        const stockChartCanvas = document.getElementById("priceChart");
        const myChart = new Chart(stockChartCanvas, {
            type: 'line',
            data: {
                datasets: [{
                    label: 'Min sell price in mil ISK',
                    data: this.prepareData(stockHistory, "minPrice", true),
                    // backgroundColor: ['rgba(0, 0, 0, 0.05)'],
                    borderColor: ['rgba(122, 122, 122, 0.8)'],
                    borderWidth: 1,
                    fill: false

                }, {
                    label: 'Max sell price in mil ISK',
                    data: this.prepareData(stockHistory, "maxPrice", true),
                    backgroundColor: ['rgba(0, 0, 0, 0.05)'],
                    borderColor: ['rgba(122, 122, 122, 0.8)'],
                    borderWidth: 1,
                    fill: "-1"

                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        stacked: false,
                    }], xAxes: [{
                        type: 'time',
                        time: {
                            unit: 'day'
                        }
                    }]
                },
                elements: {
                    line: {
                        tension: 0
                    },
                    point:{
                        radius: 0
                    }
                },
                responsive: true,
                maintainAspectRatio: true
            }
        });
        console.log(myChart);
    }

    prepareData(data: any[], quantity: string, divByMil: boolean) {
        // data.sort(function(a, b) {
        //     return +new Date(a['date']).getTime() - +new Date(b['date']).getTime();
        // });
        const result = [];
        for (let i = 0; i < data.length; i++) {
            const el = data[i];
            let yAxis = el[quantity];
            if (divByMil) {
                yAxis = yAxis / 1000000;
            }
            result.push({x: el['date'], y: yAxis});
        }
        return result;
    }

    toClipboard(value: string) {
        this.clipboard.copy(value + '');

        this.showCopiedPrice = true;
        setTimeout(function() {
            this.showCopiedPrice = false;
        }.bind(this), 4000);
    }
}
