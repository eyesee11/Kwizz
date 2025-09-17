import React, { useState } from 'react';
import axios from 'axios';

interface AIQuizGeneratorProps {
  onQuizGenerated: (questionBank: any) => void;
}

const AIQuizGenerator: React.FC<AIQuizGeneratorProps> = ({ onQuizGenerated }) => {
  const [subject, setSubject] = useState('');
  const [difficulty, setDifficulty] = useState('MEDIUM');
  const [numberOfQuestions, setNumberOfQuestions] = useState(5);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem('token');
      const response = await axios.post('/api/ai/generate', {
        subject,
        difficulty,
        numberOfQuestions
      }, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      onQuizGenerated(response.data);
    } catch (err: any) {
      console.error('Error generating quiz:', err);
      setError(err?.response?.data?.message || err?.message || 'Failed to generate quiz');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="ai-quiz-generator card brutal">
      <h3>Generate Quiz with AI</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="subject">Subject</label>
          <input
            type="text"
            id="subject"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            placeholder="e.g., JavaScript Basics"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="difficulty">Difficulty</label>
          <select
            id="difficulty"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
            required
          >
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="numberOfQuestions">Number of Questions</label>
          <input
            type="number"
            id="numberOfQuestions"
            value={numberOfQuestions}
            onChange={(e) => setNumberOfQuestions(parseInt(e.target.value))}
            min="1"
            max="20"
            required
          />
        </div>

        {error && <div className="error-message">{error}</div>}

        <button 
          type="submit" 
          className="btn brutal" 
          disabled={isLoading}
        >
          {isLoading ? 'Generating...' : 'Generate Quiz'}
        </button>
      </form>
    </div>
  );
};

export default AIQuizGenerator;