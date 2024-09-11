
export interface HomeworkAnswers {
  answersStatuses: {[key: number]: Status};
  attemptCount: number;
}

export class Status {
  status: string
  points: number
  currentAnswer: string
}
