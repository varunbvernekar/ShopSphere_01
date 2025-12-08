import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { Observable, of, map, switchMap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;

  private readonly apiUrl = 'http://localhost:3000';

  constructor(private http: HttpClient) {
    // Restore user from localStorage if available
    if (typeof window !== 'undefined' && window.localStorage) {
      const stored = localStorage.getItem('currentUser');
      if (stored) {
        try {
          this.currentUser = JSON.parse(stored);
        } catch (e) {
          console.error('Failed to parse stored user', e);
          localStorage.removeItem('currentUser');
        }
      }
    }
  }

  /**
   * Register a new user via db.json (/users).
   * Returns true if registered, false if email already exists.
   */
  register(user: User): Observable<boolean> {
    return this.http.get<User[]>(`${this.apiUrl}/users`).pipe(
      switchMap(users => {
        const exists = users.some(u => u.email === user.email);
        if (exists) {
          return of(false);
        }

        const maxId = users.reduce((max, u) => Math.max(max, u.id ?? 0), 0);
        const payload: User = {
          ...user,
          id: maxId + 1
        };

        return this.http.post<User>(`${this.apiUrl}/users`, payload).pipe(
          map(newUser => {
            // Auto-login after register
            this.currentUser = newUser;
            if (typeof window !== 'undefined' && window.localStorage) {
              localStorage.setItem('currentUser', JSON.stringify(newUser));
            }
            return true;
          })
        );
      })
    );
  }

  /**
   * Login against /users in db.json.
   * Sets currentUser in memory (no windowStorage).
   */
  login(email: string, password: string): Observable<boolean> {
    return this.http.get<User[]>(`${this.apiUrl}/users`).pipe(
      map(users => {
        const found = users.find(
          u => u.email === email && u.password === password
        );

        if (!found) {
          return false;
        }

        this.currentUser = found;
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem('currentUser', JSON.stringify(found));
        }
        return true;
      })
    );
  }

  logout(): void {
    this.currentUser = null;
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem('currentUser');
    }
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }

  isLoggedIn(): boolean {
    return this.currentUser != null;
  }

  /** Update current user in memory */
  updateCurrentUser(user: User): void {
    this.currentUser = user;
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem('currentUser', JSON.stringify(user));
    }
  }
}
