import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TheBuybackSharedModule } from '../../shared';
import {
    CapOrderService,
    CapOrderPopupService,
    CapOrderComponent,
    CapOrderDetailComponent,
    CapOrderDialogComponent,
    CapOrderPopupComponent,
    CapOrderDeletePopupComponent,
    CapOrderDeleteDialogComponent,
    capOrderRoute,
    capOrderPopupRoute,
    CapOrderResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...capOrderRoute,
    ...capOrderPopupRoute,
];

@NgModule({
    imports: [
        TheBuybackSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        CapOrderComponent,
        CapOrderDetailComponent,
        CapOrderDialogComponent,
        CapOrderDeleteDialogComponent,
        CapOrderPopupComponent,
        CapOrderDeletePopupComponent,
    ],
    entryComponents: [
        CapOrderComponent,
        CapOrderDialogComponent,
        CapOrderPopupComponent,
        CapOrderDeleteDialogComponent,
        CapOrderDeletePopupComponent,
    ],
    providers: [
        CapOrderService,
        CapOrderPopupService,
        CapOrderResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TheBuybackCapOrderModule {}
