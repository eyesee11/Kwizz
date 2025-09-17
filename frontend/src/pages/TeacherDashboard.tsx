import React, { useEffect, useState } from 'react'
import axios from 'axios'
import Modal from '../components/Modal'
import AIQuizGenerator from '../components/AIQuizGenerator'
import AnalyticsDashboard from '../components/AnalyticsDashboard'
import QuestionEditor from '../components/QuestionEditor'

type Quiz = { id: number; title: string; difficulty: 'EASY'|'MEDIUM'|'HARD' }
type Question = { id?: number; text: string; options: { text: string; correct: boolean }[] }

export default function TeacherDashboard() {
  const [quizzes, setQuizzes] = useState<Quiz[]>([])
  const [title, setTitle] = useState('')
  const [difficulty, setDifficulty] = useState<'EASY'|'MEDIUM'|'HARD'>('EASY')
  const [questionModalOpen, setQuestionModalOpen] = useState(false)
  const [editingQuizId, setEditingQuizId] = useState<number | null>(null)
  const [questions, setQuestions] = useState<Question[]>([])
  const [showAIGenerator, setShowAIGenerator] = useState(false)
  const [students, setStudents] = useState<any[]>([])
  const [summary, setSummary] = useState<any | null>(null)
  const [attempts, setAttempts] = useState<any[]>([])

  async function load() {
    try {
      const r = await axios.get('/api/quiz')
      setQuizzes(r.data)
      const s = await axios.get('/api/analytics/students')
      setStudents(s.data)
      const sum = await axios.get('/api/analytics/summary')
      setSummary(sum.data)
      const at = await axios.get('/api/analytics/attempts')
      setAttempts(at.data)
    } catch (error) {
      console.error('Error loading data:', error)
    }
  }
  
  useEffect(() => { load() }, [])

  async function createQuiz(e: React.FormEvent) {
    e.preventDefault()
    try {
      await axios.post('/api/quiz', { title, difficulty })
      setTitle('')
      setDifficulty('EASY')
      load()
    } catch (error) {
      console.error('Error creating quiz:', error)
    }
  }
  
  const handleQuizGenerated = async (questionBank: any) => {
    try {
      // First create a new quiz
      const quizResponse = await axios.post('/api/quiz', { 
        title: ` ${questionBank.subject}`, 
        difficulty: questionBank.difficulty 
      })
      
      const quizId = quizResponse.data.id
      
      // Then add all the generated questions to the quiz
      const questionsToAdd = questionBank.questions.map((q: any) => ({
        text: q.text,
        options: q.options
      }))
      
      await axios.post(`/api/quiz/${quizId}/questions/batch`, questionsToAdd)
      
      // Refresh the quiz list
      load()
      setShowAIGenerator(false)
    } catch (error) {
      console.error('Error saving AI generated quiz:', error)
    }
  }

  async function openQuestions(quizId: number) {
    setEditingQuizId(quizId)
    try {
      const r = await axios.get(`/api/quiz/${quizId}/questions`)
      setQuestions(r.data || [])
      setQuestionModalOpen(true)
    } catch (error) {
      console.error('Error loading questions:', error)
      setQuestions([])
      setQuestionModalOpen(true)
    }
  }

  function addQuestion() {
    const newQuestion: Question = {
      text: '',
      options: [
        { text: '', correct: true },
        { text: '', correct: false },
        { text: '', correct: false },
        { text: '', correct: false }
      ]
    }
    setQuestions(prev => [...prev, newQuestion])
  }

  function updateQuestion(index: number, updatedQuestion: Question) {
    setQuestions(prev => prev.map((q, i) => i === index ? updatedQuestion : q))
  }

  function deleteQuestion(index: number) {
    if (window.confirm('Are you sure you want to delete this question?')) {
      setQuestions(prev => prev.filter((_, i) => i !== index))
    }
  }

  async function saveQuestions() {
    if (!editingQuizId) return
    
    // Validate questions before saving
    const validQuestions = questions.filter(q => 
      q.text.trim() && 
      q.options.length === 4 && 
      q.options.every(opt => opt.text.trim()) &&
      q.options.some(opt => opt.correct)
    )
    
    if (validQuestions.length === 0) {
      alert('Please add at least one complete question with all options filled and one correct answer selected.')
      return
    }
    
    if (validQuestions.length !== questions.length) {
      if (!confirm(`${questions.length - validQuestions.length} incomplete questions will be skipped. Continue?`)) {
        return
      }
    }
    
    try {
      await axios.put(`/api/quiz/${editingQuizId}/questions`, validQuestions)
      alert(`${validQuestions.length} questions saved successfully!`)
      setQuestionModalOpen(false)
      load()
    } catch (error) {
      console.error('Error saving questions:', error)
      alert('Error saving questions. Please try again.')
    }
  }


  async function remove(id: number) {
    if (window.confirm('Are you sure you want to delete this quiz?')) {
      try {
        await axios.delete(`/api/quiz/${id}`)
        load()
      } catch (error) {
        console.error('Error deleting quiz:', error)
      }
    }
  }


  return (
    <>
    <div className="grid" style={{gridTemplateColumns:'1fr 1fr'}}>
      <form className="card brutal" onSubmit={createQuiz}>
        <h3 style={{marginTop:0}}>Create Quiz</h3>
        <div style={{marginBottom:12}}>
          <label>Title</label>
          <input value={title} onChange={e=>setTitle(e.target.value)} required />
        </div>
        <div style={{marginBottom:12}}>
          <label>Difficulty</label>
          <select value={difficulty} onChange={e=>setDifficulty(e.target.value as any)}>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>
        <button className="btn brutal" type="submit">Create Quiz</button>
        <button 
          type="button" 
          className="btn brutal" 
          onClick={() => setShowAIGenerator(!showAIGenerator)}
          style={{marginLeft: '10px'}}
        >
          {showAIGenerator ? 'Hide AI Generator' : 'Use AI Generator'}
        </button>
      </form>
      
      {showAIGenerator && (
        <div className="ai-generator-container" style={{marginTop: '20px'}}>
          <AIQuizGenerator onQuizGenerated={handleQuizGenerated} />
        </div>
      )}

      <div className="card brutal">
        <h3 style={{marginTop:0}}>Manage Quizzes</h3>
        <div className="quiz-table">
          {quizzes.length === 0 ? (
            <div style={{textAlign:'center', padding:'20px', color:'#666'}}>
              No quizzes created yet. Create your first quiz above!
            </div>
          ) : (
            quizzes.map(q => (
              <div key={q.id} className="quiz-row" style={{
                display:'flex', 
                justifyContent:'space-between', 
                alignItems:'center', 
                padding:'12px', 
                border:'1px solid #ddd', 
                marginBottom:'8px',
                borderRadius:'4px'
              }}>
                <div className="quiz-info" style={{flex:1}}>
                  <div style={{fontWeight:'bold', marginBottom:'4px'}}>{q.title}</div>
                  <div style={{fontSize:'14px', color:'#666'}}>
                    Difficulty: <span style={{
                      color: q.difficulty === 'EASY' ? '#4CAF50' : 
                             q.difficulty === 'MEDIUM' ? '#FF9800' : '#F44336',
                      fontWeight:'bold'
                    }}>{q.difficulty}</span>
                  </div>
                </div>
                <div className="quiz-actions" style={{display:'flex', gap:'8px'}}>
                  <button 
                    className="btn brutal btn-small" 
                    onClick={()=>openQuestions(q.id)}
                    style={{fontSize:'12px', padding:'6px 12px'}}
                  >
                    Edit Questions
                  </button>
                  <button 
                    className="btn brutal btn-small btn-danger" 
                    onClick={()=>remove(q.id)}
                    style={{fontSize:'12px', padding:'6px 12px', backgroundColor:'#f44336', color:'white'}}
                  >
                    Delete
                  </button>
              </div>
              </div>
            ))
          )}
        </div>
      </div>

      <div style={{gridColumn:'1 / -1'}}>
        <AnalyticsDashboard />
      </div>
    </div>

    <Modal open={questionModalOpen} title="Edit Questions" onClose={()=>setQuestionModalOpen(false)}>
      <div className="question-controls">
        <div className="save-controls">
          <button className="btn brutal btn-primary" onClick={saveQuestions}>Save All Questions</button>
          <button className="btn brutal" onClick={addQuestion}>Add New Question</button>
        </div>
      </div>

      <div className="questions-container">
        {questions.length === 0 ? (
          <div className="no-questions">
            <p>No questions yet. Click "Add New Question" to get started.</p>
          </div>
        ) : (
          questions.map((question, index) => (
            <QuestionEditor
              key={index}
              question={question}
              questionNumber={index + 1}
              onUpdate={(updatedQuestion) => updateQuestion(index, updatedQuestion)}
              onDelete={() => deleteQuestion(index)}
            />
          ))
        )}
      </div>
    </Modal>
    </>
  )
}