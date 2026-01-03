import { useState } from 'react';
import { uploadResume, analyzeResume } from './api';
import './App.css';

function App() {
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [uploadId, setUploadId] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [step, setStep] = useState(1);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setError(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a file');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const result = await uploadResume(file);
      setUploadId(result.uploadId);
      setStep(2);
    } catch (err) {
      setError(err.response?.data?.error || 'Upload failed');
    } finally {
      setLoading(false);
    }
  };

  const handleAnalyze = async () => {
    if (!jobDescription.trim()) {
      setError('Please enter a job description');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const result = await analyzeResume(uploadId, jobDescription);
      setAnalysis(result);
      setStep(3);
    } catch (err) {
      setError(err.response?.data?.error || 'Analysis failed');
    } finally {
      setLoading(false);
    }
  };

  const resetApp = () => {
    setFile(null);
    setJobDescription('');
    setUploadId(null);
    setAnalysis(null);
    setError(null);
    setStep(1);
  };

  const getTierColor = (tier) => {
    if (tier.includes('1')) return '#FFD700';
    if (tier.includes('2')) return '#C0C0C0';
    if (tier.includes('3')) return '#CD7F32';
    return '#888';
  };

  return (
    <div className="app">
      <header className="header">
        <h1>🔥 Resume Roaster</h1>
        <p>Get honest feedback on your resume for product-based companies</p>
      </header>

      <main className="container">
        {step === 1 && (
          <div className="upload-section">
            <h2>Step 1: Upload Your Resume</h2>
            <div className="file-input-wrapper">
              <input
                type="file"
                accept=".pdf,.docx,.txt"
                onChange={handleFileChange}
                id="file-input"
              />
              <label htmlFor="file-input" className="file-label">
                {file ? file.name : 'Choose File (PDF, DOCX, or TXT)'}
              </label>
            </div>
            <button
              onClick={handleUpload}
              disabled={!file || loading}
              className="btn btn-primary"
            >
              {loading ? 'Uploading...' : 'Upload Resume'}
            </button>
          </div>
        )}

        {step === 2 && (
          <div className="job-section">
            <h2>Step 2: Enter Job Description</h2>
            <p className="success-message">✓ Resume uploaded successfully!</p>
            <textarea
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              placeholder="Paste the job description here..."
              rows="10"
              className="job-textarea"
            />
            <div className="button-group">
              <button onClick={resetApp} className="btn btn-secondary">
                Upload New Resume
              </button>
              <button
                onClick={handleAnalyze}
                disabled={!jobDescription.trim() || loading}
                className="btn btn-primary"
              >
                {loading ? 'Analyzing...' : 'Analyze Resume'}
              </button>
            </div>
          </div>
        )}

        {step === 3 && analysis && (
          <div className="results-section">
            <h2>Analysis Results</h2>
            
            <div className="score-card">
              <div className="score-circle">
                <span className="score-number">{analysis.overallScore}</span>
                <span className="score-max">/100</span>
              </div>
              <div 
                className="tier-badge" 
                style={{ backgroundColor: getTierColor(analysis.tierPlacement) }}
              >
                {analysis.tierPlacement}
              </div>
            </div>

            <div className="verdict-card">
              <h3>Verdict</h3>
              <p>{analysis.verdict}</p>
            </div>

            <div className="issues-card">
              <h3>Top Issues</h3>
              <ul>
                {analysis.topIssues.map((issue, index) => (
                  <li key={index}>{issue}</li>
                ))}
              </ul>
            </div>

            <div className="actions-card">
              <h3>Action Items</h3>
              <ol>
                {analysis.actionItems.map((action, index) => (
                  <li key={index}>{action}</li>
                ))}
              </ol>
            </div>

            <button onClick={resetApp} className="btn btn-primary">
              Analyze Another Resume
            </button>
          </div>
        )}

        {error && (
          <div className="error-message">
            ⚠️ {error}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
