import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface Student {
  id: number;
  name: string;
  email: string;
  attempts: number;
  avgScore: number;
  bestScore: number;
  lastAttempt: string;
}

interface Analytics {
  totalAttempts: number;
  avgScore: number;
  bestScore: number;
  worstScore: number;
  totalStudents: number;
  totalQuizzes: number;
}

interface RecentAttempt {
  id: number;
  studentName: string;
  quizTitle: string;
  score: number;
  createdAt: string;
}

const AnalyticsDashboard: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [analytics, setAnalytics] = useState<Analytics | null>(null);
  const [recentAttempts, setRecentAttempts] = useState<RecentAttempt[]>([]);
  const [showStudentList, setShowStudentList] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [studentsRes, analyticsRes, attemptsRes] = await Promise.all([
        axios.get('/api/analytics/students'),
        axios.get('/api/analytics/summary'),
        axios.get('/api/analytics/attempts')
      ]);
      
      setStudents(studentsRes.data);
      setAnalytics(analyticsRes.data);
      setRecentAttempts(attemptsRes.data.slice(0, 10));
    } catch (error) {
      console.error('Error loading analytics data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="card brutal">
        <h3>Loading Analytics...</h3>
      </div>
    );
  }

  return (
    <div className="analytics-dashboard">
      {/* Analytics Overview */}
      <div className="card brutal analytics-overview">
        <h3 style={{ marginTop: 0 }}>📊 Analytics Overview</h3>
        {analytics && (
          <div className="analytics-grid">
            <div className="stat-card">
              <div className="stat-number">{analytics.totalAttempts}</div>
              <div className="stat-label">Total Attempts</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">{Math.round(analytics.avgScore)}%</div>
              <div className="stat-label">Average Score</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">{analytics.bestScore}%</div>
              <div className="stat-label">Best Score</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">{analytics.totalStudents}</div>
              <div className="stat-label">Total Students</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">{analytics.totalQuizzes}</div>
              <div className="stat-label">Total Quizzes</div>
            </div>
          </div>
        )}
      </div>

      {/* Students Section */}
      <div className="card brutal students-section">
        <div className="section-header">
          <h3 style={{ marginTop: 0 }}>👥 Students</h3>
          <button 
            className="btn brutal btn-small" 
            onClick={() => setShowStudentList(!showStudentList)}
          >
            {showStudentList ? 'Hide Details' : 'View All Students'}
          </button>
        </div>
        
        {showStudentList ? (
          <div className="students-list">
            <div className="students-table">
              <div className="table-header">
                <div>Name</div>
                <div>Email</div>
                <div>Attempts</div>
                <div>Avg Score</div>
                <div>Best Score</div>
                <div>Last Attempt</div>
              </div>
              {students.map(student => (
                <div key={student.id} className="table-row">
                  <div className="student-name">{student.name}</div>
                  <div className="student-email">{student.email}</div>
                  <div className="student-attempts">{student.attempts}</div>
                  <div className="student-avg">{Math.round(student.avgScore)}%</div>
                  <div className="student-best">{student.bestScore}%</div>
                  <div className="student-last">
                    {student.lastAttempt ? new Date(student.lastAttempt).toLocaleDateString() : 'Never'}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="students-summary">
            <div className="summary-stats">
              <div className="summary-item">
                <span className="summary-number">{students.length}</span>
                <span className="summary-text">Total Students</span>
              </div>
              <div className="summary-item">
                <span className="summary-number">
                  {students.filter(s => s.attempts > 0).length}
                </span>
                <span className="summary-text">Active Students</span>
              </div>
              <div className="summary-item">
                <span className="summary-number">
                  {Math.round(students.reduce((acc, s) => acc + s.avgScore, 0) / students.length) || 0}%
                </span>
                <span className="summary-text">Class Average</span>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Recent Attempts */}
      <div className="card brutal recent-attempts">
        <h3 style={{ marginTop: 0 }}>🕒 Recent Attempts</h3>
        {recentAttempts.length > 0 ? (
          <div className="attempts-table">
            <div className="table-header">
              <div>Student</div>
              <div>Quiz</div>
              <div>Score</div>
              <div>Time</div>
            </div>
            {recentAttempts.map(attempt => (
              <div key={attempt.id} className="table-row">
                <div className="attempt-student">{attempt.studentName}</div>
                <div className="attempt-quiz">{attempt.quizTitle}</div>
                <div className={`attempt-score ${attempt.score >= 80 ? 'high' : attempt.score >= 60 ? 'medium' : 'low'}`}>
                  {attempt.score}%
                </div>
                <div className="attempt-time">
                  {new Date(attempt.createdAt).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="no-attempts">
            <p>No recent attempts found.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
