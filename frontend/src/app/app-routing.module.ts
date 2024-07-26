import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./modules/auth/login/login.component";
import {AuthGuardService} from "./modules/auth/helper/auth-guard.service";
import {RegistrationComponent} from "./modules/auth/registration/registration.component";
import {AdminComponent} from "./modules/admin/admin.component";
import {PupilComponent} from "./modules/pupil/pupil.component";
import {SupportComponent} from "./modules/support/support.component";
import {TaskComponent} from "./modules/task/task.component";
import {TutorComponent} from "./modules/tutor/tutor.component";
import {HwConstructorComponent} from "./modules/tutor/hw-constructor/hw-constructor.component";
import {TaskChoiceComponent} from "./modules/tutor/task-choise/task-choice.component";
import {PupilsAddHomeworkComponent} from "./modules/tutor/pupils-add-homework/pupils-add-homework.component";
import { HomeworksListComponent } from './modules/pupil/homeworks.list/homeworks.list.component';
import { HomeworksDisplayingComponent } from './modules/pupil/homeworks.displaying/homeworks.displaying.component';
import { PupilHomeworkStatisticComponent } from './modules/pupil/pupil.homework.statistic/pupil.homework.statistic.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent, canActivate: [AuthGuardService]},
  {path: 'registration', component: RegistrationComponent, canActivate: [AuthGuardService]},
  {path: 'admin', component: AdminComponent, canActivate: [AuthGuardService]},
  {path: 'pupil', component: PupilComponent, canActivate: [AuthGuardService]},
  {path: 'pupil/homeworks', component: HomeworksListComponent, canActivate: [AuthGuardService]},
  {path: 'pupil/homework', component: HomeworksDisplayingComponent, canActivate: [AuthGuardService]},
  {path: 'pupil/homework/statistic', component: PupilHomeworkStatisticComponent, canActivate: [AuthGuardService]},
  {path: 'tutor/homework/statistic', component: PupilHomeworkStatisticComponent, canActivate: [AuthGuardService]},
  {path: 'support', component: SupportComponent, canActivate: [AuthGuardService]},
  {path: 'support/:id', component: SupportComponent, canActivate: [AuthGuardService]},
  {path: 'support/:id/task/:taskId', component: TaskComponent, canActivate: [AuthGuardService]},
  {path: 'support/:id/task', component: TaskComponent, canActivate: [AuthGuardService]},
  {path: 'tutor', component: TutorComponent, canActivate: [AuthGuardService]},
  {path: 'tutor/:id', component: TutorComponent, canActivate: [AuthGuardService]},
  {path: 'tutor/:id/constructor/:hwId', component: HwConstructorComponent, canActivate: [AuthGuardService]},
  {path: 'tutor/:id/constructor/:hwId/hw/add/task', component: TaskChoiceComponent, canActivate: [AuthGuardService]},
  {path: 'tutor/:id/constructor/:hwId/add/pup', component: PupilsAddHomeworkComponent, canActivate: [AuthGuardService]},
  {path: '', redirectTo: 'login', pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
