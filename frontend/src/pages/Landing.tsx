import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import ThemeToggle from '../components/ThemeToggle'

export default function Landing() {
  const { login, signup } = useAuth()
  const [isSignup, setIsSignup] = useState(false)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<'TEACHER'|'STUDENT'>('STUDENT')
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    try {
      if (isSignup) await signup(name, email, password, role)
      else await login(email, password)
    } catch (err: any) {
      console.error('Auth error:', err)
      setError(err?.response?.data?.message || err?.message || 'Auth failed')
    }
  }

  return (
    <div>
      <section className="hero">
        <div className="card brutal">
          <h2 style={{marginTop:0}}>Quiz Master: Interactive Learning Platform</h2>
          <p>Modern, real-time quiz platform with microservices architecture</p>
          <ul>
            <li>Role-based dashboards for teachers and students</li>
            <li>Real-time quiz participation</li>
            <li>Comprehensive question bank management</li>
            <li>Detailed analytics and performance tracking</li>
            <li>Responsive design for all devices</li>
          </ul>
        </div>
        <form className="card brutal" onSubmit={onSubmit}>
        <h3 style={{marginTop:0}}>{isSignup ? 'Create account' : 'Welcome back'}</h3>
        {isSignup && (
          <div style={{marginBottom:12}}>
            <label>Name</label>
            <input value={name} onChange={e=>setName(e.target.value)} required/>
          </div>
        )}
        <div style={{marginBottom:12}}>
          <label>Email</label>
          <input type="email" value={email} onChange={e=>setEmail(e.target.value)} required/>
        </div>
        <div style={{marginBottom:12}}>
          <label>Password</label>
          <input type="password" value={password} onChange={e=>setPassword(e.target.value)} required/>
        </div>
        {isSignup && (
          <div style={{marginBottom:12}}>
            <label>Role</label>
            <select value={role} onChange={e=>setRole(e.target.value as any)}>
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
            </select>
          </div>
        )}
        {error && <div style={{color:'red', fontWeight:800, marginBottom:12}}>{error}</div>}
        <button className="btn brutal" type="submit">{isSignup ? 'Sign up' : 'Login'}</button>
        <div style={{marginTop:12}}>
          <a onClick={()=>setIsSignup(!isSignup)} style={{cursor:'pointer', textDecoration:'underline'}}> {isSignup ? 'Have an account? Login' : 'New here? Sign up'}</a>
        </div>
        </form>
      </section>

      <section className="card brutal" style={{marginTop:24}}>
        <h2 style={{marginTop:0}}>About QUIZ//PLATFORM</h2>
        <p>QUIZ//PLATFORM is a cutting-edge quiz management system designed for educational institutions, corporate training, and competitive learning environments. Our platform combines modern technology with intuitive design to create an engaging and efficient quiz experience.</p>
        
        <h4>Key Features:</h4>
        <ul>
          <li><strong>AI-Powered Question Generation:</strong> Create questions instantly using advanced AI technology</li>
          <li><strong>Role-Based Access:</strong> Separate dashboards for teachers and students with appropriate permissions</li>
          <li><strong>Real-Time Analytics:</strong> Track student performance and quiz statistics in real-time</li>
          <li><strong>Responsive Design:</strong> Works seamlessly across all devices and screen sizes</li>
          <li><strong>Live Leaderboards:</strong> Foster healthy competition with real-time scoring</li>
          <li><strong>Secure Authentication:</strong> Enterprise-grade security with JWT token authentication</li>
          <li><strong>Easy Question Management:</strong> Create, edit, update, and delete questions with ease</li>
        </ul>
        
        <h4>Perfect For:</h4>
        <ul>
          <li>Educational institutions and schools</li>
          <li>Corporate training programs</li>
          <li>Online learning platforms</li>
          <li>Competitive exam preparation</li>
          <li>Team building activities</li>
        </ul>
      </section>

      <section className="card brutal" style={{marginTop:24}}>
        <h2 style={{marginTop:0}}>Contact & Support</h2>
        <p>We're here to help you get the most out of QUIZ//PLATFORM. Reach out to us for support, feature requests, or general inquiries.</p>
        
        <div className="contact-grid">
          <div className="contact-item">
            <h4>📧 Email Support</h4>
            <p>Get help with technical issues or general questions</p>
            <a href="mailto:support@quizplatform.com" className="contact-link">support@quizplatform.com</a>
          </div>
          
          {/* <div className="contact-item">
            <h4>💼 Business Inquiries</h4>
            <p>Partnership opportunities and enterprise solutions</p>
            <a href="mailto:business@quizplatform.com" className="contact-link">business@quizplatform.com</a>
          </div> */}
          
          <div className="contact-item">
            <h4>🚀 Feature Requests</h4>
            <p>Suggest new features or improvements</p>
            <a href="mailto:features@quizplatform.com" className="contact-link">features@quizplatform.com</a>
          </div>
          
          <div className="contact-item">
            <h4>📞 Phone Support</h4>
            <p>Monday - Friday, 9 AM - 6 PM EST</p>
            <a href="tel:8923709367" className="contact-link">+91 89237 09367</a>
          </div>
        </div>
        
        <div className="social-links" style={{marginTop: 24}}>
          <h4>Follow Us</h4>
          <div style={{display: 'flex', gap: '16px', marginTop: '12px'}}>
            <a href="#" className="social-link">Twitter</a>
            <a href="#" className="social-link">LinkedIn</a>
            <a href="#" className="social-link">GitHub</a>
          </div>
        </div>
      </section>
    </div>
  )
}


