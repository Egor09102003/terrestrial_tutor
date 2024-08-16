import { Injectable } from '@angular/core';
import {environment} from "./environment";

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  get apiUrl(): string {
    return environment.MAIN_API_PATH;
  }

  get filesPath(): string {
    return environment.FILES_PATH;
  }

}
