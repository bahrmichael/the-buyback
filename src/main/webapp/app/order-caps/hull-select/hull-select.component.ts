import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CapConfig} from "../../entities/cap-config/cap-config.model";

@Component({
  selector: 'jhi-hull-select',
  templateUrl: './hull-select.component.html',
  styles: []
})
export class HullSelectComponent implements OnInit {

    @Input() capConfig: CapConfig;
    @Input() index: number;
    @Output() selectedCap = new EventEmitter();

    constructor() {
    }

    ngOnInit() {
    }

    pickHull(cap: CapConfig) {
        this.selectedCap.emit(cap);
    }

}
