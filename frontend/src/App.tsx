import { useEffect, useState } from 'react'
import { Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth/AuthContext'
import { ThemeProvider } from './contexts/ThemeContext'
import ThemeToggle from './components/ThemeToggle'
import Landing from './pages/Landing'
import TeacherDashboard from './pages/TeacherDashboard'
import StudentDashboard from './pages/StudentDashboard'

function ProtectedRoute({ children, role }: { children: JSX.Element, role?: 'TEACHER' | 'STUDENT' }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/" replace />
  if (role && user.role !== role) return <Navigate to={user.role === 'TEACHER' ? '/teacher' : '/student'} replace />
  return children
}

function Shell() {
  const { user, loading, logout } = useAuth()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  
  useEffect(() => {
    // redirect post-login
    if (user) navigate(user.role === 'TEACHER' ? '/teacher' : '/student')
  }, [user, navigate])
  
  if (loading) {
    return (
      <div className="container">
        <div style={{textAlign: 'center', padding: '50px'}}>
          <h1 style={{fontFamily:'Impact, Haettenschweiler, Arial Black', letterSpacing:1}}>QUIZ//PLATFORM</h1>
          <p>Loading...</p>
        </div>
      </div>
    )
  }
  
  return (
    <div className="container">
      <div className="header">
        <div className="header-left">
          <button 
            className="btn brutal btn-toggle" 
            onClick={() => setSidebarOpen(!sidebarOpen)}
            style={{marginRight: '16px'}}
          >
            ☰
          </button>
          <h1 style={{fontFamily:'Impact, Haettenschweiler, Arial Black', letterSpacing:1}}>QUIZ//PLATFORM</h1>
        </div>
        <div className="header-right">
          <ThemeToggle />
          {user ? (
            <div className="user-info">
              <span className="user-role">{user.role}</span>
              <button className="btn brutal" onClick={logout}>Logout</button>
            </div>
          ) : (
            <div className="auth-toggle">
              {/* <button 
                className="btn brutal" 
                onClick={() => navigate('/')}
                style={{textDecoration:'none'}}
              >
                🔐 Auth
              </button> */}
            </div>
          )}
        </div>
      </div>
      
      {sidebarOpen && (
        <div className="sidebar">
          <div className="sidebar-content">
            <h3>Navigation</h3>
            <nav className="sidebar-nav">
              {user ? (
                <>
                  <Link to={user.role === 'TEACHER' ? '/teacher' : '/student'} className="sidebar-link">
                    Dashboard
                  </Link>
                  <Link to="/" className="sidebar-link" onClick={() => setSidebarOpen(false)}>
                    Home
                  </Link>
                </>
              ) : (
                <Link to="/" className="sidebar-link" onClick={() => setSidebarOpen(false)}>
                  Home
                </Link>
              )}
            </nav>
          </div>
        </div>
      )}
      
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/teacher" element={<ProtectedRoute role="TEACHER"><TeacherDashboard /></ProtectedRoute>} />
        <Route path="/student" element={<ProtectedRoute role="STUDENT"><StudentDashboard /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </div>
  )
}

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Shell />
      </AuthProvider>
    </ThemeProvider>
  )
}


