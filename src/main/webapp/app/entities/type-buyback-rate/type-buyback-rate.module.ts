import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TheBuybackSharedModule } from '../../shared';
import {
    TypeBuybackRateService,
    TypeBuybackRatePopupService,
    TypeBuybackRateComponent,
    TypeBuybackRateDetailComponent,
    TypeBuybackRateDialogComponent,
    TypeBuybackRatePopupComponent,
    TypeBuybackRateDeletePopupComponent,
    TypeBuybackRateDeleteDialogComponent,
    typeBuybackRateRoute,
    typeBuybackRatePopupRoute,
    TypeBuybackRateResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...typeBuybackRateRoute,
    ...typeBuybackRatePopupRoute,
];

@NgModule({
    imports: [
        TheBuybackSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        TypeBuybackRateComponent,
        TypeBuybackRateDetailComponent,
        TypeBuybackRateDialogComponent,
        TypeBuybackRateDeleteDialogComponent,
        TypeBuybackRatePopupComponent,
        TypeBuybackRateDeletePopupComponent,
    ],
    entryComponents: [
        TypeBuybackRateComponent,
        TypeBuybackRateDialogComponent,
        TypeBuybackRatePopupComponent,
        TypeBuybackRateDeleteDialogComponent,
        TypeBuybackRateDeletePopupComponent,
    ],
    providers: [
        TypeBuybackRateService,
        TypeBuybackRatePopupService,
        TypeBuybackRateResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TheBuybackTypeBuybackRateModule {}
