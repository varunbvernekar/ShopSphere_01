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
  // Updated to point to Spring Boot Backend
  private readonly apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {
    // Restore user from token if available
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      // In a real app we might validate this token with backend, 
      // but for now we decode it or just keep the user if we stored it?
      // Actually, let's keep it simple: we try to restore session.
      // If we stored the user object in localstorage we could restore it.
      // But usually we just have the token.
      // For this demo, we'll try to rely on what we have or just re-login.
      // Let's assume the previous code logic which seemed to try decoding.
      // But our new backend returns a dummy token.

      // Let's rely on 'currentUser' stored in localStorage if we want persistence
      // without a real /me endpoint check on load (for now).
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        this.currentUser = JSON.parse(storedUser);
      }
    }
  }

  /**
   * Register a new user via Spring Boot Backend.
   */
  register(user: User): Observable<boolean> {
    return this.http.post<any>(`${this.apiUrl}/register`, user).pipe(
      map(response => {
        // We no longer auto-login (setSession) after registration.
        // The user will be redirected to /login by the register component.
        return !!(response);
      })
    );
  }

  /**
   * Login against Spring Boot Backend.
   */
  login(email: string, password: string): Observable<boolean> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      map(response => {
        if (response && response.token) {
          this.setSession(response.user, response.token);
          return true;
        }
        return false;
      })
    );
  }

  /**
   * Logs out the current user by clearing memory state and localStorage.
   */
  logout(): void {
    this.currentUser = null;
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('currentUser');
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

  /** Update current user in memory */
  updateCurrentUser(user: User): void {
    this.currentUser = user;
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  // --- Helper Methods ---

  private setSession(user: User, token: string): void {
    this.currentUser = user;
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }
}
