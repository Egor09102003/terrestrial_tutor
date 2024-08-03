import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { TokenStorageService } from "../../../security/token-storage.service";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router:Router,
    private tokenService:TokenStorageService,
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    return next.handle(request).pipe(

      catchError( (err: HttpErrorResponse) =>  {
        switch (err.status) {
          case 401:
            this.router.navigate(['/login']);
            window.location.reload();
            break;
          case 403:
            this.tokenService.updateUserData();
            break;
        }
        return new Observable<any>
      })
    )
  }
}
