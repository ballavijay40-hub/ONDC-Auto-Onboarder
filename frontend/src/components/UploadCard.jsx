import React, { useState } from 'react';
import { UploadCloud, CheckCircle2, AlertCircle, FileText, Loader2, RefreshCw } from 'lucide-react';
import { documentService } from '../services/documentService';

export const UploadCard = ({ onUploadComplete }) => {
  const [file, setFile] = useState(null);
  const [documentType, setDocumentType] = useState('GST');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [lastUploadedDoc, setLastUploadedDoc] = useState(null);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setError(null);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file to upload');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const result = await documentService.uploadDocument(file, documentType);
      setLastUploadedDoc(result);
      setFile(null);
      if (onUploadComplete) onUploadComplete(result);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to upload document');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 className="text-base font-bold text-slate-900 mb-1">Upload Regulatory Document</h3>
      <p className="text-xs text-slate-500 mb-5">
        Upload your GST Certificate (REG-06), PAN Card, or Business License. Tess4J OCR extracts your credentials instantly.
      </p>

      {error && (
        <div className="mb-4 flex items-center space-x-2 rounded-xl bg-red-50 p-3 text-xs text-red-700 border border-red-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleUpload} className="space-y-4">
        <div>
          <label className="block text-xs font-semibold text-slate-700 mb-1">
            Document Type
          </label>
          <select
            value={documentType}
            onChange={(e) => setDocumentType(e.target.value)}
            className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100"
          >
            <option value="GST">GST Registration Certificate (REG-06)</option>
            <option value="PAN">Income Tax PAN Card</option>
            <option value="BUSINESS_LICENSE">Shop & Establishment / Business License</option>
          </select>
        </div>

        <div className="border-2 border-dashed border-slate-200 hover:border-blue-400 rounded-2xl p-6 text-center transition-colors bg-slate-50/50">
          <input
            type="file"
            id="docFile"
            accept=".pdf,.png,.jpg,.jpeg,.webp"
            onChange={handleFileChange}
            className="hidden"
          />
          <label htmlFor="docFile" className="cursor-pointer flex flex-col items-center space-y-2">
            <div className="h-12 w-12 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
              <UploadCloud className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-semibold text-slate-700">
                {file ? file.name : 'Click to select or drag document here'}
              </p>
              <p className="text-xs text-slate-400 mt-0.5">
                PDF, PNG, JPG up to 10MB
              </p>
            </div>
          </label>
        </div>

        <button
          type="submit"
          disabled={loading || !file}
          className="w-full flex items-center justify-center space-x-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-md shadow-blue-500/20 hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              <span>Running Tess4J OCR & Uploading...</span>
            </>
          ) : (
            <>
              <FileText className="h-4 w-4" />
              <span>Upload & Run OCR Extraction</span>
            </>
          )}
        </button>
      </form>

      {lastUploadedDoc && lastUploadedDoc.extractedFields && Object.keys(lastUploadedDoc.extractedFields).length > 0 && (
        <div className="mt-5 rounded-xl bg-emerald-50 border border-emerald-200 p-4">
          <div className="flex items-center space-x-2 text-emerald-800 font-semibold text-xs mb-2">
            <CheckCircle2 className="h-4 w-4 text-emerald-600" />
            <span>OCR Extraction Succeeded & Synced with Profile</span>
          </div>
          <div className="grid grid-cols-2 gap-2 text-xs">
            {Object.entries(lastUploadedDoc.extractedFields).map(([k, v]) => (
              <div key={k} className="bg-white/70 p-2 rounded-lg border border-emerald-100">
                <span className="font-semibold text-slate-600 capitalize">{k}: </span>
                <span className="text-slate-900 font-mono">{v}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default UploadCard;

