import { useEffect, useState } from 'react'
import axios from 'axios'

type Quiz = { id: number; title: string; difficulty: 'EASY'|'MEDIUM'|'HARD' }
type Result = { id: number; quizId: number; score: number; createdAt: string }
type Question = { id: number; text: string; options: { id: number; text: string; correct: boolean }[] }

export default function StudentDashboard() {
  const [quizzes, setQuizzes] = useState<Quiz[]>([])
  const [results, setResults] = useState<Result[]>([])
  const [leaderboard, setLeaderboard] = useState<any[]>([])
  const [takingQuiz, setTakingQuiz] = useState<{ quiz: Quiz; questions: Question[] } | null>(null)
  const [answers, setAnswers] = useState<Record<number, number>>({})
  const [submitting, setSubmitting] = useState(false)
  const [showResults, setShowResults] = useState(false)
  const [lastScore, setLastScore] = useState<number | null>(null)

  async function load() {
    const qs = await axios.get('/api/quiz/available')
    setQuizzes(qs.data)
    const rs = await axios.get('/api/attempt/my')
    setResults(rs.data)
    const lb = await axios.get('/api/analytics/attempts')
    setLeaderboard(lb.data.slice(0, 10))
  }
  useEffect(() => { load() }, [])
  // Removed WebSocket for simplicity

  async function attempt(quizId: number) {
    const meta = quizzes.find(q=>q.id===quizId)!
    const r = await axios.get(`/api/quiz/${quizId}/questions`)
    setTakingQuiz({ quiz: meta, questions: r.data })
    setAnswers({})
  }
  async function submitAttempt() {
    if (!takingQuiz) return
    try {
      setSubmitting(true)
      // Convert answers to the format expected by backend (option indices)
      const answerList = takingQuiz.questions.map(q => answers[q.id] || 0)
      const response = await axios.post(`/api/attempt`, { 
        quizId: takingQuiz.quiz.id, 
        answers: answerList 
      })
      setLastScore(response.data.score)
      setShowResults(true)
      setTakingQuiz(null)
      load()
    } catch (error) {
      console.error('Error submitting attempt:', error)
      alert('Failed to submit quiz. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
    <div className="grid" style={{gridTemplateColumns:'1fr 1fr'}}>
      <div className="card brutal">
        <h3 style={{marginTop:0}}>Available Quizzes</h3>
        <ul>
          {quizzes.map(q => (
            <li key={q.id} style={{display:'flex', justifyContent:'space-between', alignItems:'center', padding:'8px 0', borderBottom:'1px solid #ddd'}}>
              <span>{q.title} — {q.difficulty}</span>
              <button className="btn brutal" onClick={()=>attempt(q.id)}>Attempt</button>
            </li>
          ))}
        </ul>
      </div>
      <div className="card brutal">
        <h3 style={{marginTop:0}}>My Results</h3>
        <ul>
          {results.map(r => (
            <li key={r.id} style={{display:'flex', justifyContent:'space-between', alignItems:'center', padding:'4px 0'}}>
              <span>Quiz #{r.quizId}: {r.score}%</span>
              <small style={{color:'#666'}}>{new Date(r.createdAt).toLocaleDateString()}</small>
            </li>
          ))}
        </ul>
      </div>
      <div className="card brutal" style={{gridColumn:'1 / -1'}}>
        <h3 style={{marginTop:0}}>Live Leaderboard</h3>
        <div className="grid" style={{gridTemplateColumns:'1fr 1fr 1fr 1fr'}}>
          <div><b>Student</b></div><div><b>Quiz</b></div><div><b>Score</b></div><div><b>When</b></div>
          {leaderboard.map(a => (
            <>
              <div>{a.studentName}</div>
              <div>{a.quizTitle}</div>
              <div>{a.score}</div>
              <div>{new Date(a.createdAt).toLocaleTimeString()}</div>
            </>
          ))}
        </div>
      </div>
    </div>

    {takingQuiz && (
      <div className="card brutal" style={{gridColumn:'1 / -1'}}>
        <h3 style={{marginTop:0}}>Attempt: {takingQuiz.quiz.title}</h3>
        {takingQuiz.questions.map(q => (
          <div key={q.id} className="card brutal" style={{marginBottom:12}}>
            <div style={{fontWeight:700}}>{q.text}</div>
            {q.options.map((o, index) => (
              <label key={o.id} style={{display:'block', marginTop:8}}>
                <input type="radio" name={`q-${q.id}`} checked={answers[q.id]===index} onChange={()=>setAnswers(a=>({ ...a, [q.id]: index }))} /> {o.text}
              </label>
            ))}
          </div>
        ))}
        <div style={{display:'flex', gap:'12px', marginTop:'16px'}}>
          <button disabled={submitting} className="btn brutal" onClick={submitAttempt}>
            {submitting ? 'Submitting…' : 'Submit Quiz'}
          </button>
          <button className="btn brutal" onClick={() => setTakingQuiz(null)}>
            Cancel
          </button>
        </div>
      </div>
    )}

    {showResults && lastScore !== null && (
      <div className="card brutal" style={{gridColumn:'1 / -1', textAlign:'center', marginTop:'16px'}}>
        <h2>Quiz Completed!</h2>
        <div style={{fontSize:'24px', margin:'16px 0'}}>
          Your Score: <strong style={{color: lastScore >= 70 ? '#4CAF50' : lastScore >= 50 ? '#FF9800' : '#F44336'}}>
            {lastScore}%
          </strong>
        </div>
        <div style={{margin:'16px 0'}}>
          {lastScore >= 90 && "Excellent work! 🌟"}
          {lastScore >= 70 && lastScore < 90 && "Good job! 👍"}
          {lastScore >= 50 && lastScore < 70 && "Not bad, keep practicing! 💪"}
          {lastScore < 50 && "Keep studying and try again! 📚"}
        </div>
        <button className="btn brutal" onClick={() => setShowResults(false)}>
          Continue
        </button>
      </div>
    )}
    </>
  )
}


