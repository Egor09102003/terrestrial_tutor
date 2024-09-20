import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginComponent} from "./modules/auth/login/login.component";
import {RegistrationComponent} from "./modules/auth/registration/registration.component";
import {PupilComponent} from "./modules/pupil/pupil.component";
import {authInterceptorProviders} from "./modules/auth/helper/auth-interceptor.service";
import {AdminComponent} from "./modules/admin/admin.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {SubjectsComponent} from './modules/subjects/subjects.component';
import {NgbAccordionModule, NgbAlertModule, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import { SupportComponent } from './modules/support/support.component';
import { TaskComponent } from './modules/task/task.component';
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import { TutorComponent } from './modules/tutor/tutor.component';
import { HwConstructorComponent } from './modules/tutor/hw-constructor/hw-constructor.component';
import { BrowseNotificationsComponent } from './modules/modals/browse-notifications.component';
import { PupilsAddHomeworkComponent } from './modules/tutor/pupils-add-homework/pupils-add-homework.component';
import {provideStore} from "@ngrx/store";
import {homeworkFeature} from "./modules/tutor/storage/homework.reducer";
import {provideStoreDevtools} from "@ngrx/store-devtools";
import {TaskChoiceComponent} from "./modules/tutor/task-choise/task-choice.component";
import { HomeworksListComponent } from './modules/pupil/homeworks.list/homeworks.list.component';
import { HomeworksDisplayingComponent } from './modules/pupil/homeworks.displaying/homeworks.displaying.component';
import { PupilHomeworkStatisticComponent } from './modules/pupil/pupil.homework.statistic/pupil.homework.statistic.component';
import {NgOptimizedImage} from "@angular/common";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import { ErrorInterceptor } from './modules/auth/helper/error.interceptor';
import { TaskCardComponent } from './modules/task/card/task.card.component/task.card.component';
import {LightboxModule} from "ngx-lightbox";
import {TaskTableComponent} from "./modules/task/task-table/task-table.component";
import {TablesConstructorComponent} from "./modules/task/tables-constructor/tables-constructor.component";
import {CKEditorModule} from "@ckeditor/ckeditor5-angular";
import { HomeworkComponent } from './modules/homework/homework.component';
import { StatisticContainerComponent } from './modules/homework/statistic.container/statistic.container.component';
import { CheckTaskCardComponent } from './modules/homework/check.task.card/check.task.card.component';
import { PupilsModalComponent } from './modules/tutor/pupils.modal/pupils.modal.component';
import { CheckHomeworksComponent } from './modules/tutor/check.homeworks/check.homeworks.component';
import { HomeworkTutorsSetModalComponent } from './modules/admin/homework.tutors.set.modal/homework.tutors.set.modal.component';
import { PupilsSelectModalComponent } from './modules/pupil/pupils.select.modal/pupils.select.modal.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminComponent,
    LoginComponent,
    RegistrationComponent,
    PupilComponent,
    PupilHomeworkStatisticComponent,
    HomeworksListComponent,
    HomeworksDisplayingComponent,
    SubjectsComponent,
    SupportComponent,
    TaskComponent,
    TutorComponent,
    HwConstructorComponent,
    TaskChoiceComponent,
    BrowseNotificationsComponent,
    PupilsAddHomeworkComponent,
    TaskCardComponent,
    HomeworkComponent,
    StatisticContainerComponent,
    CheckTaskCardComponent,
    PupilsModalComponent,
    CheckHomeworksComponent,
    HomeworkTutorsSetModalComponent,
    PupilsSelectModalComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    NgbAlertModule,
    NgbModule,
    CodemirrorModule,
    NgOptimizedImage,
    CdkDropList,
    CdkDrag,
    LightboxModule,
    TaskTableComponent,
    TablesConstructorComponent,
    CKEditorModule,
    NgbAccordionModule,
  ],
  providers: [
    authInterceptorProviders,
    provideStore( {
      [homeworkFeature.name]: homeworkFeature.reducer,
    }),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: false,
      autoPause: true,
      trace: false,
      traceLimit: 75
    }),
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
