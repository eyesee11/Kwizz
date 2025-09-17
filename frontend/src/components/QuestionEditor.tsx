import React, { useState } from 'react'

interface Option {
  text: string
  correct: boolean
}

interface Question {
  id?: number
  text: string
  options: Option[]
}

interface QuestionEditorProps {
  question: Question
  onUpdate: (question: Question) => void
  onDelete: () => void
  questionNumber: number
}

export default function QuestionEditor({ question, onUpdate, onDelete, questionNumber }: QuestionEditorProps) {
  const [localQuestion, setLocalQuestion] = useState<Question>(question)

  const updateQuestion = (updates: Partial<Question>) => {
    const updated = { ...localQuestion, ...updates }
    setLocalQuestion(updated)
    onUpdate(updated)
  }

  const updateOption = (index: number, text: string) => {
    const newOptions = [...localQuestion.options]
    newOptions[index] = { ...newOptions[index], text }
    updateQuestion({ options: newOptions })
  }

  const setCorrectOption = (index: number) => {
    const newOptions = localQuestion.options.map((opt, i) => ({
      ...opt,
      correct: i === index
    }))
    updateQuestion({ options: newOptions })
  }

  return (
    <div className="question-editor-card">
      <div className="question-header">
        <h4>Question {questionNumber}</h4>
        <button 
          className="btn brutal btn-small btn-danger" 
          onClick={(e) => {
            e.preventDefault()
            e.stopPropagation()
            onDelete()
          }}
          title="Delete Question"
          type="button"
        >
          🗑️
        </button>
      </div>

      <div className="question-input-group">
        <label>Question Text</label>
        <textarea
          value={localQuestion.text}
          onChange={(e) => updateQuestion({ text: e.target.value })}
          placeholder="Enter your question here..."
          className="question-textarea"
          rows={3}
        />
      </div>

      <div className="options-section">
        <label>Answer Options</label>
        <div className="options-list">
          {localQuestion.options.map((option, index) => (
            <div key={index} className="option-item">
              <div className="option-input-wrapper">
                <input
                  type="text"
                  value={option.text}
                  onChange={(e) => updateOption(index, e.target.value)}
                  placeholder={`Option ${index + 1}`}
                  className="option-input"
                />
                <button
                  type="button"
                  className={`correct-btn ${option.correct ? 'active' : ''}`}
                  onClick={() => setCorrectOption(index)}
                  title="Mark as correct answer"
                >
                  {option.correct ? '✓' : '○'}
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
