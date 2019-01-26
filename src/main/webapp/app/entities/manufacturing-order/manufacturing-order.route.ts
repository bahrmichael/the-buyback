import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ManufacturingOrderComponent } from './manufacturing-order.component';
import { ManufacturingOrderDetailComponent } from './manufacturing-order-detail.component';
import { ManufacturingOrderPopupComponent } from './manufacturing-order-dialog.component';
import { ManufacturingOrderDeletePopupComponent } from './manufacturing-order-delete-dialog.component';

@Injectable()
export class ManufacturingOrderResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const manufacturingOrderRoute: Routes = [
    {
        path: 'manufacturing-order',
        component: ManufacturingOrderComponent,
        resolve: {
            'pagingParams': ManufacturingOrderResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ManufacturingOrders'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'manufacturing-order/:id',
        component: ManufacturingOrderDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ManufacturingOrders'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const manufacturingOrderPopupRoute: Routes = [
    {
        path: 'manufacturing-order-new',
        component: ManufacturingOrderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ManufacturingOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'manufacturing-order/:id/edit',
        component: ManufacturingOrderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ManufacturingOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'manufacturing-order/:id/delete',
        component: ManufacturingOrderDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ManufacturingOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
