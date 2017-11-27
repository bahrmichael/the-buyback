import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {ContractsModule} from "./contracts/contracts.module";

import { TheBuybackCapConfigModule } from './cap-config/cap-config.module';
import { TheBuybackCapOrderModule } from './cap-order/cap-order.module';
import { TheBuybackMarketOfferModule } from './market-offer/market-offer.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        ContractsModule,
        TheBuybackCapConfigModule,
        TheBuybackCapOrderModule,
        TheBuybackMarketOfferModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TheBuybackEntityModule {}
