import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { TypeBuybackRate } from './type-buyback-rate.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<TypeBuybackRate>;

@Injectable()
export class TypeBuybackRateService {

    private resourceUrl =  SERVER_API_URL + 'api/type-buyback-rates';

    constructor(private http: HttpClient) { }

    create(typeBuybackRate: TypeBuybackRate): Observable<EntityResponseType> {
        const copy = this.convert(typeBuybackRate);
        return this.http.post<TypeBuybackRate>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(typeBuybackRate: TypeBuybackRate): Observable<EntityResponseType> {
        const copy = this.convert(typeBuybackRate);
        return this.http.put<TypeBuybackRate>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<TypeBuybackRate>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<TypeBuybackRate[]>> {
        const options = createRequestOption(req);
        return this.http.get<TypeBuybackRate[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<TypeBuybackRate[]>) => this.convertArrayResponse(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: TypeBuybackRate = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<TypeBuybackRate[]>): HttpResponse<TypeBuybackRate[]> {
        const jsonResponse: TypeBuybackRate[] = res.body;
        const body: TypeBuybackRate[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to TypeBuybackRate.
     */
    private convertItemFromServer(typeBuybackRate: TypeBuybackRate): TypeBuybackRate {
        const copy: TypeBuybackRate = Object.assign({}, typeBuybackRate);
        return copy;
    }

    /**
     * Convert a TypeBuybackRate to a JSON which can be sent to the server.
     */
    private convert(typeBuybackRate: TypeBuybackRate): TypeBuybackRate {
        const copy: TypeBuybackRate = Object.assign({}, typeBuybackRate);
        return copy;
    }
}
