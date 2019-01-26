import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { CapOrder } from './cap-order.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<CapOrder>;

@Injectable()
export class CapOrderService {

    private resourceUrl =  SERVER_API_URL + 'api/cap-orders';

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(capOrder: CapOrder): Observable<EntityResponseType> {
        const copy = this.convert(capOrder);
        return this.http.post<CapOrder>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(capOrder: CapOrder): Observable<EntityResponseType> {
        const copy = this.convert(capOrder);
        return this.http.put<CapOrder>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<CapOrder>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<CapOrder[]>> {
        const options = createRequestOption(req);
        return this.http.get<CapOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<CapOrder[]>) => this.convertArrayResponse(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: CapOrder = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<CapOrder[]>): HttpResponse<CapOrder[]> {
        const jsonResponse: CapOrder[] = res.body;
        const body: CapOrder[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to CapOrder.
     */
    private convertItemFromServer(capOrder: CapOrder): CapOrder {
        const copy: CapOrder = Object.assign({}, capOrder);
        copy.created = this.dateUtils
            .convertDateTimeFromServer(capOrder.created);
        return copy;
    }

    /**
     * Convert a CapOrder to a JSON which can be sent to the server.
     */
    private convert(capOrder: CapOrder): CapOrder {
        const copy: CapOrder = Object.assign({}, capOrder);

        copy.created = this.dateUtils.toDate(capOrder.created);
        return copy;
    }
}
