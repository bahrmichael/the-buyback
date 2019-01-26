import { Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { CapConfigComponent } from './cap-config.component';
import { CapConfigDetailComponent } from './cap-config-detail.component';
import { CapConfigPopupComponent } from './cap-config-dialog.component';
import { CapConfigDeletePopupComponent } from './cap-config-delete-dialog.component';

export const capConfigRoute: Routes = [
    {
        path: 'cap-config',
        component: CapConfigComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapConfigs'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'cap-config/:id',
        component: CapConfigDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapConfigs'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const capConfigPopupRoute: Routes = [
    {
        path: 'cap-config-new',
        component: CapConfigPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapConfigs'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'cap-config/:id/edit',
        component: CapConfigPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapConfigs'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'cap-config/:id/delete',
        component: CapConfigDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapConfigs'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
