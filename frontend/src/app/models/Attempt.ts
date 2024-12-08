import {HomeworkStatuses} from "./enums/HomeworkStatuses";
import {Answer} from "./Answer";

export interface Attempt {
  id: number
  homework: number
  attemptPoints: number
  pupil: number
  attemptNumber: number ;
  status: HomeworkStatuses;
  solutionDate: Date;
  answers: {[key: number]: Answer};
}
