import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { CapOrderComponent } from './cap-order.component';
import { CapOrderDetailComponent } from './cap-order-detail.component';
import { CapOrderPopupComponent } from './cap-order-dialog.component';
import { CapOrderDeletePopupComponent } from './cap-order-delete-dialog.component';

@Injectable()
export class CapOrderResolvePagingParams implements Resolve<any> {

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

export const capOrderRoute: Routes = [
    {
        path: 'cap-order',
        component: CapOrderComponent,
        resolve: {
            'pagingParams': CapOrderResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapOrders'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'cap-order/:id',
        component: CapOrderDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapOrders'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const capOrderPopupRoute: Routes = [
    {
        path: 'cap-order-new',
        component: CapOrderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'cap-order/:id/edit',
        component: CapOrderPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'cap-order/:id/delete',
        component: CapOrderDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CapOrders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
