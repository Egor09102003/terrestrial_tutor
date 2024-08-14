import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../../security/auth.service";
import {TokenStorageService} from "../../../security/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  // @ts-ignore
  public loginForm: UntypedFormGroup;
  unauthorized:boolean = false;
  @ViewChild('submitButton') submitButton: ElementRef;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private router: Router,
    private fb: UntypedFormBuilder,) {
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
    this.submitButton.nativeElement.disabled = true;
    this.authService.login({
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    }).subscribe(
      data => {
        this.submitButton.nativeElement.disabled = false;
        console.log("Successfully logged in!");
        this.tokenStorage.saveToken(data.token);
        this.tokenStorage.saveUser(data);
        this.router.navigate([data.role.toLowerCase(), data.userId])
      },
    );
  }

}
