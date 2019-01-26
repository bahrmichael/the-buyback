import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { CapConfig } from './cap-config.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<CapConfig>;

@Injectable()
export class CapConfigService {

    private resourceUrl =  SERVER_API_URL + 'api/cap-configs';

    constructor(private http: HttpClient) { }

    create(capConfig: CapConfig): Observable<EntityResponseType> {
        const copy = this.convert(capConfig);
        return this.http.post<CapConfig>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(capConfig: CapConfig): Observable<EntityResponseType> {
        const copy = this.convert(capConfig);
        return this.http.put<CapConfig>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<CapConfig>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<CapConfig[]>> {
        const options = createRequestOption(req);
        return this.http.get<CapConfig[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<CapConfig[]>) => this.convertArrayResponse(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: CapConfig = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<CapConfig[]>): HttpResponse<CapConfig[]> {
        const jsonResponse: CapConfig[] = res.body;
        const body: CapConfig[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to CapConfig.
     */
    private convertItemFromServer(capConfig: CapConfig): CapConfig {
        const copy: CapConfig = Object.assign({}, capConfig);
        return copy;
    }

    /**
     * Convert a CapConfig to a JSON which can be sent to the server.
     */
    private convert(capConfig: CapConfig): CapConfig {
        const copy: CapConfig = Object.assign({}, capConfig);
        return copy;
    }
}
