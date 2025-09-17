import React, { createContext, useContext, useEffect, useState } from 'react'
import axios from 'axios'

type Role = 'TEACHER' | 'STUDENT'
type User = { id: number; name: string; role: Role }
type AuthResponse = { token: string; refreshToken: string; user: User }

type AuthContextType = {
  user: User | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  signup: (name: string, email: string, password: string, role: Role) => Promise<void>
  logout: () => Promise<void>
  refreshToken: () => Promise<boolean>
}

const AuthContext = createContext<AuthContextType>({} as any)

// Configure axios to include JWT token in requests
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Add response interceptor to handle token expiration
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // If error is 401 and we haven't tried to refresh the token yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Try to refresh the token
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          // No refresh token available, redirect to login
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
          return Promise.reject(error);
        }
        
        // Call refresh token endpoint
        const response = await axios.post('/api/auth/refresh', { token: refreshToken });
        const { token, refreshToken: newRefreshToken } = response.data;
        
        // Update tokens in localStorage
        localStorage.setItem('token', token);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        // Update the Authorization header for the original request
        originalRequest.headers.Authorization = `Bearer ${token}`;
        
        // Retry the original request
        return axios(originalRequest);
      } catch (refreshError) {
        // Refresh token failed, redirect to login
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('token')
      if (token) {
        try {
          const response = await axios.get('/api/auth/me')
          setUser(response.data)
        } catch (err: any) {
          console.log('Auth check failed:', err)
          // Only clear auth on 401/403, keep tokens for network errors
          if (err?.response?.status === 401 || err?.response?.status === 403) {
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            setUser(null)
          }
        } finally {
          setLoading(false)
        }
      } else {
        setLoading(false)
      }
    }
    
    checkAuth()
  }, [])

  async function login(email: string, password: string) {
    const response = await axios.post('/api/auth/login', { email, password })
    const { token, refreshToken, user: userData } = response.data
    localStorage.setItem('token', token)
    localStorage.setItem('refreshToken', refreshToken)
    setUser(userData)
  }
  
  async function signup(name: string, email: string, password: string, role: Role) {
    const response = await axios.post('/api/auth/signup', { name, email, password, role })
    const { token, refreshToken, user: userData } = response.data
    localStorage.setItem('token', token)
    localStorage.setItem('refreshToken', refreshToken)
    setUser(userData)
  }
  
  async function logout() {
    try {
      // Call the logout endpoint to blacklist the token
      await axios.post('/api/auth/logout')
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      setUser(null)
    }
  }
  
  async function refreshToken(): Promise<boolean> {
    try {
      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) return false
      
      const response = await axios.post('/api/auth/refresh', { token: refreshToken })
      const { token, refreshToken: newRefreshToken, user: userData } = response.data
      
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', newRefreshToken)
      setUser(userData)
      
      return true
    } catch (error) {
      console.error('Token refresh failed:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      setUser(null)
      return false
    }
  }

  return <AuthContext.Provider value={{ user, loading, login, signup, logout, refreshToken }}>{children}</AuthContext.Provider>
}

export function useAuth() { return useContext(AuthContext) }


