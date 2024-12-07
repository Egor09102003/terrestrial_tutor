import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from "@angular/router";
import {TokenStorageService} from "./security/token-storage.service";
import {ToastService} from "./components/toasts/toasts.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit{

  constructor(
    private router: Router,
    private tokenService: TokenStorageService,
    protected toastService: ToastService,
  ) {
  }

  user: {[key: string]: string} | null = {};

  ngOnInit(): void {
  }

  logout(): void {
    this.tokenService.logOut();
  }

  checkLogin(): boolean {
    this.user = this.tokenService.getUser();
    return this.user != null && Object.keys(this.user).length > 0;
  }
}
