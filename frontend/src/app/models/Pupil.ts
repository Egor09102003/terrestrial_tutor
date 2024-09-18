import { Homework } from "./Homework";
import { HomeworkAnswers } from "./HomeworkAnswers";

export interface Pupil {
  [key: string]: any;
  id: number;
  balance: number;
  homeworks: Homework[];
  price: number;
  subjects: string[];
  tutors: string[];
  username: string;
  name: string;
  surname: string;
  patronymic: string;
  attempt: HomeworkAnswers;
}
