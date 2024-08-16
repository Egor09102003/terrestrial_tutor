import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../../security/auth.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {
  // @ts-ignore
  public registrationForm: UntypedFormGroup;

  constructor(
    private authService: AuthService,
    private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.registrationForm = this.createRegisterForm();
  }

  private createRegisterForm() {
    return this.fb.group({
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
    })
  }

  submit(): void {
    this.authService.register(this.registrationForm.value).subscribe();
  }
}
