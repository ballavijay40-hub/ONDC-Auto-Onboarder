import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldCheck, ArrowRight, AlertCircle, Loader2 } from 'lucide-react';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen bg-slate-50">
      <div className="flex flex-1 flex-col justify-center px-4 py-12 sm:px-6 lg:flex-none lg:px-20 xl:px-24">
        <div className="mx-auto w-full max-w-sm lg:w-96">
          <div className="flex items-center space-x-3 mb-8">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-blue-600 text-white shadow-lg shadow-blue-500/25">
              <ShieldCheck className="h-7 w-7" />
            </div>
            <div>
              <h2 className="text-xl font-extrabold text-slate-900 tracking-tight">
                ONDC <span className="text-blue-600">Auto-Onboarder</span>
              </h2>
              <p className="text-xs text-slate-500 font-medium">Fashion & Apparel Network Gateway</p>
            </div>
          </div>

          <div>
            <h3 className="text-2xl font-bold tracking-tight text-slate-900">Sign in to your account</h3>
            <p className="mt-1 text-sm text-slate-500">
              Enter your seller credentials to access your onboarding cockpit
            </p>
          </div>

          {error && (
            <div className="mt-4 flex items-center space-x-2 rounded-xl bg-red-50 p-3 text-xs text-red-700 border border-red-200">
              <AlertCircle className="h-4 w-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div className="mt-6">
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700">Email address</label>
                <div className="mt-1">
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="seller@example.com"
                    className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder:text-slate-400 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700">Password</label>
                <div className="mt-1">
                  <input
                    type="password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 placeholder:text-slate-400 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
                  />
                </div>
              </div>

              <div>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex w-full items-center justify-center space-x-2 rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-md shadow-blue-500/25 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition disabled:opacity-50"
                >
                  {loading ? (
                    <>
                      <Loader2 className="h-4 w-4 animate-spin" />
                      <span>Authenticating...</span>
                    </>
                  ) : (
                    <>
                      <span>Sign In</span>
                      <ArrowRight className="h-4 w-4" />
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>

          <div className="mt-6 text-center text-xs text-slate-500">
            Don't have a seller account yet?{' '}
            <Link to="/register" className="font-semibold text-blue-600 hover:text-blue-500">
              Register now
            </Link>
          </div>
        </div>
      </div>

      <div className="relative hidden w-0 flex-1 lg:block bg-gradient-to-tr from-slate-900 via-blue-950 to-indigo-900">
        <div className="absolute inset-0 flex flex-col justify-between p-12 text-white">
          <div className="inline-flex items-center space-x-2 rounded-full bg-white/10 px-3 py-1 text-xs backdrop-blur-md">
            <span className="h-2 w-2 rounded-full bg-emerald-400"></span>
            <span>Automated Fashion Onboarding Engine</span>
          </div>

          <div className="max-w-md space-y-4">
            <h3 className="text-3xl font-extrabold tracking-tight">
              Fast-track your Fashion & Apparel business onto ONDC.
            </h3>
            <p className="text-sm text-blue-200 leading-relaxed">
              Automated Tess4J OCR reads your GST certificate, Claude & Gemini generate compliance audits and ONDC-standard product catalogs in seconds.
            </p>
          </div>

          <div className="text-xs text-slate-400">
            © 2026 ONDC Auto-Onboarder Project. Built with React & Spring Boot.
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
