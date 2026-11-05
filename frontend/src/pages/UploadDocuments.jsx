import React, { useState, useEffect } from 'react';
import { documentService } from '../services/documentService';
import UploadCard from '../components/UploadCard';
import { useAuth } from '../context/AuthContext';
import { 
  FileText, 
  Trash2, 
  RefreshCw, 
  CheckCircle2, 
  Clock, 
  AlertCircle, 
  Loader2 
} from 'lucide-react';

export const UploadDocuments = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [ocrLoadingId, setOcrLoadingId] = useState(null);
  const [error, setError] = useState('');
  const { refreshSeller } = useAuth();

  const fetchDocs = async () => {
    try {
      setLoading(true);
      const docs = await documentService.getMyDocuments();
      setDocuments(docs);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch uploaded documents');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocs();
  }, []);

  const handleUploadDone = async () => {
    await fetchDocs();
    await refreshSeller();
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;
    try {
      await documentService.deleteDocument(id);
      await fetchDocs();
      await refreshSeller();
    } catch (err) {
      alert('Failed to delete document: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleRunOcr = async (docId) => {
    try {
      setOcrLoadingId(docId);
      await documentService.runOcr(docId);
      await fetchDocs();
      await refreshSeller();
    } catch (err) {
      alert('OCR error: ' + (err.response?.data?.message || err.message));
    } finally {
      setOcrLoadingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-bold tracking-tight text-slate-900">
          Document Management & Tess4J OCR Engine
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Upload regulatory certificates (GSTIN, PAN, License). The integrated OCR pipeline extracts business registration identifiers and synchronizes them with your profile.
        </p>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl bg-red-50 p-4 text-xs text-red-700 border border-red-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <UploadCard onUploadComplete={handleUploadDone} />
        </div>

        <div className="lg:col-span-2 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-base font-bold text-slate-900">Uploaded Documents Repository</h3>
            <button
              onClick={fetchDocs}
              className="text-slate-500 hover:text-blue-600 transition p-1"
              title="Refresh repository"
            >
              <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
            </button>
          </div>

          {loading && documents.length === 0 ? (
            <div className="flex h-48 items-center justify-center">
              <Loader2 className="h-6 w-6 animate-spin text-blue-600" />
            </div>
          ) : documents.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 text-center border-2 border-dashed border-slate-100 rounded-xl">
              <FileText className="h-10 w-10 text-slate-300 mb-2" />
              <p className="text-sm font-semibold text-slate-600">No documents uploaded yet</p>
              <p className="text-xs text-slate-400 mt-0.5 max-w-xs">
                Upload your GST Certificate or PAN card using the form on the left.
              </p>
            </div>
          ) : (
            <div className="divide-y divide-slate-100">
              {documents.map((doc) => {
                const isCompleted = doc.ocrStatus === 'COMPLETED';
                const isRunning = ocrLoadingId === doc.id;

                return (
                  <div key={doc.id} className="py-4 first:pt-0 last:pb-0">
                    <div className="flex items-start justify-between">
                      <div className="flex items-start space-x-3">
                        <div className="h-10 w-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center shrink-0 mt-0.5">
                          <FileText className="h-5 w-5" />
                        </div>
                        <div>
                          <div className="flex items-center space-x-2">
                            <span className="font-bold text-sm text-slate-900">
                              {doc.documentType}
                            </span>
                            <span className={`inline-flex items-center space-x-1 rounded-full px-2 py-0.5 text-[10px] font-bold ${
                              isCompleted
                                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                                : 'bg-amber-50 text-amber-700 border border-amber-200'
                            }`}>
                              {isCompleted ? <CheckCircle2 className="h-3 w-3" /> : <Clock className="h-3 w-3" />}
                              <span>{doc.ocrStatus}</span>
                            </span>
                          </div>
                          <p className="text-xs text-slate-400 mt-0.5 font-mono">
                            {doc.originalFileName || doc.filePath}
                          </p>

                          {/* Extracted Fields badges */}
                          {doc.extractedFields && Object.keys(doc.extractedFields).length > 0 && (
                            <div className="mt-2 flex flex-wrap gap-1.5">
                              {Object.entries(doc.extractedFields).map(([k, v]) => (
                                <span key={k} className="inline-flex items-center rounded-md bg-slate-100 px-2 py-0.5 text-[10px] font-medium text-slate-700">
                                  <span className="font-semibold text-slate-500 mr-1">{k}:</span>
                                  <span className="font-mono">{v}</span>
                                </span>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="flex items-center space-x-1">
                        <button
                          onClick={() => handleRunOcr(doc.id)}
                          disabled={isRunning}
                          className="rounded-lg p-1.5 text-slate-500 hover:bg-slate-100 hover:text-blue-600 transition"
                          title="Rerun OCR"
                        >
                          <RefreshCw className={`h-4 w-4 ${isRunning ? 'animate-spin text-blue-600' : ''}`} />
                        </button>
                        <button
                          onClick={() => handleDelete(doc.id)}
                          className="rounded-lg p-1.5 text-slate-400 hover:bg-slate-100 hover:text-red-600 transition"
                          title="Delete"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UploadDocuments;
