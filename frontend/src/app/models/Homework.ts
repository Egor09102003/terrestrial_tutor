import {Task} from "./Task";
import {TaskChecking} from "./TaskChecking";

export class Homework {
  [key: string]: any;
  id?: number;
  name: string = '';
  targetTime: number = -1;
  pupilIds: number[] = [];
  deadLine?: Date;
  subject: string = '';
  taskChecking: {[key: number]: TaskChecking} = {};
  tasks: Task[];
  lastAttempt: number = 0;

}
