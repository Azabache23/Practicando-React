import type { LoginCredentials, AuthResponse } from '../types/Auth'

const AUTH_URL = '/api/auth/login'
const TOKEN_KEY = 'auth_token'
const USERNAME_KEY = 'auth_username'

export const login = async (credentials: LoginCredentials): Promise<AuthResponse> =>
{
  const res = await fetch(AUTH_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials),
  })

  if (!res.ok)
  {
    const errorData = await res.json().catch(() => ({}))
    throw new Error(errorData.error || `Error ${res.status}: No se pudo iniciar sesión`)
  }

  const data: AuthResponse = await res.json()

  localStorage.setItem(TOKEN_KEY, data.token)
  localStorage.setItem(USERNAME_KEY, data.username)
  
  return data
}

export const logout = () =>
{
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USERNAME_KEY)
}

export const getToken = (): string | null =>
{
  return localStorage.getItem(TOKEN_KEY)
}

export const getUsername = (): string | null =>
{
  return localStorage.getItem(USERNAME_KEY)
}

export const isAuthenticated = (): boolean =>
{
  return getToken() !== null
}
