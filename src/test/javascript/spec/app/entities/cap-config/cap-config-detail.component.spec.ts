/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { TheBuybackTestModule } from '../../../test.module';
import { CapConfigDetailComponent } from '../../../../../../main/webapp/app/entities/cap-config/cap-config-detail.component';
import { CapConfigService } from '../../../../../../main/webapp/app/entities/cap-config/cap-config.service';
import { CapConfig } from '../../../../../../main/webapp/app/entities/cap-config/cap-config.model';

describe('Component Tests', () => {

    describe('CapConfig Management Detail Component', () => {
        let comp: CapConfigDetailComponent;
        let fixture: ComponentFixture<CapConfigDetailComponent>;
        let service: CapConfigService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [TheBuybackTestModule],
                declarations: [CapConfigDetailComponent],
                providers: [
                    CapConfigService
                ]
            })
            .overrideTemplate(CapConfigDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(CapConfigDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CapConfigService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new CapConfig('123')
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith('123');
                expect(comp.capConfig).toEqual(jasmine.objectContaining({id: '123'}));
            });
        });
    });

});
