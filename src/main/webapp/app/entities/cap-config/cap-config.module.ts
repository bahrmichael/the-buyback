import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TheBuybackSharedModule } from '../../shared';
import {
    CapConfigService,
    CapConfigPopupService,
    CapConfigComponent,
    CapConfigDetailComponent,
    CapConfigDialogComponent,
    CapConfigPopupComponent,
    CapConfigDeletePopupComponent,
    CapConfigDeleteDialogComponent,
    capConfigRoute,
    capConfigPopupRoute,
} from './';

const ENTITY_STATES = [
    ...capConfigRoute,
    ...capConfigPopupRoute,
];

@NgModule({
    imports: [
        TheBuybackSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        CapConfigComponent,
        CapConfigDetailComponent,
        CapConfigDialogComponent,
        CapConfigDeleteDialogComponent,
        CapConfigPopupComponent,
        CapConfigDeletePopupComponent,
    ],
    entryComponents: [
        CapConfigComponent,
        CapConfigDialogComponent,
        CapConfigPopupComponent,
        CapConfigDeleteDialogComponent,
        CapConfigDeletePopupComponent,
    ],
    providers: [
        CapConfigService,
        CapConfigPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TheBuybackCapConfigModule {}
