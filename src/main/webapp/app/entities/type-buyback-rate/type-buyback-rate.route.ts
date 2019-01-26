import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { TypeBuybackRateComponent } from './type-buyback-rate.component';
import { TypeBuybackRateDetailComponent } from './type-buyback-rate-detail.component';
import { TypeBuybackRatePopupComponent } from './type-buyback-rate-dialog.component';
import { TypeBuybackRateDeletePopupComponent } from './type-buyback-rate-delete-dialog.component';

@Injectable()
export class TypeBuybackRateResolvePagingParams implements Resolve<any> {

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

export const typeBuybackRateRoute: Routes = [
    {
        path: 'type-buyback-rate',
        component: TypeBuybackRateComponent,
        resolve: {
            'pagingParams': TypeBuybackRateResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'TypeBuybackRates'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'type-buyback-rate/:id',
        component: TypeBuybackRateDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'TypeBuybackRates'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const typeBuybackRatePopupRoute: Routes = [
    {
        path: 'type-buyback-rate-new',
        component: TypeBuybackRatePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'TypeBuybackRates'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'type-buyback-rate/:id/edit',
        component: TypeBuybackRatePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'TypeBuybackRates'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'type-buyback-rate/:id/delete',
        component: TypeBuybackRateDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'TypeBuybackRates'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
