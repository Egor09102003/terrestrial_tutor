import { HttpClient } from '@angular/common/http';
import {Injectable} from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
  }

  public saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string {
    // @ts-ignore
    return localStorage.getItem(TOKEN_KEY);
  }

  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): any{
    // @ts-ignore
    return JSON.parse(localStorage.getItem(USER_KEY));
  }

  public updateUserData() {
    window.localStorage.removeItem(USER_KEY);
    this.authService.getCurrentUser().subscribe(user => {
      this.saveUser(user);
      this.router.navigate(['/']);
    })
  }

  logOut(): void {
    window.localStorage.clear();
    window.location.reload();
  }
}
