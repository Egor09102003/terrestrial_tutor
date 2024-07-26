import {Component, ElementRef, OnInit, ViewEncapsulation} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../../security/auth.service";
import {TokenStorageService} from "../../../security/token-storage.service";
import {Router, RouterLink} from "@angular/router";
import {catchError} from "rxjs/operators";
import {Observable, throwError} from "rxjs";

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  // @ts-ignore
  public loginForm: UntypedFormGroup;
  unauthorized:boolean = false;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private fb: UntypedFormBuilder,
    private elementRef: ElementRef) {
    // if (this.tokenStorage.getUser()) {
    //   this.router.navigate(['main']);
    // }
  }

  ngOnInit(): void {
    this.loginForm = this.createLoginForm();
  }

  createLoginForm(): UntypedFormGroup {
    return this.fb.group({
      username: ['', Validators.compose([Validators.required, Validators.email])],
      password: ['', Validators.compose([Validators.required])],
    });
  }

  submit(): void {
    this.authService.login({
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    }).subscribe(data => {
      console.log("Successfully logged in!");
      this.tokenStorage.saveToken(data.token);
      this.tokenStorage.saveUser(data);
      this.router.navigate([data.role.toLowerCase(), data.userId])
    });
  }

}
