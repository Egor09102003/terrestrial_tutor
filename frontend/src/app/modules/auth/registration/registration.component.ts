import {Component} from '@angular/core';
import {UntypedFormBuilder, Validators, FormGroup} from "@angular/forms";
import {AuthService} from "../../../security/auth.service";
import {Router} from "@angular/router";
import {ErrorsService} from "../helper/errors.service";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent {

  registrationForm: FormGroup;
  errors: any[] = [];

  constructor(
    private authService: AuthService,
    private fb: UntypedFormBuilder,
    private router: Router,
    public errorsService: ErrorsService,
    ) {
  }

  ngOnInit(): void {
    this.createRegisterForm();
  }

  private createRegisterForm() {
    this.registrationForm = this.fb.group({
      name: ['', Validators.compose([Validators.required])],
      surname: ['', Validators.compose([Validators.required])],
      patronymic: ['', Validators.compose([Validators.required])],
      email: ['', Validators.compose([Validators.required, Validators.email])],
      password: ['', Validators.compose([Validators.required])],
      confirmPassword: ['', Validators.compose([Validators.required])],
      role: ['Кто Вы?', Validators.compose([(role) => {
        if (role.value == 'Кто Вы?') {
          return {roleSelected: false};
        } else {
          return null;
        }
      }])],
    });

    this.registrationForm.controls['confirmPassword'].addValidators([
      (password) => {
        if (this.registrationForm.controls['password'].value !== password.value) {
          return {confirmPassword: false};
        } else {
          return null;
        }
      }
    ]);
    this.registrationForm.controls['confirmPassword'].updateValueAndValidity();
  }

  submit(): void {
    this.authService.register(this.registrationForm.value).subscribe({
      next: () => this.router.navigate(['/login'])
    });
  }

  invalid(controlName: string) {
    return this.registrationForm.controls[controlName].invalid
      && this.registrationForm.controls[controlName].touched;
  }

  revalidatePassword() {
    this.registrationForm.controls['confirmPassword'].updateValueAndValidity()
  }
}
