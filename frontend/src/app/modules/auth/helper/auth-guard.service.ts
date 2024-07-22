import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import {Observable} from 'rxjs';
import {TokenStorageService} from '../../../security/token-storage.service';
import { AuthService } from 'src/app/security/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService  {

  constructor(private router: Router,
              private tokenService: TokenStorageService,
              private authService: AuthService,
              ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const currentUser = this.tokenService.getUser();
    if (currentUser == null) {
      if (state.url.includes('login') || state.url.includes('registration')) {
        return true;
      } else {
        // Если пользователь не авторизован и пытается зайти на другую страницу, перенаправляем на страницу входа
        this.router.navigate(['/login']);
        return false;
      }
    } else {
      if (!state.url.includes(currentUser.role.toLowerCase()) || !route.paramMap.get('id')) {
        this.authService.getCurrentUserId().subscribe(id => {
          this.router.navigate([currentUser.role.toLowerCase(), id]);
          return false;
        });
      }
      return true;
    }
  }
}
