import React, { useState, useEffect } from 'react';
import { complianceService } from '../services/complianceService';
import ComplianceCard from '../components/ComplianceCard';
import { 
  Award, 
  ShieldCheck, 
  RefreshCw, 
  Loader2, 
  AlertCircle, 
  CheckCircle2, 
  Download, 
  FileCheck
} from 'lucide-react';

export const Compliance = () => {
  const [compliance, setCompliance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [auditLoading, setAuditLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedProvider, setSelectedProvider] = useState('claude');

  const fetchCompliance = async () => {
    try {
      setLoading(true);
      const data = await complianceService.getMyCompliance();
      setCompliance(data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch compliance record');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCompliance();
  }, []);

  const handleAudit = async () => {
    try {
      setAuditLoading(true);
      const data = await complianceService.runComplianceCheck(selectedProvider);
      setCompliance(data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Compliance audit failed');
    } finally {
      setAuditLoading(false);
    }
  };

  const downloadCertificate = () => {
    if (!compliance) return;
    const certText = `
======================================================
         ONDC NETWORK READINESS CERTIFICATE
======================================================
Seller ID: ${compliance.sellerId}
Score: ${compliance.score} / 100
Status: ${compliance.status}
Auditor: ${compliance.providerUsed || 'ONDC Compliance Engine'}
Issued At: ${new Date(compliance.checkedAt || Date.now()).toLocaleString()}

Passed Requirements:
${(compliance.passedChecks || []).map((c) => '  [PASS] ' + c).join('\n')}

Audit Notes & Remarks:
${(compliance.remarks || []).map((r) => '  - ' + r).join('\n')}

Action Items:
${(compliance.missingItems || []).map((m) => '  - ' + m).join('\n')}
======================================================
Verified for ONDC Beckn Protocol retail exchange.
    `;
    const blob = new Blob([certText.trim()], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ondc-compliance-certificate-${compliance.sellerId}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-900">
            ONDC Compliance & Regulatory Audit
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Automated verification against Indian Legal Metrology, Consumer Protection (E-Commerce) Rules 2020, and ONDC Network requirements.
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <select
            value={selectedProvider}
            onChange={(e) => setSelectedProvider(e.target.value)}
            className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-100"
          >
            <option value="claude">Anthropic Claude Sonnet 4.5</option>
            <option value="gemini">Google Gemini 2.5 Flash</option>
          </select>

          <button
            onClick={handleAudit}
            disabled={auditLoading}
            className="flex items-center space-x-1.5 rounded-xl bg-blue-600 hover:bg-blue-700 px-4 py-2 text-xs font-bold text-white shadow-md shadow-blue-500/20 transition disabled:opacity-50"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${auditLoading ? 'animate-spin' : ''}`} />
            <span>Run Deep Audit</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl bg-red-50 p-4 text-xs text-red-700 border border-red-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="flex h-64 items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
        </div>
      ) : (
        <>
          <ComplianceCard
            compliance={compliance}
            onRecheck={handleAudit}
            loading={auditLoading}
          />

          {compliance && compliance.score >= 80 && (
            <div className="rounded-2xl border border-emerald-200 bg-gradient-to-r from-emerald-50 via-teal-50 to-emerald-50 p-6 flex items-center justify-between shadow-sm">
              <div className="flex items-center space-x-3.5">
                <div className="h-12 w-12 rounded-2xl bg-emerald-600 text-white flex items-center justify-center shadow-md shadow-emerald-500/25">
                  <FileCheck className="h-6 w-6" />
                </div>
                <div>
                  <h4 className="text-sm font-extrabold text-emerald-950">
                    Official ONDC Readiness Certificate Available
                  </h4>
                  <p className="text-xs text-emerald-700 mt-0.5">
                    Your store satisfies all network criteria and is ready for live registry publication.
                  </p>
                </div>
              </div>

              <button
                onClick={downloadCertificate}
                className="flex items-center space-x-1.5 rounded-xl bg-emerald-700 hover:bg-emerald-800 px-4 py-2 text-xs font-bold text-white shadow-sm transition shrink-0"
              >
                <Download className="h-3.5 w-3.5" />
                <span>Download Certificate</span>
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Compliance;
