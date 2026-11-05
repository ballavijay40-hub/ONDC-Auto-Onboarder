import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { dashboardService } from '../services/dashboardService';
import { complianceService } from '../services/complianceService';
import StatCard from '../components/StatCard';
import { 
  ShieldCheck, 
  FileText, 
  ShoppingBag, 
  Sparkles, 
  CheckCircle2, 
  Circle, 
  ArrowRight, 
  Download, 
  AlertCircle,
  Loader2,
  RefreshCw
} from 'lucide-react';

export const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [auditLoading, setAuditLoading] = useState(false);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getStats();
      setStats(data);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const handleQuickAudit = async () => {
    try {
      setAuditLoading(true);
      await complianceService.runComplianceCheck('claude');
      await fetchStats();
    } catch (err) {
      alert('Compliance check error: ' + (err.response?.data?.message || err.message));
    } finally {
      setAuditLoading(false);
    }
  };

  const handleExportJson = async () => {
    try {
      const catalog = await dashboardService.exportBecknCatalog();
      const blob = new Blob([JSON.stringify(catalog, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ondc-catalog-${stats?.seller?.businessName || 'export'}.json`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert('Failed to export catalog: ' + (err.response?.data?.message || err.message));
    }
  };

  if (loading && !stats) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="flex flex-col items-center space-y-3">
          <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
          <p className="text-xs font-semibold text-slate-500">Loading your onboarding cockpit...</p>
        </div>
      </div>
    );
  }

  const funnelSteps = [
    { key: 'sellerRegistered', label: '1. Registration', desc: 'Account created' },
    { key: 'documentsUploaded', label: '2. Documents', desc: 'GST/PAN uploaded' },
    { key: 'ocrExtracted', label: '3. OCR Verification', desc: 'Tess4J extracted' },
    { key: 'productsUploaded', label: '4. Products', desc: 'Items created' },
    { key: 'aiCatalogGenerated', label: '5. AI Catalog', desc: 'ONDC tags enriched' },
    { key: 'ondcReady', label: '6. Network Ready', desc: 'Readiness approved' },
  ];

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-2xl bg-gradient-to-r from-blue-900 via-indigo-900 to-slate-900 p-6 text-white shadow-md">
        <div>
          <span className="inline-flex items-center space-x-1.5 rounded-full bg-white/10 px-3 py-1 text-xs font-semibold text-blue-200">
            <span className="h-2 w-2 rounded-full bg-emerald-400"></span>
            <span>Live Onboarding Pipeline</span>
          </span>
          <h2 className="text-2xl font-bold tracking-tight mt-2">
            Welcome back, {stats?.seller?.businessName || 'Seller'}!
          </h2>
          <p className="text-xs text-blue-200 mt-1">
            Track your automated verification, Tess4J OCR extractions, and ONDC compliance status.
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <button
            onClick={handleQuickAudit}
            disabled={auditLoading}
            className="flex items-center space-x-1.5 rounded-xl bg-white/10 hover:bg-white/20 px-3.5 py-2 text-xs font-semibold text-white transition backdrop-blur-md"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${auditLoading ? 'animate-spin' : ''}`} />
            <span>Re-Audit Score</span>
          </button>

          <button
            onClick={handleExportJson}
            className="flex items-center space-x-1.5 rounded-xl bg-blue-500 hover:bg-blue-400 px-4 py-2 text-xs font-bold text-white shadow-sm transition"
          >
            <Download className="h-3.5 w-3.5" />
            <span>Export ONDC JSON</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl bg-red-50 p-4 text-xs text-red-700 border border-red-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Compliance Score"
          value={`${stats?.complianceScore || 0}/100`}
          subtext={stats?.complianceStatus || 'Needs Attention'}
          icon={ShieldCheck}
          color={stats?.complianceScore >= 80 ? 'emerald' : 'amber'}
        />
        <StatCard
          title="Documents Processed"
          value={`${stats?.verifiedDocuments || 0} / ${stats?.totalDocuments || 0}`}
          subtext="Verified via Tess4J OCR"
          icon={FileText}
          color="blue"
        />
        <StatCard
          title="Fashion Catalog"
          value={stats?.totalProducts || 0}
          subtext="Items ready for ONDC sync"
          icon={ShoppingBag}
          color="indigo"
        />
        <StatCard
          title="AI Generated"
          value={stats?.aiGeneratedProducts || 0}
          subtext="Auto-enriched via Gemini/Claude"
          icon={Sparkles}
          color="emerald"
        />
      </div>

      {/* Onboarding Funnel */}
      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h3 className="text-base font-bold text-slate-900 mb-1">
          Automated Onboarding Funnel Progress
        </h3>
        <p className="text-xs text-slate-500 mb-6">
          Six-stage automated pipeline transforming raw merchant registration into a certified ONDC seller.
        </p>

        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
          {funnelSteps.map((step) => {
            const completed = stats?.onboardingFunnel ? !!stats.onboardingFunnel[step.key] : false;
            return (
              <div
                key={step.key}
                className={`rounded-xl p-4 border transition-all ${
                  completed
                    ? 'border-emerald-200 bg-emerald-50/60 text-emerald-950'
                    : 'border-slate-200 bg-slate-50/80 text-slate-600'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="text-[11px] font-bold uppercase tracking-wider">
                    {step.label}
                  </span>
                  {completed ? (
                    <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0" />
                  ) : (
                    <Circle className="h-4 w-4 text-slate-300 shrink-0" />
                  )}
                </div>
                <p className="text-xs font-semibold">{step.desc}</p>
                <span className={`inline-block mt-2 text-[10px] font-bold rounded-md px-1.5 py-0.5 ${
                  completed ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-600'
                }`}>
                  {completed ? 'Completed' : 'Pending'}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Quick Actions & Pending Items */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Pending Action Items */}
        <div className="lg:col-span-2 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-base font-bold text-slate-900">
              Onboarding Checklist & Action Items
            </h3>
            <span className="rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-semibold text-blue-700">
              {stats?.pendingActionItems?.length || 0} remaining
            </span>
          </div>

          {stats?.pendingActionItems && stats.pendingActionItems.length > 0 ? (
            <ul className="space-y-2.5">
              {stats.pendingActionItems.map((item, idx) => (
                <li
                  key={idx}
                  className="flex items-start space-x-3 rounded-xl border border-amber-100 bg-amber-50/40 p-3 text-xs text-amber-900"
                >
                  <AlertCircle className="h-4 w-4 text-amber-600 shrink-0 mt-0.5" />
                  <span className="font-medium leading-relaxed">{item}</span>
                </li>
              ))}
            </ul>
          ) : (
            <div className="flex flex-col items-center justify-center p-8 rounded-xl bg-emerald-50/50 border border-emerald-100 text-center">
              <CheckCircle2 className="h-10 w-10 text-emerald-500 mb-2" />
              <h4 className="text-sm font-bold text-emerald-900">All Criteria Complete!</h4>
              <p className="text-xs text-emerald-700 mt-1 max-w-sm">
                Your business has completed all document checks and catalog requirements. You are 100% ONDC Ready.
              </p>
            </div>
          )}
        </div>

        {/* Quick Launchpad */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm flex flex-col justify-between">
          <div>
            <h3 className="text-base font-bold text-slate-900 mb-1">
              Quick Launchpad
            </h3>
            <p className="text-xs text-slate-500 mb-4">
              Jump directly to core onboarding tools
            </p>

            <div className="space-y-2.5">
              <Link
                to="/documents"
                className="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 p-3 text-xs font-semibold text-slate-700 hover:border-blue-400 hover:bg-blue-50/50 hover:text-blue-700 transition"
              >
                <div className="flex items-center space-x-2.5">
                  <FileText className="h-4 w-4 text-blue-600" />
                  <span>Upload GST & Run OCR</span>
                </div>
                <ArrowRight className="h-3.5 w-3.5 text-slate-400" />
              </Link>

              <Link
                to="/product-upload"
                className="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 p-3 text-xs font-semibold text-slate-700 hover:border-purple-400 hover:bg-purple-50/50 hover:text-purple-700 transition"
              >
                <div className="flex items-center space-x-2.5">
                  <Sparkles className="h-4 w-4 text-purple-600" />
                  <span>AI Catalog Generator</span>
                </div>
                <ArrowRight className="h-3.5 w-3.5 text-slate-400" />
              </Link>

              <Link
                to="/compliance"
                className="flex items-center justify-between rounded-xl border border-slate-200 bg-slate-50 p-3 text-xs font-semibold text-slate-700 hover:border-emerald-400 hover:bg-emerald-50/50 hover:text-emerald-700 transition"
              >
                <div className="flex items-center space-x-2.5">
                  <ShieldCheck className="h-4 w-4 text-emerald-600" />
                  <span>View Compliance Audit</span>
                </div>
                <ArrowRight className="h-3.5 w-3.5 text-slate-400" />
              </Link>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-slate-100">
            <button
              onClick={handleExportJson}
              className="w-full flex items-center justify-center space-x-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-white py-2.5 text-xs font-bold transition shadow-xs"
            >
              <Download className="h-3.5 w-3.5" />
              <span>Download Beckn Catalog JSON</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
