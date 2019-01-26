/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { TheBuybackTestModule } from '../../../test.module';
import { CapConfigComponent } from '../../../../../../main/webapp/app/entities/cap-config/cap-config.component';
import { CapConfigService } from '../../../../../../main/webapp/app/entities/cap-config/cap-config.service';
import { CapConfig } from '../../../../../../main/webapp/app/entities/cap-config/cap-config.model';

describe('Component Tests', () => {

    describe('CapConfig Management Component', () => {
        let comp: CapConfigComponent;
        let fixture: ComponentFixture<CapConfigComponent>;
        let service: CapConfigService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [CapConfigComponent],
                providers: [
                    CapConfigService
                ]
            })
            .overrideTemplate(CapConfigComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(CapConfigComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CapConfigService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new CapConfig('123')],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.capConfigs[0]).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
