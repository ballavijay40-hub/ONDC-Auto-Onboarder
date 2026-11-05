import React from 'react';
import { ShieldCheck, AlertTriangle, CheckCircle2, XCircle, Sparkles, RefreshCw } from 'lucide-react';

export const ComplianceCard = ({ compliance, onRecheck, loading }) => {
  if (!compliance) {
    return (
      <div className="rounded-2xl border border-slate-200 bg-white p-6 text-center">
        <p className="text-sm text-slate-500">No compliance evaluation found.</p>
        <button
          onClick={() => onRecheck && onRecheck()}
          disabled={loading}
          className="mt-3 inline-flex items-center space-x-2 rounded-xl bg-blue-600 px-4 py-2 text-xs font-semibold text-white hover:bg-blue-700"
        >
          <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Run Initial Compliance Check</span>
        </button>
      </div>
    );
  }

  const isReady = compliance.score >= 80;

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <span className={`inline-flex items-center space-x-1 rounded-full px-3 py-1 text-xs font-bold ${
              isReady
                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                : 'bg-amber-50 text-amber-700 border border-amber-200'
            }`}>
              {isReady ? <ShieldCheck className="h-4 w-4" /> : <AlertTriangle className="h-4 w-4" />}
              <span>{compliance.status}</span>
            </span>
            <span className="text-xs text-slate-500 font-medium">
              Auditor: {compliance.providerUsed || 'Compliance Engine'}
            </span>
          </div>
          <h3 className="text-lg font-bold text-slate-900 mt-2">
            ONDC Network Readiness Score
          </h3>
        </div>

        <div className="flex items-center space-x-3">
          <div className="text-right">
            <span className="text-3xl font-extrabold tracking-tight text-slate-900">
              {compliance.score}
            </span>
            <span className="text-sm text-slate-400 font-semibold">/100</span>
          </div>
          <button
            onClick={() => onRecheck && onRecheck()}
            disabled={loading}
            className="flex items-center space-x-1.5 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-100 transition shadow-2xs"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>Audit</span>
          </button>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="mt-4">
        <div className="h-3 w-full rounded-full bg-slate-100 overflow-hidden">
          <div
            className={`h-full transition-all duration-500 ${
              isReady ? 'bg-gradient-to-r from-emerald-500 to-teal-500' : 'bg-gradient-to-r from-amber-500 to-orange-500'
            }`}
            style={{ width: `${compliance.score}%` }}
          />
        </div>
        <div className="flex justify-between text-[11px] text-slate-400 mt-1 font-medium">
          <span>0 (Non-compliant)</span>
          <span>Threshold: 80 (ONDC Ready)</span>
          <span>100 (Full Compliance)</span>
        </div>
      </div>

      {/* Audit Remarks */}
      {compliance.remarks && compliance.remarks.length > 0 && (
        <div className="mt-5 rounded-xl bg-slate-50 p-3.5 border border-slate-100 text-xs text-slate-700">
          <span className="font-semibold block mb-1 text-slate-800">Auditor Notes:</span>
          <ul className="list-disc pl-4 space-y-1">
            {compliance.remarks.map((r, i) => (
              <li key={i}>{r}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Checks Grid */}
      <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Passed Checks */}
        <div className="rounded-xl border border-emerald-100 bg-emerald-50/40 p-4">
          <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-800 mb-2 flex items-center space-x-1.5">
            <CheckCircle2 className="h-4 w-4 text-emerald-600" />
            <span>Passed Checks ({compliance.passedChecks ? compliance.passedChecks.length : 0})</span>
          </h4>
          <ul className="space-y-1.5 text-xs text-slate-700">
            {compliance.passedChecks && compliance.passedChecks.length > 0 ? (
              compliance.passedChecks.map((item, i) => (
                <li key={i} className="flex items-start space-x-1.5">
                  <span className="text-emerald-600 font-bold">✓</span>
                  <span>{item}</span>
                </li>
              ))
            ) : (
              <li className="text-slate-400 italic">No checks passed yet</li>
            )}
          </ul>
        </div>

        {/* Missing Items */}
        <div className="rounded-xl border border-amber-100 bg-amber-50/40 p-4">
          <h4 className="text-xs font-bold uppercase tracking-wider text-amber-800 mb-2 flex items-center space-x-1.5">
            <XCircle className="h-4 w-4 text-amber-600" />
            <span>Missing / Action Items ({compliance.missingItems ? compliance.missingItems.length : 0})</span>
          </h4>
          <ul className="space-y-1.5 text-xs text-slate-700">
            {compliance.missingItems && compliance.missingItems.length > 0 ? (
              compliance.missingItems.map((item, i) => (
                <li key={i} className="flex items-start space-x-1.5">
                  <span className="text-amber-600 font-bold">!</span>
                  <span>{item}</span>
                </li>
              ))
            ) : (
              <li className="text-emerald-700 font-medium">None! All mandatory criteria fulfilled.</li>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ComplianceCard;

