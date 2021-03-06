import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { ProfileService } from '../profiles/profile.service';
import { Principal, LoginService, ConfigService } from '../../shared';

import { VERSION } from '../../app.constants';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: [
        'navbar.css'
    ]
})
export class NavbarComponent implements OnInit {
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    swaggerEnabled: boolean;
    version: string;
    userName: string;
    private ssoUrl: string;

    constructor(
        private loginService: LoginService,
        private principal: Principal,
        private profileService: ProfileService,
        private router: Router,
        private configService: ConfigService
    ) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
    }

    ngOnInit() {
        this.profileService.getProfileInfo().then((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });
        this.principal.identity().then((account) => {
            if (account) {
                this.userName = account.login;
            }
        });
        this.configService.getSsoUrl().subscribe((data) => this.ssoUrl = data);
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.router.navigate(['/login']);
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.principal.getImageUrl() : null;
    }
}
