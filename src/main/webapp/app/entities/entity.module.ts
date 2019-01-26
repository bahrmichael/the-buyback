import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { TheBuybackCapConfigModule } from './cap-config/cap-config.module';
import { TheBuybackCapOrderModule } from './cap-order/cap-order.module';
import { TheBuybackManufacturingOrderModule } from './manufacturing-order/manufacturing-order.module';
import { TheBuybackTypeBuybackRateModule } from './type-buyback-rate/type-buyback-rate.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        TheBuybackCapConfigModule,
        TheBuybackCapOrderModule,
        TheBuybackManufacturingOrderModule,
        TheBuybackTypeBuybackRateModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TheBuybackEntityModule {}
