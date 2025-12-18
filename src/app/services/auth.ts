import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { Observable, of, map, switchMap } from 'rxjs';

/**
 * Service responsible for managing user authentication, registration, and session state.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;
  private readonly tokenKey = 'auth_token';
  private readonly apiUrl = 'http://localhost:3000';

  constructor(private http: HttpClient) {
    // Restore user from token if available
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      this.currentUser = this.decodeToken(token);
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

        // json-server generates a numeric ID automatically if we dont provide one
        const { id, ...payload } = user;

        return this.http.post<User>(`${this.apiUrl}/users`, payload).pipe(
          map(() => true)
        );
      })
    );
  }

  /**
   * Login against /users in db.json.
   * Sets currentUser in memory and saves JWT to localStorage.
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

        this.setSession(found);
        return true;
      })
    );
  }

  /**
   * Logs out the current user by clearing memory state and localStorage tokens.
   */
  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.tokenKey);
  }

  /**
   * Returns the currently authenticated user or null.
   */
  getCurrentUser(): User | null {
    return this.currentUser;
  }

  /**
   * Checks if a user is currently logged in.
   */
  isLoggedIn(): boolean {
    return this.currentUser != null;
  }

  /**
   * Retrieves the current authentication token from storage.
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /** Update current user in memory and update token */
  updateCurrentUser(user: User): void {
    this.setSession(user);
  }

  // --- JWT Helper Methods ---

  private setSession(user: User): void {
    this.currentUser = user;
    const token = this.generateMockJwt(user);
    localStorage.setItem(this.tokenKey, token);

    // We also keep 'currentUser' for now to support legacy code that might read it directly? 
    // Actually, let's migrate fully to token. But keeping 'currentUser' cleaned up.
    localStorage.removeItem('currentUser');
  }

  /**
   * Simulates generating a JWT token
   * Format: header.payload.signature
   */
  private generateMockJwt(user: User): string {
    const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
    const payload = btoa(JSON.stringify(user));
    const signature = "dummy_signature_secret"; // Normally this is a hash
    return `${header}.${payload}.${signature}`;
  }

  private decodeToken(token: string): User | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;

      const payload = atob(parts[1]);
      return JSON.parse(payload);
    } catch (e) {
      console.error('Failed to decode token', e);
      return null;
    }
  }
}
