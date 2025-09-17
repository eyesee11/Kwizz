export type Role = 'TEACHER' | 'STUDENT';

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface Quiz {
  id: number;
  title: string;
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
}

export interface Question {
  id?: number;
  text: string;
  options: QuestionOption[];
}

export interface QuestionOption {
  id?: number;
  text: string;
  correct: boolean;
}

export interface Attempt {
  id: number;
  quizId: number;
  score: number;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}
